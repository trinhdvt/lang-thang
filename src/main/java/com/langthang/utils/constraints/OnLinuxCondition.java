package com.langthang.utils.constraints;

import lombok.NonNull;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnLinuxCondition implements Condition {
    @Override
    public boolean matches(@NonNull ConditionContext context,
                           @NonNull AnnotatedTypeMetadata metadata) {
        return SystemUtils.IS_OS_LINUX;
    }
}
