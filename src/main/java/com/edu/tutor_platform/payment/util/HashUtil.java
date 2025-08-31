package com.edu.tutor_platform.payment.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.edu.tutor_platform.payment.dto.PaymentNotificationDTO;
import java.util.logging.Logger;

@Component
public class HashUtil {
    
    private static final Logger logger = Logger.getLogger(HashUtil.class.getName());
    
    @Value("${payhere.merchant-secret}")
    private String merchantSecret;

    public String generateHash(String merchantId, String orderId, double amount, String currency) {
        String md5Secret = DigestUtils.md5Hex(merchantSecret).toUpperCase();
        String hashInput = merchantId + orderId + String.format("%.2f", amount) + currency + md5Secret;
        return DigestUtils.md5Hex(hashInput).toUpperCase();
    }

    public boolean verifyNotification(PaymentNotificationDTO notification, String merchantId) {
        try {
            // Validate inputs
            if (notification == null) {
                logger.severe("PaymentNotificationDTO is null");
                return false;
            }
            
            if (merchantSecret == null || merchantSecret.trim().isEmpty()) {
                logger.severe("Merchant secret is null or empty");
                return false;
            }
            
            if (merchantId == null || merchantId.trim().isEmpty()) {
                logger.severe("Merchant ID is null or empty");
                return false;
            }
            
            // Log notification details for debugging
            logger.info("=== PayHere MD5 Verification ===");
            logger.info("Merchant ID: " + merchantId);
            logger.info("Order ID: " + notification.getOrder_id());
            logger.info("Amount: " + notification.getPayhere_amount());
            logger.info("Currency: " + notification.getPayhere_currency());
            logger.info("Status Code: " + notification.getStatus_code());
            logger.info("Received MD5: " + notification.getMd5sig());
            
            String md5Secret = DigestUtils.md5Hex(merchantSecret).toUpperCase();
            logger.info("MD5 Secret Hash: " + md5Secret);
            
            // Format amount to decimal format (PayHere expects 100.00, not 100)
            String formattedAmount = formatAmountForHash(notification.getPayhere_amount());
            
            // Build hash string exactly as PayHere expects
            String hashInput = merchantId +
                              notification.getOrder_id() +
                              formattedAmount +
                              notification.getPayhere_currency() +
                              notification.getStatus_code() +
                              md5Secret;
            
            logger.info("Hash Input String: '" + hashInput + "'");
            logger.info("Hash Input Length: " + hashInput.length());
            
            String expectedSig = DigestUtils.md5Hex(hashInput).toUpperCase();
            String receivedSig = notification.getMd5sig() != null ? notification.getMd5sig().toUpperCase() : "";
            
            logger.info("Expected MD5: " + expectedSig);
            logger.info("Received MD5: " + receivedSig);
            
            boolean isValid = expectedSig.equals(receivedSig);
            logger.info("MD5 Verification Result: " + (isValid ? "VALID" : "INVALID"));
            
            if (!isValid) {
                logger.warning("MD5 signature verification failed!");
                logger.warning("Expected: " + expectedSig);
                logger.warning("Received: " + receivedSig);
                
                // Additional debugging - check each component
                logger.info("=== Hash Components Debug ===");
                logger.info("Component 1 (merchant_id): '" + merchantId + "' (length: " + merchantId.length() + ")");
                logger.info("Component 2 (order_id): '" + notification.getOrder_id() + "' (length: " + (notification.getOrder_id() != null ? notification.getOrder_id().length() : 0) + ")");
                String formattedAmountForDebug = formatAmountForHash(notification.getPayhere_amount());
                logger.info("Component 3 (payhere_amount - original): '" + notification.getPayhere_amount() + "' (length: " + (notification.getPayhere_amount() != null ? notification.getPayhere_amount().length() : 0) + ")");
                logger.info("Component 3 (payhere_amount - formatted): '" + formattedAmountForDebug + "' (length: " + formattedAmountForDebug.length() + ")");
                logger.info("Component 4 (payhere_currency): '" + notification.getPayhere_currency() + "' (length: " + (notification.getPayhere_currency() != null ? notification.getPayhere_currency().length() : 0) + ")");
                logger.info("Component 5 (status_code): '" + notification.getStatus_code() + "' (length: " + (notification.getStatus_code() != null ? notification.getStatus_code().length() : 0) + ")");
                logger.info("Component 6 (md5_secret): '" + md5Secret + "' (length: " + md5Secret.length() + ")");
            }
            
            logger.info("=== End MD5 Verification ===");
            return isValid;
            
        } catch (Exception e) {
            logger.severe("Error verifying MD5 signature: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Format amount for PayHere MD5 hash calculation
     * PayHere expects decimal format (e.g., "100.00" not "100")
     */
    private String formatAmountForHash(String amount) {
        try {
            if (amount == null || amount.trim().isEmpty()) {
                return "0.00";
            }
            
            // Remove any whitespace
            amount = amount.trim();
            
            // If amount already has decimal places, return as is
            if (amount.contains(".")) {
                // Ensure it has exactly 2 decimal places
                double amountValue = Double.parseDouble(amount);
                return String.format("%.2f", amountValue);
            }
            
            // If amount is integer, add .00
            int amountValue = Integer.parseInt(amount);
            return String.format("%.2f", (double) amountValue);
            
        } catch (NumberFormatException e) {
            logger.severe("Invalid amount format: " + amount + ". Using 0.00");
            return "0.00";
        }
    }
}