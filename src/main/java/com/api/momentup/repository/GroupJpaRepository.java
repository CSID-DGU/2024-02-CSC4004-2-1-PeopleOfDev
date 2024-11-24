package com.api.momentup.repository;

import com.api.momentup.domain.Groups;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupJpaRepository  extends JpaRepository<Groups, Long> {
}
