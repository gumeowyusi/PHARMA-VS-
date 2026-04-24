package com.poly.service;

import com.poly.entity.LichSuDiem;
import com.poly.entity.Users;
import com.poly.repository.LichSuDiemRepository;
import com.poly.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiemTichLuyService {

    @Autowired
    private LichSuDiemRepository lichSuDiemRepository;

    @Autowired
    private UsersRepository usersRepository;

    private static final int VND_PER_POINT = 1000;

    @Transactional
    public void addPoints(String userId, int points, String ghiChu, Integer idHoaDon) {
        if (points <= 0) return;
        usersRepository.findById(userId).ifPresent(user -> {
            int current = user.getDiemTichLuy() == null ? 0 : user.getDiemTichLuy();
            user.setDiemTichLuy(current + points);
            usersRepository.save(user);
            LichSuDiem lichSu = new LichSuDiem(user, points, "EARN", ghiChu, idHoaDon);
            lichSuDiemRepository.save(lichSu);
        });
    }

    @Transactional
    public boolean redeemPoints(String userId, int points, String ghiChu) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) return false;
        int current = user.getDiemTichLuy() == null ? 0 : user.getDiemTichLuy();
        if (current < points) return false;
        user.setDiemTichLuy(current - points);
        usersRepository.save(user);
        LichSuDiem lichSu = new LichSuDiem(user, -points, "REDEEM", ghiChu, null);
        lichSuDiemRepository.save(lichSu);
        return true;
    }

    public List<LichSuDiem> getLichSuByUser(String userId) {
        return lichSuDiemRepository.findByUsers_IdUserOrderByNgayTaoDesc(userId);
    }

    public int calculatePointsForOrder(double orderTotal) {
        return (int) Math.floor(Math.max(0, orderTotal) / VND_PER_POINT);
    }
}
