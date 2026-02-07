package com.bizvenue.backend.entity;

import com.bizvenue.backend.entity.enums.ClientStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "clients")
public class Client extends User {

    @Column(name = "billing_info")
    private String billingInfo;

    @Column(name = "phone")
    private String clientPhone;

    @Enumerated(EnumType.STRING)
    private ClientStatus status;

    @Column(name = "company_name")
    private String clientCompanyName;

    @Column(name = "total_spent")
    private BigDecimal totalSpent;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @OneToMany(mappedBy = "client")
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "client")
    private List<Subscription> subscriptions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}
