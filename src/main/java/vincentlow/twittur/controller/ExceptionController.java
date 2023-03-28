package vincentlow.twittur.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.exception.BadRequestException;
import vincentlow.twittur.model.response.exception.ConflictException;
import vincentlow.twittur.model.response.exception.NotFoundException;
import vincentlow.twittur.model.response.exception.ServiceUnavailableException;

@RestControllerAdvice
public class ExceptionController extends BaseController {

  @ExceptionHandler
  public ApiResponse handleBadRequestException(BadRequestException ex) {

    return toErrorApiResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(value = {NotFoundException.class})
  public ApiResponse handleNotFoundException(NotFoundException ex) {

    return toErrorApiResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(value = {ConflictException.class})
  public ApiResponse handleConflictException(ConflictException ex) {

    return toErrorApiResponse(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(value = {DataAccessException.class, ServiceUnavailableException.class})
  public ApiResponse handleServiceUnavailableException(RuntimeException ex) {

    return toErrorApiResponse(HttpStatus.SERVICE_UNAVAILABLE, ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
  }
}
