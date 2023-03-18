package vincentlow.twittur.model.response.exception;

public class ServiceUnavailableException extends RuntimeException {

  public ServiceUnavailableException(String message) {

    super(message);
  }
}
