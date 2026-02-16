package com.app.chronolog.service;

import java.time.Duration;
import java.util.List;

import com.app.chronolog.entity.AttendanceRecord;
import com.app.chronolog.exception.DuplicateClockInException;
import com.app.chronolog.exception.DuplicateClockOutException;
import com.app.chronolog.exception.NoClockInRecordException;

public interface AttendanceService {

    // 出勤記録を作成
    public AttendanceRecord clockIn(String employeeId) throws DuplicateClockInException;

    // 退勤記録を作成
    public AttendanceRecord clockOut(String employeeId) throws NoClockInRecordException, DuplicateClockOutException;

    // 従業員の勤怠履歴を取得
    public List<AttendanceRecord> getAttendanceHistory(String employeeId);

    // 勤怠時間の計算
    public Duration calculateWorkingHours(AttendanceRecord record);
}
