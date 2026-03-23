-- ============================================================
--  PHARMA-VS: Init schema cho Docker SQL Server
--  Tuong thich Linux container (khong co FILENAME)
-- ============================================================

USE [master];
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'STORE')
BEGIN
    CREATE DATABASE [STORE];
END
GO

USE [STORE];
GO

-- ===== LOAI =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[LOAI]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[LOAI](
        [id_loai]   INT            IDENTITY(1,1) NOT NULL,
        [ten_loai]  NVARCHAR(255)  NOT NULL,
        PRIMARY KEY CLUSTERED ([id_loai] ASC)
    );
END
GO

-- ===== SANPHAM =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[SANPHAM]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[SANPHAM](
        [id_sanpham]   INT            IDENTITY(1,1) NOT NULL,
        [ten_sanpham]  NVARCHAR(255)  NOT NULL,
        [soluong]      INT            NOT NULL,
        [hinh]         VARCHAR(255)   NULL,
        [mota]         NVARCHAR(MAX)  NOT NULL,
        [motangan]     NVARCHAR(MAX)  NULL,
        [gia]          INT            NOT NULL,
        [giamgia]      INT            NOT NULL,
        [ngaytao]      DATE           NOT NULL,
        [id_loai]      INT            NULL,
        PRIMARY KEY CLUSTERED ([id_sanpham] ASC),
        FOREIGN KEY ([id_loai]) REFERENCES [dbo].[LOAI]([id_loai])
    );
END
GO

-- ===== USERS =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[USERS]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[USERS](
        [id_user]   VARCHAR(50)    NOT NULL,
        [sdt]       VARCHAR(10)    NOT NULL,
        [hinh]      VARCHAR(255)   NULL,
        [hoten]     NVARCHAR(50)   NOT NULL,
        [matkhau]   VARCHAR(60)    NOT NULL,
        [kichhoat]  BIT            NOT NULL,
        [vaitro]    BIT            NOT NULL,
        [nhanvien]  BIT            NOT NULL DEFAULT 0,
        PRIMARY KEY CLUSTERED ([id_user] ASC)
    );
END
GO

IF COL_LENGTH('dbo.USERS','nhanvien') IS NULL
    ALTER TABLE [dbo].[USERS] ADD [nhanvien] BIT NOT NULL DEFAULT 0;
GO

-- ===== GIOHANG =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[GIOHANG]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[GIOHANG](
        [id_giohang]    INT          IDENTITY(1,1) NOT NULL,
        [id_user]       VARCHAR(50)  NULL,
        [id_khach_hang] VARCHAR(50)  NULL,
        PRIMARY KEY CLUSTERED ([id_giohang] ASC),
        FOREIGN KEY ([id_user]) REFERENCES [dbo].[USERS]([id_user])
    );
END
GO

-- ===== GIOHANGCHITIET =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[GIOHANGCHITIET]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[GIOHANGCHITIET](
        [id_giohang]  INT  NOT NULL,
        [id_sanpham]  INT  NOT NULL,
        [soluong]     INT  NOT NULL,
        PRIMARY KEY CLUSTERED ([id_giohang] ASC, [id_sanpham] ASC),
        FOREIGN KEY ([id_giohang]) REFERENCES [dbo].[GIOHANG]([id_giohang]),
        FOREIGN KEY ([id_sanpham]) REFERENCES [dbo].[SANPHAM]([id_sanpham])
    );
END
GO

-- ===== KHACH_HANG =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[KHACH_HANG]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[KHACH_HANG](
        [id_khach_hang]    BIGINT         IDENTITY(1,1) NOT NULL,
        [hoten]            VARCHAR(255)   NOT NULL,
        [sdt]              VARCHAR(10)    NOT NULL,
        [email]            VARCHAR(255)   NULL,
        [dia_chi]          VARCHAR(255)   NULL,
        [trang_thai]       BIT            NOT NULL,
        [phan_loai]        NVARCHAR(255)  NULL,
        [di_ung]           NVARCHAR(255)  NULL,
        [chong_chi_dinh]   NVARCHAR(255)  NULL,
        [ghi_chu_y_te]     NVARCHAR(500)  NULL,
        PRIMARY KEY CLUSTERED ([id_khach_hang] ASC)
    );
    CREATE NONCLUSTERED INDEX [IX_KHACH_HANG_SDT] ON [dbo].[KHACH_HANG]([sdt] ASC);
END
GO

-- ===== NHAN_VIEN =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[NHAN_VIEN]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[NHAN_VIEN](
        [id_nhan_vien]  VARCHAR(50)      NOT NULL,
        [hoten]         VARCHAR(255)     NOT NULL,
        [sdt]           VARCHAR(10)      NOT NULL,
        [email]         VARCHAR(255)     NULL,
        [dia_chi]       VARCHAR(255)     NULL,
        [trang_thai]    BIT              NOT NULL,
        [luong]         DECIMAL(18, 2)   NULL,
        [ca_lam_viec]   VARCHAR(100)     NULL,
        PRIMARY KEY CLUSTERED ([id_nhan_vien] ASC)
    );
END
GO

-- ===== HOADON =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[HOADON]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[HOADON](
        [id_hoadon]       INT            IDENTITY(1,1) NOT NULL,
        [ngaytao]         DATE           NOT NULL,
        [trangthai]       VARCHAR(30)    NOT NULL,
        [diachi]          NVARCHAR(50)   NOT NULL,
        [giaohang]        NVARCHAR(MAX)  NULL,
        [id_user]         VARCHAR(50)    NULL,
        [id_khach_hang]   VARCHAR(50)    NULL,
        [discount_percent] INT           NULL,
        [discount_amount]  INT           NULL,
        PRIMARY KEY CLUSTERED ([id_hoadon] ASC),
        FOREIGN KEY ([id_user]) REFERENCES [dbo].[USERS]([id_user])
    );
    CREATE NONCLUSTERED INDEX [IX_HOADON_ID_KHACH_HANG] ON [dbo].[HOADON]([id_khach_hang] ASC);
END
GO

-- ===== HOADONCHITIET =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[HOADONCHITIET]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[HOADONCHITIET](
        [id_hoadon]   INT  NOT NULL,
        [id_sanpham]  INT  NOT NULL,
        [soluong]     INT  NOT NULL,
        [giamgia]     INT  NULL,
        [gia]         INT  NOT NULL,
        PRIMARY KEY CLUSTERED ([id_hoadon] ASC, [id_sanpham] ASC),
        FOREIGN KEY ([id_hoadon])  REFERENCES [dbo].[HOADON]([id_hoadon]),
        FOREIGN KEY ([id_sanpham]) REFERENCES [dbo].[SANPHAM]([id_sanpham])
    );
END
GO

-- ===== NOTIFICATION =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[NOTIFICATION]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[NOTIFICATION](
        [id_notification]  INT            IDENTITY(1,1) NOT NULL,
        [id_user]          VARCHAR(50)    NULL,
        [title]            NVARCHAR(255)  NOT NULL,
        [content]          NVARCHAR(MAX)  NOT NULL,
        [is_read]          BIT            NOT NULL DEFAULT 0,
        [created_at]       DATETIME       NOT NULL DEFAULT GETDATE(),
        PRIMARY KEY CLUSTERED ([id_notification] ASC),
        FOREIGN KEY ([id_user]) REFERENCES [dbo].[USERS]([id_user])
    );
END
GO

-- ===== VOUCHER =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[VOUCHER]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[VOUCHER](
        [id_voucher]       INT             IDENTITY(1,1) NOT NULL,
        [code]             VARCHAR(50)     NOT NULL,
        [type]             VARCHAR(20)     NOT NULL,
        [value]            DECIMAL(18,2)   NOT NULL,
        [max_discount]     DECIMAL(18,2)   NULL,
        [min_order_value]  DECIMAL(18,2)   NULL,
        [usage_limit]      INT             NULL,
        [usage_per_user]   INT             NULL,
        [total_used]       INT             NOT NULL DEFAULT 0,
        [start_date]       DATETIME        NULL,
        [end_date]         DATETIME        NULL,
        [active]           BIT             NOT NULL DEFAULT 1,
        CONSTRAINT PK_VOUCHER PRIMARY KEY CLUSTERED ([id_voucher] ASC)
    );
    CREATE UNIQUE INDEX UX_VOUCHER_CODE ON [dbo].[VOUCHER]([code]);
END
GO

-- ===== VOUCHER_USAGE =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[VOUCHER_USAGE]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[VOUCHER_USAGE](
        [id_usage]    INT          IDENTITY(1,1) NOT NULL,
        [id_voucher]  INT          NOT NULL,
        [id_user]     VARCHAR(50)  NOT NULL,
        [id_hoadon]   INT          NULL,
        [used_at]     DATETIME     NOT NULL DEFAULT GETDATE(),
        CONSTRAINT PK_VOUCHER_USAGE PRIMARY KEY CLUSTERED ([id_usage] ASC),
        CONSTRAINT FK_VOUCHER_USAGE_VOUCHER FOREIGN KEY ([id_voucher]) REFERENCES [dbo].[VOUCHER]([id_voucher]),
        CONSTRAINT FK_VOUCHER_USAGE_USERS   FOREIGN KEY ([id_user])    REFERENCES [dbo].[USERS]([id_user]),
        CONSTRAINT FK_VOUCHER_USAGE_HOADON  FOREIGN KEY ([id_hoadon])  REFERENCES [dbo].[HOADON]([id_hoadon])
    );
    CREATE INDEX IX_VOUCHER_USAGE_VOUCHER_USER ON [dbo].[VOUCHER_USAGE]([id_voucher],[id_user]);
END
GO

-- ===== WISHLIST =====
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[WISHLIST]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[WISHLIST](
        [id]          INT          IDENTITY(1,1) NOT NULL,
        [id_user]     VARCHAR(50)  NOT NULL,
        [id_sanpham]  INT          NOT NULL,
        [ngaytao]     DATETIME     NOT NULL DEFAULT GETDATE(),
        CONSTRAINT PK_WISHLIST        PRIMARY KEY ([id]),
        CONSTRAINT FK_WISHLIST_USER   FOREIGN KEY ([id_user])   REFERENCES [dbo].[USERS]([id_user]),
        CONSTRAINT FK_WISHLIST_SANPHAM FOREIGN KEY ([id_sanpham]) REFERENCES [dbo].[SANPHAM]([id_sanpham]),
        CONSTRAINT UQ_WISHLIST        UNIQUE ([id_user], [id_sanpham])
    );
END
GO

-- ===== Them cot HOADON neu chua co (Voucher linkage) =====
IF COL_LENGTH('dbo.HOADON','voucher_code') IS NULL
    ALTER TABLE [dbo].[HOADON] ADD [voucher_code] VARCHAR(50) NULL;
GO
IF COL_LENGTH('dbo.HOADON','voucher_discount_amount') IS NULL
    ALTER TABLE [dbo].[HOADON] ADD [voucher_discount_amount] DECIMAL(18,2) NULL;
GO
IF COL_LENGTH('dbo.HOADON','id_voucher') IS NULL
BEGIN
    ALTER TABLE [dbo].[HOADON] ADD [id_voucher] INT NULL;
    ALTER TABLE [dbo].[HOADON] WITH CHECK ADD CONSTRAINT FK_HOADON_VOUCHER
        FOREIGN KEY ([id_voucher]) REFERENCES [dbo].[VOUCHER]([id_voucher]);
END
GO

-- ===== Tao tai khoan Admin mac dinh =====
-- Email: capyboy.dev@gmail.com | Mat khau: Admin@123
IF NOT EXISTS (SELECT 1 FROM [dbo].[USERS] WHERE [id_user] = 'capyboy.dev@gmail.com')
BEGIN
    INSERT INTO [dbo].[USERS] ([id_user],[sdt],[hinh],[hoten],[matkhau],[kichhoat],[vaitro],[nhanvien])
    VALUES ('capyboy.dev@gmail.com','0000000000',NULL,N'Admin',
            '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,1,0);

    INSERT INTO [dbo].[GIOHANG] ([id_user]) VALUES ('capyboy.dev@gmail.com');

    PRINT 'Da tao tai khoan Admin: capyboy.dev@gmail.com / Admin@123';
END
GO

PRINT 'Init schema STORE hoan tat!';
GO
