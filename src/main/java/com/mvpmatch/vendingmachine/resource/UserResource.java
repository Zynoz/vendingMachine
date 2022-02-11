package com.mvpmatch.vendingmachine.resource;

import com.mvpmatch.vendingmachine.domain.User;
import com.mvpmatch.vendingmachine.dto.UserDTO;
import com.mvpmatch.vendingmachine.exception.BadRequestException;
import com.mvpmatch.vendingmachine.exception.LoginAlreadyUsedException;
import com.mvpmatch.vendingmachine.repository.UserRepository;
import com.mvpmatch.vendingmachine.security.JwtTokenFilter;
import com.mvpmatch.vendingmachine.security.JwtTokenUtil;
import com.mvpmatch.vendingmachine.service.ProductService;
import com.mvpmatch.vendingmachine.service.UserService;
import com.mvpmatch.vendingmachine.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SessionService sessionService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody @Valid UserDTO request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            User user = (User) authentication.getPrincipal();

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenUtil.generateAccessToken(user);
            JwtTokenFilter.addLogin(jwt);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
            return new ResponseEntity<>(jwt, httpHeaders, HttpStatus.OK);
        } catch (BadCredentialsException ex) {
            return new ResponseEntity<>("The login you are trying to make does not exist or the credentials are not correct", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("logout/all")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        sessionService.logOut(request);
        return new ResponseEntity<>("You were logged out", HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException, BadRequestException {
        LOGGER.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new BadRequestException("A new user cannot already have an ID");
            // Lowercase the user login before comparing with database
        } else if (userRepository.findByUsername(userDTO.getUsername().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        }

        User newUser = userService.createUser(userDTO);
        return ResponseEntity.accepted().body(newUser);
    }

    @DeleteMapping("delete")
    @Transactional
    public ResponseEntity<String> deleteMYUser(HttpServletRequest request) throws URISyntaxException, BadRequestException {
        LOGGER.debug("REST request to delete current user");

        long id = sessionService.getCurrentUserLoggedIn().getId();
        productService.deleteProductsForSellerWithId(id);
        userRepository.deleteById(id);
        sessionService.logOut(request);

        return new ResponseEntity<>("Your user was removed and all the products were deleted", HttpStatus.OK);
    }

}