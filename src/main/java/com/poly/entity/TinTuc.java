package com.poly.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TIN_TUC")
public class TinTuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tin_tuc")
    private Long idTinTuc;

    @Column(name = "tieu_de", nullable = false, length = 500)
    private String tieuDe;

    @Column(name = "noi_dung", columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Column(name = "tom_tat", length = 1000)
    private String tomTat;

    @Column(name = "the_loai", length = 50)
    private String theLoai;

    @Column(name = "tac_gia", length = 100)
    private String tacGia;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_dang", nullable = false)
    private Date ngayDang;

    @Column(name = "anh_dai", length = 500)
    private String anhDai;

    @Column(name = "nguon_bao", length = 20)
    private String nguonBao = "ADMIN";

    @Column(name = "url_nguon", length = 500)
    private String urlNguon;

    @Column(name = "luot_xem", nullable = false)
    private Integer luotXem = 0;

    @Column(name = "trang_thai", nullable = false)
    private boolean trangThai = true;

    public TinTuc() {}

    public Long getIdTinTuc() { return idTinTuc; }
    public void setIdTinTuc(Long idTinTuc) { this.idTinTuc = idTinTuc; }
    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
    public String getTomTat() { return tomTat; }
    public void setTomTat(String tomTat) { this.tomTat = tomTat; }
    public String getTheLoai() { return theLoai; }
    public void setTheLoai(String theLoai) { this.theLoai = theLoai; }
    public String getTacGia() { return tacGia; }
    public void setTacGia(String tacGia) { this.tacGia = tacGia; }
    public Date getNgayDang() { return ngayDang; }
    public void setNgayDang(Date ngayDang) { this.ngayDang = ngayDang; }
    public String getAnhDai() { return anhDai; }
    public void setAnhDai(String anhDai) { this.anhDai = anhDai; }
    public String getNguonBao() { return nguonBao; }
    public void setNguonBao(String nguonBao) { this.nguonBao = nguonBao; }
    public String getUrlNguon() { return urlNguon; }
    public void setUrlNguon(String urlNguon) { this.urlNguon = urlNguon; }
    public Integer getLuotXem() { return luotXem == null ? 0 : luotXem; }
    public void setLuotXem(Integer luotXem) { this.luotXem = luotXem; }
    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }
}
