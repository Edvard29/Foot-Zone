package com.example.footzone;

import android.util.Log;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailSender {
    private static final String EMAIL = "your_email@gmail.com"; // Ваш email
    private static final String PASSWORD = "your_app_password"; // Пароль приложения Gmail
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void sendPredictionResult(String toEmail, String subject, String body) {
        executorService.execute(() -> {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setText(body);
                Transport.send(message);
                Log.d("EmailSender", "✅ Email sent to: " + toEmail);
            } catch (MessagingException e) {
                Log.e("EmailSender", "❌ Error sending email: " + e.getMessage());
            }
        });
    }
}