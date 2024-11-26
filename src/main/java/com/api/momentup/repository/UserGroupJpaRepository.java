package com.api.momentup.repository;

import com.api.momentup.domain.Groups;
import com.api.momentup.domain.UserGroups;
import com.api.momentup.domain.Users;
import com.api.momentup.dto.group.response.HomeGroupsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserGroupJpaRepository extends JpaRepository<UserGroups, Long> {
    boolean existsByUsersAndGroups(Users users, Groups groups);

    // userNumber를 기준으로 그룹 리스트를 가져오는 쿼리 메서드
    @Query("SELECT ug.groups FROM UserGroups ug WHERE ug.users.userNumber = :userNumber")
    List<Groups> findGroupsByUserNumber(@Param("userNumber") Long userNumber);


    @Query("SELECT new com.api.momentup.dto.group.response.HomeGroupsDto( " +
            "ug.groups.groupNumber, " +
            "ug.groups.groupName, " +
            "MAX(p.writeDate)) " +
            "FROM UserGroups ug " +
            "LEFT JOIN Post p ON ug.groups.groupNumber = p.group.groupNumber " +
            "WHERE ug.users.userNumber = :userNumber " +
            "GROUP BY ug.groups.groupNumber, ug.groups.groupName")
    List<HomeGroupsDto> findGroupsWithLastPostTime(@Param("userNumber") Long userNumber);

    List<UserGroups> findByGroups_GroupNumber(Long groupNumber);
}
