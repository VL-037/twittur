package vincentlow.twittur.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import vincentlow.twittur.model.entity.Account;

public class CacheServiceImplTest {

  private final String KEY = "vincentlow.twittur";

  private final String CACHE_DATA = "CACHE_DATA";

  private final Long TTL = 600L;

  private final String KEY_PATTERN = "KEY_PATTERN";

  @InjectMocks
  private CacheServiceImpl cacheService;

  @Mock
  private StringRedisTemplate stringRedisTemplate;

  @Mock
  private ObjectMapper objectMapper;

  private Account account;

  private Set<String> keys;

  private ValueOperations<String, String> valueOperations;

  @BeforeEach
  void setUp() throws JsonProcessingException {

    openMocks(this);

    account = new Account();

    keys = new HashSet<>();

    valueOperations = mock(ValueOperations.class);
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(KEY)).thenReturn(CACHE_DATA);
    when(objectMapper.readValue(eq(CACHE_DATA), any(TypeReference.class))).thenReturn(account);

    when(objectMapper.writeValueAsString(account)).thenReturn(CACHE_DATA);
    doNothing().when(valueOperations)
        .set(KEY, CACHE_DATA, TTL, TimeUnit.SECONDS);
    when(stringRedisTemplate.keys(KEY_PATTERN)).thenReturn(keys);
  }

  @AfterEach
  void tearDown() {

    verifyNoMoreInteractions(stringRedisTemplate, objectMapper);
  }

  @Test
  void get() throws JsonProcessingException {

    Account result = cacheService.get(KEY, new TypeReference<>() {});

    verify(stringRedisTemplate).opsForValue();
    verify(objectMapper).readValue(eq(CACHE_DATA), any(TypeReference.class));

    assertNotNull(result);
  }

  @Test
  void set() throws JsonProcessingException {

    cacheService.set(KEY, account, null);

    verify(objectMapper).writeValueAsString(account);
    verify(stringRedisTemplate).opsForValue();
  }

  @Test
  void deleteByPattern() {

    cacheService.deleteByPattern(KEY_PATTERN);

    verify(stringRedisTemplate).keys(KEY_PATTERN);
    verify(stringRedisTemplate).delete(keys);
  }
}
