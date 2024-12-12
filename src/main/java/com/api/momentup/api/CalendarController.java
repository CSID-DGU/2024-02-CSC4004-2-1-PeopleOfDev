package com.api.momentup.api;

import com.api.momentup.domain.Calendar;
import com.api.momentup.domain.Groups;
import com.api.momentup.domain.Post;
import com.api.momentup.dto.ApiResult;
import com.api.momentup.dto.ResultType;
import com.api.momentup.dto.post.request.GetDatePostRequest;
import com.api.momentup.dto.post.response.CalendarPostsDto;
import com.api.momentup.dto.user.request.GetMonthCalendarsRequest;
import com.api.momentup.dto.user.response.MonthCalendarDto;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.service.CalendarService;
import com.api.momentup.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;
    private final PostService postService;

    @GetMapping("/{userNumber}")
    public ApiResult getCalendars(@PathVariable Long userNumber, @RequestBody GetMonthCalendarsRequest request) {
        try {
            List<MonthCalendarDto> calendarsByUserAndMonth = calendarService.getCalendarsByUserAndMonth(userNumber, request.getYear(), request.getMonth());


            return ApiResult.success(calendarsByUserAndMonth);
        }  catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @GetMapping("/posts/{userNumber}")
    public ApiResult getDatePosts(@PathVariable Long userNumber, @RequestBody GetDatePostRequest request) {
        try {

            System.out.println("userNumber : "+userNumber + " request : "+ request.getDate());
            List<Post> datePosts = postService.getDatePosts(userNumber, request.getDate());

            System.out.println("post size : "+datePosts.size());

            List<CalendarPostsDto> result = datePosts.stream().map(
                    p -> new CalendarPostsDto(
                            p.getPostNumber(),
                            Optional.ofNullable(p.getGroup()) // UserProfile 객체가 null 가능성을 Optional로 감싸기
                                    .map(Groups::getGroupName) // UserProfile이 null이 아니면 getPicturePath 호출
                                    .orElse("전체공개"),
                            p.getPostPicture().getPicturePath()
                    )).toList();

            return ApiResult.success(result);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }

    }
}
