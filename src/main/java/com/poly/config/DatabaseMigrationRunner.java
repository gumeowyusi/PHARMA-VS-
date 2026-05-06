package com.poly.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Creates new tables introduced after initial schema deployment.
 * Each statement is idempotent (uses IF NOT EXISTS / IF OBJECT_ID checks).
 */
@Component
public class DatabaseMigrationRunner implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    public DatabaseMigrationRunner(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        // ── 1. LIEN_HE table ──────────────────────────────────────────────────────
        jdbc.execute("""
            IF OBJECT_ID(N'dbo.LIEN_HE', N'U') IS NULL
            CREATE TABLE dbo.LIEN_HE (
                id        INT IDENTITY(1,1) PRIMARY KEY,
                ho_ten    NVARCHAR(150) NOT NULL,
                sdt       NVARCHAR(20)  NULL,
                noi_dung  NVARCHAR(MAX) NOT NULL,
                ngay_gui  DATETIME      NOT NULL DEFAULT GETDATE(),
                da_doc    BIT           NOT NULL DEFAULT 0
            )
            """);

        // ── 2. Seed test COD orders for capyboy.des@gmail.com ────────────────────
        // Only runs when the user exists but has no test seed orders yet.
        jdbc.execute("""
            IF EXISTS (SELECT 1 FROM USERS WHERE id_user = 'capyboy.des@gmail.com')
            AND NOT EXISTS (
                SELECT 1 FROM HOADON
                WHERE id_user = 'capyboy.des@gmail.com'
                  AND diachi LIKE 'TEST-SEED-%'
            )
            BEGIN
                DECLARE @sp INT = (SELECT TOP 1 id_sanpham FROM SANPHAM ORDER BY gia DESC);
                DECLARE @sp2 INT = (SELECT TOP 1 id_sanpham FROM SANPHAM ORDER BY gia ASC);

                -- Order 1: 2,000,000 VND → 2,000 points
                DECLARE @hd1 INT;
                INSERT INTO HOADON (id_user, ngaytao, trangthai, giaohang, diachi)
                VALUES ('capyboy.des@gmail.com', GETDATE(), 'received', 'Giao hàng tiêu chuẩn | COD', 'TEST-SEED-1');
                SET @hd1 = SCOPE_IDENTITY();
                INSERT INTO HOADONCHITIET (id_hoadon, id_sanpham, soluong, gia, giamgia)
                VALUES (@hd1, @sp, 4, 500000, 0);

                -- Order 2: 1,500,000 VND → 1,500 points
                DECLARE @hd2 INT;
                INSERT INTO HOADON (id_user, ngaytao, trangthai, giaohang, diachi)
                VALUES ('capyboy.des@gmail.com', GETDATE(), 'received', 'Giao hàng nhanh | COD', 'TEST-SEED-2');
                SET @hd2 = SCOPE_IDENTITY();
                INSERT INTO HOADONCHITIET (id_hoadon, id_sanpham, soluong, gia, giamgia)
                VALUES (@hd2, @sp2, 10, 150000, 0);

                -- Order 3: 800,000 VND → 800 points
                DECLARE @hd3 INT;
                INSERT INTO HOADON (id_user, ngaytao, trangthai, giaohang, diachi)
                VALUES ('capyboy.des@gmail.com', GETDATE(), 'received', 'Giao hàng tiêu chuẩn | COD', 'TEST-SEED-3');
                SET @hd3 = SCOPE_IDENTITY();
                INSERT INTO HOADONCHITIET (id_hoadon, id_sanpham, soluong, gia, giamgia)
                VALUES (@hd3, @sp, 2, 400000, 0);

                -- Total earned: (4×500000 + 10×150000 + 2×400000) = 2,000,000+1,500,000+800,000 = 4,300 points
                UPDATE USERS
                SET diem_tich_luy = ISNULL(diem_tich_luy, 0) + 4300
                WHERE id_user = 'capyboy.des@gmail.com';
            END
            """);
    }
}
