package com.bizvenue.backend.service.impl;

import com.bizvenue.backend.dto.InvoiceDTO;
import com.bizvenue.backend.entity.Client;
import com.bizvenue.backend.entity.Company;
import com.bizvenue.backend.entity.Invoice;
import com.bizvenue.backend.entity.Subscription;
import com.bizvenue.backend.mapper.EntityMapper;
import com.bizvenue.backend.repository.ClientRepository;
import com.bizvenue.backend.repository.CompanyRepository;
import com.bizvenue.backend.repository.InvoiceRepository;
import com.bizvenue.backend.repository.SubscriptionRepository;
import com.bizvenue.backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

        private final InvoiceRepository invoiceRepository;
        private final ClientRepository clientRepository;
        private final CompanyRepository companyRepository;
        private final SubscriptionRepository subscriptionRepository;
        private final EntityMapper mapper;

        @Override
        public List<InvoiceDTO> getAllInvoices() {
                return invoiceRepository.findAll().stream()
                                .map(mapper::toInvoiceDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public List<InvoiceDTO> getInvoicesByClient(int clientId) {
                return invoiceRepository.findByClientId(clientId).stream()
                                .map(mapper::toInvoiceDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public List<InvoiceDTO> getInvoicesByCompany(int companyId) {
                return invoiceRepository.findByCompanyId(companyId).stream()
                                .map(mapper::toInvoiceDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public InvoiceDTO getInvoiceById(int id) {
                return invoiceRepository.findById(id)
                                .map(mapper::toInvoiceDTO)
                                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        }

        @Override
        public InvoiceDTO createInvoice(InvoiceDTO dto) {
                Invoice invoice = new Invoice();
                invoice.setAmount(dto.getAmount());
                invoice.setStatus(dto.getStatus());
                invoice.setIssueDate(dto.getIssueDate());
                invoice.setDueDate(dto.getDueDate());
                invoice.setItems(dto.getItems());

                Client client = clientRepository.findById(dto.getClientId())
                                .orElseThrow(() -> new RuntimeException("Client not found"));
                invoice.setClient(client);

                if (dto.getCompanyId() != null) {
                        Company company = companyRepository.findById(dto.getCompanyId())
                                        .orElseThrow(() -> new RuntimeException("Company not found"));
                        invoice.setCompany(company);
                }

                if (dto.getSubscriptionId() != null) {
                        Subscription subscription = subscriptionRepository.findById(dto.getSubscriptionId())
                                        .orElseThrow(() -> new RuntimeException("Subscription not found"));
                        invoice.setSubscription(subscription);
                }

                Invoice saved = invoiceRepository.save(invoice);
                return mapper.toInvoiceDTO(saved);
        }

        @Override
        public InvoiceDTO updateInvoice(int id, InvoiceDTO dto) {
                Invoice invoice = invoiceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Invoice not found"));

                invoice.setAmount(dto.getAmount());
                invoice.setStatus(dto.getStatus());
                invoice.setIssueDate(dto.getIssueDate());
                invoice.setDueDate(dto.getDueDate());
                invoice.setItems(dto.getItems());

                // Update relationships if changed
                if (dto.getClientId() != invoice.getClient().getId()) {
                        Client client = clientRepository.findById(dto.getClientId())
                                        .orElseThrow(() -> new RuntimeException("Client not found"));
                        invoice.setClient(client);
                }

                if (dto.getCompanyId() != null) {
                        Company company = companyRepository.findById(dto.getCompanyId())
                                        .orElseThrow(() -> new RuntimeException("Company not found"));
                        invoice.setCompany(company);
                } else {
                        invoice.setCompany(null);
                }

                if (dto.getSubscriptionId() != null) {
                        Subscription subscription = subscriptionRepository.findById(dto.getSubscriptionId())
                                        .orElseThrow(() -> new RuntimeException("Subscription not found"));
                        invoice.setSubscription(subscription);
                } else {
                        invoice.setSubscription(null);
                }

                Invoice saved = invoiceRepository.save(invoice);
                return mapper.toInvoiceDTO(saved);
        }

        @Override
        public void deleteInvoice(int id) {
                invoiceRepository.deleteById(id);
        }

        @Override
        public byte[] generatePdf(int id) {
                Invoice invoice = invoiceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Invoice not found"));

                try (java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
                        com.lowagie.text.Document document = new com.lowagie.text.Document(
                                        com.lowagie.text.PageSize.A4);
                        com.lowagie.text.pdf.PdfWriter.getInstance(document, out);
                        document.open();

                        // Fonts
                        com.lowagie.text.Font titleFont = com.lowagie.text.FontFactory
                                        .getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 20);
                        com.lowagie.text.Font headerFont = com.lowagie.text.FontFactory
                                        .getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 12);
                        com.lowagie.text.Font normalFont = com.lowagie.text.FontFactory
                                        .getFont(com.lowagie.text.FontFactory.HELVETICA, 10);
                        com.lowagie.text.Font tableHeaderFont = com.lowagie.text.FontFactory
                                        .getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD, 10, java.awt.Color.WHITE);

                        // Header Section
                        com.lowagie.text.pdf.PdfPTable headerTable = new com.lowagie.text.pdf.PdfPTable(2);
                        headerTable.setWidthPercentage(100);
                        headerTable.setWidths(new float[] { 1, 1 });

                        // Company Info (Left)
                        com.lowagie.text.pdf.PdfPCell companyCell = new com.lowagie.text.pdf.PdfPCell();
                        companyCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                        companyCell.addElement(new com.lowagie.text.Paragraph("BizVenue Inc.", headerFont));
                        companyCell.addElement(
                                        new com.lowagie.text.Paragraph("123 Business Street, Tech City", normalFont));
                        companyCell.addElement(new com.lowagie.text.Paragraph("contact@bizvenue.com", normalFont));
                        headerTable.addCell(companyCell);

                        // Invoice Info (Right)
                        com.lowagie.text.pdf.PdfPCell invoiceInfoCell = new com.lowagie.text.pdf.PdfPCell();
                        invoiceInfoCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                        invoiceInfoCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                        com.lowagie.text.Paragraph invoiceTitle = new com.lowagie.text.Paragraph("INVOICE", titleFont);
                        invoiceTitle.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                        invoiceInfoCell.addElement(invoiceTitle);

                        com.lowagie.text.Paragraph invNum = new com.lowagie.text.Paragraph(
                                        "# " + String.format("INV-2024-%04d", invoice.getId()), headerFont);
                        invNum.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                        invoiceInfoCell.addElement(invNum);

                        com.lowagie.text.Paragraph dateStr = new com.lowagie.text.Paragraph(
                                        "Date: " + invoice.getIssueDate().toLocalDate(), normalFont);
                        dateStr.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                        invoiceInfoCell.addElement(dateStr);

                        headerTable.addCell(invoiceInfoCell);
                        document.add(headerTable);

                        document.add(new com.lowagie.text.Paragraph(" ")); // Spacer
                        document.add(new com.lowagie.text.Paragraph(" "));

                        // Bill To Section
                        if (invoice.getClient() != null) {
                                document.add(new com.lowagie.text.Paragraph("Bill To:", headerFont));
                                document.add(new com.lowagie.text.Paragraph(
                                                invoice.getClient().getCompanyName() != null
                                                                ? invoice.getClient().getCompanyName()
                                                                : invoice.getClient().getFullName(),
                                                normalFont));
                                document.add(new com.lowagie.text.Paragraph(invoice.getClient().getEmail(),
                                                normalFont));
                                if (invoice.getClient().getPhone() != null) {
                                        document.add(new com.lowagie.text.Paragraph(invoice.getClient().getPhone(),
                                                        normalFont));
                                }
                        }

                        document.add(new com.lowagie.text.Paragraph(" ")); // Spacer
                        document.add(new com.lowagie.text.Paragraph(" "));

                        // Items Table
                        com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(4);
                        table.setWidthPercentage(100);
                        table.setWidths(new float[] { 4, 1, 2, 2 }); // Desc, Qty, Unit Price, Total

                        // Table Header
                        String[] headers = { "Description", "Qty", "Unit Price", "Amount" };
                        for (String header : headers) {
                                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(
                                                new com.lowagie.text.Phrase(header, tableHeaderFont));
                                cell.setBackgroundColor(java.awt.Color.GRAY);
                                cell.setPadding(5);
                                table.addCell(cell);
                        }

                        // Table Data
                        // Since we don't have separate line items in database yet, we infer one line
                        // item
                        String description = "Professional Services";
                        if (invoice.getSubscription() != null && invoice.getSubscription().getPlan() != null) {
                                description = "Subscription: " + invoice.getSubscription().getPlan().getName();
                                if (invoice.getSubscription().getPlan().getProduct() != null) {
                                        description += " - "
                                                        + invoice.getSubscription().getPlan().getProduct().getName();
                                }
                        }

                        int qty = invoice.getItems() != null && invoice.getItems() > 0 ? invoice.getItems() : 1;
                        java.math.BigDecimal amount = invoice.getAmount();
                        java.math.BigDecimal unitPrice = amount.divide(java.math.BigDecimal.valueOf(qty), 2,
                                        java.math.RoundingMode.HALF_UP);

                        table.addCell(new com.lowagie.text.Phrase(description, normalFont));
                        table.addCell(new com.lowagie.text.Phrase(String.valueOf(qty), normalFont));
                        table.addCell(new com.lowagie.text.Phrase("Rs. " + unitPrice, normalFont));
                        table.addCell(new com.lowagie.text.Phrase("Rs. " + amount, normalFont));

                        // Empty rows for styling (optional, skipping for now)

                        document.add(table);

                        // Total Section
                        com.lowagie.text.pdf.PdfPTable totalTable = new com.lowagie.text.pdf.PdfPTable(2);
                        totalTable.setWidthPercentage(40);
                        totalTable.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                        totalTable.setSpacingBefore(10);
                        totalTable.setWidths(new float[] { 1, 1 });

                        // Subtotal
                        addTotalRow(totalTable, "Subtotal:", "Rs. " + amount, normalFont, false);

                        // Tax
                        addTotalRow(totalTable, "Tax (0%):", "Rs. 0.00", normalFont, false);

                        // Total
                        addTotalRow(totalTable, "Total:", "Rs. " + amount, headerFont, false);

                        // Amount Paid
                        java.math.BigDecimal amountPaid = java.math.BigDecimal.ZERO;
                        if ("PAID".equalsIgnoreCase(invoice.getStatus().toString())) {
                                amountPaid = amount;
                        }
                        addTotalRow(totalTable, "Amount Paid:", "Rs. " + amountPaid, headerFont, false);

                        // Balance Due
                        java.math.BigDecimal balanceDue = amount.subtract(amountPaid);
                        addTotalRow(totalTable, "Balance Due:", "Rs. " + balanceDue, headerFont, true);

                        document.add(totalTable);

                        // Footer
                        com.lowagie.text.Paragraph footer = new com.lowagie.text.Paragraph(
                                        "Thank you for your business!",
                                        normalFont);
                        footer.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                        footer.setSpacingBefore(50);
                        document.add(footer);

                        document.close();
                        return out.toByteArray();
                } catch (Exception e) {
                        throw new RuntimeException("Error generating PDF", e);
                }
        }

        private void addTotalRow(com.lowagie.text.pdf.PdfPTable table, String label, String value,
                        com.lowagie.text.Font font, boolean underline) {
                com.lowagie.text.pdf.PdfPCell labelCell = new com.lowagie.text.pdf.PdfPCell(
                                new com.lowagie.text.Phrase(label, font));
                labelCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
                labelCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                table.addCell(labelCell);

                com.lowagie.text.pdf.PdfPCell valueCell = new com.lowagie.text.pdf.PdfPCell(
                                new com.lowagie.text.Phrase(value, font));
                valueCell.setBorder(
                                underline ? com.lowagie.text.Rectangle.BOTTOM : com.lowagie.text.Rectangle.NO_BORDER);
                valueCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                table.addCell(valueCell);
        }

        @Override
        public void payInvoice(int id) {
                Invoice invoice = invoiceRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Invoice not found"));

                // 1. Mark Invoice as PAID
                invoice.setStatus(com.bizvenue.backend.entity.enums.InvoiceStatus.PAID);
                invoiceRepository.save(invoice);

                // 2. Activate Subscription if linked
                if (invoice.getSubscription() != null) {
                        Subscription subscription = invoice.getSubscription();
                        subscription.setStatus(com.bizvenue.backend.entity.enums.SubscriptionStatus.ACTIVE);
                        subscriptionRepository.save(subscription);
                }
        }
}
