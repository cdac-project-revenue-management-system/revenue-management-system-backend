package com.bizvenue.backend.service;

import com.bizvenue.backend.dto.ProductDTO;
import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts();

    List<ProductDTO> getProductsByCompany(int companyId);

    ProductDTO getProductById(int id);

    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO updateProduct(int id, ProductDTO productDTO);

    void deleteProduct(int id);
}
