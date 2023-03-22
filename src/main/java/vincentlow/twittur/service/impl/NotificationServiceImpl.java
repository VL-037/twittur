package vincentlow.twittur.service.impl;

import static vincentlow.twittur.utils.ValidatorUtil.validateAccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Notification;
import vincentlow.twittur.repository.NotificationRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;
import vincentlow.twittur.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private AccountRepositoryService accountRepositoryService;

  @Override
  public Page<Notification> getNotifications(String recipientId, int pageNumber, int pageSize) {

    Account account = accountRepositoryService.findByIdAndMarkForDeleteFalse(recipientId);
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    return notificationRepository.findAllByRecipientIdOrderByCreatedDateDesc(recipientId,
        PageRequest.of(pageNumber, pageSize));
  }
}
