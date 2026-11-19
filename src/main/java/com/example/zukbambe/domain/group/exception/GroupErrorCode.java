package com.example.zukbambe.domain.group.exception;

import com.example.zukbambe.global.error.exception.ErrorProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GroupErrorCode implements ErrorProperty {
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "그룹을 찾을 수 없습니다."),
    NOT_GROUP_MEMBER(HttpStatus.FORBIDDEN, "G002", "그룹 멤버가 아닙니다."),
    NOT_GROUP_OWNER(HttpStatus.FORBIDDEN, "G003", "그룹장만 할 수 있습니다."),
    GROUP_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "G004", "그룹 멤버를 찾을 수 없습니다."),
    GROUP_OWNER_CANNOT_LEAVE(HttpStatus.BAD_REQUEST, "G005", "그룹장은 탈퇴하거나 내보낼 수 없습니다. 그룹장을 위임한 뒤 탈퇴해주세요."),
    ALREADY_GROUP_MEMBER(HttpStatus.CONFLICT, "G006", "이미 그룹에 가입한 사용자입니다."),
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "G007", "그룹 초대를 찾을 수 없습니다."),
    INVITATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "G008", "이미 대기 중인 그룹 초대가 있습니다."),
    INVITATION_EXPIRED(HttpStatus.GONE, "G009", "만료된 그룹 초대입니다."),
    SELF_INVITATION(HttpStatus.BAD_REQUEST, "G010", "자기 자신을 초대할 수 없습니다."),
    SELF_OWNER_TRANSFER(HttpStatus.BAD_REQUEST, "G011", "자기 자신에게 그룹장을 위임할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
