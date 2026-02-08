package com.bizvenue.backend.service;

import com.bizvenue.backend.dto.PlanDTO;
import java.util.List;

public interface PlanService {
    List<PlanDTO> getAllPlans();

    List<PlanDTO> getPlansByProduct(int productId);

    List<PlanDTO> getPlansByCompany(int companyId);

    PlanDTO getPlanById(int id);

    PlanDTO createPlan(PlanDTO planDTO);

    PlanDTO updatePlan(int id, PlanDTO planDTO);

    void deletePlan(int id);

    PlanDTO toggleStatus(int id, String status);
}
