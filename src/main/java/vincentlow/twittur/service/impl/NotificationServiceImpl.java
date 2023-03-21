package vincentlow.twittur.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import vincentlow.twittur.model.entity.Notification;
import vincentlow.twittur.repository.NotificationRepository;
import vincentlow.twittur.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  private NotificationRepository notificationRepository;

  @Override
  public Page<Notification> getNotifications(String recipientId, int pageNumber, int pageSize) {

    return notificationRepository.findAllByRecipientIdOrderByCreatedDateDesc(recipientId,
        PageRequest.of(pageNumber, pageSize));
  }
}
