package com.api.momentup.repository;

import com.api.momentup.domain.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationJpaRepository extends JpaRepository<UserNotification, Long>, UserNotificationRepositoryCustom  {
}
