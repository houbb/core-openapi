package io.coreplatform.openapi.api.request;

import lombok.Data;

import java.util.List;

@Data
public class TagMappingRequest {
    private List<Long> tagIds;
}
