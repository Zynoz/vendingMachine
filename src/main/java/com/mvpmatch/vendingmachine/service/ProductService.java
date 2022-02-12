package com.mvpmatch.vendingmachine.service;


import com.mvpmatch.vendingmachine.domain.Product;
import com.mvpmatch.vendingmachine.domain.User;
import com.mvpmatch.vendingmachine.dto.ProductDTO;
import com.mvpmatch.vendingmachine.dto.PurchaseDTO;
import com.mvpmatch.vendingmachine.dto.ReceiptDTO;
import com.mvpmatch.vendingmachine.exception.BadRequestException;
import com.mvpmatch.vendingmachine.mapper.ProductMapper;
import com.mvpmatch.vendingmachine.repository.DepositRepository;
import com.mvpmatch.vendingmachine.repository.ProductsRepository;
import com.mvpmatch.vendingmachine.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private ChangeService changeService;

    @Autowired
    private DepositService depositService;

    @Autowired
    private SessionService sessionService;

    public List<ProductDTO> findAll() {
        return productsRepository.findAll()
                .stream()
                .map(productMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Selling a product is not allowed for values that are not multiply of 5
     */
    public ProductDTO sellProduct(ProductDTO productDTO) throws BadRequestException {
        if (productDTO.getCostInCents().divideAndRemainder(BigDecimal.valueOf(5, BigDecimal.ROUND_CEILING))[1].compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("You cannot sell a product that is not a multiply of 5");
        }

        return productMapper.mapToDTO(
                productsRepository.save(productMapper.mapToModel(productDTO))
        );
    }

    /**
     * Updating a product
     *
     * @throws BadRequestException if the product does not exist or the seller is not allowed to update this product
     */
    public ProductDTO update(ProductDTO productDTO) throws BadRequestException {
        Product theProduct = productExistAndIsOfCurrentUser(productDTO);
        if (productDTO.getName() != null) {
            theProduct.setName(productDTO.getName());
        }

        if (productDTO.getCostInCents() != null) {
            theProduct.setCostInCents(productDTO);
        }

        return productMapper.mapToDTO(productsRepository.save(theProduct));
    }

    /**
     * Deletes a product
     *
     * @throws BadRequestException if the product does not exist or the seller is not allowed to delete
     */
    public void delete(ProductDTO productDTO) throws BadRequestException {
        Product theProduct = productExistAndIsOfCurrentUser(productDTO);

        productsRepository.delete(theProduct);
    }

    private Product productExistAndIsOfCurrentUser(ProductDTO productDTO) throws BadRequestException {
        Optional<Product> product = productsRepository.findById(productDTO.getId());
        if (!product.isPresent()) {
            throw new BadRequestException("You have no product with this ID");
        }

        User currentUserLoggedIn = sessionService.getCurrentUserLoggedIn();
        if (product.get().getSeller().getId() != currentUserLoggedIn.getId()) {
            throw new BadRequestException("You cannot update or delete a product that you are not selling");
        }

        return product.get();
    }

    /**
     * Buying a product and returning the change
     */
    public ReceiptDTO buyProducts(PurchaseDTO purchaseDTO) throws BadRequestException {

        Optional<Product> byId = productsRepository.findById(purchaseDTO.getProductId());
        if (!byId.isPresent()) {
            throw new BadRequestException(String.format("The product with id %s does not exist", purchaseDTO.getProductId()));
        }

        Product product = byId.get();
        if (product.getAmountAvailable() < purchaseDTO.getAmount()) {
            throw new BadRequestException("The vending machine doesn't have so many available products. Max you can buy is " + product.getAmountAvailable());
        }

        BigDecimal totalSpent = product.getCost()
                .multiply(BigDecimal.valueOf(purchaseDTO.getAmount()));

        BigDecimal depositedAmount = depositService.getCurrentUserDepositedAmount();
        if (depositedAmount.compareTo(totalSpent) < 0) {
            throw new BadRequestException(
                    String.format("You don't have enough money deposited. Total cost would be %s but you have %s",
                            totalSpent, depositedAmount)
            );
        }

        updateProductAmount(purchaseDTO, product);

        BigDecimal valueLeftInDeposit = depositService.substractFromUserDeposit(totalSpent);

        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setDepositedAmountBeforePurchase(depositedAmount);
        receiptDTO.setPurchasedProduct(product.getName());
        receiptDTO.setTotalSpent(totalSpent);
        changeService.setChangeOnReceipt(receiptDTO, valueLeftInDeposit);

        return receiptDTO;

    }

    private void updateProductAmount(PurchaseDTO purchaseDTO, Product product) {
        product.setAmountAvailable(product.getAmountAvailable() - purchaseDTO.getAmount());

        productsRepository.save(product);
    }

    public void deleteProductsForSellerWithId(long id) {
        productsRepository.deleteBySellerId(id);
    }
}
