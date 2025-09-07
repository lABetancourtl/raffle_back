package com.aba.raffle.proyecto.services;


import jakarta.mail.MessagingException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IEmailService {
    /**
     * Sends an email with a code for activation or validation.
     *
     * @param to The recipient's email address.
     * @param subject The subject of the email.
     * @param code The code to include in the email.
     * @throws MessagingException If an error occurs while sending the email.
     */
    CompletableFuture<Void> sendEmailCode(String to, String subject, String code) throws MessagingException;

    /**
     * Sends an email with a code to multiple recipients.
     *
     * @param toList The list of email addresses.
     * @param subject The subject of the email.
     * @param coupon The coupon to include in the email.
     * @param discountAmount The discount amount.
     * @param title The title of the email.
     * @param note An additional note to include in the email.
     */
    CompletableFuture<Void> sendEmailCodeToMultipleRecipients(List<String> toList, String subject, String coupon, String discountAmount, String title, String note );

    /**
     * Sends an email for password recovery.
     *
     * @param to The recipient's email address.
     * @param subject The subject of the email.
     * @param code The recovery code.
     * @param name The recipient's name.
     * @throws MessagingException If an error occurs while sending the email.
     */
    CompletableFuture<Void> sendEmailCodeRecoveryPassword(String to, String subject, String code, String name);

    /**
     * Generates the HTML content for the admin request confirmation email.
     *
     * @param name The user's name.
     * //@param imageUrl The URL of the image to include in the email.
     */
    CompletableFuture<Void> generateAdminRequestConfirmationEmail(String to, String subject,String name);

    /**
     * Generates the HTML content for the admin request decision email.
     *
     * @param name The user's name.
     * @param isAccepted Whether the request was accepted (true) or rejected (false).
     * //@param imageUrl The URL of the image to include in the email.
     */
    CompletableFuture<Void> generateAdminRequestDecisionEmail(String to, String subject,String name, boolean isAccepted);
}