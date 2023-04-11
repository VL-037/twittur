package vincentlow.twittur.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {

  String extractUsername(String token);

  String generateAccessToken(UserDetails userDetails);

  String generateRefreshToken(UserDetails userDetails);

  boolean isTokenValid(String token, UserDetails userDetails);
}
