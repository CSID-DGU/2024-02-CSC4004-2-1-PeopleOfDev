package com.api.momentup.service;

import com.api.momentup.domain.SearchHistory;
import com.api.momentup.domain.Users;
import com.api.momentup.dto.search.response.GroupSearchResultDto;
import com.api.momentup.dto.search.response.UserSearchResultDto;
import com.api.momentup.exception.SearchHistoryNotFoundException;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.repository.SearchHistoryJpsRepository;
import com.api.momentup.repository.SearchQueryDslRepository;
import com.api.momentup.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchService {
    private final SearchHistoryJpsRepository searchHistoryJpsRepository;
    private final SearchQueryDslRepository searchQueryDslRepository;
    private final UserJpaRepository userJpaRepository;

    @Transactional
    public Map<String, Object> searchUsersAndGroups(Long ownUserNumber, String keyword) throws UserNotFoundException {
        saveSearchHistory(ownUserNumber, keyword);
        // 유저 검색
        List<UserSearchResultDto> users = searchQueryDslRepository.searchUsersByNumberWithFollow(ownUserNumber, keyword);

        // 그룹 검색
        List<GroupSearchResultDto> groups = searchQueryDslRepository.searchGroupsByNameWithMemberCount(keyword);

        // 결과 조합
        Map<String, Object> result = new HashMap<>();
        result.put("users", users);
        result.put("groups", groups);

        return result;
    }

    public List<SearchHistory> getRecentSearchHistory(Long currentUserNumber) throws UserNotFoundException {
        Users currentUser = userJpaRepository.findById(currentUserNumber)
                .orElseThrow(UserNotFoundException::new);

        return searchHistoryJpsRepository.findDistinctSearchHistoryByUser(currentUser);
    }

    private void saveSearchHistory(Long ownUserNumber, String keyword) throws UserNotFoundException {
        Users currentUser = userJpaRepository.findById(ownUserNumber)
                .orElseThrow(UserNotFoundException::new);

        SearchHistory history = SearchHistory.createSearchHistory(
                currentUser,
                keyword
        );

        searchHistoryJpsRepository.save(history);
    }

    @Transactional
    public void deleteSearchHistory(Long searchHistoryNumber) throws SearchHistoryNotFoundException {
        SearchHistory findSearchHistory = searchHistoryJpsRepository.findById(searchHistoryNumber)
                .orElseThrow(SearchHistoryNotFoundException::new);

        searchHistoryJpsRepository.delete(findSearchHistory);
    }

}
