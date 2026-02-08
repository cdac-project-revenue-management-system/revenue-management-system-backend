package com.bizvenue.backend.service.impl;

import com.bizvenue.backend.dto.SubscriptionDTO;
import com.bizvenue.backend.entity.Client;
import com.bizvenue.backend.entity.Plan;
import com.bizvenue.backend.entity.Subscription;
import com.bizvenue.backend.mapper.EntityMapper;
import com.bizvenue.backend.repository.ClientRepository;
import com.bizvenue.backend.repository.PlanRepository;
import com.bizvenue.backend.repository.SubscriptionRepository;
import com.bizvenue.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ClientRepository clientRepository;
    private final PlanRepository planRepository;
    private final com.bizvenue.backend.repository.InvoiceRepository invoiceRepository; // Added for automatic invoice
                                                                                       // creation
    private final EntityMapper mapper;

    // ... (existing methods until createSubscription)

    @Override
    public List<SubscriptionDTO> getAllSubscriptions() {
        return subscriptionRepository.findAll().stream()
                .map(mapper::toSubscriptionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionDTO> getSubscriptionsByClient(int clientId) {
        return subscriptionRepository.findByClientId(clientId).stream()
                .map(mapper::toSubscriptionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionDTO> getSubscriptionsByCompany(int companyId) {
        return subscriptionRepository.findByPlanProductCompanyId(companyId).stream()
                .map(mapper::toSubscriptionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionDTO getSubscriptionById(int id) {
        return subscriptionRepository.findById(id)
                .map(mapper::toSubscriptionDTO)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

    @Override
    public SubscriptionDTO createSubscription(SubscriptionDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Client not found"));

        Plan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        Subscription subscription = new Subscription();
        subscription.setClient(client);
        subscription.setPlan(plan);
        subscription.setAmount(plan.getPrice());
        // Set Status based on DTO, default to ACTIVE if null
        com.bizvenue.backend.entity.enums.SubscriptionStatus status = dto.getStatus() != null
                ? dto.getStatus()
                : com.bizvenue.backend.entity.enums.SubscriptionStatus.ACTIVE;
        subscription.setStatus(status);

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        subscription.setStartDate(now);

        if (plan.getInterval() == com.bizvenue.backend.entity.enums.PlanInterval.YEARLY) {
            subscription.setNextBilling(now.plusYears(1));
        } else {
            subscription.setNextBilling(now.plusMonths(1));
        }

        Subscription saved = subscriptionRepository.save(subscription);

        // Link to Company only if not already linked (First Subscription or Manual Add)
        if (plan.getProduct() != null && plan.getProduct().getCompany() != null) {
            if (client.getCompany() == null) {
                client.setCompany(plan.getProduct().getCompany());
                client.setClientCompanyName(plan.getProduct().getCompany().getCompanyName()); // Sync name
            }
        }
        client.setStatus(com.bizvenue.backend.entity.enums.ClientStatus.ACTIVE);
        clientRepository.save(client);

        // Auto-create Invoice (Best Effort)
        try {
            com.bizvenue.backend.entity.Invoice invoice = new com.bizvenue.backend.entity.Invoice();
            invoice.setClient(client);
            invoice.setSubscription(saved);
            invoice.setAmount(plan.getPrice());
            invoice.setIssueDate(now);
            invoice.setDueDate(now); // Due immediately

            // Set Invoice Status based on Subscription Status
            if (status == com.bizvenue.backend.entity.enums.SubscriptionStatus.PENDING) {
                invoice.setStatus(com.bizvenue.backend.entity.enums.InvoiceStatus.PENDING);
            } else {
                invoice.setStatus(com.bizvenue.backend.entity.enums.InvoiceStatus.PAID);
            }

            invoice.setItems(1); // 1 Subscription
            if (plan.getProduct() != null && plan.getProduct().getCompany() != null) {
                invoice.setCompany(plan.getProduct().getCompany());
                System.out.println("DEBUG SUBS: Linked Invoice " + saved.getId() + " to Company "
                        + plan.getProduct().getCompany().getId());
            } else {
                System.out.println(
                        "DEBUG SUBS: WARNING - Could not find company for plan. Product: " + plan.getProduct());
            }
            invoiceRepository.save(invoice);
            System.out.println("DEBUG SUBS: Invoice saved successfully.");
        } catch (Exception e) {
            System.err
                    .println("Failed to auto-create invoice for subscription " + saved.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }

        return mapper.toSubscriptionDTO(saved);
    }

    @Override
    public SubscriptionDTO updateSubscription(int id, SubscriptionDTO dto) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setStatus(dto.getStatus());
        subscription.setAmount(dto.getAmount());
        subscription.setStartDate(dto.getStartDate());
        subscription.setNextBilling(dto.getNextBilling());

        if (dto.getClientId() != subscription.getClient().getId()) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            subscription.setClient(client);
        }
        if (dto.getPlanId() != subscription.getPlan().getId()) {
            Plan plan = planRepository.findById(dto.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Plan not found"));
            subscription.setPlan(plan);
        }

        Subscription saved = subscriptionRepository.save(subscription);
        return mapper.toSubscriptionDTO(saved);
    }

    @Override
    public void deleteSubscription(int id) {
        subscriptionRepository.deleteById(id);
    }

    @Override
    public SubscriptionDTO renewSubscription(int id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        com.bizvenue.backend.entity.enums.PlanInterval interval = subscription.getPlan().getInterval();

        java.time.LocalDateTime currentBilling = subscription.getNextBilling();
        if (currentBilling == null) {
            currentBilling = java.time.LocalDateTime.now();
        }

        if (interval == com.bizvenue.backend.entity.enums.PlanInterval.YEARLY) {
            subscription.setNextBilling(currentBilling.plusYears(1));
        } else {
            subscription.setNextBilling(currentBilling.plusMonths(1));
        }

        subscription.setStatus(com.bizvenue.backend.entity.enums.SubscriptionStatus.ACTIVE);
        Subscription saved = subscriptionRepository.save(subscription);
        return mapper.toSubscriptionDTO(saved);
    }

    @Override
    public SubscriptionDTO cancelSubscription(int id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setStatus(com.bizvenue.backend.entity.enums.SubscriptionStatus.CANCELLED);
        Subscription saved = subscriptionRepository.save(subscription);
        return mapper.toSubscriptionDTO(saved);
    }
}
