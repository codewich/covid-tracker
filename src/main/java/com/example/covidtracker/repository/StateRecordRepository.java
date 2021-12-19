package com.example.covidtracker.repository;

import com.example.covidtracker.models.StateRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StateRecordRepository extends JpaRepository<StateRecord, Long> {

    List<StateRecord> findByDateAndState(LocalDate date, String state);

    List<StateRecord> findByDate(LocalDate date);

    List<StateRecord> findByState(String state);


}
