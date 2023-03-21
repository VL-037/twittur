package vincentlow.twittur.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import vincentlow.twittur.model.constant.KafkaConstant;
import vincentlow.twittur.model.request.PushNotificationRequest;
import vincentlow.twittur.service.KafkaPublisherService;

@Service
public class KafkaPublisherServiceImpl implements KafkaPublisherService {

  @Autowired
  private KafkaTemplate<String, Object> kafkaTemplate;

  @Override
  public void pushNotification(PushNotificationRequest request) {

    kafkaTemplate.send(KafkaConstant.PUSH_NOTIFICATION, request);
  }
}
