package vincentlow.twittur.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import vincentlow.twittur.model.constant.Role;

public class JWTServiceImplTest {

  private String JWT_SECRET_KEY = "792F423F4528482B4D6251655368566D597133743677397A24432646294A404E";

  private final String USERNAME = "USERNAME";

  private final String INVALID_USERNAME = "INVALID_USERNAME";

  private final String PASSWORD = "PASSWORD";

  private final String CLAIM_KEY = "CLAIM_KEY";

  private final String CLAIM_VALUE = "CLAIM_VALUE";

  private final Date ISSUED_AT = new Date(System.currentTimeMillis());

  private final Date EXPIRATION_DATE = new Date(System.currentTimeMillis() + (1000 * 60 * 60));

  @InjectMocks
  private JWTServiceImpl jwtService;

  private UserDetails userDetails;

  private String token;

  private Claims claims;

  private Map<String, Object> extractClaims;

  private String createToken(String username) {

    return createToken(username, EXPIRATION_DATE);
  }

  private String createToken(String username, Date expirationDate) {

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(ISSUED_AT)
        .setExpiration(expirationDate)
        .signWith(getSignInKey())
        .compact();
  }

  private Key getSignInKey() {

    byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @BeforeEach
  void setUp() throws Exception {

    openMocks(this);

    Field field = jwtService.getClass()
        .getDeclaredField("jwtSecretKey");
    field.setAccessible(true);
    field.set(jwtService, JWT_SECRET_KEY);

    token = createToken(USERNAME);

    claims = Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    userDetails = User.withUsername(USERNAME)
        .password(PASSWORD)
        .roles(Role.USER.name())
        .build();

    extractClaims = new HashMap<>();
    extractClaims.put(CLAIM_KEY, CLAIM_VALUE);
  }

  @Test
  void extractUsername() {

    String result = jwtService.extractUsername(token);

    assertEquals(USERNAME, result);
  }

  @Test
  void extractClaim() {

    String subject = jwtService.extractClaim(token, Claims::getSubject);

    assertEquals(claims.getSubject(), subject);
  }

  @Test
  void generateToken() {

    String result = jwtService.generateToken(userDetails);

    assertNotNull(result);
  }

  @Test
  void generateToken_WithClaims() {

    String result = jwtService.generateToken(extractClaims, userDetails);

    assertNotNull(result);
  }

  @Test
  void isTokenValid_validToken() {

    boolean result = jwtService.isTokenValid(token, userDetails);

    assertTrue(result);
  }

  @Test
  void isTokenValid_invalidToken() {

    token = createToken(INVALID_USERNAME);

    boolean result = jwtService.isTokenValid(token, userDetails);

    assertFalse(result);
  }
}
