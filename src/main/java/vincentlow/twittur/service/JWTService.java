package vincentlow.twittur.service;

import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {

  String extractUsername(String token);

  String generateToken(UserDetails userDetails);

  String generateToken(Map<String, Object> extractClaims, UserDetails userDetails);

  boolean isTokenValid(String token, UserDetails userDetails);
}
