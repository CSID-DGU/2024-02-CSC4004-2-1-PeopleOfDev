package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long searchHistoryNumber;

    @ManyToOne
    @JoinColumn(name = "user_number", nullable = false)
    private Users user;

    private String keyword;
    private LocalDateTime searchDate;

    public static SearchHistory createSearchHistory(Users user, String keyword) {
        SearchHistory history = new SearchHistory();
        history.user = user;
        history.keyword = keyword;
        history.searchDate = LocalDateTime.now();

        return history;
    }
}
