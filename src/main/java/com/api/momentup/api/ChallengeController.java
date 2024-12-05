package com.api.momentup.api;

import com.api.momentup.domain.Challenge;
import com.api.momentup.domain.ChallengeDetail;
import com.api.momentup.domain.ChallengePicture;
import com.api.momentup.domain.Groups;
import com.api.momentup.dto.ApiResult;
import com.api.momentup.dto.ResultType;
import com.api.momentup.dto.challenge.request.CreateChallengeRequest;
import com.api.momentup.dto.challenge.response.ChallengeDetailDto;
import com.api.momentup.dto.challenge.response.ChallengesDto;
import com.api.momentup.exception.ChallengeNotFoundException;
import com.api.momentup.exception.GroupNotFoundException;
import com.api.momentup.exception.GroupNotJoinException;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/challenge")
@RequiredArgsConstructor
public class ChallengeController {
    private final ChallengeService challengeService;

    @PostMapping("")
    public ApiResult createChallenge(@RequestPart("challenge") CreateChallengeRequest request, @RequestPart(value = "picture", required = false) MultipartFile challengePicture) {
        try {
            Long challenge = challengeService.createChallenge(request.getChallengeName(), request.getUserNumber(),
                    request.getGroupNumber(), request.getChallengeType(), challengePicture);

            return ApiResult.success(challenge);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (GroupNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (IOException e) {
            return ApiResult.error(ResultType.FAIL.getCode(), ResultType.FAIL.getMessage());
        } catch (GroupNotJoinException e) {
            return ApiResult.error(ResultType.FAIL.getCode(),e.getMessage());
        }
    }

    @DeleteMapping("/{challengeNumber}")
    public ApiResult removeChallenge(@PathVariable Long challengeNumber) {
        try {
            challengeService.removeChallenge(challengeNumber);

            return ApiResult.success(null);
        }  catch (ChallengeNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @GetMapping("/{userNumber}")
    public ApiResult getChallenges(@PathVariable Long userNumber) {
        try {
            List<Challenge> challenges = challengeService.getChallenges(userNumber);

            List<ChallengesDto> result = challenges.stream()
                    .map(c -> new ChallengesDto(
                           c.getChallengeNumber(),
                           c.getGroups().getGroupName(),
                            Optional.ofNullable(c.getChallengePicture()) // UserProfile 객체가 null 가능성을 Optional로 감싸기
                                    .map(ChallengePicture::getPicturePath) // UserProfile이 null이 아니면 getPicturePath 호출
                                    .orElse("")
                    )).toList();

            return ApiResult.success(result);
        }  catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @GetMapping("/detail/{challengeNumber}")
    public ApiResult getChallengeDetail(@PathVariable Long challengeNumber) {
        try {
            ChallengeDetailDto challengeDetail = challengeService.getChallengeDetail(challengeNumber);

            return ApiResult.success(challengeDetail);
        } catch (ChallengeNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }
}
