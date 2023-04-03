package vincentlow.twittur.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.util.Pair;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Email;
import vincentlow.twittur.model.request.EmailRequest;
import vincentlow.twittur.repository.EmailRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;

public class EmailServiceImplTest {

  private final String EMAIL_ADDRESS = "EMAIL_ADDRESS";

  private final String SUBJECT = "SUBJECT";

  private final String BODY = "BODY";

  private final boolean SENT = true;

  @InjectMocks
  private EmailServiceImpl emailService;

  @Mock
  private AccountRepositoryService accountRepositoryService;

  @Mock
  private JavaMailSender javaMailSender;

  @Mock
  private EmailRepository emailRepository;

  private Account account;

  private Email email;

  private List<Email> emails;

  private EmailRequest emailRequest;

  private MimeMessage mimeMessage;

  @BeforeEach
  void setUp() {

    openMocks(this);

    account = new Account();
    account.setEmailAddress(EMAIL_ADDRESS);

    email = new Email();
    email.setRecipient(EMAIL_ADDRESS);
    email.setSubject(SUBJECT);
    email.setBody(BODY);
    email.setSent(SENT);

    emails = new ArrayList<>();
    emails.add(email);

    emailRequest = EmailRequest.builder()
        .recipient(EMAIL_ADDRESS)
        .subject(SUBJECT)
        .body(BODY)
        .build();

    mimeMessage = new MimeMessage((Session) null);

    when(accountRepositoryService.findByEmailAddressAndMarkForDeleteFalse(EMAIL_ADDRESS)).thenReturn(account);
    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    doNothing().when(javaMailSender)
        .send(any(MimeMessage.class));
    when(emailRepository.save(any(Email.class))).thenReturn(email);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(accountRepositoryService, javaMailSender, emailRepository);
  }

  @Test
  void sendEmail() {

    Email result = emailService.sendEmail(emailRequest);

    verify(accountRepositoryService).findByEmailAddressAndMarkForDeleteFalse(EMAIL_ADDRESS);
    verify(javaMailSender).createMimeMessage();
    verify(javaMailSender).send(any(MimeMessage.class));
    verify(emailRepository).save(any(Email.class));

    assertNotNull(result);
    assertEquals(EMAIL_ADDRESS, result.getRecipient());
    assertEquals(SUBJECT, result.getSubject());
    assertEquals(BODY, result.getBody());
    assertEquals(SENT, result.getSent());
  }

  @Test
  void resendFailedEmails() {

    emails.forEach(e -> e.setSent(false));

    when(emailRepository.findAllBySentFalseAndMarkForDeleteFalse()).thenReturn(emails);
    when(emailRepository.saveAll(any(List.class))).thenReturn(emails);

    Pair<Integer, Integer> result = emailService.resendFailedEmails();

    verify(emailRepository).findAllBySentFalseAndMarkForDeleteFalse();
    verify(javaMailSender).createMimeMessage();
    verify(javaMailSender).send(any(MimeMessage.class));
    verify(emailRepository).saveAll(any(List.class));

    assertNotNull(result);
    assertNotNull(result.getFirst());
    assertNotNull(result.getSecond());
  }
}
