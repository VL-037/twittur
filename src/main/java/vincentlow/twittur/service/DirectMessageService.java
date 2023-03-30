package vincentlow.twittur.service;

import org.springframework.data.domain.Page;

import vincentlow.twittur.model.entity.DirectMessage;
import vincentlow.twittur.model.request.DirectMessageRequest;

public interface DirectMessageService {

  DirectMessage sendMessage(String senderId, String recipientId, DirectMessageRequest request);

  Page<DirectMessage> getDirectMessages(String senderId, String recipientId, int pageNumber, int pageSize);
}
