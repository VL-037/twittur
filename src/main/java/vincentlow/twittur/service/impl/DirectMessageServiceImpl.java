package vincentlow.twittur.service.impl;

import static vincentlow.twittur.utils.ValidatorUtil.validateAccount;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vincentlow.twittur.model.constant.DirectMessageStatus;
import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.BaseEntity;
import vincentlow.twittur.model.entity.DirectMessage;
import vincentlow.twittur.model.request.DirectMessageRequest;
import vincentlow.twittur.repository.DirectMessageRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;
import vincentlow.twittur.service.DirectMessageService;

@Service
public class DirectMessageServiceImpl implements DirectMessageService {

  @Autowired
  private AccountRepositoryService accountRepositoryService;

  @Autowired
  private DirectMessageRepository directMessageRepository;

  @Override
  public DirectMessage sendMessage(String senderId, String recipientId, DirectMessageRequest request) {

    Account sender = accountRepositoryService.findByIdAndMarkForDeleteFalse(senderId);
    validateAccount(sender, ExceptionMessage.SENDER_ACCOUNT_NOT_FOUND);

    Account recipient = accountRepositoryService.findByIdAndMarkForDeleteFalse(recipientId);
    validateAccount(recipient, ExceptionMessage.RECIPIENT_ACCOUNT_NOT_FOUND);

    DirectMessage directMessage = new DirectMessage();
    directMessage.setSender(sender);
    directMessage.setRecipient(recipient);
    directMessage.setMessage(request.getMessage());
    directMessage.setStatus(DirectMessageStatus.DELIVERED);

    LocalDateTime now = LocalDateTime.now();
    directMessage.setCreatedBy(sender.getId());
    directMessage.setCreatedDate(now);
    directMessage.setUpdatedBy(sender.getId());
    directMessage.setUpdatedDate(now);

    return directMessageRepository.save(directMessage);
  }

  @Override
  public List<DirectMessage> getDirectMessages(String senderId, String recipientId, int pageNumber, int pageSize) {

    Account sender = accountRepositoryService.findByIdAndMarkForDeleteFalse(senderId);
    validateAccount(sender, ExceptionMessage.SENDER_ACCOUNT_NOT_FOUND);

    Account recipient = accountRepositoryService.findByIdAndMarkForDeleteFalse(recipientId);
    validateAccount(recipient, ExceptionMessage.RECIPIENT_ACCOUNT_NOT_FOUND);

    Pageable pageable = PageRequest.of(pageNumber, pageSize);

    Page<DirectMessage> senderToRecipientMessages =
        directMessageRepository.findAllBySenderIdAndRecipientIdOrderByCreatedDateDesc(senderId, recipientId, pageable);
    Page<DirectMessage> recipientToSenderMessages =
        directMessageRepository.findAllBySenderIdAndRecipientIdOrderByCreatedDateDesc(recipientId, senderId, pageable);

    List<DirectMessage> allMessages = new ArrayList<>();
    allMessages.addAll(senderToRecipientMessages.getContent());
    allMessages.addAll(recipientToSenderMessages.getContent());

    /**
     * oldest to newest. UI can consume [for 0 to N] and attach to
     * the beginning of the array when doing Infinite Scrolling (scroll UP)
     */
    allMessages.sort(Comparator.comparing(BaseEntity::getCreatedDate));
    return allMessages;
  }
}
