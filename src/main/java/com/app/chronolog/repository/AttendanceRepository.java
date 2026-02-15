package com.app.chronolog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.chronolog.entity.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    public List<AttendanceRecord> findByEmployeeIdOrderByWorkDateDesc(String employeeId);

    public Optional<AttendanceRecord> findByEmployeeIdAndWorkDate(String employeeId, LocalDate workDate);

    public boolean existsByEmployeeIdAndWorkDate(String employeeId, LocalDate workDate);

}
