USE [STORE];
GO

IF COL_LENGTH('dbo.USERS','nhanvien') IS NULL
BEGIN
    ALTER TABLE [dbo].[USERS] ADD [nhanvien] BIT NOT NULL CONSTRAINT DF_USERS_NHANVIEN DEFAULT 0;
END
GO

-- Optional: nếu muốn chuyển các tài khoản không phải admin thành nhân viên thì bật câu này
-- UPDATE [dbo].[USERS] SET [nhanvien] = 1 WHERE [vaitro] = 0;

SELECT id_user, hoten, vaitro, nhanvien FROM [dbo].[USERS];
GO
