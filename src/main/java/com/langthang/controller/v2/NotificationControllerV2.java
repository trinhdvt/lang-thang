package com.langthang.controller.v2;

import com.langthang.model.dto.v2.response.NotificationDtoV2;
import com.langthang.model.entity.Notification_;
import com.langthang.security.services.CurrentUser;
import com.langthang.services.INotificationServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/notifications")
@RestController
@Validated
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class NotificationControllerV2 {

    private final INotificationServices notificationServices;

    enum NotificationFilter {
        SEEN, UNSEEN
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<NotificationDtoV2> getAllNotification(
            @PageableDefault(
                    sort = {Notification_.NOTIFY_DATE},
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            @RequestParam(value = "filter", required = false) NotificationFilter filter,
            @AuthenticationPrincipal CurrentUser currentUser) {

        var userId = currentUser.getUserId();

        if (filter == null) return notificationServices.getAll(userId, pageable);

        return notificationServices.getAll(userId, filter.equals(NotificationFilter.SEEN), pageable);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearAllNotifications(@AuthenticationPrincipal CurrentUser currentUser) {
        notificationServices.clearNotifications(currentUser.getUserId());
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{notification_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void maskAsSeen(@PathVariable("notification_id") int notificationId,
                           @AuthenticationPrincipal CurrentUser currentUser) {
        notificationServices.clearNotifications(currentUser.getUserId(), notificationId);
    }
}