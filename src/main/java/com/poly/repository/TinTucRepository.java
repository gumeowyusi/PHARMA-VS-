package com.poly.repository;

import com.poly.entity.TinTuc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TinTucRepository extends JpaRepository<TinTuc, Long> {
    Page<TinTuc> findByTrangThaiTrueOrderByNgayDangDesc(Pageable pageable);
    List<TinTuc> findByTrangThaiTrueOrderByNgayDangDesc();
    List<TinTuc> findByTheLoaiAndTrangThaiTrueOrderByNgayDangDesc(String theLoai);
    Page<TinTuc> findAllByOrderByNgayDangDesc(Pageable pageable);
}
