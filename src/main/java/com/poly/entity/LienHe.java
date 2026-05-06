package com.poly.entity;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "LIEN_HE")
public class LienHe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ho_ten", nullable = false, length = 150)
    private String hoTen;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Column(name = "ngay_gui", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayGui;

    @Column(name = "da_doc", nullable = false)
    private boolean daDoc = false;

    public LienHe() {}

    public LienHe(String hoTen, String sdt, String noiDung) {
        this.hoTen   = hoTen;
        this.sdt     = sdt;
        this.noiDung = noiDung;
        this.ngayGui = new Date();
        this.daDoc   = false;
    }

    public Integer getId()               { return id; }
    public void setId(Integer id)        { this.id = id; }
    public String getHoTen()             { return hoTen; }
    public void setHoTen(String hoTen)   { this.hoTen = hoTen; }
    public String getSdt()               { return sdt; }
    public void setSdt(String sdt)       { this.sdt = sdt; }
    public String getNoiDung()           { return noiDung; }
    public void setNoiDung(String nd)    { this.noiDung = nd; }
    public Date getNgayGui()             { return ngayGui; }
    public void setNgayGui(Date d)       { this.ngayGui = d; }
    public boolean isDaDoc()             { return daDoc; }
    public void setDaDoc(boolean daDoc)  { this.daDoc = daDoc; }
}
