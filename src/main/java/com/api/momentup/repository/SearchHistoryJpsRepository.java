package com.api.momentup.repository;

import com.api.momentup.domain.SearchHistory;
import com.api.momentup.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchHistoryJpsRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByUserOrderBySearchDateDesc(Users user);

    @Query("SELECT sh FROM SearchHistory sh " +
            "WHERE sh.user = :user " +
            "AND sh.searchDate = (SELECT MAX(innerSh.searchDate) FROM SearchHistory innerSh " +
            "                    WHERE innerSh.user = sh.user AND innerSh.keyword = sh.keyword) " +
            "GROUP BY sh.keyword " +
            "ORDER BY MAX(sh.searchDate) DESC")
    List<SearchHistory> findDistinctSearchHistoryByUser(@Param("user") Users user);
}
