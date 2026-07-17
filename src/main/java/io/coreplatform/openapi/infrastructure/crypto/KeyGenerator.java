package io.coreplatform.openapi.infrastructure.crypto;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class KeyGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int KEY_BYTES = 32;
    private static final String LIVE_PREFIX = "sk_live_";
    private static final String TEST_PREFIX = "sk_test_";

    /**
     * Generate a new API key with the given environment.
     *
     * @param environment "LIVE" or "TEST"
     * @return a KeyPair containing the raw key and its hash
     */
    public KeyPair generate(String environment) {
        String prefix = "TEST".equalsIgnoreCase(environment) ? TEST_PREFIX : LIVE_PREFIX;

        // Generate 32 random bytes, encode as base64url without padding
        byte[] randomBytes = new byte[KEY_BYTES];
        SECURE_RANDOM.nextBytes(randomBytes);
        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        String rawKey = prefix + secret;

        // Hash the raw key for storage
        String keyHash = sha256(rawKey);

        return new KeyPair(rawKey, keyHash, prefix);
    }

    /**
     * Hash a raw API key to look up in the database.
     */
    public String hash(String rawKey) {
        return sha256(rawKey);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Holds the generated raw key (returned to user once) and its hash (stored in DB).
     */
    public record KeyPair(String rawKey, String keyHash, String keyPrefix) {
    }
}