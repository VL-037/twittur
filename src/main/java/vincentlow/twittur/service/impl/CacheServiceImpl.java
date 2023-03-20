package vincentlow.twittur.service.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import vincentlow.twittur.service.CacheService;

@Service
public class CacheServiceImpl implements CacheService {

  @Value("${cache.default.ttl}")
  private long DEFAULT_TTL;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public <T> T get(String key, TypeReference<T> typeRef) {

    try {
      String cacheData = stringRedisTemplate.opsForValue()
          .get(key);
      if (Objects.nonNull(cacheData)) {
        return objectMapper.readValue(cacheData, typeRef);
      }
    } catch (Exception e) {
    }
    return null;
  }

  @Override
  public <T> void set(String key, T data, Long ttl) {

    try {
      if (Objects.nonNull(data)) {
        String valueString = this.objectMapper.writeValueAsString(data);
        stringRedisTemplate.opsForValue()
            .set(key, valueString, getTTL(ttl), TimeUnit.SECONDS);
      }
    } catch (Exception e) {
    }
  }

  @Override
  public void deleteByPattern(String pattern) {

    Set<String> keys = stringRedisTemplate.keys(pattern);
    stringRedisTemplate.delete(keys);
  }

  private long getTTL(Long ttl) {

    return Optional.ofNullable(ttl)
        .orElse(DEFAULT_TTL);
  }
}
