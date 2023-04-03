package vincentlow.twittur.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;

import vincentlow.twittur.integration.BaseIntegrationTest;
import vincentlow.twittur.model.constant.ApiPath;
import vincentlow.twittur.model.entity.Email;
import vincentlow.twittur.model.response.SchedulerFailedEmailResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.repository.EmailRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;

public class SchedulerControllerIntegrationTest extends BaseIntegrationTest {

  private String SCHEDULER_CONTROLLER_DIR = "scheduler-controller";

  @Autowired
  private AccountRepositoryService accountRepositoryService;

  @Autowired
  private JavaMailSender javaMailSender;

  @Autowired
  private EmailRepository emailRepository;

  private Email email1;

  private Email email2;

  private Email email3;

  @BeforeEach
  void setUp() {

    email1 = getEntityFromPath(EMAIL_ENTITY_DIR, "email1", new TypeReference<>() {});
    email2 = getEntityFromPath(EMAIL_ENTITY_DIR, "email2", new TypeReference<>() {});
    email3 = getEntityFromPath(EMAIL_ENTITY_DIR, "email3", new TypeReference<>() {});
  }

  @AfterEach
  void tearDown() {

    emailRepository.deleteAll();
  }

  @Test
  public void resendFailedEmails_success() throws Exception {

    ApiSingleResponse<SchedulerFailedEmailResponse> expectation =
        getExpectationFromPath(SCHEDULER_CONTROLLER_DIR, new TypeReference<>() {});

    List<Email> emails = new ArrayList<>();
    emails.add(email1);
    emails.add(email2);
    emails.add(email3);

    emailRepository.saveAll(emails);

    MvcResult mvcResult = mockMvc
        .perform(post(ApiPath.SCHEDULER + "/resend-failed-emails").accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiSingleResponse<SchedulerFailedEmailResponse> response = getMvcResponse(mvcResult, new TypeReference<>() {});

    baseSuccessApiSingleResponseAssertion(response);
    assertThat(response.getData()).usingRecursiveComparison()
        .ignoringFields(ignoredFields.toArray(new String[0]))
        .isEqualTo(expectation.getData());
  }

  @Test
  public void resendFailedEmails_zeroFailed_success() throws Exception {

    ApiSingleResponse<SchedulerFailedEmailResponse> expectation =
        getExpectationFromPath(SCHEDULER_CONTROLLER_DIR, new TypeReference<>() {});

    List<Email> emails = new ArrayList<>();
    emails.add(email1);
    emails.add(email2);
    emails.add(email3);

    emails.forEach(e -> e.setSent(true));

    emailRepository.saveAll(emails);

    MvcResult mvcResult = mockMvc
        .perform(post(ApiPath.SCHEDULER + "/resend-failed-emails").accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    ApiSingleResponse<SchedulerFailedEmailResponse> response = getMvcResponse(mvcResult, new TypeReference<>() {});

    baseSuccessApiSingleResponseAssertion(response);
    assertThat(response.getData()).usingRecursiveComparison()
        .ignoringFields(ignoredFields.toArray(new String[0]))
        .isEqualTo(expectation.getData());
  }
}
