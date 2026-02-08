package com.bizvenue.backend.service.impl;

import com.bizvenue.backend.dto.PlanDTO;
import com.bizvenue.backend.entity.Plan;
import com.bizvenue.backend.entity.Product;
import com.bizvenue.backend.mapper.EntityMapper;
import com.bizvenue.backend.repository.PlanRepository;
import com.bizvenue.backend.repository.ProductRepository;
import com.bizvenue.backend.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final ProductRepository productRepository;
    private final EntityMapper mapper;

    @Override
    public List<PlanDTO> getAllPlans() {
        return planRepository.findAll().stream()
                .map(mapper::toPlanDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanDTO> getPlansByProduct(int productId) {
        return planRepository.findByProductId(productId).stream()
                .map(mapper::toPlanDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanDTO> getPlansByCompany(int companyId) {
        return planRepository.findByProductCompanyId(companyId).stream()
                .map(mapper::toPlanDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlanDTO getPlanById(int id) {
        return planRepository.findById(id)
                .map(mapper::toPlanDTO)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
    }

    @Override
    public PlanDTO createPlan(PlanDTO dto) {
        Plan plan = new Plan();
        plan.setName(dto.getName());
        plan.setPrice(dto.getPrice());
        plan.setInterval(dto.getInterval());
        plan.setFeatures(dto.getFeatures());
        plan.setIsPopular(dto.getIsPopular());
        plan.setStatus(dto.getStatus());

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        plan.setProduct(product);

        Plan saved = planRepository.save(plan);
        return mapper.toPlanDTO(saved);
    }

    @Override
    public PlanDTO updatePlan(int id, PlanDTO dto) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        plan.setName(dto.getName());
        plan.setPrice(dto.getPrice());
        plan.setInterval(dto.getInterval());
        plan.setFeatures(dto.getFeatures());
        plan.setIsPopular(dto.getIsPopular());
        plan.setStatus(dto.getStatus());

        if (dto.getProductId() != plan.getProduct().getId()) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            plan.setProduct(product);
        }

        Plan saved = planRepository.save(plan);
        return mapper.toPlanDTO(saved);
    }

    @Override
    public void deletePlan(int id) {
        planRepository.deleteById(id);
    }

    @Override
    public PlanDTO toggleStatus(int id, String status) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        try {
            plan.setStatus(com.bizvenue.backend.entity.enums.PlanStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }

        Plan saved = planRepository.save(plan);
        return mapper.toPlanDTO(saved);
    }
}
