package com.app.chronolog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mockito.Mockito;

import com.app.chronolog.entity.AttendanceRecord;
import com.app.chronolog.exception.DuplicateClockInException;
import com.app.chronolog.exception.DuplicateClockOutException;
import com.app.chronolog.exception.NoClockInRecordException;
import com.app.chronolog.repository.AttendanceRepository;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;

public class AttendanceServicePropertyTest {

    private AttendanceRepository repository = Mockito.mock(AttendanceRepository.class);

    @Property
    // Feature: attendance-management, Property 1: 出勤記録の作成と完全性
    public void clockInCreateCompleteRecord(@ForAll @AlphaChars @StringLength(min = 1, max = 10) String employeeId) {
        // Given: サービスとリポジトリのセットアップ
        reset(repository);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        AttendanceService service = new AttendanceServiceImpl(repository);
        LocalDateTime beforeClockIn = LocalDateTime.now();

        // When: 出勤処理を実行
        AttendanceRecord record = service.clockIn(employeeId);
        LocalDateTime afterClockIn = LocalDateTime.now();

        // Then: レコードが完全であることを検証
        assertThat(record.getEmployeeId()).isEqualTo(employeeId);
        assertThat(record.getWorkDate()).isEqualTo(LocalDate.now());
        assertThat(record.getClockInTime()).isBetween(beforeClockIn, afterClockIn);
        assertThat(record.getClockOutTime()).isNull();
    }

    @Property
    // Feature: attendance-management, Property 2: 重複出勤の禁止
    public void duplicateClockInThrowException(@ForAll @AlphaChars @StringLength(min = 1, max = 10) String employeeId) {
        // Given: 既に出勤記録が存在する
        reset(repository);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        AttendanceService service = new AttendanceServiceImpl(repository);
        service.clockIn(employeeId);

        // When & Then: 2回目の出勤は例外をスロー
        when(repository.existsByEmployeeIdAndWorkDate(employeeId, LocalDate.now())).thenReturn(true);
        assertThatThrownBy(() -> service.clockIn(employeeId))
                .isInstanceOf(DuplicateClockInException.class)
                .hasMessageContaining("既に出勤記録があります");
    }

    @Property
    // Feature: attendance-management, Property 3: 退勤記録の更新
    public void clockOutCreateCompleteRecord(@ForAll @AlphaChars @StringLength(min = 1, max = 10) String employeeId) {
        // Given: 出勤を記録する
        reset(repository);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        AttendanceService service = new AttendanceServiceImpl(repository);
        AttendanceRecord clockInRecord = service.clockIn(employeeId);

        // When:退勤処理を実行
        when(repository.findByEmployeeIdAndWorkDate(employeeId, LocalDate.now()))
                .thenReturn(Optional.of(clockInRecord));
        LocalDateTime beforeClockOut = LocalDateTime.now();
        AttendanceRecord record = service.clockOut(employeeId);

        // Then: レコードが完全であること
        LocalDateTime afterClockOut = LocalDateTime.now();
        assertThat(record.getClockOutTime()).isBetween(beforeClockOut, afterClockOut);

    }

    @Property
    // Feature: attendance-management, Property 4: 出勤記録なしでの退勤拒否
    public void 出勤記録なしでの退勤拒否(@ForAll @AlphaChars @StringLength(min = 1, max = 10) String employeeId) {
        // Given: リポジトリのクリア
        reset(repository);
        AttendanceService service = new AttendanceServiceImpl(repository);

        // When&Then: 出勤記録がない場合は例外をスロー
        assertThatThrownBy(() -> service.clockOut(employeeId))
                .isInstanceOf(NoClockInRecordException.class)
                .hasMessageContaining("本日の出勤記録が作成されていません");
    }

    @Property
    // Feature: attendance-management, Property 5: 重複退勤の防止
    public void 重複退勤の防止(@ForAll @AlphaChars @StringLength(min = 1, max = 10) String employeeId) {
        // Given: 出勤記録の作成
        reset(repository);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        AttendanceService service = new AttendanceServiceImpl(repository);
        AttendanceRecord clockInRecord = service.clockIn(employeeId);

        // Given: 退勤記録の作成
        when(repository.findByEmployeeIdAndWorkDate(employeeId, LocalDate.now()))
                .thenReturn(Optional.of(clockInRecord));
        AttendanceRecord clockOutRecord = service.clockOut(employeeId);

        // When&Then: 退勤記録が重複している場合、例外をスロー
        when(repository.findByEmployeeIdAndWorkDate(employeeId, LocalDate.now()))
                .thenReturn(Optional.of(clockOutRecord));
        assertThatThrownBy(() -> service.clockOut(employeeId))
                .isInstanceOf(DuplicateClockOutException.class)
                .hasMessageContaining("本日既に退勤記録があります");
    }

    @Property
    // Feature: attendance-management, Property 6: 勤怠履歴の日付降順ソート
    public void 勤怠履歴の日付降順ソート(@ForAll @AlphaChars @StringLength(min = 1, max = 10) String employeeId) {
        // Given: ソート済みのレコードを作成
        reset(repository);
        List<AttendanceRecord> sortedRecords = createSortedRecords(employeeId, 3);
        when(repository.findByEmployeeIdOrderByWorkDateDesc(employeeId))
                .thenReturn(sortedRecords);
        AttendanceService service = new AttendanceServiceImpl(repository);

        // When: 履歴取得
        List<AttendanceRecord> result = service.getAttendanceHistory(employeeId);

        // Then: 日付が降順であることを検証
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).getWorkDate()).isAfterOrEqualTo(result.get(i + 1).getWorkDate());
        }

        // Then: 全レコードが指定した従業員のものであること
        assertThat(result).allMatch(r -> r.getEmployeeId().equals(employeeId));
    }

    private List<AttendanceRecord> createSortedRecords(String employeeId, int count) {
        List<AttendanceRecord> records = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AttendanceRecord record = new AttendanceRecord();
            record.setEmployeeId(employeeId);
            record.setWorkDate(LocalDate.now().minusDays(i));
            record.setClockInTime(LocalDate.now().minusDays(i).atTime(9, 0));
            records.add(record);
        }
        return records;
    }
}
