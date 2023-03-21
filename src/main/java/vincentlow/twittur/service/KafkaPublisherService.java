package vincentlow.twittur.service;

import vincentlow.twittur.model.request.PushNotificationRequest;

public interface KafkaPublisherService {

  void pushNotification(PushNotificationRequest request);
}
