package com.aba.raffle.proyecto.services.impl;



import com.aba.raffle.proyecto.entities.EmailColor;
import com.aba.raffle.proyecto.services.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender _mailSender) {
        this.mailSender = _mailSender;
    }

    @Override
    public CompletableFuture<Void> sendEmailCode(String to, String subject, String code) {
        return CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = null;
                helper = new MimeMessageHelper(message,true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText( htmlContentActivationCode(code), true);
                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> sendEmailCodeToMultipleRecipients(List<String> toList, String subject, String coupon, String discountAmount, String title, String note) {
        return CompletableFuture.runAsync(() -> {
            try {
                String contentHtml = htmlContentCoupon(coupon,EmailColor.SUNNY_YELLOW, title, discountAmount, note);
                sendBulkEmail(toList, subject, contentHtml);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> sendEmailCodeRecoveryPassword(String to, String subject, String code, String name) {
        return CompletableFuture.runAsync(() -> {
            try {
                String note = "Use the following code to recover your password.";
                EmailColor backgroundColor = EmailColor.PURPLE_RAIN;

                String contentHtml = htmlContentPasswordRecovery(code, backgroundColor, name);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(contentHtml, true);

                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> generateAdminRequestDecisionEmail(String to, String subject, String name, boolean isAccepted) {
        return CompletableFuture.runAsync(() -> {
            try {
                String contentHtml = htmlContentAdminRequestDecisionEmail(name,isAccepted,"https://firebasestorage.googleapis.com/v0/b/eventpro-7f680.appspot.com/o/mone-use.png?alt=media&token=0b828906-2471-43da-b5a8-551abb2f9551");

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(contentHtml, true);

                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> sendPurchaseConfirmationEmail(
            String to,
            String nombre,
            double monto,
            String moneda,
            String metodo,
            LocalDateTime fecha,
            List<String> numerosComprados
    ) {
        return CompletableFuture.runAsync(() -> {
            try {
                String contentHtml = htmlContentPurchase(nombre, monto, moneda, metodo, fecha, numerosComprados);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject("🎉 Confirmación de tu compra en RafflePro");
                helper.setText(contentHtml, true);

                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    public CompletableFuture<Void> generateAdminRequestConfirmationEmail(String to, String subject, String name) {
        return CompletableFuture.runAsync(() -> {
            try {
                String contentHtml = htmlContentAdminRequestConfirmationEmail(name,"https://firebasestorage.googleapis.com/v0/b/eventpro-7f680.appspot.com/o/funny-monkey.png?alt=media&token=70ec89a4-dc22-4507-bcef-2a9bbbefa4f7");

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(contentHtml, true);

                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    private void sendBulkEmail(List<String> toList, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String[] toArray = toList.toArray(new String[0]);
        helper.setTo(toArray);

        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    private String htmlContentCode(String code) {
        return "<div style=\"font-family: Arial, sans-serif; background-color: #052320; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #fff; padding: 40px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">"
                + "<h2 style=\"color: #333; font-weight: bold; margin-bottom: 20px;\">EventoPro Account Activation</h2>"
                + "<p style=\"color: #666; margin-bottom: 30px;\">Thank you for registering with EventoPro. Please use the following activation code to activate your account:</p>"
                + "<div style=\"background-color: #f0f0f0; padding: 15px; border-radius: 5px;\">"
                + "<h3 style=\"color: #333; font-weight: bold; margin: 0;\">" + code + "</h3>"
                + "</div>"
                + "<p style=\"color: #666; margin-top: 30px;\">Please note that this code is valid for approximately 15 minutes. We recommend using it promptly to complete your registration process.</p>"
                + "<p style=\"color: #666; margin-top: 30px;\">If you did not request this activation code, please disregard this email. Your account remains secure.</p>"
                + "</div>"
                + "</div>";
    }

    private String htmlContentCoupon(String couponCode, EmailColor backgroundColor, String couponTitle, String discountAmount, String note) {
        return "<div style=\"font-family: Arial, sans-serif; background-color: " + backgroundColor.getHexCode() + "; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 40px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">"
                + "<h2 style=\"color: #333333; font-weight: bold; margin-bottom: 20px;\">" + couponTitle + "</h2>"
                + "<p style=\"color: #666666; margin-bottom: 30px;\">Congratulations! You have received an exclusive discount coupon.</p>"
                + "<div style=\"background-color: #f0f0f0; padding: 15px; border-radius: 5px; text-align: center;\">"
                + "<h3 style=\"color: #333333; font-weight: bold; margin: 0;\">Coupon Code: " + couponCode + "</h3>"
                + "</div>"
                + "<p style=\"color: #ff6347; font-size: 24px; font-weight: bold; text-align: center; margin-top: 30px;\">You get " + discountAmount + " off on your next purchase!</p>"
                + "<p style=\"color: #666666; margin-top: 30px;\">" + note + "</p>"
                + "<p style=\"color: #666666; margin-top: 30px;\">If you did not request this coupon, please disregard this email. Your account remains secure.</p>"
                + "</div>"
                + "</div>";
    }

    private String htmlContentPasswordRecovery(String recoveryCode, EmailColor backgroundColor, String userName) {
        return "<div style=\"font-family: Arial, sans-serif; background-color: " + backgroundColor.getHexCode() + "; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 40px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">"
                + "<h2 style=\"color: #333333; font-weight: bold; margin-bottom: 20px;\">Password Recovery</h2>"
                + "<p style=\"color: #666666; margin-bottom: 30px;\">Hi " + userName + ",</p>"
                + "<p style=\"color: #666666; margin-bottom: 30px;\">We received a request to reset your password. Use the following recovery code to proceed:</p>"
                + "<div style=\"background-color: #f0f0f0; padding: 15px; border-radius: 5px; text-align: center;\">"
                + "<h3 style=\"color: #333333; font-weight: bold; margin: 0;\">" + recoveryCode + "</h3>"
                + "</div>"
                + "<p style=\"color: #666666; margin-top: 30px;\">This code is valid for the next 5 minutes. If you didn't request this, no worries! Your account remains secure.</p>"
                + "<p style=\"color: #666666; margin-top: 30px;\">You're doing great! We're here to help if you need anything else.</p>"
                + "</div>"
                + "</div>";
    }

    private String htmlContentAdminRequestConfirmationEmail(String name, String imageUrl) {
        return "<div style=\"font-family: Arial, sans-serif; background-color: #052320; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #fff; padding: 40px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">"
                + "<h2 style=\"color: #333; font-weight: bold; margin-bottom: 20px;\">Gracias por registrarte en EventPro</h2>"
                + "<p style=\"color: #666; margin-bottom: 30px;\">Estimado/a " + name + ",</p>"
                + "<p style=\"color: #666; margin-bottom: 30px;\">¡Gracias por registrarte en **EventPro**! Hemos recibido tu solicitud para ser administrador y gestionar eventos en nuestra plataforma.</p>"
                + "<p style=\"color: #666; margin-bottom: 30px;\">Tu solicitud será evaluada por nuestro equipo. Te notificaremos por correo adicional si es aceptada o rechazada.</p>"
                + "<p style=\"color: #666; margin-top: 30px;\">Mientras tanto, si tienes alguna pregunta o necesitas más información, no dudes en ponerte en contacto con nosotros.</p>"
                + "<div style=\"background-color: #f0f0f0; padding: 15px; border-radius: 5px; margin-top: 30px; text-align: center;\">"
                + "<h3 style=\"color: #333; font-weight: bold; margin: 0;\">Gracias por ser parte de nuestra comunidad.</h3>"
                + "</div>"
                + "<p style=\"color: #666; margin-top: 30px; text-align: center;\">El equipo de EventPro</p>"
                + "<div style=\"text-align: center; margin-top: 30px;\">"
                + "<img src=\"" + imageUrl + "\" alt=\"Imagen de bienvenida\" style=\"max-width: 100%; height: auto; border-radius: 10px;\">"
                + "</div>"
                + "</div>"
                + "</div>";
    }

    private String htmlContentAdminRequestDecisionEmail(String name, boolean isAccepted, String imageUrl) {
        String subject = isAccepted ? "Solicitud de administración aceptada en EventPro" : "Solicitud de administración rechazada en EventPro";
        String decisionMessage = isAccepted
                ? "<p style=\"color: #666; margin-bottom: 30px;\">Nos complace informarte que tu solicitud para ser administrador ha sido **aceptada**. Ahora podrás gestionar eventos en nuestra plataforma.</p>"
                : "<p style=\"color: #666; margin-bottom: 30px;\">Lamentablemente, tu solicitud para ser administrador ha sido **rechazada**. Te animamos a seguir participando en EventPro y estar atento a futuras oportunidades.</p>";

        return "<div style=\"font-family: Arial, sans-serif; background-color: #052320; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #fff; padding: 40px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">"
                + "<h2 style=\"color: #333; font-weight: bold; margin-bottom: 20px;\">" + subject + "</h2>"
                + "<p style=\"color: #666; margin-bottom: 30px;\">Estimado/a " + name + ",</p>"
                + decisionMessage
                + "<p style=\"color: #666; margin-top: 30px;\">Te agradecemos por tu interés en ser parte de **EventPro**. Si tienes alguna pregunta o necesitas más información, no dudes en ponerte en contacto con nosotros.</p>"
                + "<div style=\"background-color: #f0f0f0; padding: 15px; border-radius: 5px; margin-top: 30px; text-align: center;\">"
                + "<h3 style=\"color: #333; font-weight: bold; margin: 0;\">Gracias por ser parte de nuestra comunidad.</h3>"
                + "</div>"
                + "<p style=\"color: #666; margin-top: 30px; text-align: center;\">El equipo de EventPro</p>"
                + "<div style=\"text-align: center; margin-top: 30px;\">"
                + "<img src=\"" + imageUrl + "\" alt=\"Imagen de bienvenida\" style=\"max-width: 100%; height: auto; border-radius: 10px;\">"
                + "</div>"
                + "</div>"
                + "</div>";
    }

    private String htmlContentActivationCode(String codigoActivacion) {
        return """
    <html>
        <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
            <div style="max-width: 600px; margin: auto; background-color: white; padding: 20px; border-radius: 8px;">
                <h2 style="color: #4CAF50; text-align: center;">Verificación de cuenta</h2>
                <p>Haz clic en el siguiente enlace para validar tu correo y usar tu código de verificación:</p>
                <p style="text-align: center;">
                    <a href="https://app-frontend-raffle-uq.web.app/validationEmail" 
                       style="display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px;">
                        Validar cuenta
                    </a>
                </p>
                <p style="text-align: center; margin-top: 10px;">Tu código de verificación es: <strong>%s</strong></p>
                <p>Si no solicitaste este código, ignora este correo.</p>
                <p style="font-size: 12px; color: #888;">Correo automático, no responder.</p>
            </div>
        </body>
    </html>
    """.formatted(codigoActivacion);
    }



    private String htmlContentPurchase(
            String nombre,
            double monto,
            String moneda,
            String metodo,
            LocalDateTime fecha,
            List<String> numeros
    ) {
        String numerosHtml = numeros.stream()
                .map(n -> "<span style='display:inline-block; background:#4CAF50; color:white; padding:6px 12px; margin:4px; border-radius:6px; font-weight:bold;'>" + n + "</span>")
                .reduce("", (a, b) -> a + b);

        return """
    <html>
      <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
        <div style="max-width: 600px; margin:auto; background:white; padding: 20px; border-radius:8px; box-shadow:0 2px 6px rgba(0,0,0,0.1);">
          <h2 style="color: #2E7D32; text-align: center;">¡Gracias por tu compra, %s! </h2>
          <p>Hemos recibido tu pago exitosamente. Aquí están los detalles:</p>

          <ul style="list-style:none; padding:0;">
            <li><strong>Monto:</strong> %s %.2f</li>
            <li><strong>Método de pago:</strong> %s</li>
            <li><strong>Fecha de aprobación:</strong> %s</li>
          </ul>

          <h3 style="margin-top:20px;">Tus números comprados:</h3>
          <div style="margin-top:10px;">%s</div>

          <p style="margin-top:20px; color:#555;">Guarda este correo como comprobante de tu compra. ¡Mucha suerte en el sorteo!</p>
          <hr>
          <p style="font-size:12px; color:#999; text-align:center;">Este es un correo automático, por favor no responder.</p>
        </div>
      </body>
    </html>
    """.formatted(nombre, moneda, monto, metodo, fecha.toString(), numerosHtml);
    }



}