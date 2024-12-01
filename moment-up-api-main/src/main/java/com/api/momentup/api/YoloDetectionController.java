package com.api.momentup.api;

import com.api.momentup.dto.ResultResponseDto;
import com.api.momentup.service.YoloDetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/yolo")
public class YoloDetectionController {

    private final YoloDetectionService service;

    public YoloDetectionController(YoloDetectionService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResultResponseDto> uploadImages(
            @RequestParam("frontImage") MultipartFile frontImage,
            @RequestParam("backImage") MultipartFile backImage,
            @RequestParam("leftImage") MultipartFile leftImage,
            @RequestParam("rightImage") MultipartFile rightImage,
            @RequestParam("upImage") MultipartFile upImage) throws IOException {

        ResultResponseDto response = service.uploadAndProcessImages(frontImage, backImage, leftImage, rightImage, upImage);
        return ResponseEntity.ok(response);
    }
}
