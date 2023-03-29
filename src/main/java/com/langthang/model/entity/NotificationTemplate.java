package com.langthang.model.entity;

import com.langthang.model.constraints.NotificationType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "notification_template")
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "type", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    @Type(PostgreSQLEnumType.class)
    private NotificationType type;

    @Column(name = "template", nullable = false)
    private String template;
}
