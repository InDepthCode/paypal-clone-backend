package com.paypal.notificationservice.controller;


import com.paypal.notificationservice.entity.Notification;
import com.paypal.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    @Autowired
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public Notification sendNotification(@RequestBody Notification notification) {
        return notificationService.sendNotification(notification);
    }

    @GetMapping("/{userId}")
    public List<Notification> findByUserId(@PathVariable Long userId) {  // Changed to Long
        return notificationService.getNotificationsByUserId(userId);
    }
}
