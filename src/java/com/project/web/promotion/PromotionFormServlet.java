package com.project.web.promotion;

import com.project.dao.PromotionDAO;
import com.project.model.Promotion;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
// SQLException removed; use generic exception handling after JPA migration
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "PromotionFormServlet", urlPatterns = {"/promotions/new", "/promotions/edit"})
public class PromotionFormServlet extends HttpServlet {

    private final PromotionDAO promotionDAO = new PromotionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        boolean isEdit = "/promotions/edit".equals(request.getServletPath());
        if (isEdit) {
            Integer maKM = parseInteger(request.getParameter("id"));
            if (maKM == null) {
                response.sendRedirect(request.getContextPath() + "/promotions");
                return;
            }

            try {
                Promotion promotion = promotionDAO.findById(maKM);
                if (promotion == null) {
                    response.sendRedirect(request.getContextPath() + "/promotions");
                    return;
                }
                request.setAttribute("promotion", promotion);
            } catch (Exception ex) {
                request.setAttribute("errorMessage", "Khong tai duoc khuyen mai: " + ex.getMessage());
            }
        } else {
            request.setAttribute("promotion", new Promotion());
        }

        request.setAttribute("isEdit", isEdit);
        request.getRequestDispatcher("/promotions/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        boolean isEdit = "/promotions/edit".equals(request.getServletPath());

        Promotion promotion = buildPromotionFromRequest(request);
        if (isEdit) {
            Integer maKM = parseInteger(request.getParameter("id"));
            if (maKM == null) {
                response.sendRedirect(request.getContextPath() + "/promotions");
                return;
            }
            promotion.setMaKM(maKM);
        }

        String validationError = validatePromotion(promotion);
        if (validationError != null) {
            request.setAttribute("errorMessage", validationError);
            request.setAttribute("promotion", promotion);
            request.setAttribute("isEdit", isEdit);
            request.getRequestDispatcher("/promotions/form.jsp").forward(request, response);
            return;
        }

        try {
            Integer excludeMaKM = isEdit ? promotion.getMaKM() : null;
            if (promotionDAO.existsCouponCode(promotion.getMaCoupon(), excludeMaKM)) {
                request.setAttribute("errorMessage", "Ma coupon da ton tai. Vui long nhap ma khac.");
                request.setAttribute("promotion", promotion);
                request.setAttribute("isEdit", isEdit);
                request.getRequestDispatcher("/promotions/form.jsp").forward(request, response);
                return;
            }

            if (isEdit) {
                promotionDAO.update(promotion);
                response.sendRedirect(request.getContextPath() + "/promotions?msg=updated");
            } else {
                promotionDAO.insert(promotion);
                response.sendRedirect(request.getContextPath() + "/promotions?msg=created");
            }
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Luu khuyen mai that bai: " + ex.getMessage());
            request.setAttribute("promotion", promotion);
            request.setAttribute("isEdit", isEdit);
            request.getRequestDispatcher("/promotions/form.jsp").forward(request, response);
        }
    }

    private Promotion buildPromotionFromRequest(HttpServletRequest request) {
        Promotion promotion = new Promotion();
        promotion.setTenKM(request.getParameter("tenKM"));
        promotion.setHinhThuc(request.getParameter("hinhThuc"));
        promotion.setGiaTri(parseBigDecimal(request.getParameter("giaTri")));
        promotion.setMaCoupon(request.getParameter("maCoupon"));
        promotion.setNgayBD(parseDate(request.getParameter("ngayBD")));
        promotion.setNgayKT(parseDate(request.getParameter("ngayKT")));
        return promotion;
    }

    private String validatePromotion(Promotion promotion) {
        if (promotion.getTenKM() == null || promotion.getTenKM().trim().isEmpty()) {
            return "Ten khuyen mai khong duoc de trong.";
        }

        if (promotion.getHinhThuc() == null || promotion.getHinhThuc().trim().isEmpty()) {
            return "Hinh thuc khuyen mai khong duoc de trong.";
        }

        if (promotion.getGiaTri() == null) {
            return "Gia tri khuyen mai khong duoc de trong.";
        }

        if (promotion.getGiaTri().compareTo(BigDecimal.ZERO) < 0) {
            return "Gia tri khuyen mai phai lon hon hoac bang 0.";
        }

        if (("%".equalsIgnoreCase(promotion.getHinhThuc()) || "COUPON".equalsIgnoreCase(promotion.getHinhThuc()))
                && (promotion.getMaCoupon() == null || promotion.getMaCoupon().trim().isEmpty())) {
            return "Hinh thuc nay can ma coupon de ap dung vao hoa don.";
        }

        if (promotion.getNgayBD() != null && promotion.getNgayKT() != null
                && promotion.getNgayBD().after(promotion.getNgayKT())) {
            return "Ngay bat dau khong duoc lon hon ngay ket thuc.";
        }

        return null;
    }

    private Integer parseInteger(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Date parseDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        try {
            return Date.valueOf(raw.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}