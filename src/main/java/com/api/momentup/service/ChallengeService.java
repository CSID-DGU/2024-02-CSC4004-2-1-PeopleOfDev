package com.api.momentup.service;


import com.api.momentup.domain.*;
import com.api.momentup.dto.challenge.response.ChallengeDetailCalendars;
import com.api.momentup.dto.challenge.response.ChallengeDetailDto;
import com.api.momentup.exception.ChallengeNotFoundException;
import com.api.momentup.exception.GroupNotFoundException;
import com.api.momentup.exception.GroupNotJoinException;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.repository.ChallengeJpaRepository;
import com.api.momentup.repository.GroupJpaRepository;
import com.api.momentup.repository.UserGroupJpaRepository;
import com.api.momentup.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {
    private final ChallengeJpaRepository challengeJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final UserGroupJpaRepository userGroupJpaRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public Long createChallenge(String challengeName, Long userNumber, Long groupNumber, ChallengeType challengeType, MultipartFile file) throws UserNotFoundException, GroupNotFoundException, IOException, GroupNotJoinException {
        Users findUser = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        Groups findGroups = null;

        if(groupNumber != null) {
            findGroups = groupJpaRepository.findById(groupNumber)
                    .orElseThrow(GroupNotFoundException::new);

            if(!Objects.equals(findGroups.getCreator().getUserNumber(), userNumber)) {
                UserGroups findUserGroups = userGroupJpaRepository.findByGroupsAndUsers(findGroups, findUser)
                        .orElseThrow(GroupNotJoinException::new);
            }
        }

        Challenge challenge = Challenge.createChallenge(challengeName, findUser, findGroups, challengeType);
        ChallengePicture challengePicture = null;

        if(file != null) {
            try {
                String usbDir = "challenge";

                String originalFilename = file.getOriginalFilename();
                String filename = UUID.randomUUID() + "_" + originalFilename;

                // 파일 저장 경로 설정
                Path filePath = Paths.get(uploadDir, usbDir ,filename);
                Files.createDirectories(filePath.getParent()); // 디렉토리 생성
                Files.copy(file.getInputStream(), filePath); // 파일 저장

                String saveFilePath = "/uploaded/photos/"+ usbDir+ "/" + filename;

                challengePicture = ChallengePicture.createChallengePicture(saveFilePath, originalFilename);
                challenge.setChallengePicture(challengePicture);
            }  catch (IOException e) {
                throw new IOException();
            }
        }
        challengeJpaRepository.save(challenge);

        return challenge.getChallengeNumber();
    }

    @Transactional
    public void removeChallenge(Long challengeNumber) throws ChallengeNotFoundException {
        Challenge findChallenge = challengeJpaRepository.findById(challengeNumber)
                .orElseThrow(ChallengeNotFoundException::new);

        challengeJpaRepository.delete(findChallenge);
    }

    public List<Challenge> getChallenges(Long userNumber) throws UserNotFoundException {
        Users findUser = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        List<Challenge> findChallenges = challengeJpaRepository.findByUsers(findUser);

        return findChallenges;
    }

    public ChallengeDetailDto getChallengeDetail(Long challengeNumber) throws ChallengeNotFoundException {
        Challenge findChallenge = challengeJpaRepository.findById(challengeNumber)
                .orElseThrow(ChallengeNotFoundException::new);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd");

        List<ChallengeDetailCalendars> calendars = findChallenge.getChallengeDetails().stream()
                .map(d -> new ChallengeDetailCalendars(
                        d.getChallengeDetailNumber(),
                        d.getDate().format(formatter),
                        d.getCalendarResult()
                )).toList();

        ChallengeDetailDto challengeDetailDto = new ChallengeDetailDto(
                findChallenge.getChallengeNumber(), findChallenge.getGroups().getGroupName(), findChallenge.getChallengeName(),
                findChallenge.getChallengeType(),
                Optional.ofNullable(findChallenge.getChallengePicture()) // UserProfile 객체가 null 가능성을 Optional로 감싸기
                        .map(ChallengePicture::getPicturePath) // UserProfile이 null이 아니면 getPicturePath 호출
                        .orElse(""),
                calendars

        );

        return challengeDetailDto;
    }
}
