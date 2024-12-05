package com.api.momentup.repository;

import com.api.momentup.domain.Alarm;
import com.api.momentup.domain.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmJpsRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByUsers(Users users);
}
