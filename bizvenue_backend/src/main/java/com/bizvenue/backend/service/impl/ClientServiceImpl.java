package com.bizvenue.backend.service.impl;

import com.bizvenue.backend.dto.ClientDTO;
import com.bizvenue.backend.entity.Client;
import com.bizvenue.backend.mapper.EntityMapper;
import com.bizvenue.backend.repository.ClientRepository;
import com.bizvenue.backend.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final EntityMapper mapper;

    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(mapper::toClientDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientDTO> getClientsByCompany(int companyId) {
        return clientRepository.findByCompanyId(companyId).stream()
                .map(client -> mapper.toClientDTO(client, companyId))
                .collect(Collectors.toList());
    }

    @Override
    public ClientDTO getClientById(int id) {
        return clientRepository.findById(id)
                .map(mapper::toClientDTO)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    @Override
    public ClientDTO createClient(ClientDTO dto) {
        Client client = new Client();
        // User fields
        client.setEmail(dto.getEmail());
        client.setPassword(dto.getPassword());
        client.setFullName(dto.getFullName());
        client.setRole(dto.getRole());

        // Client fields
        client.setPhone(dto.getPhone());
        client.setBillingInfo(dto.getBillingInfo());
        client.setStatus(dto.getStatus());
        client.setCompanyName(dto.getCompanyName());
        client.setTotalSpent(dto.getTotalSpent());

        Client saved = clientRepository.save(client);
        return mapper.toClientDTO(saved);
    }

    @Override
    public ClientDTO updateClient(int id, ClientDTO dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        if (dto.getFullName() != null)
            client.setFullName(dto.getFullName());
        if (dto.getPhone() != null)
            client.setPhone(dto.getPhone());
        if (dto.getBillingInfo() != null)
            client.setBillingInfo(dto.getBillingInfo());
        if (dto.getStatus() != null)
            client.setStatus(dto.getStatus());
        if (dto.getCompanyName() != null)
            client.setCompanyName(dto.getCompanyName());
        if (dto.getEmail() != null)
            client.setEmail(dto.getEmail()); // Allow email update if provided

        Client saved = clientRepository.save(client);
        return mapper.toClientDTO(saved);
    }

    @Override
    public void deleteClient(int id) {
        clientRepository.deleteById(id);
    }

    @Override
    public void suspendClient(int id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        client.setStatus(com.bizvenue.backend.entity.enums.ClientStatus.INACTIVE);
        clientRepository.save(client);
    }
}
