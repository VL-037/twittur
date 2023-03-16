package vincentlow.twittur.utils;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import vincentlow.twittur.model.entity.Account;

public class ObjectMapper {

  public static <S, T> T map(S source, Class<T> targetClass) {

    MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    mapperFactory.classMap(Account.class, targetClass)
        .exclude("tweets")
        .byDefault()
        .register();

    MapperFacade mapper = mapperFactory.getMapperFacade();

    return mapper.map(source, targetClass);
  }
}
