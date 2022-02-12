package com.mvpmatch.vendingmachine.resource;

import com.mvpmatch.vendingmachine.IntegrationTest;
import com.mvpmatch.vendingmachine.domain.Deposit;
import com.mvpmatch.vendingmachine.domain.Product;
import com.mvpmatch.vendingmachine.domain.Role;
import com.mvpmatch.vendingmachine.domain.User;
import com.mvpmatch.vendingmachine.repository.DepositRepository;
import com.mvpmatch.vendingmachine.repository.ProductsRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
@Transactional
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class VendingMachineResourceIntTests {

    User buyer = new User("buyer", "test", Collections.singletonList(new Role(Role.RoleType.BUYER)));
    User seller = new User("seller", "test", Collections.singletonList(new Role(Role.RoleType.SELLER)));

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc restAccountMockMvc;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private DepositRepository depositRepository;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

    }

    @Test
    void testNonAuthenticatedUser() throws Exception {
        restAccountMockMvc
                .perform(get("/api/vendingmachine").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void testDepositNotAuthorized() throws Exception {
        restAccountMockMvc
                .perform(put("/api/vendingmachine/deposit")
                        .content("{\"depositAmount\":10}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testDepositRoleSeller_wrongCoin() throws Exception {
        restAccountMockMvc
                .perform(
                        put("/api/vendingmachine/deposit")
                                .content("{\"depositAmount\":14}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(user(seller))
                )

                .andExpect(status().isForbidden())
                .andExpect(content().string("{\"message\":\"You don't have the correct role for this operation.\",\"details\":[\"Access is denied\"]}"));
    }

    @Test
    void testDepositRoleBuyer_wrongCoin() throws Exception {
        restAccountMockMvc
                .perform(
                        put("/api/vendingmachine/deposit")
                                .content("{\"depositAmount\":14}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(user(buyer))
                )

                .andExpect(status().isOk())
                .andExpect(content().string("You cannot insert coins with value other than 5, 10, 20, 50 and 100"));
    }

    @Test
    void testDepositRoleBuyer_goodCoin() throws Exception {
        restAccountMockMvc
                .perform(
                        put("/api/vendingmachine/deposit")
                                .content("{\"depositAmount\":10}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(user(buyer))
                )

                .andExpect(status().isOk())
                .andExpect(content().string("Coins accepted. You now have 10"));
    }

    @Test
    void testDepositRoleBuyer_goodCoin1_deposit10_twice_total_20() throws Exception {
        restAccountMockMvc
                .perform(
                        put("/api/vendingmachine/deposit")
                                .content("{\"depositAmount\":10}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(user(buyer))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Coins accepted. You now have 10"));
        restAccountMockMvc
                .perform(
                        put("/api/vendingmachine/deposit")
                                .content("{\"depositAmount\":10}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(user(buyer))
                )

                .andExpect(status().isOk())
                .andExpect(content().string("Coins accepted. You now have 20"));
    }

    @Test
    @Transactional
    void testBuyRoleBuyer_buy() throws Exception {
        buyer.setId(1L);

        Product newProduct = new Product();
        newProduct.setAmountAvailable(10);
        newProduct.setName("Coca - Cola");
        newProduct.setCost(BigDecimal.valueOf(10));
        newProduct = productsRepository.save(newProduct);

        Deposit entity = new Deposit();
        entity.setDepositAmount(BigDecimal.valueOf(20));
        entity.setUserId(1L);
        depositRepository.save(entity);

        restAccountMockMvc
                .perform(
                        post("/api/vendingmachine/buy")
                                .content("{\"productId\":" + newProduct.getId() + ", \"amount\":1}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(user(buyer))
                )

                .andExpect(status().isOk())
                .andExpect(content().string("{\"totalSpent\":10,\"purchasedProduct\":\"Coca - Cola\",\"depositedAmountBeforePurchase\":20,\"yourChangeInCents\":10,\"yourChange\":{\"CENTS_5\":0,\"CENTS_10\":1,\"CENTS_20\":0,\"CENTS_50\":0,\"CENTS_100\":0}}"));
    }

    @Test
    @Transactional
    void testBuyRoleBuyer_buy_negative_amount() throws Exception {
        buyer.setId(1L);

        Product newProduct = new Product();
        newProduct.setAmountAvailable(10);
        newProduct.setName("Coca - Cola");
        newProduct.setCost(BigDecimal.valueOf(10));
        newProduct = productsRepository.save(newProduct);

        Deposit entity = new Deposit();
        entity.setDepositAmount(BigDecimal.valueOf(20));
        entity.setUserId(1L);
        depositRepository.save(entity);

        restAccountMockMvc
                .perform(
                        post("/api/vendingmachine/buy")
                                .content("{\"productId\":" + newProduct.getId() + ", \"amount\":-1}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(user(buyer))
                )

                .andExpect(status().is4xxClientError())
                .andExpect(content().string("{\"message\":\"Something wrong with your request\",\"details\":[\"You cannot buy negative amount of products\"]}"));
    }

    @Test
    @Transactional
    void testBuyRoleBuyer_buy_product_not_existing() throws Exception {
        buyer.setId(1L);

        Product newProduct = new Product();
        newProduct.setAmountAvailable(10);
        newProduct.setName("Coca - Cola");
        newProduct.setCost(BigDecimal.valueOf(10));
        newProduct = productsRepository.save(newProduct);

        Deposit entity = new Deposit();
        entity.setDepositAmount(BigDecimal.valueOf(20));
        entity.setUserId(1L);
        depositRepository.save(entity);

        restAccountMockMvc
                .perform(
                        post("/api/vendingmachine/buy")
                                .content("{\"productId\":4, \"amount\":-1}")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(user(buyer))
                )

                .andExpect(status().is4xxClientError())
                .andExpect(content().string("{\"message\":\"Something wrong with your request\",\"details\":[\"You cannot buy negative amount of products\"]}"));
    }
}
