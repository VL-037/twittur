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
public class CreateTweetRequest implements Serializable {

  private static final long serialVersionUID = 735161378073457820L;

  private String message;
}
