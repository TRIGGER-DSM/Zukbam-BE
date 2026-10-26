package com.example.zukbambe.domain.group.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record GroupOwnerTransferRequest(
    @NotNull
    Long newOwnerId
) { }
