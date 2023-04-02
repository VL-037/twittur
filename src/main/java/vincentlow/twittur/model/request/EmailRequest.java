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
public class EmailRequest implements Serializable {

  private static final long serialVersionUID = -6302253556959597791L;

  private String recipient;

  private String subject;

  private String body;
}
