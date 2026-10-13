package com.planio.app.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public void sendDeadlineReminder(
            String email,
            String taskTitle,
            int days
    ) {
        MimeMessage message = mailSender.createMimeMessage();

        try {

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");


            helper.setFrom(from, "Planio");

            helper.setTo(email);
            helper.setSubject("Planio reminder: task deadline");


            String html = """
                    <html>
                    <body>

                    <h2>Planio Reminder</h2>

                    <p>Hello!</p>

                    <p>
                    Your task 
                    "<b>%s</b>"
                    is due in 
                    <b>%d days</b>.
                    </p>

                    <hr>

                    <p>
                    Open Planio to check your task.
                    </p>

                    <br>

                    <p>
                    Regards,<br>
                    Planio Team
                    </p>

                    </body>
                    </html>
                    """.formatted(taskTitle, days);


            helper.setText(html, true);


            mailSender.send(message);


        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
