package com.app.chronolog.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.app.chronolog.entity.AttendanceRecord;
import com.app.chronolog.exception.DuplicateClockInException;
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
}
