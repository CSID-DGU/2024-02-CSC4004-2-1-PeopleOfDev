package com.api.momentup.repository;

import com.api.momentup.domain.Groups;
import com.api.momentup.domain.NotificationType;
import com.api.momentup.domain.UserNotification;
import com.api.momentup.domain.Users;

import java.util.Optional;

public interface UserNotificationRepositoryCustom {
    Optional<UserNotification> findLatestNotificationByUserAndGroup(Users user, Groups group, NotificationType notificationType);
}
