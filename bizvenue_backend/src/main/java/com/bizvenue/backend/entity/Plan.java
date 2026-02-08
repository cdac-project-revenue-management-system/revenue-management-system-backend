package com.bizvenue.backend.entity;

import com.bizvenue.backend.entity.enums.PlanInterval;
import com.bizvenue.backend.entity.enums.PlanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String name;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_interval") // interval is a keyword in MySQL
    private PlanInterval interval;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private List<String> features;

    @Column(name = "is_popular")
    private Boolean isPopular;

    @Enumerated(EnumType.STRING)
    private PlanStatus status;

    @OneToMany(mappedBy = "plan")
    private List<Subscription> subscriptions;
}
