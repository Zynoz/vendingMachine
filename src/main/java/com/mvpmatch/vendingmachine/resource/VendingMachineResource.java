package com.mvpmatch.vendingmachine.resource;

import com.mvpmatch.vendingmachine.domain.Role;
import com.mvpmatch.vendingmachine.dto.DepositDTO;
import com.mvpmatch.vendingmachine.dto.ProductDTO;
import com.mvpmatch.vendingmachine.dto.PurchaseDTO;
import com.mvpmatch.vendingmachine.dto.ReceiptDTO;
import com.mvpmatch.vendingmachine.enums.COINS;
import com.mvpmatch.vendingmachine.exception.BadRequestException;
import com.mvpmatch.vendingmachine.service.DepositService;
import com.mvpmatch.vendingmachine.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/vendingmachine")
public class VendingMachineResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(VendingMachineResource.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private DepositService depositService;

    private static final List<BigDecimal> allowedCoins = Arrays.asList(
            COINS.CENTS_5.getAmount(),
            COINS.CENTS_10.getAmount(),
            COINS.CENTS_20.getAmount(),
            COINS.CENTS_50.getAmount(),
            COINS.CENTS_100.getAmount()
    );

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return new ResponseEntity<>(productService.findAll(), HttpStatus.OK);
    }

    /**
     * Implement /deposit endpoint so users with a “buyer” role can deposit only 5, 10, 20,
     * 50 and 100 cent coins into their vending machine account (one coin at the time)
     */
    @RolesAllowed(Role.BUYER)
    @PutMapping("/deposit")
    public String depositSum(@RequestBody DepositDTO depositDTO) {
        if (!allowedCoins.contains(depositDTO.getDepositAmount())) {
            return "You cannot insert coins with value other than 5, 10, 20, 50 and 100";
        }

        BigDecimal amountCollected = depositService.depositForCurrentUser(depositDTO.getDepositAmount());

        return "Coins accepted. You now have " + amountCollected;
    }

    /**
     * API should return total they’ve
     * spent, the product they’ve purchased and their change if there’s any (in an array of
     * 5, 10, 20, 50 and 100 cent coins)
     */
    @RolesAllowed(Role.BUYER)
    @PostMapping("/buy")
    public ResponseEntity<ReceiptDTO> buyProduct(@RequestBody PurchaseDTO purchaseDTO) throws BadRequestException {
        if (purchaseDTO.getAmount() <= 0) {
            throw new BadRequestException("You cannot buy negative amount of products");
        }

        return new ResponseEntity<>(productService.buyProducts(purchaseDTO), HttpStatus.OK);
    }

    @RolesAllowed(Role.BUYER)
    @PostMapping("/reset")
    public ResponseEntity<String> resetDeposit() {
        return new ResponseEntity<>("Your deposit is reset to 0. You should have received your money back if you had any deposited\n" + depositService.resetForCurrentUser(), HttpStatus.OK);
    }


    @PostMapping
    @RolesAllowed(Role.SELLER)
    public String sellProduct(@RequestBody @Valid ProductDTO productDTO) throws BadRequestException {
        LOGGER.debug("Request for selling a product");

        ProductDTO savedProduct = productService.sellProduct(productDTO);

        LOGGER.debug("Product that was saved {}", savedProduct);

        return "Your product was saved\n" + savedProduct;

    }

    @PutMapping
    @RolesAllowed(Role.SELLER)
    public String updateProduct(@RequestBody @Valid ProductDTO productDTO) throws BadRequestException {
        LOGGER.debug("Request for updating a product");

        if (productDTO.getId() == null) {
            throw new BadRequestException("To update and item we need the id");
        }

        ProductDTO updatedProduct = productService.update(productDTO);

        LOGGER.info("Product was updated successfully {}", updatedProduct);

        return "Your product was updated\n" + updatedProduct;

    }

    @DeleteMapping
    @RolesAllowed(Role.SELLER)
    public String deleteProduct(@RequestBody @Valid ProductDTO productDTO) throws BadRequestException {
        LOGGER.debug("Request for deleting a product");

        if (productDTO.getId() == null) {
            throw new BadRequestException("To delete and item we need the Id");
        }

        productService.delete(productDTO);

        LOGGER.info("Product with id {} was deleted successfully", productDTO.getId());

        return String.format("You deleted successfully product with id %s", productDTO.getId());

    }

}
