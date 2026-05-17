package com.project.web.shop;

import com.project.dao.BookDAO;
import com.project.model.Book;
import com.project.model.dto.ShopCartItem;
// SQLException removed
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;

public final class ShopCartSupport {

    public static final String CART_SESSION_KEY = "SHOP_CART";
    public static final String ORDER_HISTORY_SESSION_KEY = "SHOP_ORDER_HISTORY";

    private ShopCartSupport() {
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, Integer> getCart(HttpSession session) {
        Object raw = session.getAttribute(CART_SESSION_KEY);
        if (raw instanceof Map) {
            return (Map<Integer, Integer>) raw;
        }
        Map<Integer, Integer> cart = new LinkedHashMap<Integer, Integer>();
        session.setAttribute(CART_SESSION_KEY, cart);
        return cart;
    }

    public static int getCartCount(Map<Integer, Integer> cart) {
        int count = 0;
        for (Integer qty : cart.values()) {
            if (qty != null && qty > 0) {
                count += qty;
            }
        }
        return count;
    }

    public static List<ShopCartItem> buildCartItems(BookDAO bookDAO, Map<Integer, Integer> cart) {
        List<ShopCartItem> items = new ArrayList<ShopCartItem>();
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Integer maSach = entry.getKey();
            Integer soLuong = entry.getValue();
            if (maSach == null || soLuong == null || soLuong <= 0) {
                continue;
            }

            Book book;
            try {
                book = bookDAO.findById(maSach);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            if (book == null) {
                continue;
            }

            ShopCartItem item = new ShopCartItem();
            item.setMaSach(book.getMaSach());
            item.setTenSach(book.getTenSach());
            item.setTacGia(book.getTacGia());
            item.setDonGia(book.getDonGia());
            item.setSoLuong(soLuong);
            item.setTonKho(book.getSoLuong() == null ? 0 : book.getSoLuong());
            item.setAnhBia(book.getAnhBia());
            items.add(item);
        }

        return items;
    }

    @SuppressWarnings("unchecked")
    public static List<Integer> getOrderHistory(HttpSession session) {
        Object raw = session.getAttribute(ORDER_HISTORY_SESSION_KEY);
        if (raw instanceof List) {
            return (List<Integer>) raw;
        }
        List<Integer> history = new LinkedList<Integer>();
        session.setAttribute(ORDER_HISTORY_SESSION_KEY, history);
        return history;
    }

    public static void addOrderToHistory(HttpSession session, int maHD) {
        List<Integer> history = getOrderHistory(session);
        history.remove(Integer.valueOf(maHD));
        history.add(0, maHD);

        while (history.size() > 20) {
            history.remove(history.size() - 1);
        }
    }
}