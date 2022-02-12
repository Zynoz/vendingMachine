package com.mvpmatch.vendingmachine.mapper;

import com.mvpmatch.vendingmachine.domain.Product;
import com.mvpmatch.vendingmachine.dto.ProductDTO;
import com.mvpmatch.vendingmachine.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    @Autowired
    private SessionService sessionService;

    public ProductDTO mapToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();

        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setCostInCents(product.getCost());
        productDTO.setAmountAvailable(product.getAmountAvailable());

        if (product.getSeller() != null) {
            productDTO.setSellerId(product.getSeller().getId());
        }

        return productDTO;
    }

    public Product mapToModel(ProductDTO productDTO) {
        Product product = new Product();

        product.setName(productDTO.getName());
        product.setAmountAvailable(productDTO.getAmountAvailable());

        product.setCostInCents(productDTO);

        product.setSeller(sessionService.getCurrentUserLoggedIn());
        return product;
    }

}
