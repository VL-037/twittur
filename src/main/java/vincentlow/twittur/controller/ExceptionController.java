package vincentlow.twittur.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.exception.BadRequestException;
import vincentlow.twittur.model.response.exception.ConflictException;
import vincentlow.twittur.model.response.exception.NotFoundException;
import vincentlow.twittur.model.response.exception.ServiceUnavailableException;

@Slf4j
@RestControllerAdvice
public class ExceptionController extends BaseController {

  @ExceptionHandler
  public ApiResponse handleBadRequestException(BadRequestException ex) {

    log.error("#handleBadRequestException ERROR! with error: {}", ex.getMessage());
    return toErrorApiResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(value = {NotFoundException.class})
  public ApiResponse handleNotFoundException(NotFoundException ex) {

    log.error("#handleNotFoundException ERROR! with error: {}", ex.getMessage());
    return toErrorApiResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(value = {ConflictException.class})
  public ApiResponse handleConflictException(ConflictException ex) {

    log.error("#handleConflictException ERROR! with error: {}", ex.getMessage());
    return toErrorApiResponse(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(value = {DataAccessException.class, ServiceUnavailableException.class})
  public ApiResponse handleServiceUnavailableException(RuntimeException ex) {

    log.error("#handleServiceUnavailableException ERROR! with error: {}", ex.getMessage());
    return toErrorApiResponse(HttpStatus.SERVICE_UNAVAILABLE, ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
  }
}
