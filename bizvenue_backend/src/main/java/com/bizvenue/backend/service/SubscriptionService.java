package com.bizvenue.backend.service;

import com.bizvenue.backend.dto.SubscriptionDTO;
import java.util.List;

public interface SubscriptionService {
    List<SubscriptionDTO> getAllSubscriptions();

    List<SubscriptionDTO> getSubscriptionsByClient(int clientId);

    List<SubscriptionDTO> getSubscriptionsByCompany(int companyId);

    SubscriptionDTO getSubscriptionById(int id);

    SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO);

    SubscriptionDTO updateSubscription(int id, SubscriptionDTO subscriptionDTO);

    void deleteSubscription(int id);

    SubscriptionDTO renewSubscription(int id);

    SubscriptionDTO cancelSubscription(int id);
}
