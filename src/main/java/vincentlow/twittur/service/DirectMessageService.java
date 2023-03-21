package vincentlow.twittur.service;

import java.util.List;

import vincentlow.twittur.model.entity.DirectMessage;
import vincentlow.twittur.model.request.DirectMessageRequest;

public interface DirectMessageService {

  DirectMessage sendMessage(String senderId, String recipientId, DirectMessageRequest request);

  List<DirectMessage> getDirectMessages(String senderId, String recipientId, int pageNumber, int pageSize);
}
