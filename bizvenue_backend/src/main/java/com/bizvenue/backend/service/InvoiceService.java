package com.bizvenue.backend.service;

import com.bizvenue.backend.dto.InvoiceDTO;
import java.util.List;

public interface InvoiceService {
    List<InvoiceDTO> getAllInvoices();

    List<InvoiceDTO> getInvoicesByClient(int clientId);

    List<InvoiceDTO> getInvoicesByCompany(int companyId);

    InvoiceDTO getInvoiceById(int id);

    InvoiceDTO createInvoice(InvoiceDTO invoiceDTO);

    InvoiceDTO updateInvoice(int id, InvoiceDTO invoiceDTO);

    void deleteInvoice(int id);

    byte[] generatePdf(int id);

    void payInvoice(int id);
}
