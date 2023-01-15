package com.langthang.repository;

import com.langthang.model.constraints.NotificationType;
import com.langthang.model.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    NotificationTemplate findByType(NotificationType type);
}
