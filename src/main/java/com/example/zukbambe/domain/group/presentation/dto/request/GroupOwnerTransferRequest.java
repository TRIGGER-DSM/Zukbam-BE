package com.example.zukbambe.domain.group.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record GroupOwnerTransferRequest(
    @NotNull(message = "새 그룹장 ID는 필수입니다.")
    Long newOwnerId
) {
}
