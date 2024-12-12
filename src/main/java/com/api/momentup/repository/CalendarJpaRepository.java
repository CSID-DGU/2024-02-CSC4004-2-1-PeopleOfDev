package com.api.momentup.repository;

import com.api.momentup.domain.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CalendarJpaRepository extends JpaRepository<Calendar, Long> {
    @Query("SELECT c FROM Calendar c " +
            "WHERE c.user.userNumber = :userNumber " +
            "AND FUNCTION('YEAR', c.date) = :year " +
            "AND FUNCTION('MONTH', c.date) = :month")
    List<Calendar> findByUserAndYearAndMonth(@Param("userNumber") Long userNumber,
                                             @Param("year") int year,
                                             @Param("month") int month);
}
