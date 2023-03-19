package vincentlow.twittur.model.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountRequest implements Serializable {

  private static final long serialVersionUID = 1742974303828086822L;

  private String username;

  private String accountName;

  private String bio;

  private String emailAddress;

  private String phoneNumber;

  private String oldPassword;

  private String newPassword;

  private String confirmNewPassword;
}
