package com.example.vippsapp.controllers;

import com.example.vippsapp.services.MobilePayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {


    private final MobilePayService mobilePayService;


    public PaymentController(MobilePayService mobilePayService) {
        this.mobilePayService = mobilePayService;
    }

    public ResponseEntity<String> createPayment(@RequestParam String orderId,
                                                @RequestParam    double amount){
        String paymentResponse = mobilePayService.createPayment(orderId, amount);
        return ResponseEntity.ok(paymentResponse);
    }
}
