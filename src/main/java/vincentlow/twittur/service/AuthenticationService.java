package vincentlow.twittur.service;

import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.LoginRequest;
import vincentlow.twittur.model.response.AuthenticationResponse;

public interface AuthenticationService {

  AuthenticationResponse register(CreateAccountRequest request);

  AuthenticationResponse login(LoginRequest request);
}
