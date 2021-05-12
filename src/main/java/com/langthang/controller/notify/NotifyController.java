package com.langthang.controller.notify;

import com.langthang.dto.NotifyDTO;
import com.langthang.event.OnNewNotifyEvent;
import com.langthang.model.entity.Notify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/notify")
@Validated
public class NotifyController {


    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping
    public ResponseEntity<Object> addNotification(
            @RequestBody @Valid NotifyDTO notifyDTO) {

        Notify notify = dtoToEntity(notifyDTO);

        eventPublisher.publishEvent(new OnNewNotifyEvent(notifyDTO.getAccountId()));

        return ResponseEntity.ok(notifyDTO);
    }

    private Notify dtoToEntity(NotifyDTO notifyDTO) {
        notifyDTO.setNotifyDate(new Date());

        return null;
    }
}
