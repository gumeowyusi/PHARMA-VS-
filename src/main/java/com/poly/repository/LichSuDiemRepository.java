package com.poly.repository;

import com.poly.entity.LichSuDiem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuDiemRepository extends JpaRepository<LichSuDiem, Long> {
    List<LichSuDiem> findByUsers_IdUserOrderByNgayTaoDesc(String userId);
    Page<LichSuDiem> findByUsers_IdUserOrderByNgayTaoDesc(String userId, Pageable pageable);
    boolean existsByIdHoaDonAndLoaiGiaoDich(Integer idHoaDon, String loaiGiaoDich);
}
