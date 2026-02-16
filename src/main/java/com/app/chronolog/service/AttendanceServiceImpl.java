package com.app.chronolog.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.app.chronolog.entity.AttendanceRecord;
import com.app.chronolog.exception.DuplicateClockInException;
import com.app.chronolog.exception.DuplicateClockOutException;
import com.app.chronolog.exception.NoClockInRecordException;
import com.app.chronolog.repository.AttendanceRepository;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private AttendanceRepository attendanceRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public AttendanceRecord clockIn(String employeeId) {
        validationEmployeeId(employeeId);
        // 重複チェック
        LocalDate today = LocalDate.now();
        if (attendanceRepository.existsByEmployeeIdAndWorkDate(employeeId, today)) {
            throw new DuplicateClockInException("従業員ID: " + employeeId + "は本日既に出勤記録があります");
        }

        // 出勤記録を作成
        AttendanceRecord input = new AttendanceRecord();
        input.setEmployeeId(employeeId);
        input.setWorkDate(today);
        input.setClockInTime(LocalDateTime.now());

        return attendanceRepository.save(input);
    }

    @Override
    public AttendanceRecord clockOut(String employeeId) {
        validationEmployeeId(employeeId);
        // 出勤記録チェック
        LocalDate today = LocalDate.now();
        Optional<AttendanceRecord> clockInInfo = attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, today);
        if (clockInInfo.isEmpty()) {
            throw new NoClockInRecordException("従業員ID: " + employeeId + "は本日の出勤記録が作成されていません");
        }

        // 退勤記録の重複チェック
        AttendanceRecord createdClockInfo = clockInInfo.get();
        if (createdClockInfo.getClockOutTime() != null) {
            throw new DuplicateClockOutException("従業員ID: " + employeeId + "は本日既に退勤記録があります");
        }

        createdClockInfo.setClockOutTime(LocalDateTime.now());

        return attendanceRepository.save(createdClockInfo);
    }

    @Override
    public List<AttendanceRecord> getAttendanceHistory(String employeeId) {
        validationEmployeeId(employeeId);
        // 勤怠履歴の取得
        return attendanceRepository.findByEmployeeIdOrderByWorkDateDesc(employeeId);
    }

    @Override
    public Duration calculateWorkingHours(AttendanceRecord record) {
        // 勤怠時間の計算
        LocalDateTime clockInTime = record.getClockInTime();
        LocalDateTime clockOutTime = record.getClockOutTime();

        if (clockInTime == null || clockOutTime == null) {
            throw new IllegalArgumentException("出勤または退勤どちらかの情報が記録されていません");
        }

        return Duration.between(clockInTime, clockOutTime);
    }

    private boolean validationEmployeeId(String employeeId) {
        if (StringUtils.isBlank(employeeId)) {
            throw new IllegalArgumentException("従業員IDが設定されていません");
        }

        if (employeeId.length() > 50) {
            throw new IllegalArgumentException("従業員IDは50文字以内で設定してください");
        }

        return true;
    }
}
