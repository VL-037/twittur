package vincentlow.twittur.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TweetResponse extends BaseResponse {

  private String message;
}
