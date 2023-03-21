package vincentlow.twittur.model.request;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PushNotificationRequest implements Serializable {

  private static final long serialVersionUID = 6899624534853427253L;

  private String title;

  private String message;

  private String imageUrl;

  private String redirectUrl;
}
