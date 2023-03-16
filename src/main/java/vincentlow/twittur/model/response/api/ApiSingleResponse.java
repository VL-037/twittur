package vincentlow.twittur.model.response.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiSingleResponse<T> extends ApiResponse {

  private static final long serialVersionUID = 2197712303730261146L;

  private T value;

  @Override
  public String toString() {

    return "SingleResponse{" +
        "value=" + value +
        '}';
  }
}
