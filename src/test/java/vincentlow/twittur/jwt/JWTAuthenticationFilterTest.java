package vincentlow.twittur.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.google.common.net.HttpHeaders;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vincentlow.twittur.model.constant.Role;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.service.JWTService;

class JWTAuthenticationFilterTest {

  private final String VALID_JWT_TOKEN = "VALID.JWT.TOKEN";

  private final String INVALID_JWT_TOKEN = "INVALID.JWT.TOKEN";

  private final String USERNAME = "USERNAME";

  @InjectMocks
  private JWTAuthenticationFilter jwtAuthenticationFilter;

  @Mock
  private JWTService jwtService;

  @Mock
  private UserDetailsService userDetailsService;

  private HttpServletRequest httpServletRequest;

  private HttpServletResponse httpServletResponse;

  private FilterChain filterChain;

  private Account account;

  @BeforeEach
  void setUp() throws ServletException, IOException {

    openMocks(this);
    SecurityContextHolder.clearContext();

    httpServletRequest = mock(HttpServletRequest.class);
    httpServletResponse = mock(HttpServletResponse.class);
    filterChain = mock(FilterChain.class);

    account = new Account();
    account.setUsername(USERNAME);
    account.setRole(Role.USER);

    when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + VALID_JWT_TOKEN);
    doNothing().when(filterChain)
        .doFilter(httpServletRequest, httpServletResponse);
    when(jwtService.extractUsername(VALID_JWT_TOKEN)).thenReturn(USERNAME);
    when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(account);
    when(jwtService.isTokenValid(VALID_JWT_TOKEN, account)).thenReturn(true);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(jwtService, userDetailsService);
  }

  @Test
  void doFilterInternal_validToken() throws ServletException, IOException {

    jwtAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

    UsernamePasswordAuthenticationToken authentication =
        (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext()
            .getAuthentication();

    verify(jwtService).extractUsername(VALID_JWT_TOKEN);
    verify(userDetailsService).loadUserByUsername(USERNAME);
    verify(jwtService).isTokenValid(VALID_JWT_TOKEN, account);

    assertNotNull(authentication);
    assertTrue(authentication.isAuthenticated());
    assertEquals(USERNAME, authentication.getName());
    assertEquals(account.getAuthorities(), authentication.getAuthorities());
  }

  @Test
  void doFilterInternal_invalidToken() throws ServletException, IOException {

    when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + INVALID_JWT_TOKEN);
    when(jwtService.isTokenValid(INVALID_JWT_TOKEN, account)).thenReturn(false);

    jwtAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

    UsernamePasswordAuthenticationToken authentication =
        (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext()
            .getAuthentication();

    verify(jwtService).extractUsername(INVALID_JWT_TOKEN);

    assertNull(authentication);
    assertNull(SecurityContextHolder.getContext()
        .getAuthentication());
  }

  @Test
  void doFilterInternal_nullAuthorizationHeader() throws ServletException, IOException {

    when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    jwtAuthenticationFilter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

    UsernamePasswordAuthenticationToken authentication =
        (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext()
            .getAuthentication();

    assertNull(authentication);
    assertNull(SecurityContextHolder.getContext()
        .getAuthentication());
  }
}
