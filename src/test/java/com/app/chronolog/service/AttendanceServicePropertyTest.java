package com.app.chronolog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.mockito.Mockito;

import com.app.chronolog.entity.AttendanceRecord;
import com.app.chronolog.exception.DuplicateClockInException;
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
}
