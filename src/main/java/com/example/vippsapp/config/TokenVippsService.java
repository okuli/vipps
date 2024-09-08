package com.example.vippsapp.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;


public class TokenVippsService {

    private String token;
    private Instant expired;

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

    private static TokenVippsService instance;

    private TokenVippsService(){}

    public static TokenVippsService getInstance() {
        if(instance == null) {
            synchronized (TokenVippsService.class) {
                if (instance == null) {
                    instance = new TokenVippsService();
                }
            }
        }
        return instance;
    }

    public String token() {
        if(token == null ){
            getToken();
        }
        return token;
    }

    public Instant expired() {
        return expired;
    }

    private void getToken() {
        String url = apiUrl + "/accesstoken/get";

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

        HttpEntity<String> request = new HttpEntity<>(header);

        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,request,String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(response.getBody());
            token = json.get("access_token").toString();
            expired = Instant.ofEpochSecond(json.get("expires_on").longValue());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
