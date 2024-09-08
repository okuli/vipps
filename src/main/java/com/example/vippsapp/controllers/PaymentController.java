package com.example.vippsapp.controllers;

import com.example.vippsapp.dto.*;
import com.example.vippsapp.services.MobilePayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {


    private final MobilePayService mobilePayService;


    public PaymentController(MobilePayService mobilePayService) {
        this.mobilePayService = mobilePayService;
    }

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request){
        PaymentResponse paymentResponse = mobilePayService.createPayment(request);
        if(paymentResponse != null) {
            return ResponseEntity.ok(paymentResponse);
        }
        return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
    }

    @GetMapping("/status")
    public ResponseEntity<PaymentStatusResponse> getStatusPayment(@PathVariable("order") String order) {
        PaymentStatusResponse status = mobilePayService.getPaymentStatus(order);
        if(status != null) {
            return ResponseEntity.ok(status);
        }
        return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
    }

    @PostMapping("/cancel")
    public ResponseEntity<PaymentCancelResponse> cancelPayment(@PathVariable("order") String order, @RequestBody PaymentCancelRequest request) {
        PaymentCancelResponse response = mobilePayService.cancelPayment(order,request);
        if(response != null) {
            return ResponseEntity.ok(response);
        }
        return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
    }
}
