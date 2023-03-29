package vincentlow.twittur.utils;

import java.util.Objects;

import org.springframework.beans.BeanUtils;

import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.model.entity.DirectMessage;
import vincentlow.twittur.model.response.BaseResponse;
import vincentlow.twittur.model.response.DirectMessageResponse;

@Slf4j
public class ObjectMappingHelper {

  public static <S, T extends BaseResponse> T toResponse(S source, Class<T> targetClass) {

    if (Objects.isNull(source)) {
      return null;
    }
    try {
      return ObjectMapper.map(source, targetClass);
    } catch (Exception e) {
      log.error("#toResponse ERROR! with source: {}, targetClass: {}, and error: {}", source, targetClass,
          e.getMessage(), e);
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
