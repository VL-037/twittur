package vincentlow.twittur.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountFollowerResponse extends BaseResponse {

  private static final long serialVersionUID = 1317696345227734355L;

  private String username;

  private String accountName;
}
