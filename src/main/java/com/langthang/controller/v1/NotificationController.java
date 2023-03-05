package com.langthang.controller.v1;

import com.langthang.services.INotificationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class NotificationController {

    private final INotificationServices notificationServices;

    @Autowired
    public NotificationController(INotificationServices notificationServices) {
        this.notificationServices = notificationServices;
    }

    @PutMapping("/notifications/seenAll")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> markAllAsSeen(Authentication authentication) {

        String currentEmail = authentication.getName();

        notificationServices.maskAllAsSeen(currentEmail);

        return ResponseEntity.noContent().build();
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