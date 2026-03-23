USE [STORE];
GO

IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc giảm đau - hạ sốt')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc giảm đau - hạ sốt');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc kháng viêm')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc kháng viêm');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc kháng sinh')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc kháng sinh');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc ho - cảm cúm')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc ho - cảm cúm');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc dị ứng')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc dị ứng');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc tiêu hóa')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc tiêu hóa');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc dạ dày')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc dạ dày');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc men vi sinh')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc men vi sinh');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc tim mạch')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc tim mạch');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc huyết áp')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc huyết áp');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc tiểu đường')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc tiểu đường');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc hô hấp')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc hô hấp');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc xương khớp')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc xương khớp');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc da liễu')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc da liễu');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc mắt')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc mắt');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc tai mũi họng')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc tai mũi họng');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc phụ khoa')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc phụ khoa');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc nam khoa')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc nam khoa');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thuốc bổ não - tuần hoàn')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thuốc bổ não - tuần hoàn');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Vitamin và khoáng chất')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Vitamin và khoáng chất');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thực phẩm bảo vệ sức khỏe')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thực phẩm bảo vệ sức khỏe');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Thiết bị y tế')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Thiết bị y tế');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Chăm sóc cá nhân')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Chăm sóc cá nhân');
IF NOT EXISTS (SELECT 1 FROM [dbo].[LOAI] WHERE [ten_loai] = N'Sữa và dinh dưỡng')
    INSERT INTO [dbo].[LOAI]([ten_loai]) VALUES (N'Sữa và dinh dưỡng');

SELECT [id_loai], [ten_loai]
FROM [dbo].[LOAI]
ORDER BY [ten_loai];
GO
