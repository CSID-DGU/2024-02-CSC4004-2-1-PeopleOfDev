package com.api.momentup.repository;

import com.api.momentup.domain.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class UserNotificationRepositoryCustomImpl implements UserNotificationRepositoryCustom  {
    private final JPAQueryFactory queryFactory;

    public UserNotificationRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<UserNotification> findLatestNotificationByUserAndGroup(
            Users user, Groups group, NotificationType notificationType) {
        QUserNotification userNotification = QUserNotification.userNotification;

        // 동적 조건 설정
        BooleanExpression groupCondition = group != null
                ? userNotification.group.eq(group)
                : userNotification.group.isNull();

        // 쿼리 실행
        UserNotification latestNotification = queryFactory.selectFrom(userNotification)
                .where(userNotification.user.eq(user)
                        .and(userNotification.notificationType.eq(notificationType))
                        .and(groupCondition))
                .orderBy(userNotification.notificationTime.desc())
                .fetchFirst(); // 가장 최신 알림 가져오기

        return Optional.ofNullable(latestNotification);
    }
}
