package vincentlow.twittur.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import vincentlow.twittur.model.response.api.ApiResponse;
import vincentlow.twittur.model.response.exception.BadRequestException;
import vincentlow.twittur.model.response.exception.InternalServerErrorException;
import vincentlow.twittur.model.response.exception.NotFoundException;

@ControllerAdvice
@RestController
public class ExceptionController extends BaseController {

  @ExceptionHandler
  public ApiResponse handleBadRequestException(BadRequestException ex) {

    return toErrorApiResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(value = {NotFoundException.class})
  public ApiResponse handleNotFoundException(NotFoundException ex) {

    return toErrorApiResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(value = {InternalServerErrorException.class})
  public ApiResponse handleInternalServerErrorException(InternalServerErrorException ex) {

    return toErrorApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }
}
