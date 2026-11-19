package com.example.zukbambe.domain.friend.exception;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FriendErrorCode implements ErrorProperty {
    FRIENDSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "F001", "친구 관계를 찾을 수 없습니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "F002", "친구 요청을 찾을 수 없습니다."),
    SELF_FRIEND_REQUEST(HttpStatus.BAD_REQUEST, "F003", "자기 자신에게 친구 요청을 보낼 수 없습니다."),
    ALREADY_FRIENDS(HttpStatus.CONFLICT, "F004", "이미 친구인 사용자입니다."),
    FRIEND_REQUEST_ALREADY_EXISTS(HttpStatus.CONFLICT, "F005", "이미 대기 중인 친구 요청이 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
