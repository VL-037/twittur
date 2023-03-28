package vincentlow.twittur.model.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vincentlow.twittur.model.constant.NotificationType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationRequest implements Serializable {

  private static final long serialVersionUID = 6899624534853427253L;

  private String senderId;

  private String title;

  private String message;

  private String imageUrl;

  private NotificationType type;

  private String redirectUrl;
}
