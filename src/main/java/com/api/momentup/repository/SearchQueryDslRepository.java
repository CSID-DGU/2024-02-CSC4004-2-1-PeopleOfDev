package com.api.momentup.repository;

import com.api.momentup.domain.*;
import com.api.momentup.dto.search.response.GroupSearchResultDto;
import com.api.momentup.dto.search.response.UserSearchResultDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    public SearchQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    public List<UserSearchResultDto> searchUsersByNumberWithFollow(Long currentUserNumber, String keyword) {
        QUsers targetUser = QUsers.users;
        QFollow follow = QFollow.follow;

        // 현재 유저 엔티티 가져오기
        Users currentUser = queryFactory.selectFrom(QUsers.users)
                .where(QUsers.users.userNumber.eq(currentUserNumber))
                .fetchOne();

        return queryFactory.select(
                        Projections.constructor(
                                UserSearchResultDto.class,
                                targetUser.userNumber,
                                targetUser.userId,
                                follow.isNotNull() // 팔로우 여부 확인
                        )
                )
                .from(targetUser)
                .leftJoin(follow)
                .on(follow.follower.eq(currentUser).and(follow.following.eq(targetUser)))
                .where(targetUser.userId.containsIgnoreCase(keyword)) // 키워드 검색
                .fetch();
    }

    public List<GroupSearchResultDto> searchGroupsByNameWithMemberCount(String groupName) {
        QGroups group = QGroups.groups;
        QUserGroups userGroup = QUserGroups.userGroups;

        return queryFactory.select(
                        Projections.constructor(
                                GroupSearchResultDto.class,
                                group.groupNumber,
                                group.groupName,
                                userGroup.count() // 멤버 수
                        )
                )
                .from(group)
                .leftJoin(userGroup).on(userGroup.groups.eq(group))
                .where(group.groupName.containsIgnoreCase(groupName))
                .groupBy(group.groupNumber)
                .fetch();
    }

}
