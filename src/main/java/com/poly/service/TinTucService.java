package com.poly.service;

import com.poly.entity.TinTuc;
import com.poly.repository.TinTucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TinTucService {

    @Autowired
    private TinTucRepository tinTucRepository;

    public List<TinTuc> getAllPublished() {
        return tinTucRepository.findByTrangThaiTrueOrderByNgayDangDesc();
    }

    public List<TinTuc> getByTheLoai(String theLoai) {
        return tinTucRepository.findByTheLoaiAndTrangThaiTrueOrderByNgayDangDesc(theLoai);
    }

    public Page<TinTuc> getAllForAdmin(int page, int size) {
        return tinTucRepository.findAllByOrderByNgayDangDesc(PageRequest.of(page, size));
    }

    public TinTuc getById(Long id) {
        return tinTucRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tin tức không tồn tại!"));
    }

    public TinTuc create(TinTuc tinTuc) {
        if (tinTuc.getNgayDang() == null) tinTuc.setNgayDang(new Date());
        if (tinTuc.getNguonBao() == null || tinTuc.getNguonBao().isBlank()) tinTuc.setNguonBao("ADMIN");
        return tinTucRepository.save(tinTuc);
    }

    public TinTuc update(Long id, TinTuc updated) {
        TinTuc existing = getById(id);
        existing.setTieuDe(updated.getTieuDe());
        existing.setNoiDung(updated.getNoiDung());
        existing.setTomTat(updated.getTomTat());
        existing.setTheLoai(updated.getTheLoai());
        existing.setTacGia(updated.getTacGia());
        existing.setAnhDai(updated.getAnhDai());
        existing.setTrangThai(updated.isTrangThai());
        if (updated.getNgayDang() != null) existing.setNgayDang(updated.getNgayDang());
        return tinTucRepository.save(existing);
    }

    public void incrementView(Long id) {
        tinTucRepository.findById(id).ifPresent(t -> {
            t.setLuotXem(t.getLuotXem() + 1);
            tinTucRepository.save(t);
        });
    }

    public void delete(Long id) {
        tinTucRepository.deleteById(id);
    }

    public void toggleTrangThai(Long id) {
        TinTuc t = getById(id);
        t.setTrangThai(!t.isTrangThai());
        tinTucRepository.save(t);
    }
}
