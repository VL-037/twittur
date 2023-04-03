package vincentlow.twittur.service.impl;

import static vincentlow.twittur.utils.ValidatorUtil.validateAccount;
import static vincentlow.twittur.utils.ValidatorUtil.validateArgument;
import static vincentlow.twittur.utils.ValidatorUtil.validateState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.model.constant.ErrorCode;
import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Email;
import vincentlow.twittur.model.request.EmailRequest;
import vincentlow.twittur.repository.EmailRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;
import vincentlow.twittur.service.EmailService;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

  @Autowired
  private AccountRepositoryService accountRepositoryService;

  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private EmailRepository emailRepository;

  @Override
  public Email sendEmail(EmailRequest request) {

    validateState(Objects.nonNull(request), ErrorCode.REQUEST_MUST_NOT_BE_NULL.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getRecipient()),
        ErrorCode.EMAIL_RECIPIENT_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getSubject()),
        ErrorCode.EMAIL_SUBJECT_MUST_NOT_BE_BLANK.getMessage());
    validateArgument(StringUtils.isNotBlank(request.getBody()),
        ErrorCode.EMAIL_BODY_MUST_NOT_BE_BLANK.getMessage());

    Email email = new Email();
    email.setRecipient(request.getRecipient());
    email.setSubject(request.getSubject());
    email.setBody(request.getBody());

    LocalDateTime now = LocalDateTime.now();
    email.setCreatedBy("system");
    email.setCreatedDate(now);
    email.setUpdatedBy("system");
    email.setUpdatedDate(now);

    Account account = accountRepositoryService.findByEmailAddressAndMarkForDeleteFalse(request.getRecipient());
    validateAccount(account, ExceptionMessage.ACCOUNT_NOT_FOUND);

    String htmlBody = "<!DOCTYPE html>\n" +
        "<html>\n" +
        "  <body style=\"background-color: #f2f2f2;\">\n" +
        "    <div style=\"background-color: white; padding: 20px; border: 1px solid #ccc;\">\n" +
        "      <h1 style=\"color: #2d2d2d;\">Welcome to my Email!</h1>\n" +
        "      <p style=\"color: #666;\">" + email.getBody() + "</p>\n" +
        "      <hr style=\"border-top: 1px solid #ccc;\">\n" +
        "      <h2 style=\"color: #2d2d2d;\">Some Important Information</h2>\n" +
        "      <ul style=\"color: #666;\">\n" +
        "        <li>Item 1</li>\n" +
        "        <li>Item 2</li>\n" +
        "        <li>Item 3</li>\n" +
        "      </ul>\n" +
        "      <div style=\"background-color: #f2f2f2; padding: 10px;\">\n" +
        "        <p style=\"color: #666;\">This is a box with some more information.</p>\n" +
        "      </div>\n" +
        "      <hr style=\"border-top: 1px solid #ccc;\">\n" +
        "      <p style=\"color: #666;\">Thanks for reading!</p>\n" +
        "    </div>\n" +
        "  </body>\n" +
        "</html>";

    email.setBody(htmlBody);
    send(email);
    return emailRepository.save(email);
  }

  @Override
  public Pair<Integer, Integer> resendFailedEmails() {

    List<Email> emails = emailRepository.findAllBySentFalseAndMarkForDeleteFalse();
    List<Email> succeedEmails = new ArrayList<>();

    for (Email email : emails) {
      Email sentEmail = send(email);
      if (sentEmail.getSent()) {
        succeedEmails.add(sentEmail);
      }
    }

    int processed = emails.size();
    int succeed = emailRepository.saveAll(succeedEmails)
        .size();

    return Pair.of(processed, succeed);
  }

  private Email send(Email email) {

    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setTo(email.getRecipient());
      helper.setSubject(email.getSubject());
      helper.setText(email.getBody(), true);

      email.setSent(true);
      javaMailSender.send(message);
    } catch (MessagingException e) {
      log.error("#EmailServiceImpl#send ERROR! with email: {}, and error: {}", email, e.getMessage(), e);
      email.setSent(false);
    }
    return email;
  }
}
