package vincentlow.twittur.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import vincentlow.twittur.model.constant.NotificationType;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationResponse extends BaseResponse {

  private String title;

  private String message;

  private String imageUrl;

  private String redirectUrl;

  private NotificationType type;

  private boolean hasRead;
}
