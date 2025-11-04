package com.paypal.notificationservice.service;


import com.paypal.notificationservice.entity.Notification;

import java.util.List;

public interface NotificationService {

    Notification sendNotification(Notification notification);

    List<Notification> getNotificationsByUserId(Long userId);

}
