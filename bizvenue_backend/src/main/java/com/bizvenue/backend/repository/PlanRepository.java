package com.bizvenue.backend.repository;

import com.bizvenue.backend.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
    List<Plan> findByProductId(int productId);

    List<Plan> findByProductCompanyId(int companyId);
}
