package com.example.vippsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@Setter
@NoArgsConstructor
public class PaymentCancelResponse {
    @JsonProperty("paymentInstrument")
    private String paymentInstrument;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("transactionInfo")
    TransactionInfo transactionInfo;
    @JsonProperty("transactionSummary")
    TransactionSummary transactionSummary;

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class TransactionSummary {
        @JsonProperty("capturedAmount")
        private int capturedAmount;
        @JsonProperty("refundedAmount")
        private int refundedAmount;
        @JsonProperty("remainingAmountToCapture")
        private int remainingAmountToCapture;
        @JsonProperty("remainingAmountToRefund")
        private int remainingAmountToRefund;
        @JsonProperty("bankIdentificationNumber")
        private int bankIdentificationNumber;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class TransactionInfo {
        @JsonProperty("amount")
        private int amount;
        @JsonProperty("status")
        private String status;
        @JsonProperty("timeStamp")
        private String timeStamp;
        @JsonProperty("transactionId")
        private String transactionId;
        @JsonProperty("transactionText")
        private String transactionText;
    }
}
