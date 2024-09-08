package com.example.vippsapp.services;

import com.example.vippsapp.config.TokenVippsService;
import com.example.vippsapp.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@Service
public class MobilePayService {

    @Value("${vipps.mobilepay.api.url}")
    private String apiUrl;

    @Value("${vipps.mobilepay.client.id}")
    private String clientId;

    @Value("${vipps.mobilepay.client.secret}")
    private String clientSecret;

    @Value("${vipps.mobilepay.merchant.serial}")
    private String merchantSerial;

    @Value("${vipps.mobilepay.ocp.apimKey}")
    private String ocpApim;

    @Value("${vipps.mobilepay.vipps.systemName}")
    private String vippsSystemName;

    @Value("${vipps.mobilepay.vipps.systemVersion}")
    private String vippsSystemVersion;

    @Value("${vipps.mobilepay.vipps.systemPlugin}")
    private String vippsSystemPlugin;

    @Value("${vipps.mobilepay.vipps.systemPluginVer}")
    private String vippsSystemPluginVer;

    private String accessToken;
    private Instant tokenExpiryTime;
    private final RestTemplate restTemplate;

    public MobilePayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getToken() {
        TokenVippsService tokenVippsService = TokenVippsService.getInstance();
        accessToken = tokenVippsService.token();
        tokenExpiryTime = tokenVippsService.expired();
        return accessToken;
    }

    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        // Ensure the token is valid before proceeding
        if (accessToken == null || Instant.now().isAfter(tokenExpiryTime)) {
            getToken();
        }

        String paymentUrl = apiUrl + "ecomm/v2/payments";

        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", "application/json");
        header.set("client_id", clientId);
        header.set("client_secret", clientSecret);
        header.set("Ocp-Apim-Subscription-Key", ocpApim);
        header.set("Merchant-Serial-Number", merchantSerial);
        header.set("Vipps-System-Name", vippsSystemName);
        header.set("Vipps-System-Version", vippsSystemVersion);
        header.set("Vipps-System-Plugin-Name", vippsSystemPlugin);
        header.set("Vipps-System-Plugin-Version", vippsSystemPluginVer );

        HttpEntity request = new HttpEntity<>(paymentRequest, header);
        PaymentResponse paymentResponse = new PaymentResponse();

        try {
            ResponseEntity<String> response = restTemplate.exchange(paymentUrl, HttpMethod.POST, request, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(response.getBody());
            paymentResponse.setOrderId(json.get("orderId").toString());
            paymentResponse.setUrl(json.get("url").toString());
            return paymentResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return paymentResponse;
        }
    }

    public PaymentStatusResponse getPaymentStatus(String order) {
        if (accessToken == null || Instant.now().isAfter(tokenExpiryTime)) {
            getToken();
        }

        String paymentUrl = String.format(apiUrl + "/ecomm/v2/payments/%s/details" , order) ;

        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", "application/json");
        header.set("client_id", clientId);
        header.set("client_secret", clientSecret);
        header.set("Ocp-Apim-Subscription-Key", ocpApim);
        header.set("Merchant-Serial-Number", merchantSerial);
        header.set("Vipps-System-Name", vippsSystemName);
        header.set("Vipps-System-Version", vippsSystemVersion);
        header.set("Vipps-System-Plugin-Name", vippsSystemPlugin);
        header.set("Vipps-System-Plugin-Version", vippsSystemPluginVer );

        HttpEntity request = new HttpEntity<>(header);
        PaymentStatusResponse paymentStatusResponse = new PaymentStatusResponse();
        try{
            ResponseEntity<String> response = restTemplate.exchange(paymentUrl, HttpMethod.GET, request, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(response.getBody());


            JsonNode shippingDetailsNode = json.get("shippingDetails");
            JsonNode transactionLogHistory = json.get("transactionLogHistory");
            JsonNode transactionSummary = json.get("transactionSummary");
            JsonNode userDetails = json.get("userDetails");
            JsonNode addressNode = shippingDetailsNode.get("address");

            paymentStatusResponse.setOrderId(order);
            paymentStatusResponse.setSub(json.get("sub").toString());
            PaymentStatusResponse.ShippingDetails shippingDetails = new PaymentStatusResponse.ShippingDetails();
            PaymentStatusResponse.ShippingDetails.Address address = new PaymentStatusResponse.ShippingDetails.Address();

            address.setAddressLine1(addressNode.get("addressLine1").toString());
            address.setAddressLine2(addressNode.get("addressLine2").toString());
            address.setCity(addressNode.get("city").toString());
            address.setCountry(addressNode.get("country").toString());
            address.setPostCode(addressNode.get("postCode").toString());
            shippingDetails.setShippingCost(shippingDetailsNode.get("shippingCost").intValue());
            shippingDetails.setShippingMethod(shippingDetailsNode.get("shippingMethod").toString());
            shippingDetails.setShippingMethodId(shippingDetailsNode.get("shippingMethodId").toString());
            shippingDetails.setAddress(address);
            paymentStatusResponse.setShippingDetails(shippingDetails);
            List<PaymentStatusResponse.TransactionLogHistory> listLogHistory = new ArrayList<>();
            for(JsonNode element : transactionLogHistory) {
                PaymentStatusResponse.TransactionLogHistory transactionLogHistorySet = new PaymentStatusResponse.TransactionLogHistory();
                transactionLogHistorySet.setAmount(element.get("amount").intValue());
                transactionLogHistorySet.setOperation(element.get("operation").toString());
                transactionLogHistorySet.setOperationSuccess(element.get("operationSuccess").booleanValue());
                transactionLogHistorySet.setRequestId(element.get("requestId").toString());
                transactionLogHistorySet.setTimeStamp(element.get("timeStamp").toString());
                transactionLogHistorySet.setTransactionId(element.get("transactionId").toString());
                transactionLogHistorySet.setTransactionText(element.get("transactionText").toString());;
                listLogHistory.add(transactionLogHistorySet);
            }
            paymentStatusResponse.setTransactionLogHistory(listLogHistory);
            PaymentStatusResponse.TransactionSummary transactionSummarySet = new PaymentStatusResponse.TransactionSummary();
            transactionSummarySet.setCapturedAmount(transactionSummary.get("capturedAmount").intValue());
            transactionSummarySet.setRefundedAmount(transactionSummary.get("refundedAmount").intValue());
            transactionSummarySet.setRefundedAmount(transactionSummary.get("remainingAmountToCapture").intValue());
            transactionSummarySet.setRemainingAmountToRefund(transactionSummary.get("remainingAmountToRefund").intValue());
            transactionSummarySet.setBankIdentificationNumber(transactionSummary.get("bankIdentificationNumber").intValue());
            paymentStatusResponse.setTransactionSummary(transactionSummarySet);
            PaymentStatusResponse.UserDetails userDetailsSet = new PaymentStatusResponse.UserDetails();
            userDetailsSet.setEmail(userDetails.get("email").toString());
            userDetailsSet.setFirstName(userDetails.get("firstName").toString());
            userDetailsSet.setLastName(userDetails.get("lastName").toString());
            userDetailsSet.setMobileNumber(userDetails.get("mobileNumber").toString());
            userDetailsSet.setUserId(userDetails.get("userId").toString());
            paymentStatusResponse.setUserDetails(userDetailsSet);
            return paymentStatusResponse;

        } catch (Exception ex) {
            ex.printStackTrace();
            return paymentStatusResponse;
        }
    }

    public PaymentCancelResponse cancelPayment(String orderId, PaymentCancelRequest paymentCancelRequest) {
        if (accessToken == null || Instant.now().isAfter(tokenExpiryTime)) {
            getToken();
        }

        String paymentUrl = String.format(apiUrl + "/ecomm/v2/payments/%s/cancel" , orderId) ;

        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", "application/json");
        header.set("client_id", clientId);
        header.set("client_secret", clientSecret);
        header.set("Ocp-Apim-Subscription-Key", ocpApim);
        header.set("Merchant-Serial-Number", merchantSerial);
        header.set("Vipps-System-Name", vippsSystemName);
        header.set("Vipps-System-Version", vippsSystemVersion);
        header.set("Vipps-System-Plugin-Name", vippsSystemPlugin);
        header.set("Vipps-System-Plugin-Version", vippsSystemPluginVer );

        HttpEntity request = new HttpEntity<>(paymentCancelRequest,header);
        PaymentCancelResponse paymentCancelResponse = new PaymentCancelResponse();
        try{
            ResponseEntity<String> response = restTemplate.exchange(paymentUrl, HttpMethod.POST, request, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(response.getBody());

            JsonNode transactionInfo = json.get("transactionInfo");
            JsonNode transactionSummary = json.get("transactionSummary");

            paymentCancelResponse.setPaymentInstrument(json.get("paymentInstrument").toString());
            paymentCancelResponse.setOrderId(orderId);
            PaymentCancelResponse.TransactionInfo payTransactionInfoSet = new PaymentCancelResponse.TransactionInfo();
            payTransactionInfoSet.setAmount(transactionInfo.get("amount").intValue());
            payTransactionInfoSet.setStatus(transactionInfo.get("status").toString());
            payTransactionInfoSet.setTimeStamp(transactionInfo.get("timeStamp").toString());
            payTransactionInfoSet.setTransactionId(transactionInfo.get("transactionId").toString());
            payTransactionInfoSet.setTransactionText(transactionInfo.get("transactionText").toString());
            paymentCancelResponse.setTransactionInfo(payTransactionInfoSet);
            PaymentCancelResponse.TransactionSummary transactionSummarySet = new PaymentCancelResponse.TransactionSummary();
            transactionSummarySet.setCapturedAmount(transactionSummary.get("capturedAmount").intValue());
            transactionSummarySet.setRefundedAmount(transactionSummary.get("refundedAmount").intValue());
            transactionSummarySet.setRemainingAmountToCapture(transactionSummary.get("remainingAmountToCapture").intValue());
            transactionSummarySet.setRemainingAmountToRefund(transactionSummary.get("remainingAmountToRefund").intValue());
            transactionSummarySet.setBankIdentificationNumber(transactionSummary.get("bankIdentificationNumber").intValue());
            paymentCancelResponse.setTransactionSummary(transactionSummarySet);
            return paymentCancelResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            return paymentCancelResponse;
        }
    }
}

