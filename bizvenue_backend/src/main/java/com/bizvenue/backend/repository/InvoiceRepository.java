package com.bizvenue.backend.repository;

import com.bizvenue.backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByClientId(int clientId);

    List<Invoice> findByCompanyId(int companyId);

    List<Invoice> findBySubscriptionId(int subscriptionId);
}
