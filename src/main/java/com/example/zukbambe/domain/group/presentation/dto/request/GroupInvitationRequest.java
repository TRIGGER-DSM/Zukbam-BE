package com.example.zukbambe.domain.group.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record GroupInvitationRequest(
    @NotNull(message = "초대할 유저 ID는 필수입니다.")
    Long inviteeId
) {
}
