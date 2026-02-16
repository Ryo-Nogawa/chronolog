package com.app.chronolog.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.app.chronolog.entity.AttendanceRecord;
import com.app.chronolog.exception.DuplicateClockInException;
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
        // 出勤記録チェック
        LocalDate today = LocalDate.now();
        Optional<AttendanceRecord> clockInInfo = attendanceRepository.findByEmployeeIdAndWorkDate(employeeId, today);
        if (clockInInfo.isEmpty()) {
            throw new NoClockInRecordException("従業員ID: " + employeeId + "は本日の出勤記録が作成されていません");
        }

        // 退勤記録を作成
        AttendanceRecord createdClockInfo = clockInInfo.get();
        createdClockInfo.setClockOutTime(LocalDateTime.now());

        return attendanceRepository.save(createdClockInfo);
    }
}
