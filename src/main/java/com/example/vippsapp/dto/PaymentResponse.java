package com.example.vippsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("url")
    private String url;
}
