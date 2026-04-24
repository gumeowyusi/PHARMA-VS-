-- ============================================================
-- PHARMA-VS Migration Script
-- Chạy script này trên SQL Server để kích hoạt các tính năng:
--   1. Đăng nhập Google / Facebook (OAuth2)
--   2. Tích điểm theo chi tiêu (1.000 VNĐ = 1 điểm)
--   3. Hệ thống tin tức (admin post + báo điện tử)
-- ============================================================

-- ─────────────────────────────────────────────────────────────
-- 1. Thêm cột OAuth2 vào bảng USERS
-- ─────────────────────────────────────────────────────────────
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'USERS' AND COLUMN_NAME = 'oauth_provider'
)
BEGIN
    ALTER TABLE USERS ADD oauth_provider NVARCHAR(20) NULL;
    PRINT 'Added column oauth_provider to USERS';
END

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'USERS' AND COLUMN_NAME = 'oauth_id'
)
BEGIN
    ALTER TABLE USERS ADD oauth_id NVARCHAR(100) NULL;
    PRINT 'Added column oauth_id to USERS';
END

-- ─────────────────────────────────────────────────────────────
-- 2. Thêm cột điểm tích lũy vào bảng USERS
-- ─────────────────────────────────────────────────────────────
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'USERS' AND COLUMN_NAME = 'diem_tich_luy'
)
BEGIN
    ALTER TABLE USERS ADD diem_tich_luy INT NOT NULL DEFAULT 0;
    PRINT 'Added column diem_tich_luy to USERS';
END

-- ─────────────────────────────────────────────────────────────
-- 3. Tạo bảng lịch sử điểm LICH_SU_DIEM
-- ─────────────────────────────────────────────────────────────
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_NAME = 'LICH_SU_DIEM'
)
BEGIN
    CREATE TABLE LICH_SU_DIEM (
        id            BIGINT IDENTITY(1,1) PRIMARY KEY,
        id_user       VARCHAR(50)   NOT NULL,
        so_luong_diem INT           NOT NULL,
        loai_giao_dich NVARCHAR(20) NOT NULL,   -- EARN | REDEEM
        ghi_chu       NVARCHAR(255) NULL,
        ngay_tao      DATETIME      NOT NULL DEFAULT GETDATE(),
        id_hoa_don    INT           NULL,
        CONSTRAINT FK_LICH_SU_DIEM_USER    FOREIGN KEY (id_user)    REFERENCES USERS(id_user),
        CONSTRAINT FK_LICH_SU_DIEM_HOADON  FOREIGN KEY (id_hoa_don) REFERENCES HOADON(id_hoadon)
    );
    PRINT 'Created table LICH_SU_DIEM';
END

-- ─────────────────────────────────────────────────────────────
-- 4. Tạo bảng tin tức TIN_TUC
-- ─────────────────────────────────────────────────────────────
IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_NAME = 'TIN_TUC'
)
BEGIN
    CREATE TABLE TIN_TUC (
        id_tin_tuc  BIGINT IDENTITY(1,1) PRIMARY KEY,
        tieu_de     NVARCHAR(500) NOT NULL,
        noi_dung    NVARCHAR(MAX) NULL,
        tom_tat     NVARCHAR(1000) NULL,
        the_loai    NVARCHAR(50)  NULL,
        tac_gia     NVARCHAR(100) NULL,
        ngay_dang   DATETIME      NOT NULL DEFAULT GETDATE(),
        anh_dai     NVARCHAR(500) NULL,
        nguon_bao   NVARCHAR(20)  NOT NULL DEFAULT 'ADMIN',
        url_nguon   NVARCHAR(500) NULL,
        luot_xem    INT           NOT NULL DEFAULT 0,
        trang_thai  BIT           NOT NULL DEFAULT 1
    );
    PRINT 'Created table TIN_TUC';
END

-- ─────────────────────────────────────────────────────────────
-- 5. Thêm dữ liệu mẫu cho TIN_TUC (tuỳ chọn)
-- ─────────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT 1 FROM TIN_TUC WHERE tieu_de = N'5 lưu ý quan trọng khi tự dùng thuốc cảm cúm tại nhà')
BEGIN
    INSERT INTO TIN_TUC (tieu_de, tom_tat, the_loai, tac_gia, ngay_dang, anh_dai, luot_xem, trang_thai, noi_dung)
    VALUES (
        N'5 lưu ý quan trọng khi tự dùng thuốc cảm cúm tại nhà',
        N'Đọc nhãn kỹ, dùng đúng liều, đúng thời điểm. Không kết hợp nhiều thuốc cảm cùng lúc vì dễ trùng hoạt chất paracetamol.',
        N'huong-dan',
        N'DS. Nguyễn Thị Lan',
        GETDATE(),
        N'fas fa-pills',
        0, 1,
        N'<p>Khi bị cảm cúm nhẹ, nhiều người có thói quen tự mua thuốc điều trị tại nhà. Điều này hoàn toàn có thể thực hiện an toàn nếu bạn nắm được các nguyên tắc cơ bản.</p><h6>1. Đọc kỹ nhãn thuốc trước khi dùng</h6><p>Kiểm tra thành phần hoạt chất, đặc biệt chú ý hàm lượng Paracetamol.</p>'
    );
    PRINT 'Inserted sample article';
END

PRINT 'Migration completed successfully!';
