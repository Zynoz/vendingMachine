package com.mvpmatch.vendingmachine.resource;

import com.mvpmatch.vendingmachine.domain.User;
import com.mvpmatch.vendingmachine.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomepageResource {

    @Autowired
    private DepositService depositService;

    @GetMapping
    public String hello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return String.format("Hello %s. You now have %s deposit amount",
                    ((User) authentication.getPrincipal()).getUsername(),
                    depositService.getCurrentUserDepositedAmount());
        }
        return "Welcome to vending machine application. Login first by using POST method to /api/user/login";
    }
}
