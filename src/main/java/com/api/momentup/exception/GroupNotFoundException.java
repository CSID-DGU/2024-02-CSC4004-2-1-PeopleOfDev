package com.api.momentup.exception;

public class GroupNotFoundException extends Exception{
    public GroupNotFoundException() {
        super("해당 하는 그룹이 존재하지 않습니다.");  // 부모 클래스의 생성자 호출하여 메시지 전달
    }
}
