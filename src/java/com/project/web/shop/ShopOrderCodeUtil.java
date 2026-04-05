package com.project.web.shop;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class ShopOrderCodeUtil {

    private static final int SECRET = 7319;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private ShopOrderCodeUtil() {
    }

    public static String encode(int maHD) {
        int body = maHD ^ SECRET;
        String bodyBase36 = Integer.toString(Math.abs(body), 36).toUpperCase();
        while (bodyBase36.length() < 6) {
            bodyBase36 = "0" + bodyBase36;
        }
        if (bodyBase36.length() > 6) {
            bodyBase36 = bodyBase36.substring(bodyBase36.length() - 6);
        }

        String datePart = LocalDate.now().format(DATE_FORMAT);
        int checksum = Math.abs((body * 31 + datePart.hashCode() + SECRET) % 1000);
        String checksumPart = String.format("%03d", checksum);

        return "BK-" + datePart + "-" + bodyBase36 + checksumPart;
    }

    public static Integer decode(String code) {
        if (code == null) {
            return null;
        }

        String raw = code.trim();
        if (!raw.startsWith("BK-")) {
            return null;
        }

        String[] parts = raw.split("-");
        if (parts.length != 3) {
            return null;
        }

        try {
            String datePart = parts[1];
            String encodedPart = parts[2];
            if (datePart.length() != 8 || encodedPart.length() < 7) {
                return null;
            }

            String bodyPart = encodedPart.substring(0, encodedPart.length() - 3);
            String checksumPart = encodedPart.substring(encodedPart.length() - 3);

            int body = Integer.parseInt(bodyPart, 36);
            int checksum = Integer.parseInt(checksumPart);
            int expected = Math.abs((body * 31 + datePart.hashCode() + SECRET) % 1000);
            if (checksum != expected) {
                return null;
            }

            int maHD = body ^ SECRET;
            return maHD > 0 ? maHD : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}