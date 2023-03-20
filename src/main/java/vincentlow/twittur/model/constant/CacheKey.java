package vincentlow.twittur.model.constant;

public interface CacheKey {

  String BASE_KEY = "vincentlow.twittur:";

  String FIND_ONE_ACCOUNT = BASE_KEY + "findOneAccount-%s"; // username | emailAddress

  String FIND_ONE_ACCOUNT_PATTERN = BASE_KEY + "findOneAccount-*";

  String FIND_ALL_ACCOUNTS = BASE_KEY + "findAllAccounts-%s-%s"; // pageNumber-pageSize

  String FIND_ALL_ACCOUNTS_PATTERN = BASE_KEY + "findAllAccounts-*";

  String FIND_ACCOUNT_FOLLOWERS = BASE_KEY + "findAccountFollowers-%s"; // accountId
  String FIND_ACCOUNT_FOLLOWERS_PATTERN = BASE_KEY + "findAccountFollowers-*";

  String FIND_ACCOUNT_FOLLOWING = BASE_KEY + "findAccountFollowing-%s"; // accountId
  String FIND_ACCOUNT_FOLLOWING_PATTERN = BASE_KEY + "findAccountFollowing-*"; // accountId
}
