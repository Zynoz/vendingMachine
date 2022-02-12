package com.mvpmatch.vendingmachine.session;

import com.mvpmatch.vendingmachine.domain.User;
import com.mvpmatch.vendingmachine.dto.UserDTO;
import com.mvpmatch.vendingmachine.security.JwtTokenFilter;
import com.mvpmatch.vendingmachine.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class SessionService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Tries to log in a user.
     *
     * Will throw BadCredentialsException if the user inserted wrong credentials.
     * This exception will be handled in the GlobalExceptionHandler
     * @return The JWT token for the current session
     */
    public String loginUser(UserDTO request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = (User) authentication.getPrincipal();

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generateAccessToken(user);
        JwtTokenFilter.addLogin(jwt);
        return jwt;
    }

    public User getCurrentUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((User) authentication.getPrincipal());
    }

    /**
     * Clearing spring context for current user and invalidating session
     */
    public void logOut(HttpServletRequest request) {

        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(null);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
