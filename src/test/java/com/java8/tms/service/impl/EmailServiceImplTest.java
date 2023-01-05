package com.java8.tms.service.impl;


import com.java8.tms.user.dto.EmailDetailsDTO;
import com.java8.tms.user.service.impl.EmailServiceImpl;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class EmailServiceImplTest {


   @Autowired
    private EmailServiceImpl underTest;
    @Mock
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;

    @Rule
    public SmtpServerRule smtpServerRule = new SmtpServerRule(2525);

    @BeforeEach
    void setUp() {

        
    }

    @Test
    void setUpEmailDetails() {

        //give
        String email = "quocsy2511@gmail.com";
        String password = "1234";
        String fullName = "Nguyen Quoc Sy";
        String sendPasswordEmailBodyMsg = "Hi " + fullName + "\nHere is your login account\n\tEmail: "
                + email + "\n\tPassword: " + password;
        EmailDetailsDTO emailDetailsDTO = new EmailDetailsDTO(
                email,sendPasswordEmailBodyMsg,"Sign-in account"
        );

        underTest.setUpEmailDetailsForSignup(email, password, fullName);

    }

    @Test
    void sendMailNoAttachment() throws MessagingException, IOException {
        EmailDetailsDTO details  = new EmailDetailsDTO();
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom("quocsy2511@gmail.com");
        mailMessage.setTo("synqse151029@fpt.edu.vn");
        mailMessage.setText("test send mail");
        mailMessage.setSubject("TESTING");

        javaMailSender.send(mailMessage);

        MimeMessage[] receivedMessages = smtpServerRule.getMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage current = receivedMessages[0];

        assertEquals(details.getSubject(), current.getSubject());
        assertEquals(details.getRecipient(), current.getAllRecipients()[0].toString());
//        assertEquals(details.getMsgBody(), current.getContent().toString());


    }


    @Test
    void sendMailWithAttachment() {
    }
}