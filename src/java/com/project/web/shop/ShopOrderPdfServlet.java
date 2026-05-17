package com.project.web.shop;

import com.project.dao.InvoiceDAO;
import com.project.model.Invoice;
import com.project.model.InvoiceItem;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
// SQLException removed; use generic exception handling after JPA migration
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ShopOrderPdfServlet", urlPatterns = {"/shop/order/pdf"})
public class ShopOrderPdfServlet extends HttpServlet {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        jakarta.servlet.http.HttpSession session = request.getSession(true);
        String code = request.getParameter("code");
        Integer maHD = ShopOrderCodeUtil.decode(code);
        if (maHD == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order code");
            return;
        }

        boolean canAccess = ShopOrderAccessSupport.isOrderInHistory(session, maHD)
                || ShopOrderAccessSupport.hasCode(session, code);

        if (!canAccess) {
            String phone = trimToNull(request.getParameter("phone"));
            String email = trimToNull(request.getParameter("email"));
            if (phone != null || email != null) {
                try {
                    Invoice verified = invoiceDAO.findByIdForCustomerLookup(maHD, phone, email);
                    if (verified != null) {
                        ShopOrderAccessSupport.grantCode(session, code);
                        canAccess = true;
                    }
                } catch (Exception ex) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot verify order");
                    return;
                }
            }
        }

        if (!canAccess) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Order access denied");
            return;
        }

        try {
            Invoice invoice = invoiceDAO.findById(maHD);
            if (invoice == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                return;
            }

            List<InvoiceItem> items = invoiceDAO.findItemsByInvoiceId(maHD);
            List<String> lines = buildLines(invoice, items, ShopOrderCodeUtil.encode(maHD));
            byte[] pdf = buildSimplePdf(lines);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=order-" + maHD + ".pdf");
            response.setContentLength(pdf.length);
            response.getOutputStream().write(pdf);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot generate PDF");
        }
    }

    private List<String> buildLines(Invoice invoice, List<InvoiceItem> items, String orderCode) {
        List<String> lines = new ArrayList<String>();
        lines.add("BOOKORA ORDER INVOICE");
        lines.add("Order code: " + orderCode);
        lines.add("Invoice id: " + invoice.getMaHD());
        lines.add("Date: " + invoice.getNgayLap());
        lines.add("Customer: " + safe(invoice.getTenKH()));
        lines.add("Phone: " + safe(invoice.getDienThoaiKH()));
        lines.add("Email: " + safe(invoice.getEmailKH()));
        lines.add("---------------------------------------------");
        if (items == null || items.isEmpty()) {
            lines.add("No items");
        } else {
            for (InvoiceItem item : items) {
                lines.add(safe(item.getTenSach()) + " | Qty: " + item.getSoLuong() + " | Unit: " + item.getDonGia() + " | Total: " + item.getThanhTien());
            }
        }
        lines.add("---------------------------------------------");
        lines.add("Discount: " + (invoice.getGiamGia() == null ? 0 : invoice.getGiamGia()));
        lines.add("VAT: " + (invoice.getThueVAT() == null ? 0 : invoice.getThueVAT()));
        lines.add("Total: " + (invoice.getTongTien() == null ? 0 : invoice.getTongTien()));
        return lines;
    }

    private String safe(String v) {
        return v == null ? "" : v;
    }

    private byte[] buildSimplePdf(List<String> lines) {
        try {
            StringBuilder content = new StringBuilder();
            content.append("BT\n");
            content.append("/F1 11 Tf\n");
            content.append("14 TL\n");
            content.append("50 790 Td\n");

            int count = 0;
            for (String line : lines) {
                if (count > 52) {
                    break;
                }
                content.append("(").append(escapePdfText(line)).append(") Tj\nT*\n");
                count++;
            }
            content.append("ET\n");

            byte[] streamBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            List<Integer> offsets = new ArrayList<Integer>();

            out.write("%PDF-1.4\n".getBytes(StandardCharsets.ISO_8859_1));

            offsets.add(out.size());
            out.write("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

            offsets.add(out.size());
            out.write("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

            offsets.add(out.size());
            out.write("3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

            offsets.add(out.size());
            out.write("4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

            offsets.add(out.size());
            out.write(("5 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n").getBytes(StandardCharsets.ISO_8859_1));
            out.write(streamBytes);
            out.write("endstream\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));

            int xrefOffset = out.size();
            out.write(("xref\n0 " + (offsets.size() + 1) + "\n").getBytes(StandardCharsets.ISO_8859_1));
            out.write("0000000000 65535 f \n".getBytes(StandardCharsets.ISO_8859_1));
            for (Integer offset : offsets) {
                out.write(String.format("%010d 00000 n \n", offset).getBytes(StandardCharsets.ISO_8859_1));
            }
            out.write(("trailer\n<< /Size " + (offsets.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF").getBytes(StandardCharsets.ISO_8859_1));

            return out.toByteArray();
        } catch (IOException ex) {
            return new byte[0];
        }
    }

    private String escapePdfText(String text) {
        if (text == null) {
            return "";
        }
        String normalized = text.replaceAll("[^\\x20-\\x7E]", "?");
        return normalized.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}