package com.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.HoaDonChiTiet;
import com.poly.entity.HoaDonChiTietId;

import jakarta.transaction.Transactional;
import java.util.List;

public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, HoaDonChiTietId>{
	List<HoaDonChiTiet> findByHoaDon_IdHoadon(Integer idHoadon);
	@Modifying
	@Transactional
	@Query("DELETE FROM HoaDonChiTiet h WHERE h.hoaDon.users.idUser = :idUser")
	void deleteByUserId(@Param("idUser") String idUser);
}
