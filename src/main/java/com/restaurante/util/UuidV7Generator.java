package com.restaurante.util;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.UUID;

public final class UuidV7Generator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private UuidV7Generator() {
    }

    public static UUID generate() {
        long timestamp = System.currentTimeMillis();
        byte[] uuidBytes = new byte[16];

        ByteBuffer.wrap(uuidBytes, 0, 8).putLong(timestamp << 16);

        byte[] randomBytes = new byte[10];
        SECURE_RANDOM.nextBytes(randomBytes);
        System.arraycopy(randomBytes, 0, uuidBytes, 6, 10);

        uuidBytes[6] = (byte) ((uuidBytes[6] & 0x0F) | 0x70);
        uuidBytes[8] = (byte) ((uuidBytes[8] & 0x3F) | 0x80);

        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (uuidBytes[i] & 0xFF);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (uuidBytes[i] & 0xFF);
        }
        return new UUID(msb, lsb);
    }
}
