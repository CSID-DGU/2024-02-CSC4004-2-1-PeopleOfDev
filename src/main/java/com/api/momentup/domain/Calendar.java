package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long calendarNumber;

    @ManyToOne
    @JoinColumn(name = "user_number", nullable = false)
    private Users user;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private CalendarResultType result; // True for success, False for failure

    public void setUser(Users user) {
        this.user = user;
    }

    public static Calendar createCalendar(CalendarResultType result) {
        Calendar calendar = new Calendar();
        calendar.date = LocalDate.now();
        calendar.result = result;

        return calendar;
    }
}
