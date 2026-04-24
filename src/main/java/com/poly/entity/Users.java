package com.poly.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.ToString;

@Entity
@ToString
@Table(name = "USERS")
public class Users {

	private String idUser;
	private String sdt;
	private String hinh;
	private String hoten;
	private String matkhau;
	private boolean vaitro;
	private boolean nhanvien;
	private boolean kichhoat;
	private List<GioHang> gioHangs;
	private List<HoaDon> hoaDons;
	private String oauthProvider;
	private String oauthId;
	private Integer diemTichLuy = 0;

	public Users() {
	}

	public Users(String idUser, String sdt, String hinh, String hoten, String matkhau, boolean vaitro,
			List<GioHang> gioHangs, List<HoaDon> hoaDons) {
		this.idUser = idUser;
		this.sdt = sdt;
		this.hinh = hinh;
		this.hoten = hoten;
		this.matkhau = matkhau;
		this.vaitro = vaitro;
		this.gioHangs = gioHangs;
		this.hoaDons = hoaDons;
	}

	@Id

	@Column(name = "id_user", unique = true, nullable = false, length = 50)
	public String getIdUser() {
		return this.idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	@Column(name = "sdt", nullable = false, length = 10)
	public String getSdt() {
		return this.sdt;
	}

	public void setSdt(String sdt) {
		this.sdt = sdt;
	}

	@Column(name = "hinh")
	public String getHinh() {
		return this.hinh;
	}

	public void setHinh(String hinh) {
		this.hinh = hinh;
	}

	@Column(name = "hoten", nullable = false)
	public String getHoten() {
		return this.hoten;
	}

	public void setHoten(String hoten) {
		this.hoten = hoten;
	}

	@Column(name = "matkhau", nullable = false, length = 60)
	public String getMatkhau() {
		return this.matkhau;
	}

	public void setMatkhau(String matkhau) {
		this.matkhau = matkhau;
	}

	@Column(name = "vaitro", nullable = false)
	public boolean isVaitro() {
		return this.vaitro;
	}

	public void setVaitro(boolean vaitro) {
		this.vaitro = vaitro;
	}

	@Column(name = "nhanvien", nullable = false)
	public boolean isNhanvien() {
		return nhanvien;
	}

	public void setNhanvien(boolean nhanvien) {
		this.nhanvien = nhanvien;
	}
	
	@Column(name = "kichhoat", nullable = false)
	public boolean isKichhoat() {
		return this.kichhoat;
	}

	public void setKichhoat(boolean kichhoat) {
		this.kichhoat = kichhoat;
	}

	@Transient
	public String getRoleName() {
		if (this.vaitro) {
			return "ADMIN";
		}
		if (this.nhanvien) {
			return "STAFF";
		}
		return "CUSTOMER";
	}

	public void setRoleName(String roleName) {
		String role = roleName == null ? "CUSTOMER" : roleName.trim().toUpperCase();
		switch (role) {
		case "ADMIN":
			this.vaitro = true;
			this.nhanvien = false;
			break;
		case "STAFF":
			this.vaitro = false;
			this.nhanvien = true;
			break;
		default:
			this.vaitro = false;
			this.nhanvien = false;
			break;
		}
	}

	@Transient
	public boolean isAdmin() {
		return this.vaitro;
	}

	@Transient
	public boolean isStaff() {
		return !this.vaitro && this.nhanvien;
	}

	@Transient
	public boolean isCustomer() {
		return !this.vaitro && !this.nhanvien;
	}

	@Column(name = "oauth_provider", length = 20)
	public String getOauthProvider() { return oauthProvider; }
	public void setOauthProvider(String oauthProvider) { this.oauthProvider = oauthProvider; }

	@Column(name = "oauth_id", length = 100)
	public String getOauthId() { return oauthId; }
	public void setOauthId(String oauthId) { this.oauthId = oauthId; }

	@Column(name = "diem_tich_luy")
	public Integer getDiemTichLuy() { return diemTichLuy == null ? 0 : diemTichLuy; }
	public void setDiemTichLuy(Integer diemTichLuy) { this.diemTichLuy = diemTichLuy == null ? 0 : diemTichLuy; }

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
	public List<GioHang> getGioHangs() {
		return this.gioHangs;
	}

	public void setGioHangs(List<GioHang> gioHangs) {
		this.gioHangs = gioHangs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
	public List<HoaDon> getHoaDons() {
		return this.hoaDons;
	}

	public void setHoaDons(List<HoaDon> hoaDons) {
		this.hoaDons = hoaDons;
	}

}
