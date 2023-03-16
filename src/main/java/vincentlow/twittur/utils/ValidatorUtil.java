package vincentlow.twittur.utils;

import com.google.common.base.Preconditions;

public class ValidatorUtil {

  public static void validateArgument(boolean expression, String errorMessage) {

    Preconditions.checkArgument(expression, errorMessage);
  }

  public static void validateState(boolean expression, String errorMessage) {

    Preconditions.checkState(expression, errorMessage);
  }
}
