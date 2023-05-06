package vincentlow.twittur.controller;

import static vincentlow.twittur.utils.ObjectMappingHelper.toResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.model.constant.ApiPath;
import vincentlow.twittur.model.entity.Email;
import vincentlow.twittur.model.request.EmailRequest;
import vincentlow.twittur.model.response.EmailResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.service.EmailService;

@Slf4j
@RestController
@RequestMapping(value = ApiPath.EMAIL, produces = MediaType.APPLICATION_JSON_VALUE)
public class EmailController extends BaseController {

  @Autowired
  private EmailService emailService;

  @PostMapping
  private ResponseEntity<ApiSingleResponse<EmailResponse>> sendEmail(@RequestBody EmailRequest request) {

    try {
      Email email = emailService.sendEmail(request);
      EmailResponse response = toResponse(email, EmailResponse.class);

      return toSuccessResponseEntity(toApiSingleResponse(response));
    } catch (RuntimeException e) {
      log.error("#sendEmail ERROR! with request: {}, and error: {}", request, e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
