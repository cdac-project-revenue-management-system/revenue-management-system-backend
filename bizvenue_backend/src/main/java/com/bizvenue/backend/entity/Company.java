package com.bizvenue.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "companies")
public class Company extends User {

    @Column(name = "company_name", nullable = false)
    private String businessName;

    @Column(name = "phone")
    private String businessPhone;

    @OneToMany(mappedBy = "company")
    private List<Product> products;

    @OneToMany(mappedBy = "company")
    private List<Invoice> invoices;
}
