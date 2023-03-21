package vincentlow.twittur.utils;

import java.util.Objects;

import org.springframework.beans.BeanUtils;

import vincentlow.twittur.model.entity.DirectMessage;
import vincentlow.twittur.model.response.BaseResponse;
import vincentlow.twittur.model.response.DirectMessageResponse;

public class ObjectMappingHelper {

  public static <S, T extends BaseResponse> T toResponse(S source, Class<T> targetClass) {

    if (Objects.isNull(source)) {
      return null;
    }
    try {
      return ObjectMapper.map(source, targetClass);
    } catch (Exception e) {
      return null;
    }
  }

  public static DirectMessageResponse toDirectMessageResponse(DirectMessage directMessage) {

    DirectMessageResponse response = DirectMessageResponse.builder()
        .senderId(directMessage.getSender()
            .getId())
        .recipientId(directMessage.getRecipient()
            .getId())
        .build();
    BeanUtils.copyProperties(directMessage, response);

    return response;
  }
}
