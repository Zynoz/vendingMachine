package com.mvpmatch.vendingmachine.session;

import com.mvpmatch.vendingmachine.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public final class SessionService {

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
