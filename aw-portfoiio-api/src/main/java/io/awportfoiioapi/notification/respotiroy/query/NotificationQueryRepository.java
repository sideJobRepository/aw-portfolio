package io.awportfoiioapi.notification.respotiroy.query;

import io.awportfoiioapi.notification.entity.Notification;

import java.util.List;

public interface NotificationQueryRepository {

    List<Notification> findByOptionsId(Long id);
}
