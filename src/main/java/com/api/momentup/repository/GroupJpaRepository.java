package com.api.momentup.repository;

import com.api.momentup.domain.Groups;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupJpaRepository  extends JpaRepository<Groups, Long> {
    Optional<Groups> findByGroupInviteCode(String inviteCode);

    // 특정 그룹의 생성자인지 확인
}
