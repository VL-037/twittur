package vincentlow.twittur.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountResponse extends BaseResponse {

  private static final long serialVersionUID = -3849267693758029874L;

  private String username;

  private String accountName;

  private String bio;

  private int tweetsCount;
}
