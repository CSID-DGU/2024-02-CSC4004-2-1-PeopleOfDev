package com.api.momentup.api;

import com.api.momentup.domain.SearchHistory;
import com.api.momentup.dto.ApiResult;
import com.api.momentup.dto.ResultType;
import com.api.momentup.dto.search.request.SearchRequest;
import com.api.momentup.dto.search.response.SearchHistoryDto;
import com.api.momentup.exception.SearchHistoryNotFoundException;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @PostMapping("")
    public ApiResult getSearchResult(@RequestBody SearchRequest request) {
        try {
            Map<String, Object> result = searchService.searchUsersAndGroups(request.getOwnUserNumber(), request.getKeyword());
            return ApiResult.success(result);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }

    }

    @GetMapping("/history/{userNumber}")
    public ApiResult getSearchHistory(@PathVariable Long userNumber) {
        try {
            List<SearchHistory> findHistory = searchService.getRecentSearchHistory(userNumber);

            List<SearchHistoryDto> collect = findHistory.stream().map(h ->
                    new SearchHistoryDto(h.getSearchHistoryNumber(), h.getKeyword()))
                    .toList();

            return ApiResult.success(collect);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @DeleteMapping("/history/{searchHistoryNumber}")
    public ApiResult removeSearchHistory(@PathVariable Long searchHistoryNumber) {
        try {
            searchService.deleteSearchHistory(searchHistoryNumber);

            return ApiResult.success(null);
        }  catch (SearchHistoryNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }
}
