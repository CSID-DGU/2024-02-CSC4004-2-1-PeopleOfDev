package com.api.momentup.service;

import com.api.momentup.repository.GroupJoinRequestJpaRepository;
import com.api.momentup.repository.GroupJpaRepository;
import com.api.momentup.repository.PostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostJpaRepository postJpaRepository;

    public void writePost(String content, MultipartFile file) {

    }

    public void writePost(String content) {

    }
}
