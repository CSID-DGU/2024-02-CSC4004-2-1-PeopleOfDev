package com.api.momentup.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class YoloDetection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DETECTION_NAME")
    private String name;

    private String accuracy;
    private String rate;

    @Column(name = "RESULT_IMAGE_URL")
    private String resultImageUrl; // YOLO 결과 이미지 URL

}
