package xyz.needpainkiller.core.lib.mail;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmtpAuthenticator extends Authenticator {
    public SmtpAuthenticator() {
        super();
    }

    @Value("${spring.mail.username}")
    String username;
    @Value("${spring.mail.password}")
    String password;

    @Override
    public PasswordAuthentication getPasswordAuthentication() {

        if ((username != null) && (username.length() > 0) && (password != null)
                && (password.length() > 0)) {

            return new PasswordAuthentication(username, password);
        }

        return null;
    }
}