-- Migration: Add loyalty points support
-- Run once against the PHARMA database

-- 1. Add diem_tich_luy column to USERS if missing
IF COL_LENGTH('dbo.USERS', 'diem_tich_luy') IS NULL
    ALTER TABLE [dbo].[USERS] ADD [diem_tich_luy] INT NOT NULL DEFAULT 0;
GO

-- 2. Add voucher columns to HOADON if missing
IF COL_LENGTH('dbo.HOADON', 'voucher_code') IS NULL
    ALTER TABLE [dbo].[HOADON] ADD [voucher_code] VARCHAR(50) NULL;
GO

IF COL_LENGTH('dbo.HOADON', 'voucher_discount_amount') IS NULL
    ALTER TABLE [dbo].[HOADON] ADD [voucher_discount_amount] FLOAT NULL DEFAULT 0;
GO

-- 3. Create LICH_SU_DIEM table if missing
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[LICH_SU_DIEM]') AND type = N'U')
BEGIN
    CREATE TABLE [dbo].[LICH_SU_DIEM] (
        [id]              BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [id_user]         VARCHAR(50)          NOT NULL,
        [so_luong_diem]   INT                  NOT NULL,
        [loai_giao_dich]  VARCHAR(20)          NOT NULL,  -- EARN / REDEEM
        [ghi_chu]         NVARCHAR(255)        NULL,
        [ngay_tao]        DATETIME             NOT NULL DEFAULT GETDATE(),
        [id_hoa_don]      INT                  NULL,
        CONSTRAINT FK_LICHSUDIEM_USER FOREIGN KEY ([id_user]) REFERENCES [dbo].[USERS]([id_user])
    );
END
GO

PRINT 'Loyalty points migration complete.';
GO
