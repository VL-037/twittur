package vincentlow.twittur.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import vincentlow.twittur.model.constant.ApiPath;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.LoginRequest;
import vincentlow.twittur.model.response.AuthenticationResponse;
import vincentlow.twittur.service.AuthenticationService;

public class AuthenticationControllerTest {

  private final String ACCESS_TOKEN = "ACCESS_TOKEN";

  @InjectMocks
  private AuthenticationController authenticationController;

  @Mock
  private AuthenticationService authenticationService;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  private HttpStatus httpStatus;

  private CreateAccountRequest createAccountRequest;

  private LoginRequest loginRequest;

  private AuthenticationResponse authenticationResponse;

  @BeforeEach
  void setUp() {

    openMocks(this);
    mockMvc = standaloneSetup(authenticationController).build();

    objectMapper = new ObjectMapper();

    httpStatus = HttpStatus.OK;

    createAccountRequest = CreateAccountRequest.builder()
        .build();

    loginRequest = LoginRequest.builder()
        .build();

    authenticationResponse = AuthenticationResponse.builder()
        .accessToken(ACCESS_TOKEN)
        .build();

    when(authenticationService.register(createAccountRequest)).thenReturn(authenticationResponse);
    when(authenticationService.login(loginRequest)).thenReturn(authenticationResponse);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(authenticationService);
  }

  @Test
  void register_success() throws Exception {

    this.mockMvc.perform(post(ApiPath.AUTHENTICATION + "/register").accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createAccountRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.data.accessToken", equalTo(ACCESS_TOKEN)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(authenticationService).register(createAccountRequest);
  }

  @Test
  void login() throws Exception {

    this.mockMvc.perform(post(ApiPath.AUTHENTICATION + "/login").accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", equalTo(httpStatus.value())))
        .andExpect(jsonPath("$.status", equalTo(httpStatus.name())))
        .andExpect(jsonPath("$.data.accessToken", equalTo(ACCESS_TOKEN)))
        .andExpect(jsonPath("$.error", equalTo(null)));

    verify(authenticationService).login(loginRequest);
  }
}
