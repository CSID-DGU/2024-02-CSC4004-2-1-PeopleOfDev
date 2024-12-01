package com.api.momentup.service;

import com.api.momentup.domain.YoloDetection;
import com.api.momentup.dto.ResultResponseDto;
import com.api.momentup.repository.YoloDetectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 서비스 구현
@Slf4j
@Service
public class YoloDetectionServiceImpl implements YoloDetectionService {

    private final YoloDetectionRepository repository;
    private final RestTemplate restTemplate;

    public YoloDetectionServiceImpl(YoloDetectionRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    @Override
    public ResultResponseDto uploadAndProcessImages(
            MultipartFile frontImage, MultipartFile backImage, MultipartFile leftImage,
            MultipartFile rightImage, MultipartFile upImage) throws IOException {

        // 1. 로컬 디렉토리에 이미지 저장
        MultipartFile[] files = {frontImage, backImage, leftImage, rightImage, upImage};
        List<String> localImagePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            String localPath = saveImageToLocal(file);
            localImagePaths.add(localPath);
        }

        // 2. FastAPI에 이미지 경로 전송
        String fastApiUrl = "http://127.0.0.1:8000/predict_images"; // 임의로 설정
        ResultResponseDto response = restTemplate.postForObject(fastApiUrl, localImagePaths, ResultResponseDto.class);

        // 3. 결과 저장
        if (response != null) {
            for (ResultResponseDto.ResultDto dto : response.getResults()) {
                YoloDetection detection = new YoloDetection();
                detection.setName(dto.getName());
                detection.setAccuracy(dto.getAccuracy());
                detection.setRate(dto.getRate());
                detection.setResultImageUrl(dto.getResultImageUrl());
                repository.save(detection);
            }
        }

        return response;
    }

    private String saveImageToLocal(MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = uploadDir + file.getOriginalFilename();
        File localFile = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            fos.write(file.getBytes());
        }
        return localFile.getAbsolutePath();
    }
}
