package com.api.momentup.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResultResponseDto {

    private int count; // FastAPI가 반환하는 탐지된 총 조합 수
    private List<ResultDto> results; // 탐지 결과 리스트

    @Getter
    @Setter
    public static class ResultDto {
        private String name;       // 탐지된 객체 이름
        private String accuracy;   // 탐지 정확도
        private String rate;       // 평가 등급
        private String resultImageUrl;   // YOLO 탐지 결과 이미지 URL
    }
}