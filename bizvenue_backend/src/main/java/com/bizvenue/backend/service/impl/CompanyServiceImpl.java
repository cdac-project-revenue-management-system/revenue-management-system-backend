package com.bizvenue.backend.service.impl;

import com.bizvenue.backend.dto.CompanyDTO;
import com.bizvenue.backend.entity.Company;
import com.bizvenue.backend.mapper.EntityMapper;
import com.bizvenue.backend.repository.CompanyRepository;
import com.bizvenue.backend.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final EntityMapper mapper;

    @Override
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(mapper::toCompanyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyDTO getCompanyById(int id) {
        return companyRepository.findById(id)
                .map(mapper::toCompanyDTO)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    @Override
    public CompanyDTO createCompany(CompanyDTO dto) {
        Company company = new Company();
        // User fields
        company.setEmail(dto.getEmail());
        company.setPassword(dto.getPassword());
        company.setFullName(dto.getFullName());
        company.setRole(dto.getRole());

        // Company fields
        company.setCompanyName(dto.getCompanyName());
        company.setPhone(dto.getPhone());

        Company saved = companyRepository.save(company);
        return mapper.toCompanyDTO(saved);
    }

    @Override
    public CompanyDTO updateCompany(int id, CompanyDTO dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        company.setCompanyName(dto.getCompanyName());
        company.setPhone(dto.getPhone());
        company.setFullName(dto.getFullName());

        Company saved = companyRepository.save(company);
        return mapper.toCompanyDTO(saved);
    }

    @Override
    public void deleteCompany(int id) {
        companyRepository.deleteById(id);
    }
}
