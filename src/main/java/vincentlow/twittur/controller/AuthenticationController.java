package vincentlow.twittur.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.model.constant.ApiPath;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.LoginRequest;
import vincentlow.twittur.model.response.AuthenticationResponse;
import vincentlow.twittur.model.response.api.ApiSingleResponse;
import vincentlow.twittur.service.AuthenticationService;

@Slf4j
@RestController
@RequestMapping(ApiPath.AUTHENTICATION)
public class AuthenticationController extends BaseController {

  @Autowired
  private AuthenticationService authenticationService;

  @PostMapping("/register")
  public ApiSingleResponse<AuthenticationResponse> register(@RequestBody CreateAccountRequest request) {

    try {
      return toSuccessApiResponse(authenticationService.register(request));
    } catch (RuntimeException e) {
      log.error("#register ERROR! with request: {}, and error: {}", request, e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @PostMapping("/login")
  public ApiSingleResponse<AuthenticationResponse> login(@RequestBody LoginRequest request) {

    try {
      return toSuccessApiResponse(authenticationService.login(request));
    } catch (RuntimeException e) {
      log.error("#login ERROR! with request: {}, and error: {}", request, e.getMessage(), e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
