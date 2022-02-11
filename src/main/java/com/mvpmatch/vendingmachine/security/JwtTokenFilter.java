package com.mvpmatch.vendingmachine.security;

import com.mvpmatch.vendingmachine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

/**
 * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
 * found.
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;


    private static Map<String, List<String>> userSessions = new HashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (ObjectUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String token = header.split(" ")[1].trim();

        // Get jwt token and validate
        String username = jwtTokenUtil.getUsername(token);
        if (!jwtTokenUtil.validate(token) || userSessions.get(username) == null || userSessions.get(username).isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        if(request.getRequestURI().contains("logout")){
            userSessions.get(username).clear();
        }

        // Get user identity and set it on the spring security context
        UserDetails userDetails = userRepository
                .findByUsername(username)
                .orElse(null);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                ofNullable(userDetails)
                        .map(UserDetails::getAuthorities)
                        .orElse(new ArrayList<>())
        );

        authentication
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    public static void addLogin(String token){
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        userSessions.computeIfAbsent(jwtTokenUtil.getUsername(token), f ->  new ArrayList<>());
        userSessions.get(jwtTokenUtil.getUsername(token)).add(token);
    }
}
