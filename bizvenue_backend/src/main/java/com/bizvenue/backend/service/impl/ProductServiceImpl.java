package com.bizvenue.backend.service.impl;

import com.bizvenue.backend.dto.ProductDTO;
import com.bizvenue.backend.entity.Company;
import com.bizvenue.backend.entity.Product;
import com.bizvenue.backend.mapper.EntityMapper;
import com.bizvenue.backend.repository.CompanyRepository;
import com.bizvenue.backend.repository.ProductRepository;
import com.bizvenue.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final EntityMapper mapper;

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(mapper::toProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByCompany(int companyId) {
        return productRepository.findByCompanyId(companyId).stream()
                .map(mapper::toProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(int id) {
        return productRepository.findById(id)
                .map(mapper::toProductDTO)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public ProductDTO createProduct(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setStatus(dto.getStatus());
        product.setRevenue(dto.getRevenue() != null ? dto.getRevenue() : java.math.BigDecimal.ZERO);
        product.setActiveSubscriptions(dto.getActiveSubscriptions() != null ? dto.getActiveSubscriptions() : 0);

        System.out.println("Creating product: " + dto.getName() + ", CompanyID: " + dto.getCompanyId());

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company with ID " + dto.getCompanyId() + " not found"));
        product.setCompany(company);

        Product saved = productRepository.saveAndFlush(product);
        System.out.println("Product saved with ID: " + saved.getId());
        return mapper.toProductDTO(saved);
    }

    @Override
    public ProductDTO updateProduct(int id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setStatus(dto.getStatus());
        product.setRevenue(dto.getRevenue());
        product.setActiveSubscriptions(dto.getActiveSubscriptions());

        if (dto.getCompanyId() != product.getCompany().getId()) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            product.setCompany(company);
        }

        Product saved = productRepository.saveAndFlush(product);
        return mapper.toProductDTO(saved);
    }

    @Override
    public void deleteProduct(int id) {
        productRepository.deleteById(id);
    }
}
