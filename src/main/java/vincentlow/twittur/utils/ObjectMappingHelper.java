package vincentlow.twittur.utils;

import java.util.Objects;

import vincentlow.twittur.model.response.BaseResponse;

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
}
