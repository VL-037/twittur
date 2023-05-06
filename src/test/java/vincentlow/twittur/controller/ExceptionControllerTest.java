package vincentlow.twittur.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

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

    ResponseEntity result = exceptionController.handleBadRequestException(new BadRequestException(EXCEPTION_MESSAGE));

    assertNotNull(result);
    assertTrue(result.getStatusCode()
        .is4xxClientError());
  }

  @Test
  void handleInternalAuthenticationServiceException() {

    ResponseEntity result = exceptionController
        .handleAuthenticationException(new InternalAuthenticationServiceException(EXCEPTION_MESSAGE));

    assertNotNull(result);
    assertTrue(result.getStatusCode()
        .is4xxClientError());
  }

  @Test
  void handleNotFoundException() {

    ResponseEntity result = exceptionController.handleNotFoundException(new NotFoundException(EXCEPTION_MESSAGE));

    assertNotNull(result);
    assertTrue(result.getStatusCode()
        .is4xxClientError());
  }

  @Test
  void handleConflictException() {

    ResponseEntity result = exceptionController.handleConflictException(new ConflictException(EXCEPTION_MESSAGE));

    assertNotNull(result);
    assertTrue(result.getStatusCode()
        .is4xxClientError());
  }

  @Test
  void handleServiceUnavailableException() {

    ResponseEntity result =
        exceptionController.handleServiceUnavailableException(new ServiceUnavailableException(EXCEPTION_MESSAGE));

    assertNotNull(result);
    assertTrue(result.getStatusCode()
        .is5xxServerError());
  }
}
