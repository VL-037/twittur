package vincentlow.twittur.model.request;

import java.io.Serializable;
import java.sql.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateAccountRequest implements Serializable {

  private static final long serialVersionUID = 1742974303828086822L;

  private String firstName;

  private String lastName;

  private Date dateOfBirth;

  private String username;

  private String accountName;

  private String bio;

  private String emailAddress;
}
