package vincentlow.twittur.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.response.exception.ServiceUnavailableException;

@Slf4j
public class StringUtil {

  public static void trimStrings(Object object) {

    Class<?> clazz = object.getClass();
    Field[] fields = clazz.getDeclaredFields();

    Arrays.stream(fields)
        .filter(field -> field.getType() == String.class)
        .forEach(field -> {
          try {
            field.setAccessible(true);
            String fieldValue = (String) field.get(object);

            if (Objects.nonNull(fieldValue)) {
              String trimmedFieldValue = fieldValue.trim();
              field.set(object, trimmedFieldValue);
            }
          } catch (IllegalAccessException e) {
            log.error("#trimStrings ERROR! with object: {}, and error: {}", object, e.getMessage(), e);
            throw new ServiceUnavailableException(ExceptionMessage.SERVICE_TEMPORARILY_UNAVAILABLE);
          }
        });
  }
}
