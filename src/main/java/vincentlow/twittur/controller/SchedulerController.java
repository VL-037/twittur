package vincentlow.twittur.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.model.constant.ApiPath;
import vincentlow.twittur.model.response.SchedulerFailedEmailResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.service.SchedulerService;

@Slf4j
@RestController
@RequestMapping(value = ApiPath.SCHEDULER, produces = MediaType.APPLICATION_JSON_VALUE)
public class SchedulerController extends BaseController {

  @Autowired
  private SchedulerService schedulerService;

  @PostMapping("/resend-failed-emails")
  private ResponseEntity<ApiSingleResponse<SchedulerFailedEmailResponse>> resendFailedEmails() {

    try {
      Pair<Integer, Integer> result = schedulerService.resendFailedEmails();
      SchedulerFailedEmailResponse response = SchedulerFailedEmailResponse.builder()
          .processedEmails(result.getFirst())
          .succeedEmails(result.getSecond())
          .build();

      return toSuccessResponseEntity(toApiSingleResponse(response));
    } catch (RuntimeException e) {
      log.error("#resendFailedEmails ERROR! with error: {}", e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
