package vincentlow.twittur.model.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // needed when req field = 1
@AllArgsConstructor
public class UpdateTweetRequest implements Serializable {

  private static final long serialVersionUID = 1268844290279042120L;

  private String message;
}
