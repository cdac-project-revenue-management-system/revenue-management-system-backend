package com.bizvenue.backend.mapper;

import com.bizvenue.backend.dto.*;
import com.bizvenue.backend.entity.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EntityMapper {

    public UserDTO toUserDTO(User user) {
        if (user == null)
            return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public ClientDTO toClientDTO(Client client) {
        return toClientDTO(client, null);
    }

    public ClientDTO toClientDTO(Client client, Integer companyId) {
        if (client == null)
            return null;
        ClientDTO dto = new ClientDTO();
        // User fields
        dto.setId(client.getId());
        dto.setEmail(client.getEmail());
        dto.setFullName(client.getFullName());
        dto.setRole(client.getRole());
        dto.setCreatedAt(client.getCreatedAt());

        // Client fields
        dto.setPhone(client.getPhone());
        dto.setBillingInfo(client.getBillingInfo());
        dto.setStatus(client.getStatus());
        dto.setCompanyName(client.getCompanyName());
        dto.setTotalSpent(client.getTotalSpent());
        dto.setLastActivity(client.getLastActivity());

        // Company specific stats
        if (companyId != null) {
            long subCount = 0;
            if (client.getSubscriptions() != null) {
                subCount = client.getSubscriptions().stream()
                        .filter(s -> s.getPlan() != null && s.getPlan().getProduct() != null
                                && s.getPlan().getProduct().getCompany() != null
                                && s.getPlan().getProduct().getCompany().getId() == companyId)
                        .count();
            }
            dto.setSubscriptions((int) subCount);

            java.math.BigDecimal companySpent = java.math.BigDecimal.ZERO;
            if (client.getInvoices() != null) {
                companySpent = client.getInvoices().stream()
                        .filter(i -> i.getCompany() != null && i.getCompany().getId() == companyId
                                && i.getStatus() == com.bizvenue.backend.entity.enums.InvoiceStatus.PAID)
                        .map(com.bizvenue.backend.entity.Invoice::getAmount)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            }
            dto.setTotalSpent(companySpent);
        } else {
            // Global stats
            if (client.getSubscriptions() != null) {
                dto.setSubscriptions(client.getSubscriptions().size());
            } else {
                dto.setSubscriptions(0);
            }
        }

        return dto;
    }

    public CompanyDTO toCompanyDTO(Company company) {
        if (company == null)
            return null;
        CompanyDTO dto = new CompanyDTO();
        // User fields
        dto.setId(company.getId());
        dto.setEmail(company.getEmail());
        dto.setFullName(company.getFullName());
        dto.setRole(company.getRole());
        dto.setCreatedAt(company.getCreatedAt());

        // Company fields
        dto.setCompanyName(company.getCompanyName());
        dto.setPhone(company.getPhone());
        return dto;
    }

    public ProductDTO toProductDTO(Product product) {
        if (product == null)
            return null;
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        if (product.getCompany() != null) {
            dto.setCompanyId(product.getCompany().getId());
        }
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setStatus(product.getStatus());
        dto.setCreatedAt(product.getCreatedAt());

        // Dynamic stats calculation
        int activeSubCount = 0;
        java.math.BigDecimal totalRev = java.math.BigDecimal.ZERO;

        if (product.getPlans() != null) {
            dto.setPlansCount(product.getPlans().size());

            for (Plan plan : product.getPlans()) {
                if (plan.getSubscriptions() != null) {
                    for (Subscription sub : plan.getSubscriptions()) {
                        // Count active subs
                        if (sub.getStatus() == com.bizvenue.backend.entity.enums.SubscriptionStatus.ACTIVE) {
                            activeSubCount++;
                        }

                        // Sum paid invoices
                        if (sub.getInvoices() != null) {
                            java.math.BigDecimal subRev = sub.getInvoices().stream()
                                    .filter(i -> i.getStatus() == com.bizvenue.backend.entity.enums.InvoiceStatus.PAID)
                                    .map(com.bizvenue.backend.entity.Invoice::getAmount)
                                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                            totalRev = totalRev.add(subRev);
                        }
                    }
                }
            }
        } else {
            dto.setPlansCount(0);
        }

        dto.setActiveSubscriptions(activeSubCount);
        dto.setRevenue(totalRev);

        return dto;
    }

    public PlanDTO toPlanDTO(Plan plan) {
        if (plan == null)
            return null;
        PlanDTO dto = new PlanDTO();
        dto.setId(plan.getId());
        if (plan.getProduct() != null) {
            dto.setProductId(plan.getProduct().getId());
            dto.setProduct(plan.getProduct().getName()); // Frontend field
            if (plan.getProduct().getCompany() != null) {
                dto.setCompanyId(plan.getProduct().getCompany().getId());
                dto.setCompanyName(plan.getProduct().getCompany().getCompanyName());
            }
        }
        dto.setName(plan.getName());
        dto.setPrice(plan.getPrice());
        dto.setInterval(plan.getInterval());
        dto.setFeatures(plan.getFeatures());
        dto.setIsPopular(plan.getIsPopular());
        dto.setStatus(plan.getStatus());

        if (plan.getSubscriptions() != null) {
            dto.setSubscribers(plan.getSubscriptions().size()); // Frontend field
        } else {
            dto.setSubscribers(0);
        }

        return dto;
    }

    public SubscriptionDTO toSubscriptionDTO(Subscription subscription) {
        if (subscription == null)
            return null;
        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(subscription.getId());

        if (subscription.getClient() != null) {
            dto.setClientId(subscription.getClient().getId());
            dto.setClient(subscription.getClient().getCompanyName() != null ? subscription.getClient().getCompanyName()
                    : subscription.getClient().getFullName()); // Frontend field
            dto.setClientEmail(subscription.getClient().getEmail()); // Frontend field
        }

        if (subscription.getPlan() != null) {
            dto.setPlanId(subscription.getPlan().getId());
            dto.setPlan(subscription.getPlan().getName()); // Frontend field
            dto.setInterval(subscription.getPlan().getInterval() != null
                    ? subscription.getPlan().getInterval().name().toLowerCase()
                    : null); // Frontend field

            if (subscription.getPlan().getProduct() != null) {
                dto.setProduct(subscription.getPlan().getProduct().getName()); // Frontend field
            }
        }

        dto.setStatus(subscription.getStatus());
        dto.setAmount(subscription.getAmount());
        dto.setStartDate(subscription.getStartDate());
        dto.setNextBilling(subscription.getNextBilling());
        return dto;
    }

    public InvoiceDTO toInvoiceDTO(Invoice invoice) {
        if (invoice == null)
            return null;
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());

        if (invoice.getClient() != null) {
            dto.setClientId(invoice.getClient().getId());
            dto.setClient(invoice.getClient().getCompanyName() != null ? invoice.getClient().getCompanyName()
                    : invoice.getClient().getFullName()); // Frontend field
            dto.setClientEmail(invoice.getClient().getEmail()); // Frontend field
        }

        if (invoice.getCompany() != null) {
            dto.setCompanyId(invoice.getCompany().getId());
        }
        if (invoice.getSubscription() != null) {
            dto.setSubscriptionId(invoice.getSubscription().getId());
        }
        dto.setAmount(invoice.getAmount());
        dto.setStatus(invoice.getStatus());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setItems(invoice.getItems());
        return dto;
    }
}
