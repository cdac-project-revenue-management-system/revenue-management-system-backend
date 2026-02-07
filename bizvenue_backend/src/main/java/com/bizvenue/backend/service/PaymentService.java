package com.bizvenue.backend.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public String createOrder(double amount) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);
        JSONObject options = new JSONObject();
        options.put("amount", (int) (amount * 100)); // Amount in paise
        options.put("currency", "INR"); // Changed to INR for testing
        options.put("receipt", "txn_" + System.currentTimeMillis());
        Order order = client.orders.create(options);
        return order.toString();
    }

    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) throws RazorpayException {
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", orderId);
        options.put("razorpay_payment_id", paymentId);
        options.put("razorpay_signature", signature);
        return Utils.verifyPaymentSignature(options, keySecret);
    }
}
