package com.api.momentup.service;

import com.api.momentup.dto.ResultResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// 서비스 인터페이스
public interface YoloDetectionService {
    ResultResponseDto uploadAndProcessImages(
            MultipartFile frontImage, MultipartFile backImage, MultipartFile leftImage,
            MultipartFile rightImage, MultipartFile upImage) throws IOException;
}
