package com.mvpmatch.vendingmachine.resource;

import com.mvpmatch.vendingmachine.domain.Role;
import com.mvpmatch.vendingmachine.dto.UserDTO;
import com.mvpmatch.vendingmachine.exception.BadRequestException;
import com.mvpmatch.vendingmachine.exception.LoginAlreadyUsedException;
import com.mvpmatch.vendingmachine.service.ProductService;
import com.mvpmatch.vendingmachine.service.UserService;
import com.mvpmatch.vendingmachine.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SessionService sessionService;

    @GetMapping
    @RolesAllowed(Role.ADMIN)
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody @Valid UserDTO request) {
        String jwt = sessionService.loginUser(request);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        return new ResponseEntity<>(jwt, httpHeaders, HttpStatus.OK);
    }


    @PostMapping("logout/all")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        sessionService.logOut(request);
        return new ResponseEntity<>("You were logged out", HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserDTO userDTO) throws BadRequestException {
        LOGGER.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new BadRequestException("A new user cannot already have an ID");
        } else if (userService.getByUsername(userDTO.getUsername().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        }

        return ResponseEntity.accepted().body(userService.createUser(userDTO));
    }

    @DeleteMapping("delete")
    @Transactional
    public ResponseEntity<String> deleteMyUser(HttpServletRequest request) {
        LOGGER.debug("REST request to delete current user");

        long id = sessionService.getCurrentUserLoggedIn().getId();
        productService.deleteProductsForSellerWithId(id);
        userService.deleteUser(id);
        sessionService.logOut(request);

        return new ResponseEntity<>("Your user was removed and all the products were deleted", HttpStatus.OK);
    }

}