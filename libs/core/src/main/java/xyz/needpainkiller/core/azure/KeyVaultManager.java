package xyz.needpainkiller.core.azure;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KeyVaultManager {
    private final String keyVaultUri;

    public KeyVaultManager(String keyVaultUri) {
        if (keyVaultUri == null || keyVaultUri.isEmpty()) {
            log.error("keyVaultUri is null or empty");
            throw new IllegalArgumentException("keyVaultUri is null or empty");
        }
        this.keyVaultUri = keyVaultUri;
    }

    public String getSecretValue(String secretName) {
        SecretClient secretClient = new SecretClientBuilder().vaultUrl(keyVaultUri).credential(new DefaultAzureCredentialBuilder().build()).buildClient();
        KeyVaultSecret retrievedSecret = secretClient.getSecret(secretName);
        return retrievedSecret.getValue();
    }
}
