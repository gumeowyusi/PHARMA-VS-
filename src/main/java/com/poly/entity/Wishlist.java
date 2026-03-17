package com.poly.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "WISHLIST", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_user", "id_sanpham"})
})
public class Wishlist {

    private Integer id;
    private Users users;
    private SanPham sanPham;
    private Date ngaytao;

    public Wishlist() {}

    public Wishlist(Users users, SanPham sanPham, Date ngaytao) {
        this.users = users;
        this.sanPham = sanPham;
        this.ngaytao = ngaytao;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    public Users getUsers() { return users; }
    public void setUsers(Users users) { this.users = users; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sanpham", nullable = false)
    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngaytao", nullable = false)
    public Date getNgaytao() { return ngaytao; }
    public void setNgaytao(Date ngaytao) { this.ngaytao = ngaytao; }
}
