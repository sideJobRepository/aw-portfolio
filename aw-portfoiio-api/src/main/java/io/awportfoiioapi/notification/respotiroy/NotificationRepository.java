package io.awportfoiioapi.notification.respotiroy;

import io.awportfoiioapi.notification.entity.Notification;
import io.awportfoiioapi.notification.respotiroy.query.NotificationQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long>, NotificationQueryRepository {

}
