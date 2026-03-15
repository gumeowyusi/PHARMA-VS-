-- ============================================================
--   SQL: Thêm hoặc cập nhật capyboy.dev@gmail.com làm ADMIN
--   Chạy script này trên SQL Server (database STORE)
-- ============================================================

USE [STORE];
GO

-- Nếu user capyboy.dev@gmail.com đã tồn tại → cập nhật thành admin + kích hoạt
IF EXISTS (SELECT 1 FROM [dbo].[USERS] WHERE [id_user] = 'capyboy.dev@gmail.com')
BEGIN
    UPDATE [dbo].[USERS]
    SET 
        [vaitro]   = 1,   -- 1 = Admin
        [kichhoat] = 1    -- 1 = Đã kích hoạt
    WHERE [id_user] = 'capyboy.dev@gmail.com';

    PRINT 'Đã cập nhật capyboy.dev@gmail.com thành Admin!';
END
ELSE
BEGIN
    -- Nếu chưa có → tạo mới với mật khẩu mặc định là "Admin@123"
    -- (BCrypt hash của "Admin@123")
    -- Sau khi đăng nhập nên đổi mật khẩu ngay
    INSERT INTO [dbo].[USERS] ([id_user], [sdt], [hinh], [hoten], [matkhau], [kichhoat], [vaitro])
    VALUES (
        'capyboy.dev@gmail.com',
        '0000000000',
        NULL,
        N'Admin',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- Admin@123
        1,   -- kichhoat = true
        1    -- vaitro = true (Admin)
    );

    -- Tạo giỏ hàng cho admin
    INSERT INTO [dbo].[GIOHANG] ([id_user])
    VALUES ('capyboy.dev@gmail.com');

    PRINT 'Đã tạo tài khoản Admin mới: capyboy.dev@gmail.com';
    PRINT 'Mật khẩu mặc định: Admin@123  (Hãy đổi ngay sau khi đăng nhập!)';
END
GO

-- Kiểm tra kết quả
SELECT [id_user], [hoten], [kichhoat], [vaitro]
FROM [dbo].[USERS]
WHERE [id_user] = 'capyboy.dev@gmail.com';
GO
