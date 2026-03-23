USE [STORE];
GO

-- Xoa du lieu LOAI hien tai de nap lai dung Unicode
DELETE FROM [dbo].[LOAI];
DBCC CHECKIDENT ('[dbo].[LOAI]', RESEED, 0);
GO

INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES
(N'Thuốc giảm đau - hạ sốt'),
(N'Thuốc kháng viêm'),
(N'Thuốc kháng sinh'),
(N'Thuốc ho - cảm cúm'),
(N'Thuốc dị ứng'),
(N'Thuốc tiêu hóa'),
(N'Thuốc dạ dày'),
(N'Thuốc men vi sinh'),
(N'Thuốc tim mạch'),
(N'Thuốc huyết áp'),
(N'Thuốc tiểu đường'),
(N'Thuốc hô hấp'),
(N'Thuốc xương khớp'),
(N'Thuốc da liễu'),
(N'Thuốc mắt'),
(N'Thuốc tai mũi họng'),
(N'Thuốc phụ khoa'),
(N'Thuốc nam khoa'),
(N'Thuốc bổ não - tuần hoàn'),
(N'Vitamin và khoáng chất'),
(N'Thực phẩm bảo vệ sức khỏe'),
(N'Thiết bị y tế'),
(N'Chăm sóc cá nhân'),
(N'Sữa và dinh dưỡng');
GO

SELECT [id_loai], [ten_loai] FROM [dbo].[LOAI] ORDER BY [id_loai];
GO
