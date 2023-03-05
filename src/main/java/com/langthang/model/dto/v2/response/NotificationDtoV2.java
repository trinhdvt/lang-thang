package com.langthang.model.dto.v2.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.langthang.model.constraints.NotificationType;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDtoV2 implements Serializable {
    private Integer id;
    private String content;
    private Instant createdDate;
    private boolean seen;
    private NotificationType type;
    private UserDtoV2 sourceUser;
    @JsonIgnoreProperties({"content"})
    private PostDtoV2 targetPost;
}
