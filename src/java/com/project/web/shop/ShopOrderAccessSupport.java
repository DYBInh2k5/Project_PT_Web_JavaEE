package com.project.web.shop;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.servlet.http.HttpSession;

public final class ShopOrderAccessSupport {

    private static final String SESSION_KEY = "SHOP_ORDER_AUTH_CODES";

    private ShopOrderAccessSupport() {
    }

    public static boolean isOrderInHistory(HttpSession session, Integer maHD) {
        if (session == null || maHD == null) {
            return false;
        }
        List<Integer> history = ShopCartSupport.getOrderHistory(session);
        return history.contains(maHD);
    }

    public static void grantCode(HttpSession session, String code) {
        if (session == null || code == null || code.trim().isEmpty()) {
            return;
        }
        Set<String> authCodes = getAuthCodes(session);
        authCodes.add(normalize(code));
    }

    public static boolean hasCode(HttpSession session, String code) {
        if (session == null || code == null || code.trim().isEmpty()) {
            return false;
        }
        Set<String> authCodes = getAuthCodes(session);
        return authCodes.contains(normalize(code));
    }

    @SuppressWarnings("unchecked")
    private static Set<String> getAuthCodes(HttpSession session) {
        Object raw = session.getAttribute(SESSION_KEY);
        if (raw instanceof Set) {
            return (Set<String>) raw;
        }

        Set<String> authCodes = new HashSet<String>();
        session.setAttribute(SESSION_KEY, authCodes);
        return authCodes;
    }

    private static String normalize(String code) {
        return code.trim().toUpperCase();
    }
}