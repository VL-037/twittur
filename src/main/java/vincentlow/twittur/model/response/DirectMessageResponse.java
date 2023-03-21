package vincentlow.twittur.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vincentlow.twittur.model.constant.DirectMessageStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectMessageResponse extends BaseResponse {

  private static final long serialVersionUID = 4952082994489931280L;

  private String senderId;

  private String recipientId;

  private String message;

  private DirectMessageStatus status;
}
