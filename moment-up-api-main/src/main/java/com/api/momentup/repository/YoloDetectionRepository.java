package com.api.momentup.repository;

import com.api.momentup.domain.YoloDetection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YoloDetectionRepository extends JpaRepository<YoloDetection, Long> {
    //
}