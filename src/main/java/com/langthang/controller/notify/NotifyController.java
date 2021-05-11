package com.langthang.controller.notify;

import com.langthang.dto.NotifyDTO;
import com.langthang.event.OnNewNotifyEvent;
import com.langthang.model.entity.Notify;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
@Slf4j
public class NotifyController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping
    public ResponseEntity<Object> addNotification(
            @RequestBody @Valid NotifyDTO notifyDTO) {
        log.info("Receiving new notify: {}", notifyDTO);

        Notify notify = dtoToEntity(notifyDTO);
        log.info("Converted to Entity and attempting to save: {}", notify);

        log.info("Broadcasting message to all client");
        eventPublisher.publishEvent(new OnNewNotifyEvent(notifyDTO.getAccountId()));

        return ResponseEntity.ok(notifyDTO);
    }

    private Notify dtoToEntity(NotifyDTO notifyDTO) {
        notifyDTO.setNotifyDate(new Date());

        return modelMapper.map(notifyDTO, Notify.class);
    }
}
