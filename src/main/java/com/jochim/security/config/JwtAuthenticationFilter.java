package com.jochim.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
// 2. OncePerRequestFilter is applied
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        // check if jwt is present
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            // 3. if token is missing, return 403
            filterChain.doFilter(request, response);
            return;
        }
        // extract the token value
        jwt = authHeader.split(" ")[1].trim();
        userEmail = jwtService.extractUsername(jwt);

        // if the user is not authenticated yet
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null ){
            // 4. get user details from dockerized database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            // 5. validate the JWT with the user details
            if(jwtService.isTokenValid(jwt, userDetails)){
                // 6. update SecurityContextHolder
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 3. if token is missing, return 403
        filterChain.doFilter(request, response);
    }
}
