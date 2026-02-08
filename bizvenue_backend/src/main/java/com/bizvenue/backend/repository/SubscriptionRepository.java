package com.bizvenue.backend.repository;

import com.bizvenue.backend.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    List<Subscription> findByClientId(int clientId);

    List<Subscription> findByPlanId(int planId);

    List<Subscription> findByPlanProductCompanyId(int companyId);
}
