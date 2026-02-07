package com.bizvenue.backend.service;

import com.bizvenue.backend.dto.ClientDTO;
import java.util.List;

public interface ClientService {
    List<ClientDTO> getAllClients();

    List<ClientDTO> getClientsByCompany(int companyId);

    ClientDTO getClientById(int id);

    ClientDTO createClient(ClientDTO clientDTO);

    ClientDTO updateClient(int id, ClientDTO clientDTO);

    void deleteClient(int id);

    void suspendClient(int id);
}
