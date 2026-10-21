
package com.spendwise.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Locale;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.equals("/api/auth/register")
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/admin-login");

    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            String email = jwtService.extractEmail(token);
            if (jwtService.isAdminToken(token)) {
                String configuredAdminEmail =
                        adminEmail.trim().toLowerCase(Locale.ROOT);

                if (!email.equalsIgnoreCase(configuredAdminEmail)
                        || !jwtService.isTokenValid(token, configuredAdminEmail)) {
                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "Invalid admin authentication token");
                    return;
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
                return;
            }

            if (email != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                UserDetails user =
                        userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(token, user.getUsername())) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user, null, user.getAuthorities());

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired authentication token");
            return;
        } catch (org.springframework.security.core.userdetails
                         .UsernameNotFoundException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid authentication token");
            return;
        }

        filterChain.doFilter(request, response);
    }
    @Value("${app.admin.email}")
    private String adminEmail;
}
