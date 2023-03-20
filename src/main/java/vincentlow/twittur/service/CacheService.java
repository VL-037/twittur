package vincentlow.twittur.service;

import com.fasterxml.jackson.core.type.TypeReference;

public interface CacheService {

  <T> T get(String key, TypeReference<T> typeRef);

  <T> void set(String key, T data, Long ttl);

  void deleteByPattern(String pattern);
}
