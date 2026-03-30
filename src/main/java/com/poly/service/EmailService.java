package com.poly.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.poly.entity.HoaDonChiTiet;
import com.poly.entity.Users;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;
	
	@Value("${spring.mail.username}")
	private String fromEmail;

	public void sendEmailAcctiveAccount(String to, String subject, String body) throws MessagingException, IOException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		helper.setTo(to);
		helper.setFrom(fromEmail);
		helper.setSubject(subject);
		helper.setText("<h3>Nhấn vào link sau để kích hoạt tài khoản: http://localhost:8080/active-account?token="
				+ body + "</h3>", true);

		// Gửi email
		emailSender.send(message);
	}

	public void sendEmailPassword(String to, String subject, Users user) throws MessagingException, IOException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		helper.setTo(to);
		helper.setFrom(fromEmail);
		helper.setSubject(subject);
		helper.setText("<h3>Mật khẩu tài khoản cũ của bạn là: " + user.getMatkhau() + "</h3>", true);

		// Gửi email
		emailSender.send(message);
	}
	
	public void sendOrderConfirmationEmail(String to, String subject,
			com.poly.entity.HoaDon hoaDon, List<HoaDonChiTiet> listHoaDonChiTiets, double deliveryPrice)
			throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		double subtotal = 0;
		StringBuilder rows = new StringBuilder();
		for (HoaDonChiTiet item : listHoaDonChiTiets) {
			double lineTotal = (item.getGiamgia() == 0)
					? (double) item.getGia() * item.getSoluong()
					: ((double) item.getGia() * (100 - item.getGiamgia()) / 100) * item.getSoluong();
			subtotal += lineTotal;
			String discountBadge = item.getGiamgia() > 0
					? "<span style='background:#fee2e2;color:#dc2626;padding:2px 6px;border-radius:4px;font-size:11px;margin-left:4px;'>-" + item.getGiamgia() + "%</span>"
					: "";
			rows.append("<tr>")
				.append("<td style='padding:10px 12px;border-bottom:1px solid #f1f5f9;text-align:left;'>")
				.append(item.getSanPham().getTenSanpham()).append(discountBadge).append("</td>")
				.append("<td style='padding:10px 12px;border-bottom:1px solid #f1f5f9;text-align:center;'>").append(item.getSoluong()).append("</td>")
				.append("<td style='padding:10px 12px;border-bottom:1px solid #f1f5f9;text-align:right;color:#0d6efd;font-weight:600;'>")
				.append(String.format("%,.0f", lineTotal)).append("₫</td>")
				.append("</tr>");
		}
		double total = subtotal + deliveryPrice;

		String paymentInfo = hoaDon.getGiaohang() != null && hoaDon.getGiaohang().contains("PayOS")
				? "💳 PayOS (Đã thanh toán online)" : "💵 COD (Thanh toán khi nhận hàng)";
		String statusLabel = "paid".equals(hoaDon.getTrangthai())
				? "<span style='background:#dbeafe;color:#1d4ed8;padding:4px 12px;border-radius:20px;font-size:13px;font-weight:600;'>✅ Đã thanh toán</span>"
				: "<span style='background:#fef9c3;color:#854d0e;padding:4px 12px;border-radius:20px;font-size:13px;font-weight:600;'>⏳ Chờ xác nhận</span>";

		String html = "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='margin:0;padding:0;background:#f8fafc;font-family:Arial,sans-serif;'>"
			+ "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f8fafc;padding:30px 0;'><tr><td align='center'>"
			+ "<table width='600' cellpadding='0' cellspacing='0' style='background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 2px 12px rgba(0,0,0,.08);'>"
			// Header
			+ "<tr><td style='background:linear-gradient(135deg,#0d6efd,#4facfe);padding:32px 40px;text-align:center;'>"
			+ "<div style='background:rgba(255,255,255,.15);display:inline-block;padding:10px 18px;border-radius:8px;margin-bottom:12px;'>"
			+ "<span style='color:#fff;font-size:20px;font-weight:800;letter-spacing:1px;'>🏥 MEDISALE</span></div>"
			+ "<h1 style='color:#fff;margin:0;font-size:22px;font-weight:700;'>Đặt hàng thành công!</h1>"
			+ "<p style='color:rgba(255,255,255,.85);margin:8px 0 0;font-size:14px;'>Cảm ơn bạn đã tin tưởng MEDISALE</p>"
			+ "</td></tr>"
			// Order ID + status
			+ "<tr><td style='padding:24px 40px 0;'>"
			+ "<div style='background:#f8fafc;border-radius:8px;padding:16px 20px;display:flex;align-items:center;justify-content:space-between;border:1px solid #e2e8f0;'>"
			+ "<div><span style='color:#64748b;font-size:13px;'>Mã đơn hàng</span><br>"
			+ "<span style='font-size:20px;font-weight:800;color:#0d6efd;'>#" + hoaDon.getIdHoadon() + "</span></div>"
			+ "<div style='text-align:right;'><span style='color:#64748b;font-size:13px;'>Trạng thái</span><br>" + statusLabel + "</div>"
			+ "</div></td></tr>"
			// Info row
			+ "<tr><td style='padding:16px 40px 0;'>"
			+ "<table width='100%'><tr>"
			+ "<td width='50%' style='padding-right:8px;'><div style='background:#f8fafc;border-radius:8px;padding:12px 16px;border:1px solid #e2e8f0;'>"
			+ "<div style='color:#64748b;font-size:12px;margin-bottom:4px;'>📦 Phương thức giao hàng</div>"
			+ "<div style='font-size:13px;font-weight:600;color:#1e293b;'>" + (hoaDon.getGiaohang() != null ? hoaDon.getGiaohang().split("\\|")[0].trim() : "Tiêu chuẩn") + "</div>"
			+ "</div></td>"
			+ "<td width='50%' style='padding-left:8px;'><div style='background:#f8fafc;border-radius:8px;padding:12px 16px;border:1px solid #e2e8f0;'>"
			+ "<div style='color:#64748b;font-size:12px;margin-bottom:4px;'>💳 Thanh toán</div>"
			+ "<div style='font-size:13px;font-weight:600;color:#1e293b;'>" + paymentInfo + "</div>"
			+ "</div></td>"
			+ "</tr></table></td></tr>"
			// Address
			+ "<tr><td style='padding:12px 40px 0;'>"
			+ "<div style='background:#f8fafc;border-radius:8px;padding:12px 16px;border:1px solid #e2e8f0;'>"
			+ "<div style='color:#64748b;font-size:12px;margin-bottom:4px;'>📍 Địa chỉ giao hàng</div>"
			+ "<div style='font-size:13px;font-weight:600;color:#1e293b;'>" + hoaDon.getDiachi() + "</div>"
			+ "</div></td></tr>"
			// Product table
			+ "<tr><td style='padding:20px 40px 0;'>"
			+ "<h3 style='margin:0 0 12px;font-size:15px;color:#1e293b;'>🛒 Sản phẩm đặt mua</h3>"
			+ "<table width='100%' style='border-collapse:collapse;border:1px solid #e2e8f0;border-radius:8px;overflow:hidden;'>"
			+ "<thead><tr style='background:#f1f5f9;'>"
			+ "<th style='padding:10px 12px;text-align:left;font-size:13px;color:#64748b;font-weight:600;'>Sản phẩm</th>"
			+ "<th style='padding:10px 12px;text-align:center;font-size:13px;color:#64748b;font-weight:600;'>SL</th>"
			+ "<th style='padding:10px 12px;text-align:right;font-size:13px;color:#64748b;font-weight:600;'>Thành tiền</th>"
			+ "</tr></thead><tbody>" + rows + "</tbody>"
			+ "<tfoot>"
			+ "<tr><td colspan='2' style='padding:8px 12px;text-align:right;color:#64748b;font-size:13px;'>Tạm tính:</td>"
			+ "<td style='padding:8px 12px;text-align:right;font-weight:600;'>" + String.format("%,.0f", subtotal) + "₫</td></tr>"
			+ "<tr><td colspan='2' style='padding:8px 12px;text-align:right;color:#64748b;font-size:13px;'>Phí giao hàng:</td>"
			+ "<td style='padding:8px 12px;text-align:right;font-weight:600;'>" + String.format("%,.0f", deliveryPrice) + "₫</td></tr>"
			+ "<tr style='background:#eff6ff;'><td colspan='2' style='padding:12px;text-align:right;font-size:15px;font-weight:700;color:#0d6efd;'>Tổng cộng:</td>"
			+ "<td style='padding:12px;text-align:right;font-size:16px;font-weight:800;color:#dc2626;'>" + String.format("%,.0f", total) + "₫</td></tr>"
			+ "</tfoot></table></td></tr>"
			// Footer
			+ "<tr><td style='padding:24px 40px 32px;text-align:center;'>"
			+ "<p style='color:#64748b;font-size:13px;margin:0 0 16px;'>Đơn hàng sẽ được xử lý trong <strong>1-2 ngày làm việc</strong>.</p>"
			+ "<a href='http://localhost:8080/order' style='display:inline-block;background:#0d6efd;color:#fff;padding:12px 28px;border-radius:8px;text-decoration:none;font-weight:600;font-size:14px;'>Xem đơn hàng của bạn →</a>"
			+ "<p style='color:#94a3b8;font-size:12px;margin:20px 0 0;'>© 2025 MEDISALE · Chăm sóc sức khỏe của bạn</p>"
			+ "</td></tr>"
			+ "</table></td></tr></table>"
			+ "</body></html>";

		helper.setTo(to);
		helper.setFrom(fromEmail);
		helper.setSubject(subject);
		helper.setText(html, true);
		emailSender.send(message);
	}
	 
	 public void sendPasswordResetEmail(String toEmail, String subject, String token) {
	        try {
	            MimeMessage mimeMessage = emailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
	            helper.setTo(toEmail);
	            helper.setFrom(fromEmail);
	            helper.setSubject(subject);
	            
	            String htmlContent = """
	                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 5px;">
	                        <div style="text-align: center; margin-bottom: 20px;">
	                            <h2 style="color: #4a6ee0;">Đặt Lại Mật Khẩu</h2>
	                        </div>
	                        <div style="margin-bottom: 20px;">
	                            <p>Xin chào,</p>
	                            <p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Nhấn vào nút bên dưới để đặt lại mật khẩu:</p>
	                        </div>
	                        <div style="text-align: center; margin-bottom: 20px;">
	                            <a href="http://localhost:8080/reset-password?token=TOKEN" style="display: inline-block; padding: 12px 24px; background-color: #4a6ee0; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;">Đặt Lại Mật Khẩu</a>
	                        </div>
	                        <div style="margin-bottom: 20px;">
	                            <p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>
	                            <p>Liên kết này sẽ hết hạn sau 30 phút.</p>
	                        </div>
	                    </div>
	                    """.replace("TOKEN", token);
	            
	            helper.setText(htmlContent, true);
	            emailSender.send(mimeMessage);
	            
	        } catch (Exception e) {
	            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
	        }
	    }
}
