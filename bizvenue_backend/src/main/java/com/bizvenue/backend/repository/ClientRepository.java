package com.bizvenue.backend.repository;

import com.bizvenue.backend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c FROM Client c " +
            "LEFT JOIN c.subscriptions s " +
            "LEFT JOIN s.plan p " +
            "LEFT JOIN p.product pr " +
            "WHERE c.company.id = :companyId " +
            "OR pr.company.id = :companyId")
    java.util.List<Client> findByCompanyId(int companyId);
}
