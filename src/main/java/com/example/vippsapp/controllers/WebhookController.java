package com.example.vippsapp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    @PostMapping("/payment-status")
    public ResponseEntity<Void> handlePaymentStatus(@RequestBody String statusPayload) {
        // Process the payment status update
        System.out.println("Received payment status: " + statusPayload);
        return ResponseEntity.ok().build();
    }
}