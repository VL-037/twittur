package vincentlow.twittur.listener;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import vincentlow.twittur.model.constant.KafkaConstant;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Notification;
import vincentlow.twittur.repository.NotificationRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;

@Component
public class PushNotificationListener {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private AccountRepositoryService accountRepositoryService;

  @KafkaListener(topics = KafkaConstant.PUSH_NOTIFICATION, groupId = KafkaConstant.GROUP_ID)
  public void processPushNotification(ConsumerRecord<String, String> record) {

    try {
      Notification baseNotification = objectMapper.readValue(record.value(), Notification.class);

      LocalDateTime now = LocalDateTime.now();
      baseNotification.setCreatedBy(baseNotification.getSenderId());
      baseNotification.setCreatedDate(now);
      baseNotification.setUpdatedBy(baseNotification.getSenderId());
      baseNotification.setUpdatedDate(now);

      List<Account> followers = accountRepositoryService.findAllFollowers(baseNotification.getSenderId());

      for (Account follower : followers) {
        Notification followerNotification = new Notification();
        BeanUtils.copyProperties(baseNotification, followerNotification);

        followerNotification.setRecipient(follower);
        notificationRepository.save(followerNotification);
      }

    } catch (Exception e) {
    }
  }
}
