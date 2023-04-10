package vincentlow.twittur.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.exception.BadRequestException;
import vincentlow.twittur.model.response.exception.ConflictException;
import vincentlow.twittur.model.response.exception.NotFoundException;
import vincentlow.twittur.model.response.exception.ServiceUnavailableException;

public class ExceptionControllerTest {

  private final String EXCEPTION_MESSAGE = "EXCEPTION_MESSAGE";

  @InjectMocks
  private ExceptionController exceptionController;

  @BeforeEach
  void setUp() {

    MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() {}

  @Test
  void handleBadRequestException() {

    ApiResponse result = exceptionController.handleBadRequestException(new BadRequestException(EXCEPTION_MESSAGE));

    assertNotNull(result);
    assertEquals(HttpStatus.BAD_REQUEST.value(), result.getCode());
    assertEquals(HttpStatus.BAD_REQUEST.name(), result.getStatus());
    assertEquals(EXCEPTION_MESSAGE, result.getError());
  }

  @Test
  void handleInternalAuthenticationServiceException() {

    ApiResponse result = exceptionController
        .handleAuthenticationException(new InternalAuthenticationServiceException(EXCEPTION_MESSAGE));

    assertNotNull(result);
    assertEquals(HttpStatus.FORBIDDEN.value(), result.getCode());
    assertEquals(HttpStatus.FORBIDDEN.name(), result.getStatus());
    assertEquals(ExceptionMessage.AUTHENTICATION_FAILED, result.getError());
  }

  @Test
  void handleNotFoundException() {

    ApiResponse result = exceptionController.handleNotFoundException(new NotFoundException(EXCEPTION_MESSAGE));

    assertNotNull(result);
    assertEquals(HttpStatus.NOT_FOUND.value(), result.getCode());
    assertEquals(HttpStatus.NOT_FOUND.name(), result.getStatus());
    assertEquals(EXCEPTION_MESSAGE, result.getError());
  }

  @Test
  void handleConflictException() {

    ApiResponse result = exceptionController.handleConflictException(new ConflictException(EXCEPTION_MESSAGE));

    assertNotNull(result);
    assertEquals(HttpStatus.CONFLICT.value(), result.getCode());
    assertEquals(HttpStatus.CONFLICT.name(), result.getStatus());
    assertEquals(EXCEPTION_MESSAGE, result.getError());
  }

  @Test
  void handleServiceUnavailableException() {

    ApiResponse result =
        exceptionController.handleServiceUnavailableException(new ServiceUnavailableException(EXCEPTION_MESSAGE));

    assertNotNull(result);
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), result.getCode());
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE.name(), result.getStatus());
    assertEquals(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE, result.getError());
  }
}
