package com.example.vippsapp.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Getter
@Setter
public class PaymentRequest {
    CustomerInfor customerInfor;

    MerchantInfo merchantInfo;

    Transaction transaction;

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class CustomerInfor {
        private String mobileNumber;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class MerchantInfo {
        private String merchantSerialNumber;
        private String callbackPrefix;
        private String fallBack;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class Transaction {
        private int amount;
        private String order;
        private String transactionText;
    }
}
