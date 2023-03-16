package vincentlow.twittur.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class StringUtil {

  public static void trimStrings(Object object) {

    if (Objects.isNull(object)) {
      return;
    }

    Class<?> clazz = object.getClass();
    Field[] fields = clazz.getDeclaredFields();

    Arrays.stream(fields)
        .filter(field -> field.getType() == String.class)
        .forEach(field -> {
          try {
            field.setAccessible(true);
            String fieldValue = (String) field.get(object);

            if (fieldValue != null) {
              String trimmedFieldValue = fieldValue.trim();
              field.set(object, trimmedFieldValue);
            }
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        });
  }
}
