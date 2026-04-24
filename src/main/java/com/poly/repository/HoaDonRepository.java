package com.poly.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.HoaDon;
import com.poly.entity.ReportRevenueStatistics;

import jakarta.transaction.Transactional;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
	@Modifying
	@Transactional
	@Query("DELETE FROM HoaDon h WHERE h.users.idUser = :idUser")
	void deleteByUserId(@Param("idUser") String idUser);

	Page<HoaDon> findByUsers_idUser(String email, Pageable pageable);

	@Query("SELECT COUNT(h) FROM HoaDon h WHERE h.trangthai = 'received'")
	long countReceivedOrders();
	
	@Query("SELECT new com.poly.entity.ReportRevenueStatistics( " 
			+ "SUM(hdct.soluong * (hdct.gia * (1 - hdct.giamgia/100.0))), "
			+ "SUM(hdct.soluong), " 
			+ "MAX(hdct.gia * (1 - hdct.giamgia/100.0)), " 
			+ "MIN((hdct.gia * (1 - hdct.giamgia/100.0))), " 
			+ "AVG(hdct.gia * (1 - hdct.giamgia/100.0))) " 
			+ "FROM HoaDonChiTiet hdct "
			+ "JOIN hdct.sanPham sp " 
			+ "JOIN hdct.hoaDon hd "
			+ "JOIN sp.loai l "
			+ "WHERE l.idLoai = :idLoai AND hd.trangthai = 'received'")
	ReportRevenueStatistics thongKeDoanhThuTheoLoai(@Param("idLoai") Integer loaiId);

	Page<HoaDon> findByKhachHang_IdKhachHang(Long idKhachHang, Pageable pageable);

	Page<HoaDon> findByTrangthai(String trangthai, Pageable pageable);

	long countByTrangthai(String trangthai);

	@Query(value = "SELECT MONTH(hd.ngaytao) AS thang, " +
			"SUM(hdct.soluong * (hdct.gia * (1 - hdct.giamgia / 100.0))) AS doanhThu " +
			"FROM HOADON hd JOIN HOADONCHITIET hdct ON hd.id_hoadon = hdct.id_hoadon " +
			"WHERE hd.trangthai = 'received' AND YEAR(hd.ngaytao) = :nam " +
			"GROUP BY MONTH(hd.ngaytao) ORDER BY thang", nativeQuery = true)
	List<Object[]> doanhThuTheoThang(@Param("nam") int nam);

	@Query(value = "SELECT TOP 10 sp.ten_sanpham, SUM(hdct.soluong) AS tongBan, " +
			"SUM(hdct.soluong * (hdct.gia * (1 - hdct.giamgia / 100.0))) AS doanhThu " +
			"FROM HOADONCHITIET hdct " +
			"JOIN HOADON hd ON hd.id_hoadon = hdct.id_hoadon " +
			"JOIN SANPHAM sp ON sp.id_sanpham = hdct.id_sanpham " +
			"WHERE hd.trangthai = 'received' " +
			"GROUP BY sp.id_sanpham, sp.ten_sanpham ORDER BY tongBan DESC", nativeQuery = true)
	List<Object[]> topSanPhamBanChay();

	@Query(value = "SELECT COUNT(*) FROM HOADON WHERE trangthai = 'ondelivery'", nativeQuery = true)
	long countOnDeliveryOrders();

	@Query(value = "SELECT COUNT(*) FROM USERS", nativeQuery = true)
	long countAllUsers();

	@Query(value = "SELECT COALESCE(SUM(hdct.soluong * (hdct.gia * (1 - hdct.giamgia / 100.0))), 0) " +
			"FROM HOADON hd JOIN HOADONCHITIET hdct ON hd.id_hoadon = hdct.id_hoadon " +
			"WHERE hd.trangthai = 'received'", nativeQuery = true)
	Double tongDoanhThuToanBo();
}
