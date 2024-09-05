package com.example.vippsapp.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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

    private String accessToken;
    private Instant tokenExpiryTime;
    private final RestTemplate restTemplate;

    public MobilePayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String createPayment(String orderId, double amount) {
        // Ensure the token is valid before proceeding
        if (accessToken == null || Instant.now().isAfter(tokenExpiryTime)) {
            refreshToken();
        }

        String paymentUrl = apiUrl + "/v2/payments";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + accessToken);

        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("merchantSerialNumber", merchantSerial);
        paymentRequest.put("orderId", orderId);
        paymentRequest.put("amount", Map.of("currency", "NOK", "value", (int) (amount * 100)));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(paymentRequest, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(paymentUrl, HttpMethod.POST, request, String.class);
            return response.getBody();
        } catch (RestClientException e) {
            return "Payment creation failed: " + e.getMessage();
        }
    }

    private void refreshToken() {
        String tokenUrl = apiUrl + "/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodeCredentials(clientId, clientSecret));
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        Map<String, String> body = Map.of(
                OAuth2ParameterNames.GRANT_TYPE, "client_credentials",
                OAuth2ParameterNames.SCOPE, "payment"
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<OAuth2AccessTokenResponse> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                OAuth2AccessTokenResponse.class
        );

        accessToken = response.getBody().getAccessToken().getTokenValue();
        tokenExpiryTime = Instant.now().plusSeconds(response.getBody().getAccessToken().getExpiresIn());
    }

    private String retryPaymentRequest(String paymentUrl, HttpEntity<Map<String, Object>> request) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(paymentUrl, HttpMethod.POST, request, String.class);
            return response.getBody();
        } catch (RestClientException e) {
            return "Payment creation failed after token refresh: " + e.getMessage();
        }
    }

    private String encodeCredentials(String clientId, String clientSecret) {
        String credentials = clientId + ":" + clientSecret;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}

