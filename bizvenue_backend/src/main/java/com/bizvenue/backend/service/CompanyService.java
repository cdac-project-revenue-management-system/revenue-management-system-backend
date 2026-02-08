package com.bizvenue.backend.service;

import com.bizvenue.backend.dto.CompanyDTO;
import java.util.List;

public interface CompanyService {
    List<CompanyDTO> getAllCompanies();

    CompanyDTO getCompanyById(int id);

    CompanyDTO createCompany(CompanyDTO companyDTO);

    CompanyDTO updateCompany(int id, CompanyDTO companyDTO);

    void deleteCompany(int id);
}
