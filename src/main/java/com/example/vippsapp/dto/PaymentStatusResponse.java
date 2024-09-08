package com.example.vippsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusResponse {
    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("shippingDetails")
    ShippingDetails shippingDetails;

    @JsonProperty("transactionLogHistory")
    List<TransactionLogHistory> transactionLogHistory;

    @JsonProperty("transactionSummary")
    TransactionSummary transactionSummary;

    @JsonProperty("userDetails")
    UserDetails userDetails;

    @JsonProperty("sub")
    private String sub;

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class UserDetails {
        @JsonProperty("email")
        private String email;
        @JsonProperty("firstName")
        private String firstName;
        @JsonProperty("lastName")
        private String lastName;
        @JsonProperty("mobileNumber")
        private  String mobileNumber;
        @JsonProperty("userId")
        private String userId;
    }

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
    public static class TransactionLogHistory {
        @JsonProperty("amount")
        private int amount;
        @JsonProperty("operation")
        private String operation;
        @JsonProperty("operationSuccess")
        private Boolean operationSuccess;
        @JsonProperty("requestId")
        private String requestId;
        @JsonProperty("timeStamp")
        private String timeStamp;
        @JsonProperty("transactionId")
        private String transactionId;
        @JsonProperty("transactionText")
        private String transactionText;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class ShippingDetails {
        @JsonProperty("shippingCost")
        private int shippingCost;
        @JsonProperty("shippingMethod")
        private String shippingMethod;
        @JsonProperty("shippingMethodId")
        private String shippingMethodId;
        @JsonProperty("address")
        Address address;

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        public static class Address {
            @JsonProperty("addressLine1")
            private String addressLine1;
            @JsonProperty("addressLine2")
            private String addressLine2;
            @JsonProperty("city")
            private String city;
            @JsonProperty("country")
            private String country;
            @JsonProperty("postCode")
            private String postCode;
        }

    }

}
