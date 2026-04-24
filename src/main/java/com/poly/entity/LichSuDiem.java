package com.poly.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "LICH_SU_DIEM")
public class LichSuDiem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private Users users;

    @Column(name = "so_luong_diem", nullable = false)
    private Integer soLuongDiem;

    @Column(name = "loai_giao_dich", nullable = false, length = 20)
    private String loaiGiaoDich; // EARN, REDEEM

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_tao", nullable = false)
    private Date ngayTao;

    @Column(name = "id_hoa_don")
    private Integer idHoaDon;

    public LichSuDiem() {}

    public LichSuDiem(Users users, Integer soLuongDiem, String loaiGiaoDich, String ghiChu, Integer idHoaDon) {
        this.users = users;
        this.soLuongDiem = soLuongDiem;
        this.loaiGiaoDich = loaiGiaoDich;
        this.ghiChu = ghiChu;
        this.ngayTao = new Date();
        this.idHoaDon = idHoaDon;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Users getUsers() { return users; }
    public void setUsers(Users users) { this.users = users; }
    public Integer getSoLuongDiem() { return soLuongDiem; }
    public void setSoLuongDiem(Integer soLuongDiem) { this.soLuongDiem = soLuongDiem; }
    public String getLoaiGiaoDich() { return loaiGiaoDich; }
    public void setLoaiGiaoDich(String loaiGiaoDich) { this.loaiGiaoDich = loaiGiaoDich; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }
    public Integer getIdHoaDon() { return idHoaDon; }
    public void setIdHoaDon(Integer idHoaDon) { this.idHoaDon = idHoaDon; }
}
