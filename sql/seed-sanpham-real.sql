USE [STORE];
GO

DECLARE @today DATE = CAST(GETDATE() AS DATE);

-- Thuốc giảm đau - hạ sốt
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Panadol Extra')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Panadol Extra', 120, NULL,
       N'Thuốc giảm đau, hạ sốt chứa Paracetamol phối hợp Caffeine.',
       N'Giảm đau - hạ sốt',
       45000, 5, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc giảm đau - hạ sốt';

-- Thuốc kháng viêm
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Mobic 7.5mg')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Mobic 7.5mg', 80, NULL,
       N'Thuốc kháng viêm không steroid (meloxicam) dùng trong viêm đau xương khớp.',
       N'Kháng viêm NSAID',
       125000, 0, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc kháng viêm';

-- Thuốc kháng sinh
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Augmentin 1g')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Augmentin 1g', 70, NULL,
       N'Kháng sinh phối hợp amoxicillin và acid clavulanic.',
       N'Kháng sinh phổ rộng',
       185000, 0, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc kháng sinh';

-- Thuốc ho - cảm cúm
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Prospan Syrup')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Prospan Syrup', 90, NULL,
       N'Siro ho chiết xuất lá thường xuân, hỗ trợ long đờm giảm ho.',
       N'Siro ho thảo dược',
       98000, 10, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc ho - cảm cúm';

-- Thuốc dị ứng
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Clarityne 10mg')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Clarityne 10mg', 110, NULL,
       N'Thuốc chống dị ứng chứa loratadine.',
       N'Kháng histamine',
       78000, 5, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc dị ứng';

-- Thuốc tiêu hóa
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Smecta')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Smecta', 100, NULL,
       N'Bột pha uống diosmectite hỗ trợ điều trị tiêu chảy cấp và mạn.',
       N'Hỗ trợ tiêu hóa',
       95000, 8, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc tiêu hóa';

-- Thuốc dạ dày
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Gastropulgite')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Gastropulgite', 85, NULL,
       N'Thuốc bao niêm mạc dạ dày, hỗ trợ giảm đau rát dạ dày.',
       N'Bảo vệ dạ dày',
       67000, 0, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc dạ dày';

-- Thuốc men vi sinh
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Enterogermina')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Enterogermina', 95, NULL,
       N'Men vi sinh chứa Bacillus clausii giúp cân bằng hệ vi sinh đường ruột.',
       N'Men vi sinh đường ruột',
       128000, 5, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc men vi sinh';

-- Thuốc tim mạch
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Plavix 75mg')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Plavix 75mg', 60, NULL,
       N'Thuốc chống kết tập tiểu cầu clopidogrel dùng trong bệnh tim mạch.',
       N'Hỗ trợ tim mạch',
       520000, 0, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc tim mạch';

-- Thuốc huyết áp
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Amlor 5mg')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Amlor 5mg', 75, NULL,
       N'Thuốc điều trị tăng huyết áp chứa amlodipine.',
       N'Điều trị tăng huyết áp',
       89000, 0, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc huyết áp';

-- Thuốc tiểu đường
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Glucophage 850mg')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Glucophage 850mg', 90, NULL,
       N'Thuốc điều trị đái tháo đường type 2 chứa metformin.',
       N'Kiểm soát đường huyết',
       115000, 0, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc tiểu đường';

-- Thuốc hô hấp
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Seretide Evohaler 25/125')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Seretide Evohaler 25/125', 40, NULL,
       N'Bình xịt định liều dùng trong hen phế quản và COPD.',
       N'Bình xịt hô hấp',
       365000, 0, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc hô hấp';

-- Thuốc xương khớp
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Voltaren Emulgel')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Voltaren Emulgel', 88, NULL,
       N'Gel bôi ngoài da chứa diclofenac giúp giảm đau cơ xương khớp.',
       N'Gel giảm đau xương khớp',
       159000, 10, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc xương khớp';

-- Thuốc da liễu
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Dermovate Cream')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Dermovate Cream', 55, NULL,
       N'Kem bôi da chứa clobetasol propionate dùng trong viêm da nặng.',
       N'Kem bôi da liễu',
       74000, 0, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc da liễu';

-- Thuốc mắt
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'V.Rohto Dryeye')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'V.Rohto Dryeye', 130, NULL,
       N'Thuốc nhỏ mắt hỗ trợ giảm khô và mỏi mắt.',
       N'Nhỏ mắt giảm khô',
       69000, 5, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc mắt';

-- Thuốc tai mũi họng
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Otrivin 0.1%')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Otrivin 0.1%', 100, NULL,
       N'Thuốc xịt mũi chứa xylometazoline giảm nghẹt mũi.',
       N'Xịt mũi giảm nghẹt',
       78000, 0, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc tai mũi họng';

-- Thuốc phụ khoa
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Polygynax')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Polygynax', 48, NULL,
       N'Viên đặt phụ khoa phối hợp kháng sinh và kháng nấm.',
       N'Viên đặt phụ khoa',
       145000, 0, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc phụ khoa';

-- Thuốc nam khoa
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Proxeed Plus')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Proxeed Plus', 35, NULL,
       N'Sản phẩm hỗ trợ sức khỏe sinh sản nam giới.',
       N'Hỗ trợ nam khoa',
       1450000, 5, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc nam khoa';

-- Thuốc bổ não - tuần hoàn
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Tanakan 40mg')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Tanakan 40mg', 62, NULL,
       N'Thuốc chứa cao bạch quả, hỗ trợ tuần hoàn não.',
       N'Hỗ trợ tuần hoàn não',
       198000, 0, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thuốc bổ não - tuần hoàn';

-- Vitamin và khoáng chất
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Berocca Performance')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Berocca Performance', 140, NULL,
       N'Viên sủi vitamin nhóm B, C và khoáng chất hỗ trợ chuyển hóa năng lượng.',
       N'Bổ sung vitamin',
       165000, 12, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Vitamin và khoáng chất';

-- Thực phẩm bảo vệ sức khỏe
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Blackmores Fish Oil 1000')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Blackmores Fish Oil 1000', 76, NULL,
       N'Viên dầu cá omega-3 hỗ trợ sức khỏe tim mạch và não bộ.',
       N'TPBVSK dầu cá',
       325000, 7, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thực phẩm bảo vệ sức khỏe';

-- Thiết bị y tế
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Omron HEM-7120')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Omron HEM-7120', 28, NULL,
       N'Máy đo huyết áp bắp tay tự động.',
       N'Máy đo huyết áp',
       890000, 3, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Thiết bị y tế';

-- Chăm sóc cá nhân
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'P/S Sensitive Expert')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'P/S Sensitive Expert', 160, NULL,
       N'Kem đánh răng hỗ trợ giảm ê buốt răng.',
       N'Chăm sóc răng miệng',
       55000, 10, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Chăm sóc cá nhân';

-- Sữa và dinh dưỡng
IF NOT EXISTS (SELECT 1 FROM dbo.SANPHAM WHERE ten_sanpham = N'Ensure Gold')
INSERT INTO dbo.SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai)
SELECT N'Ensure Gold', 52, NULL,
       N'Sữa bột dinh dưỡng dành cho người trưởng thành.',
       N'Sữa dinh dưỡng',
       865000, 4, @today, l.id_loai
FROM dbo.LOAI l WHERE l.ten_loai = N'Sữa và dinh dưỡng';

SELECT COUNT(*) AS total_products FROM dbo.SANPHAM;
GO
