package vincentlow.twittur.jwt;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.net.HttpHeaders;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vincentlow.twittur.service.JWTService;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

  private final String STARTS_WITH_BEARER = "Bearer "; // including space

  @Autowired
  private JWTService jwtService;

  @Autowired
  private UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (Objects.isNull(authHeader) || !authHeader.startsWith(STARTS_WITH_BEARER)) {
      filterChain.doFilter(request, response);
      return;
    }

    String jwt = authHeader.substring(STARTS_WITH_BEARER.length());
    String username = jwtService.extractUsername(jwt);

    if (Objects.nonNull(username) &&
        Objects.isNull(SecurityContextHolder.getContext()
            .getAuthentication())) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      if (jwtService.isTokenValid(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext()
            .setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}
