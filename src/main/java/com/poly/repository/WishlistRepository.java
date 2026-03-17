package com.poly.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

    Page<Wishlist> findByUsers_IdUserOrderByNgaytaoDesc(String idUser, Pageable pageable);

    Optional<Wishlist> findByUsers_IdUserAndSanPham_IdSanpham(String idUser, Integer idSanpham);

    boolean existsByUsers_IdUserAndSanPham_IdSanpham(String idUser, Integer idSanpham);

    void deleteByUsers_IdUserAndSanPham_IdSanpham(String idUser, Integer idSanpham);

    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.users.idUser = :idUser")
    long countByIdUser(@Param("idUser") String idUser);
}
