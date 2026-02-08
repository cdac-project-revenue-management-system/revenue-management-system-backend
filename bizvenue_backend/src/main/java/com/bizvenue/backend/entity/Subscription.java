package com.bizvenue.backend.entity;

import com.bizvenue.backend.entity.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private BigDecimal amount;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "next_billing_date")
    private LocalDateTime nextBilling;

    @OneToMany(mappedBy = "subscription")
    private List<Invoice> invoices;
}
