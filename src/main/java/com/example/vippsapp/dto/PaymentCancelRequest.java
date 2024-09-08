package com.example.vippsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@Setter
public class PaymentCancelRequest {
    @JsonProperty("merchantInfo")
    MerchantInfo merchantInfo;

    @JsonProperty("transaction")
    Transaction transaction;

    @JsonProperty("shouldReleaseRemainingFunds")
    private Boolean shouldReleaseRemainingFunds;

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class Transaction {
        @JsonProperty("transactionText")
        private String transactionText;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class MerchantInfo {
        @JsonProperty("merchantSerialNumber")
        private String merchantSerialNumber;
    }
}
