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
public class AccountRelationshipRequest implements Serializable {

  private static final long serialVersionUID = 3698874953445224283L;

  private String followedId;

  private String followerId;
}
