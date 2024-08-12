package com.uvg.digital.service;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.polling.SyncPoller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	private final EmailClient emailClient;
    private final String senderAddress;

    public EmailService(
            @Value("${azure.communication.email.connection-string}") String connectionString,
            @Value("${azure.communication.email.sender}") String senderAddress) {
        this.emailClient = new EmailClientBuilder()
            .connectionString(connectionString)
            .buildClient();
        this.senderAddress = senderAddress;
    }

    public void sendEmail(String toEmail, String subject, String body) {
        EmailAddress toAddress = new EmailAddress(toEmail);
        EmailMessage emailMessage = new EmailMessage()
            .setSenderAddress(senderAddress)
            .setToRecipients(toAddress)
            .setSubject(subject)
            .setBodyPlainText(body);

        SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage, null);
        poller.waitForCompletion();
    }

}
