package vincentlow.twittur.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;

import com.google.common.net.HttpHeaders;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vincentlow.twittur.model.constant.TokenType;
import vincentlow.twittur.model.entity.Token;
import vincentlow.twittur.repository.TokenRepository;

public class LogoutServiceImplTest {

  private String ACCESS_TOKEN = "ACCESS_TOKEN";

  private String AUTHORIZATION_HEADER_VALUE = "Bearer " + ACCESS_TOKEN;

  @InjectMocks
  private LogoutServiceImpl logoutService;

  @Mock
  private TokenRepository tokenRepository;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private HttpServletResponse httpServletResponse;

  @Mock
  private Authentication authentication;

  private Token token;

  @BeforeEach
  void setUp() {

    openMocks(this);

    token = new Token();
    token.setToken(ACCESS_TOKEN);
    token.setType(TokenType.BEARER);

    when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(AUTHORIZATION_HEADER_VALUE);
    when(tokenRepository.findByToken(ACCESS_TOKEN)).thenReturn(token);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(tokenRepository);
  }

  @Test
  void logout() {

    logoutService.logout(httpServletRequest, httpServletResponse, authentication);

    verify(httpServletRequest).getHeader(HttpHeaders.AUTHORIZATION);
    verify(tokenRepository).findByToken(ACCESS_TOKEN);
    verify(tokenRepository).save(token);
  }
}
