package com.team.docrate.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyRequestListItemDto {

    private Long requestId;
    private String requestType;
    private String targetName;
    private String status;
    private String createdAt;
}
