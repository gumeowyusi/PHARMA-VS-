package com.poly.repository;

import com.poly.entity.LienHe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LienHeRepository extends JpaRepository<LienHe, Integer> {

    List<LienHe> findAllByOrderByNgayGuiDesc();

    @Query("SELECT COUNT(l) FROM LienHe l WHERE l.daDoc = false")
    long countUnread();
}
