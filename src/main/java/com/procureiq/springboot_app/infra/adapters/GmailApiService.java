package com.procureiq.springboot_app.infra.adapters;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Label;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class GmailApiService {

    @Value("${gmail.credentials.json.path:}")
    private String credentialsJsonPath;

    @Value("${gmail.user:me}")
    private String gmailUser;

    private Gmail gmailService;
    private boolean isMockMode = false;

    private final List<Message> mockMessages = new ArrayList<>();
    private final List<Draft> mockDrafts = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            InputStream in = null;
            String credentialsEnv = System.getenv("GMAIL_CREDENTIALS_JSON");
            if (credentialsEnv != null && !credentialsEnv.trim().isEmpty()) {
                in = new java.io.ByteArrayInputStream(credentialsEnv.getBytes());
            } else if (credentialsJsonPath != null && !credentialsJsonPath.trim().isEmpty()) {
                in = new FileInputStream(credentialsJsonPath);
            }

            if (in == null) {
                isMockMode = true;
                return;
            }

            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                    .createScoped(List.of(
                            "https://www.googleapis.com/auth/gmail.compose",
                            "https://www.googleapis.com/auth/gmail.readonly",
                            "https://www.googleapis.com/auth/gmail.send"
                    ));
            gmailService = new Gmail.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                    .setApplicationName("procureiq-springboot")
                    .build();
        } catch (Exception e) {
            isMockMode = true;
        }
    }

    public boolean isMockMode() {
        return isMockMode;
    }

    public Message sendEmail(String to, String subject, String bodyText) throws Exception {
        if (isMockMode) {
            Message message = new Message()
                    .setId("mock-msg-" + System.currentTimeMillis())
                    .setThreadId("mock-thread-" + System.currentTimeMillis());
            mockMessages.add(message);
            return message;
        }

        MimeMessage emailContent = createEmail(to, gmailUser.equals("me") ? "sender@example.com" : gmailUser, subject, bodyText);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        return gmailService.users().messages().send(gmailUser, message).execute();
    }

    public Draft createDraft(String to, String subject, String bodyText) throws Exception {
        if (isMockMode) {
            Draft draft = new Draft()
                    .setId("mock-draft-" + System.currentTimeMillis())
                    .setMessage(new Message().setId("mock-draft-msg-" + System.currentTimeMillis()));
            mockDrafts.add(draft);
            return draft;
        }

        MimeMessage emailContent = createEmail(to, gmailUser.equals("me") ? "sender@example.com" : gmailUser, subject, bodyText);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        Draft draft = new Draft();
        draft.setMessage(message);

        return gmailService.users().drafts().create(gmailUser, draft).execute();
    }

    public List<Message> listMessages() throws Exception {
        if (isMockMode) {
            return mockMessages;
        }

        ListMessagesResponse response = gmailService.users().messages().list(gmailUser).execute();
        List<Message> messages = new ArrayList<>();
        if (response.getMessages() != null) {
            messages.addAll(response.getMessages());
        }
        return messages;
    }

    public List<Draft> listDrafts() throws Exception {
        if (isMockMode) {
            return mockDrafts;
        }
        List<Draft> drafts = new ArrayList<>();
        var response = gmailService.users().drafts().list(gmailUser).execute();
        if (response.getDrafts() != null) {
            drafts.addAll(response.getDrafts());
        }
        return drafts;
    }

    public Label getLabelStats(String labelId) throws Exception {
        if (isMockMode) {
            return new Label()
                    .setId(labelId)
                    .setName(labelId)
                    .setMessagesTotal(10)
                    .setMessagesUnread(2);
        }
        return gmailService.users().labels().get(gmailUser, labelId).execute();
    }

    private MimeMessage createEmail(String to, String from, String subject, String bodyText) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }
}
