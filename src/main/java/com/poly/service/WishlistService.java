package com.poly.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.entity.SanPham;
import com.poly.entity.Users;
import com.poly.entity.Wishlist;
import com.poly.repository.SanPhamRepository;
import com.poly.repository.UsersRepository;
import com.poly.repository.WishlistRepository;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private SanPhamRepository sanPhamRepository;

    public Page<Wishlist> getWishlist(String idUser, int page, int limit) {
        return wishlistRepository.findByUsers_IdUserOrderByNgaytaoDesc(idUser, PageRequest.of(page, limit));
    }

    public boolean isWishlisted(String idUser, Integer idSanpham) {
        return wishlistRepository.existsByUsers_IdUserAndSanPham_IdSanpham(idUser, idSanpham);
    }

    public long countWishlist(String idUser) {
        return wishlistRepository.countByIdUser(idUser);
    }

    @Transactional
    public boolean toggle(String idUser, Integer idSanpham) {
        if (wishlistRepository.existsByUsers_IdUserAndSanPham_IdSanpham(idUser, idSanpham)) {
            wishlistRepository.deleteByUsers_IdUserAndSanPham_IdSanpham(idUser, idSanpham);
            return false;
        } else {
            Users user = usersRepository.findById(idUser)
                    .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
            SanPham sanPham = sanPhamRepository.findById(idSanpham)
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
            wishlistRepository.save(new Wishlist(user, sanPham, new Date()));
            return true;
        }
    }

    @Transactional
    public void remove(String idUser, Integer idSanpham) {
        wishlistRepository.deleteByUsers_IdUserAndSanPham_IdSanpham(idUser, idSanpham);
    }
}
