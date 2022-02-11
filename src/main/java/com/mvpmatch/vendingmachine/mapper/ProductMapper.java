package com.mvpmatch.vendingmachine.mapper;

import com.mvpmatch.vendingmachine.domain.Product;
import com.mvpmatch.vendingmachine.dto.ProductDTO;
import com.mvpmatch.vendingmachine.repository.UserRepository;
import com.mvpmatch.vendingmachine.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProductMapper {

    public static final BigDecimal DOLAR_TO_CENT_CONVERTION_RATE = BigDecimal.valueOf(100);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionService sessionService;

    public ProductDTO mapToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();

        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setCostInCents(product.getCost());
        productDTO.setAmountAvailable(product.getAmountAvailable());

        if (product.getSeller() != null){
            productDTO.setSellerId(product.getSeller().getId());
        }

        return productDTO;
    }

    public Product mapToModel(ProductDTO productDTO){
        Product product = new Product();

        product.setName(productDTO.getName());
        product.setAmountAvailable(productDTO.getAmountAvailable());

        setCostInCents(productDTO, product);

        product.setSeller(sessionService.getCurrentUserLoggedIn());
        return product;
    }

    private void setCostInCents(ProductDTO productDTO, Product product) {

        BigDecimal costInCents = productDTO.getCostInCents().setScale(2, RoundingMode.CEILING);
        if(costInCents.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) > 0){
            costInCents = costInCents.multiply(DOLAR_TO_CENT_CONVERTION_RATE);
        }
        product.setCost(costInCents);
    }

    public void update(Product productToUpdate, ProductDTO update) {
        if(update.getName() != null) {
            productToUpdate.setName(update.getName());
        }

        if(update.getCostInCents() != null) {
            setCostInCents(update, productToUpdate);
        }
    }
}
