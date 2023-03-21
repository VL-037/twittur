package vincentlow.twittur.service;

import org.springframework.data.domain.Page;

import vincentlow.twittur.model.entity.Notification;

public interface NotificationService {

  Page<Notification> getNotifications(String recipientId, int pageNumber, int pageSize);
}
