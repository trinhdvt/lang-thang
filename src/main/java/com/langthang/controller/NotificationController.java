package com.langthang.controller;

import com.langthang.dto.NotificationDTO;
import com.langthang.services.INotificationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class NotificationController {

    private final INotificationServices notificationServices;

    @Autowired
    public NotificationController(INotificationServices notificationServices) {
        this.notificationServices = notificationServices;
    }

    @GetMapping("/notifications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getAllNotifications(
            @PageableDefault(sort = {"seen", "notifyDate"}) Pageable pageable,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        List<NotificationDTO> listNotifications = notificationServices.getNotifications(currentEmail, pageable);

        return ResponseEntity.ok(listNotifications);
    }

    @GetMapping(value = "/notifications/unseen")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getUnseenNotifications(
            @PageableDefault Pageable pageable,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        List<NotificationDTO> unseenNotifications = notificationServices.getUnseenNotifications(currentEmail, pageable);

        return ResponseEntity.ok(unseenNotifications);
    }

    @PutMapping("/notifications/{notification_id}/seen")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> maskAsSeen(
            @PathVariable("notification_id") int notificationId,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        notificationServices.maskAsSeen(notificationId, currentEmail);

        return ResponseEntity.noContent().build();
    }
}
