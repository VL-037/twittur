package vincentlow.twittur.utils;

import java.util.Objects;

import com.google.common.base.Preconditions;

import vincentlow.twittur.model.constant.ExceptionMessage;
import vincentlow.twittur.model.entity.Account;
import vincentlow.twittur.model.response.exception.BadRequestException;
import vincentlow.twittur.model.response.exception.NotFoundException;

public class ValidatorUtil {

  public static void validateArgument(boolean expression, String errorMessage) {

    Preconditions.checkArgument(expression, errorMessage);
  }

  public static void validateState(boolean expression, String errorMessage) {

    Preconditions.checkState(expression, errorMessage);
  }

  public static void validatePageableRequest(int pageNumber, int pageSize) {

    if (pageNumber < 0) {
      throw new BadRequestException(ExceptionMessage.PAGE_NUMBER_MUST_BE_AT_LEAST_0);
    } else if (pageSize < 1 || pageSize > 100) {
      throw new BadRequestException(ExceptionMessage.PAGE_SIZE_MUST_BE_BETWEEN_1_AND_100);
    }
  }

  public static Account validateAccount(Account account) {

    if (Objects.isNull(account)) {
      throw new NotFoundException(ExceptionMessage.ACCOUNT_NOT_FOUND);
    }
    return account;
  }
}
