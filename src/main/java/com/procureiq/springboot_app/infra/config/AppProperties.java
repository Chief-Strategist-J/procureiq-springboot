package com.procureiq.springboot_app.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Value("${github.token:}")
    private String githubToken;

    @Value("${gmail.credentials.json.path:}")
    private String gmailCredentialsJsonPath;

    @Value("${gmail.user:me}")
    private String gmailUser;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${twilio.api.base:https://api.twilio.com/2010-04-01/Accounts}")
    private String twilioApiBase;

    @Value("${vapi.calls.endpoint:https://api.vapi.ai/call/phone}")
    private String vapiCallsEndpoint;

    public String getGithubToken() {
        return githubToken;
    }

    public String getGmailCredentialsJsonPath() {
        return gmailCredentialsJsonPath;
    }

    public String getGmailUser() {
        return gmailUser;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public String getTwilioApiBase() {
        return twilioApiBase;
    }

    public String getVapiCallsEndpoint() {
        return vapiCallsEndpoint;
    }
}
