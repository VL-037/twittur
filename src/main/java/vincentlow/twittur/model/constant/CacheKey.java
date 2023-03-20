package vincentlow.twittur.model.constant;

public interface CacheKey {

  String BASE_KEY = "vincentlow.twittur:";

  String FIND_ONE_ACCOUNT = BASE_KEY + "findOneAccount-%s"; // username | emailAddress

  String FIND_ONE_ACCOUNT_PATTERN = BASE_KEY + "findOneAccount-*";

  String FIND_ALL_ACCOUNTS = BASE_KEY + "findAllAccounts-%s-%s"; // pageNumber-pageSize

  String FIND_ALL_ACCOUNTS_PATTERN = BASE_KEY + "findAllAccounts-*";
}
