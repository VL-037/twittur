package vincentlow.twittur.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import vincentlow.twittur.model.constant.TokenType;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.entity.Token;
import vincentlow.twittur.model.request.CreateAccountRequest;
import vincentlow.twittur.model.request.LoginRequest;
import vincentlow.twittur.model.response.AuthenticationResponse;
import vincentlow.twittur.repository.TokenRepository;
import vincentlow.twittur.repository.service.AccountRepositoryService;
import vincentlow.twittur.service.JWTService;

public class AuthenticationServiceImplTest {

  private final String ACCOUNT_ID = "ACCOUNT_ID";

  private final String FIRST_NAME = "FIRST_NAME";

  private final String LAST_NAME = "LAST_NAME";

  private final LocalDate DATE_OF_BIRTH = LocalDate.parse("2000-01-01");

  private final String USERNAME = "USERNAME";

  private final String ACCOUNT_NAME = "ACCOUNT_NAME";

  private final String BIO = "BIO";

  private final String EMAIL_ADDRESS = "EMAIL_ADDRESS";

  private final String NEW_EMAIL_ADDRESS = "NEW_EMAIL_ADDRESS";

  private final String PHONE_NUMBER = "+621234567890";

  private final String ENCRYPTED_PASSWORD = "ENCRYPTED_PASSWORD";

  private final String PASSWORD = "PASSWORD_PASSWORD";

  private final String ACCESS_TOKEN = "ACCESS_TOKEN";

  @InjectMocks
  private AuthenticationServiceImpl authenticationService;

  @Mock
  private AccountRepositoryService accountRepositoryService;

  @Mock
  private TokenRepository tokenRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JWTService jwtService;

  @Mock
  private AuthenticationManager authenticationManager;

  private Account account;

  private Token token;

  private List<Token> tokens;

  private CreateAccountRequest createAccountRequest;

  private LoginRequest loginRequest;

  @BeforeEach
  void setUp() {

    openMocks(this);

    account = new Account();
    account.setId(ACCOUNT_ID);
    account.setFirstName(FIRST_NAME);
    account.setLastName(LAST_NAME);
    account.setDateOfBirth(DATE_OF_BIRTH);
    account.setUsername(USERNAME);
    account.setAccountName(ACCOUNT_NAME);
    account.setBio(BIO);
    account.setEmailAddress(EMAIL_ADDRESS);
    account.setPhoneNumber(PHONE_NUMBER);
    account.setPassword(ENCRYPTED_PASSWORD);
    account.setTweets(Collections.EMPTY_LIST);
    account.setFollowers(Collections.EMPTY_LIST);
    account.setFollowing(Collections.EMPTY_LIST);
    account.setSentMessages(Collections.EMPTY_LIST);
    account.setReceivedMessages(Collections.EMPTY_LIST);
    account.setNotifications(Collections.EMPTY_LIST);

    token = new Token();
    token.setAccount(account);
    token.setToken(ACCESS_TOKEN);
    token.setType(TokenType.BEARER);

    tokens = new ArrayList<>();
    tokens.add(token);

    createAccountRequest = CreateAccountRequest.builder()
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .dateOfBirth(DATE_OF_BIRTH)
        .username(USERNAME)
        .accountName(ACCOUNT_NAME)
        .emailAddress(EMAIL_ADDRESS)
        .phoneNumber(PHONE_NUMBER)
        .password(PASSWORD)
        .confirmPassword(PASSWORD)
        .build();

    loginRequest = LoginRequest.builder()
        .username(USERNAME)
        .password(PASSWORD)
        .build();

    when(accountRepositoryService.findByUsernameAndMarkForDeleteFalse(USERNAME)).thenReturn(null);
    when(accountRepositoryService.findByEmailAddressAndMarkForDeleteFalse(USERNAME)).thenReturn(null);
    when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCRYPTED_PASSWORD);
    when(accountRepositoryService.save(any(Account.class))).thenReturn(account);
    when(jwtService.generateToken(any(Account.class))).thenReturn(ACCESS_TOKEN);
    when(tokenRepository.save(any(Token.class))).thenReturn(token);

    when(accountRepositoryService.findByUsernameOrEmailAddressAndMarkForDeleteFalse(USERNAME)).thenReturn(account);
    when(tokenRepository.findAllValidTokensByAccountId(ACCOUNT_ID)).thenReturn(tokens);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(accountRepositoryService, tokenRepository, passwordEncoder, jwtService,
        authenticationManager);
  }

  @Test
  void register() {

    AuthenticationResponse result = authenticationService.register(createAccountRequest);

    verify(accountRepositoryService).findByUsernameAndMarkForDeleteFalse(USERNAME);
    verify(accountRepositoryService).findByEmailAddressAndMarkForDeleteFalse(EMAIL_ADDRESS);
    verify(passwordEncoder).encode(PASSWORD);
    verify(accountRepositoryService).save(any(Account.class));
    verify(jwtService).generateToken(any(Account.class));
    verify(tokenRepository).save(any(Token.class));

    assertNotNull(result);
    assertEquals(token.getToken(), result.getAccessToken());
    assertEquals(TokenType.BEARER, token.getType());
    assertEquals(account, token.getAccount());
  }

  @Test
  void login() {

    AuthenticationResponse result = authenticationService.login(loginRequest);

    verify(authenticationManager).authenticate(any(Authentication.class));
    verify(accountRepositoryService).findByUsernameOrEmailAddressAndMarkForDeleteFalse(USERNAME);
    verify(jwtService).generateToken(any(Account.class));
    verify(tokenRepository).findAllValidTokensByAccountId(ACCOUNT_ID);
    verify(tokenRepository).saveAll(tokens);
    verify(tokenRepository).save(any(Token.class));

    assertNotNull(result);
    assertEquals(ACCESS_TOKEN, result.getAccessToken());
  }
}
