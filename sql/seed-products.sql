-- ============================================================
-- SEED DATA: Danh mục thuốc + Sản phẩm thật + Tin tức
-- ============================================================
USE STORE;

-- ─────────────────────────────────────────────────────────────
-- 1. DANH MỤC LOẠI THUỐC
-- ─────────────────────────────────────────────────────────────
SET IDENTITY_INSERT LOAI ON;
IF NOT EXISTS (SELECT 1 FROM LOAI WHERE id_loai = 1)
INSERT INTO LOAI (id_loai, ten_loai) VALUES
(1,  N'Thuốc giảm đau & hạ sốt'),
(2,  N'Thuốc cảm cúm & hô hấp'),
(3,  N'Thuốc tiêu hóa & dạ dày'),
(4,  N'Vitamin & khoáng chất'),
(5,  N'Thuốc kháng sinh'),
(6,  N'Thuốc tim mạch & huyết áp'),
(7,  N'Thuốc dị ứng & da liễu'),
(8,  N'Thực phẩm chức năng');
SET IDENTITY_INSERT LOAI OFF;

-- ─────────────────────────────────────────────────────────────
-- 2. SẢN PHẨM THUỐC (60 sản phẩm thật)
-- ─────────────────────────────────────────────────────────────

-- ==== LOẠI 1: Giảm đau & hạ sốt ====
IF NOT EXISTS (SELECT 1 FROM SANPHAM WHERE ten_sanpham = N'Panadol Extra 500mg/65mg')
INSERT INTO SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai) VALUES
(N'Panadol Extra 500mg/65mg',
 200, NULL,
 N'<p><strong>Panadol Extra</strong> (GlaxoSmithKline) – Thuốc giảm đau hạ sốt chứa Paracetamol 500mg + Caffeine 65mg. Hiệu quả nhanh hơn Panadol thường 33%. Dùng cho đau đầu, đau răng, đau cơ, sốt nhẹ đến vừa.</p><p><strong>Thành phần:</strong> Paracetamol 500mg, Caffeine 65mg.</p><p><strong>Liều dùng:</strong> Người lớn 1-2 viên/lần, mỗi 4-6 giờ, tối đa 8 viên/ngày.</p>',
 N'Giảm đau hạ sốt hiệu quả nhanh, chứa Paracetamol 500mg + Caffeine, phù hợp cho đau đầu, đau răng, sốt.',
 28000, 0, GETDATE(), 1),

(N'Efferalgan 500mg (viên sủi)',
 150, NULL,
 N'<p><strong>Efferalgan 500mg</strong> (Bristol-Myers Squibb) – Viên sủi bọt giảm đau hạ sốt, hấp thu nhanh hơn dạng viên thường. Thích hợp cho người khó nuốt viên.</p><p><strong>Thành phần:</strong> Paracetamol 500mg.</p><p><strong>Liều dùng:</strong> 1-2 viên/lần pha vào 250ml nước, 3-4 lần/ngày.</p>',
 N'Viên sủi bọt Paracetamol 500mg, hấp thu nhanh, dễ uống, giảm đau hạ sốt hiệu quả.',
 45000, 10, GETDATE(), 1),

(N'Tylenol Extended Release 650mg',
 100, NULL,
 N'<p><strong>Tylenol ER 650mg</strong> (Johnson & Johnson) – Viên giải phóng kéo dài, tác dụng lên đến 8 giờ. Phù hợp cho các cơn đau mãn tính, đau lưng, đau khớp.</p><p><strong>Thành phần:</strong> Paracetamol 650mg.</p>',
 N'Paracetamol 650mg giải phóng kéo dài, hiệu quả giảm đau đến 8 giờ, phù hợp đau lưng, đau khớp.',
 55000, 0, GETDATE(), 1),

(N'Voltaren Emulgel 1% (Diclofenac)',
 80, NULL,
 N'<p><strong>Voltaren Emulgel 1%</strong> (Novartis) – Gel bôi ngoài da chứa Diclofenac 1%, giảm đau kháng viêm hiệu quả cho đau cơ, đau khớp, bong gân. Không gây tác dụng phụ toàn thân.</p><p><strong>Thành phần:</strong> Diclofenac Diethylamine 1.16% (tương đương Diclofenac Na 1%).</p>',
 N'Gel bôi ngoài Diclofenac 1%, giảm đau kháng viêm tại chỗ cho đau cơ, bong gân, viêm khớp.',
 175000, 15, GETDATE(), 1),

(N'Diclofenac 50mg (hộp 30 viên)',
 200, NULL,
 N'<p><strong>Diclofenac 50mg</strong> – Thuốc kháng viêm giảm đau không steroid (NSAIDs). Dùng cho viêm khớp dạng thấp, thoái hóa khớp, đau lưng cấp tính.</p><p><strong>Lưu ý:</strong> Uống sau ăn, không dùng cho người loét dạ dày.</p>',
 N'Kháng viêm giảm đau NSAIDs, dùng cho viêm khớp, đau lưng. Uống sau bữa ăn.',
 38000, 0, GETDATE(), 1),

(N'Ibuprofen 400mg (hộp 20 viên)',
 150, NULL,
 N'<p><strong>Ibuprofen 400mg</strong> – Thuốc giảm đau, hạ sốt, kháng viêm. Hiệu quả cho đau đầu, đau bụng kinh, sốt. Ít ảnh hưởng dạ dày hơn Aspirin.</p>',
 N'Giảm đau hạ sốt kháng viêm, hiệu quả cho đau đầu, đau bụng kinh, đau cơ khớp.',
 35000, 0, GETDATE(), 1);

-- ==== LOẠI 2: Cảm cúm & hô hấp ====
IF NOT EXISTS (SELECT 1 FROM SANPHAM WHERE ten_sanpham = N'Decolgen Forte (hộp 25 viên)')
INSERT INTO SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai) VALUES
(N'Decolgen Forte (hộp 25 viên)',
 300, NULL,
 N'<p><strong>Decolgen Forte</strong> (Zuellig Pharma) – Thuốc trị cảm cúm, nghẹt mũi, chảy nước mũi. Chứa 3 hoạt chất: Paracetamol 500mg + Phenylpropanolamine + Chlorpheniramine.</p><p><strong>Công dụng:</strong> Giảm sốt, thông mũi, giảm chảy nước mũi do cảm cúm.</p>',
 N'Thuốc cảm cúm 3 tác dụng: hạ sốt, thông mũi, giảm chảy nước mũi. Nổi tiếng tại Việt Nam.',
 32000, 0, GETDATE(), 2),

(N'Coldacmin Forte (hộp 10 gói)',
 200, NULL,
 N'<p><strong>Coldacmin Forte</strong> – Thuốc điều trị cảm lạnh, cảm cúm dạng bột pha uống. Hương chanh dễ uống. Chứa Paracetamol + Vitamin C + Phenylephrine.</p>',
 N'Thuốc cảm cúm dạng bột pha, hương chanh dễ uống, chứa Vitamin C tăng sức đề kháng.',
 45000, 10, GETDATE(), 2),

(N'Nasacort Aqua 55mcg (xịt mũi)',
 60, NULL,
 N'<p><strong>Nasacort Aqua</strong> (Sanofi) – Thuốc xịt mũi chứa Triamcinolone acetonide, điều trị viêm mũi dị ứng theo mùa và quanh năm. Hiệu quả nhanh trong 24 giờ.</p><p><strong>Liều dùng:</strong> 2 lần xịt mỗi bên mũi, 1 lần/ngày.</p>',
 N'Xịt mũi kháng viêm Triamcinolone, điều trị viêm mũi dị ứng, hiệu quả kéo dài 24 giờ.',
 185000, 20, GETDATE(), 2),

(N'Tiffy Flu (hộp 10 viên)',
 250, NULL,
 N'<p><strong>Tiffy Flu</strong> – Thuốc trị cảm cúm phổ biến tại Việt Nam. Chứa Paracetamol 500mg + Phenylephrine 10mg + Chlorpheniramine 2mg. Giảm sốt, thông mũi, hết hắt hơi.</p>',
 N'Thuốc cảm cúm Tiffy, giảm sốt thông mũi hết hắt hơi, phổ biến và hiệu quả.',
 25000, 0, GETDATE(), 2),

(N'Strepsils Cam & Vitamin C (hộp 24 viên)',
 180, NULL,
 N'<p><strong>Strepsils</strong> (Reckitt Benckiser) – Viên ngậm kháng khuẩn giảm đau họng. Hương cam kết hợp Vitamin C. Có tác dụng kháng khuẩn 2,4-dichlorobenzyl alcohol + Amylmetacresol.</p>',
 N'Viên ngậm kháng khuẩn giảm đau họng, hương cam dễ chịu, kết hợp Vitamin C.',
 55000, 0, GETDATE(), 2),

(N'Prospan Siro Ho Trẻ Em 100ml',
 120, NULL,
 N'<p><strong>Prospan</strong> (Engelhard Arzneimittel) – Siro ho thảo dược chiết xuất từ lá thường xuân (Hedera helix). An toàn cho trẻ từ 1 tuổi, không chứa alcohol. Giảm ho, long đờm.</p>',
 N'Siro ho thảo dược từ lá thường xuân, an toàn cho trẻ từ 1 tuổi, không alcohol.',
 145000, 15, GETDATE(), 2);

-- ==== LOẠI 3: Tiêu hóa & dạ dày ====
IF NOT EXISTS (SELECT 1 FROM SANPHAM WHERE ten_sanpham = N'Omeprazole 20mg (hộp 28 viên)')
INSERT INTO SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai) VALUES
(N'Omeprazole 20mg (hộp 28 viên)',
 200, NULL,
 N'<p><strong>Omeprazole 20mg</strong> – Thuốc ức chế bơm proton (PPI), giảm tiết acid dạ dày mạnh và kéo dài. Điều trị loét dạ dày tá tràng, GERD, hội chứng Zollinger-Ellison.</p><p><strong>Liều dùng:</strong> 1 viên/ngày trước bữa ăn sáng.</p>',
 N'Ức chế bơm proton giảm acid dạ dày, điều trị loét dạ dày, trào ngược thực quản.',
 65000, 0, GETDATE(), 3),

(N'Nexium 40mg (hộp 28 viên)',
 100, NULL,
 N'<p><strong>Nexium 40mg</strong> (AstraZeneca) – Esomeprazole thế hệ mới, hiệu quả vượt trội hơn Omeprazole. Điều trị loét dạ dày do H.pylori, GERD nặng, bảo vệ dạ dày khi dùng NSAIDs dài ngày.</p>',
 N'Esomeprazole 40mg thế hệ mới, hiệu quả mạnh hơn Omeprazole, điều trị dạ dày tiên tiến.',
 285000, 10, GETDATE(), 3),

(N'Phosphalugel Gel dạ dày (hộp 26 gói)',
 150, NULL,
 N'<p><strong>Phosphalugel</strong> (Ipsen) – Gel trung hòa acid dạ dày nhanh, bảo vệ niêm mạc. Chứa Aluminium phosphate gel. Phù hợp cho đau dạ dày cấp tính, ợ chua, ợ nóng.</p><p><strong>Lưu ý:</strong> Có thể gây táo bón nếu dùng lâu dài.</p>',
 N'Gel trung hòa acid dạ dày nhanh chóng, giảm ợ chua ợ nóng, bảo vệ niêm mạc dạ dày.',
 95000, 0, GETDATE(), 3),

(N'Smecta (Diosmectite) hộp 30 gói',
 200, NULL,
 N'<p><strong>Smecta</strong> (Ipsen) – Diosmectite bảo vệ niêm mạc đường tiêu hóa, điều trị tiêu chảy cấp và mãn tính. An toàn cho trẻ em và phụ nữ có thai. Không hấp thu vào máu.</p>',
 N'Thuốc tiêu chảy Smecta, bảo vệ niêm mạc ruột, an toàn cho trẻ em và thai phụ.',
 55000, 0, GETDATE(), 3),

(N'Enterogermina 5ml (hộp 20 ống)',
 120, NULL,
 N'<p><strong>Enterogermina</strong> (Sanofi) – Men vi sinh Bacillus clausii phục hồi hệ vi khuẩn đường ruột sau dùng kháng sinh. Điều trị tiêu chảy, rối loạn tiêu hóa. An toàn cho trẻ sơ sinh.</p>',
 N'Men vi sinh Bacillus clausii, phục hồi hệ vi khuẩn ruột sau kháng sinh, điều trị tiêu chảy.',
 115000, 20, GETDATE(), 3),

(N'Motilium-M 10mg (hộp 30 viên)',
 150, NULL,
 N'<p><strong>Motilium-M 10mg</strong> (Janssen) – Domperidone chống nôn buồn nôn, tăng nhu động dạ dày. Hiệu quả cho buồn nôn do thuốc, liệt dạ dày, khó tiêu chức năng.</p>',
 N'Domperidone 10mg chống nôn, chữa buồn nôn, ợ hơi, khó tiêu. Thương hiệu Janssen uy tín.',
 68000, 0, GETDATE(), 3),

(N'Neopeptine Drops (30ml)',
 100, NULL,
 N'<p><strong>Neopeptine Drops</strong> – Enzym tiêu hóa hỗ trợ cho trẻ em và người lớn. Chứa Papain + Diastase. Giúp tiêu hóa đạm và tinh bột tốt hơn, giảm đầy bụng khó tiêu.</p>',
 N'Enzym tiêu hóa Papain + Diastase, giảm đầy bụng khó tiêu, hỗ trợ tiêu hóa cho cả gia đình.',
 78000, 0, GETDATE(), 3);

-- ==== LOẠI 4: Vitamin & khoáng chất ====
IF NOT EXISTS (SELECT 1 FROM SANPHAM WHERE ten_sanpham = N'Vitamin C 1000mg Upsa (hộp 10 viên sủi)')
INSERT INTO SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai) VALUES
(N'Vitamin C 1000mg Upsa (hộp 10 viên sủi)',
 300, NULL,
 N'<p><strong>Vitamin C 1000mg UPSA</strong> (Bristol-Myers Squibb) – Viên sủi bổ sung Vitamin C liều cao, tăng sức đề kháng, chống oxy hóa. Hương cam thơm ngon, dễ uống hàng ngày.</p><p><strong>Liều dùng:</strong> 1 viên/ngày pha 200ml nước.</p>',
 N'Vitamin C 1000mg viên sủi, tăng đề kháng chống oxy hóa, hương cam thơm ngon dễ uống.',
 85000, 0, GETDATE(), 4),

(N'Enervon-C (hộp 30 viên)',
 250, NULL,
 N'<p><strong>Enervon-C</strong> (Unilab) – Vitamin tổng hợp B-Complex + Vitamin C. Tăng cường năng lượng, giảm mệt mỏi, tăng sức đề kháng. Phổ biến tại Đông Nam Á.</p>',
 N'Vitamin tổng hợp B-Complex + C, tăng năng lượng giảm mệt mỏi, tăng sức đề kháng.',
 125000, 15, GETDATE(), 4),

(N'Calcium Sandoz 500mg (hộp 20 viên sủi)',
 200, NULL,
 N'<p><strong>Calcium Sandoz 500mg</strong> (Novartis) – Bổ sung Canxi + Vitamin D3 phòng loãng xương. Dạng viên sủi dễ hấp thu, hương cam. Phù hợp cho người lớn tuổi, phụ nữ có thai.</p>',
 N'Canxi 500mg + Vitamin D3 viên sủi, phòng loãng xương, dễ hấp thu. Phù hợp thai phụ và người cao tuổi.',
 145000, 10, GETDATE(), 4),

(N'Vitamin D3 2000IU (hộp 60 viên)',
 150, NULL,
 N'<p><strong>Vitamin D3 2000IU</strong> – Bổ sung Vitamin D3 (Cholecalciferol) liều vừa, hỗ trợ hấp thu Canxi, tăng cường miễn dịch, ngăn ngừa loãng xương và suy giảm miễn dịch.</p>',
 N'Vitamin D3 2000IU, hỗ trợ hấp thu canxi, tăng miễn dịch, phòng loãng xương.',
 185000, 0, GETDATE(), 4),

(N'Becozyme C Forte (hộp 30 viên)',
 200, NULL,
 N'<p><strong>Becozyme C Forte</strong> (Bayer) – Bổ sung B1, B2, B6, B12, Pantothenate và Vitamin C. Hỗ trợ chuyển hóa năng lượng, bảo vệ hệ thần kinh, giảm mệt mỏi.</p>',
 N'B-Complex đầy đủ + Vitamin C của Bayer, hỗ trợ thần kinh, tăng năng lượng, giảm stress.',
 95000, 0, GETDATE(), 4),

(N'Omega-3 Fish Oil 1000mg (hộp 100 viên)',
 120, NULL,
 N'<p><strong>Omega-3 Fish Oil 1000mg</strong> – Dầu cá tự nhiên giàu EPA + DHA. Bảo vệ tim mạch, giảm triglyceride, hỗ trợ trí não và thị lực. Không có mùi tanh nhờ công nghệ khử mùi đặc biệt.</p>',
 N'Dầu cá Omega-3 EPA+DHA, bảo vệ tim mạch giảm triglyceride, tốt cho não và mắt.',
 245000, 25, GETDATE(), 4),

(N'Zinc 10mg (hộp 60 viên)',
 200, NULL,
 N'<p><strong>Zinc 10mg</strong> – Kẽm hữu cơ (Zinc gluconate) tăng cường miễn dịch, hỗ trợ tăng trưởng, bảo vệ da và tóc. Cần thiết cho trẻ em biếng ăn chậm lớn.</p>',
 N'Kẽm hữu cơ 10mg, tăng miễn dịch, giúp trẻ biếng ăn, bảo vệ da tóc móng.',
 65000, 0, GETDATE(), 4);

-- ==== LOẠI 5: Kháng sinh ====
IF NOT EXISTS (SELECT 1 FROM SANPHAM WHERE ten_sanpham = N'Augmentin 625mg (hộp 14 viên)')
INSERT INTO SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai) VALUES
(N'Augmentin 625mg (hộp 14 viên)',
 80, NULL,
 N'<p><strong>Augmentin 625mg</strong> (GlaxoSmithKline) – Amoxicillin 500mg + Clavulanate 125mg. Kháng sinh phổ rộng điều trị nhiễm khuẩn đường hô hấp, tiết niệu, da mô mềm.</p><p><strong>Cần kê đơn bác sĩ.</strong> Uống trong bữa ăn để giảm tác dụng phụ tiêu hóa.</p>',
 N'Kháng sinh Amoxicillin + Clavulanate, phổ rộng, điều trị nhiễm khuẩn hô hấp, tiết niệu. Cần kê đơn.',
 245000, 0, GETDATE(), 5),

(N'Azithromycin 250mg (hộp 6 viên)',
 100, NULL,
 N'<p><strong>Azithromycin 250mg</strong> – Kháng sinh macrolide, tác dụng kéo dài do tích lũy trong mô. Điều trị viêm phổi cộng đồng, viêm xoang, nhiễm khuẩn da. Chỉ cần uống 1 lần/ngày x 3-5 ngày.</p>',
 N'Kháng sinh Azithromycin, uống 1 lần/ngày, điều trị viêm phổi, viêm xoang, nhiễm khuẩn da.',
 185000, 0, GETDATE(), 5),

(N'Ciprofloxacin 500mg (hộp 10 viên)',
 80, NULL,
 N'<p><strong>Ciprofloxacin 500mg</strong> – Kháng sinh fluoroquinolone phổ rộng, điều trị nhiễm khuẩn tiết niệu, đường ruột, hô hấp, xương khớp. Hiệu lực mạnh với vi khuẩn gram âm.</p>',
 N'Kháng sinh Ciprofloxacin phổ rộng, hiệu quả nhiễm khuẩn tiết niệu, đường ruột. Cần kê đơn.',
 95000, 0, GETDATE(), 5),

(N'Metronidazole 500mg (hộp 20 viên)',
 120, NULL,
 N'<p><strong>Metronidazole 500mg</strong> – Kháng sinh/kháng ký sinh trùng, điều trị nhiễm Helicobacter pylori (kết hợp phác đồ), viêm âm đạo, amoebiasis, áp xe gan amip.</p>',
 N'Kháng sinh Metronidazole, điều trị H.pylori dạ dày, nhiễm ký sinh trùng, áp xe amip.',
 45000, 0, GETDATE(), 5);

-- ==== LOẠI 6: Tim mạch & huyết áp ====
IF NOT EXISTS (SELECT 1 FROM SANPHAM WHERE ten_sanpham = N'Aspirin Cardio 100mg (hộp 28 viên)')
INSERT INTO SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai) VALUES
(N'Aspirin Cardio 100mg (hộp 28 viên)',
 150, NULL,
 N'<p><strong>Aspirin Cardio 100mg</strong> (Bayer) – Liều thấp chống kết tập tiểu cầu, phòng ngừa nhồi máu cơ tim và đột quỵ ở người có nguy cơ cao. Viên bao tan trong ruột bảo vệ dạ dày.</p>',
 N'Aspirin 100mg liều thấp, phòng ngừa đột quỵ nhồi máu cơ tim, viên tan trong ruột bảo vệ dạ dày.',
 55000, 0, GETDATE(), 6),

(N'Concor 5mg Bisoprolol (hộp 30 viên)',
 80, NULL,
 N'<p><strong>Concor 5mg</strong> (Merck) – Bisoprolol fumarate 5mg, thuốc chẹn beta-1 chọn lọc. Điều trị tăng huyết áp, suy tim mãn, đau thắt ngực ổn định. Dùng 1 lần/ngày.</p><p><strong>Cần kê đơn bác sĩ.</strong></p>',
 N'Bisoprolol 5mg chẹn beta tim mạch, điều trị tăng huyết áp suy tim. Của Merck. Cần kê đơn.',
 185000, 0, GETDATE(), 6),

(N'Amlor 5mg Amlodipine (hộp 30 viên)',
 80, NULL,
 N'<p><strong>Amlor 5mg</strong> (Pfizer) – Amlodipine besylate 5mg, thuốc chẹn kênh Canxi. Điều trị tăng huyết áp và đau thắt ngực. Tác dụng 24 giờ, dùng 1 viên/ngày.</p>',
 N'Amlodipine 5mg của Pfizer, điều trị tăng huyết áp đau thắt ngực. Tác dụng 24 giờ.',
 265000, 10, GETDATE(), 6),

(N'Atorvastatin 20mg (hộp 30 viên)',
 100, NULL,
 N'<p><strong>Atorvastatin 20mg</strong> – Thuốc nhóm statin, hạ cholesterol LDL hiệu quả, giảm triglyceride, tăng HDL. Phòng ngừa biến cố tim mạch ở người có nguy cơ cao.</p>',
 N'Statin hạ cholesterol LDL, giảm triglyceride, phòng ngừa biến cố tim mạch. Dùng buổi tối.',
 145000, 20, GETDATE(), 6);

-- ==== LOẠI 7: Dị ứng & da liễu ====
IF NOT EXISTS (SELECT 1 FROM SANPHAM WHERE ten_sanpham = N'Zyrtec 10mg Cetirizine (hộp 10 viên)')
INSERT INTO SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai) VALUES
(N'Zyrtec 10mg Cetirizine (hộp 10 viên)',
 180, NULL,
 N'<p><strong>Zyrtec 10mg</strong> (UCB Pharma) – Cetirizine HCl 10mg, kháng histamine thế hệ 2, ít buồn ngủ. Điều trị viêm mũi dị ứng, nổi mề đay, ngứa da. Tác dụng kéo dài 24 giờ.</p>',
 N'Cetirizine 10mg kháng histamine thế hệ 2, ít buồn ngủ, trị dị ứng mũi mề đay. Tác dụng 24 giờ.',
 125000, 0, GETDATE(), 7),

(N'Telfast 180mg Fexofenadine (hộp 10 viên)',
 120, NULL,
 N'<p><strong>Telfast 180mg</strong> (Sanofi) – Fexofenadine 180mg, kháng histamine mạnh nhất thế hệ 3, không qua hàng rào máu não nên hoàn toàn không gây buồn ngủ. Dùng cho viêm mũi dị ứng, mề đay mãn tính.</p>',
 N'Fexofenadine 180mg kháng histamine thế hệ 3 mạnh nhất, không buồn ngủ, trị dị ứng hiệu quả.',
 175000, 15, GETDATE(), 7),

(N'Chlorpheniramine 4mg (hộp 100 viên)',
 300, NULL,
 N'<p><strong>Chlorpheniramine 4mg</strong> – Kháng histamine thế hệ 1 kinh điển, trị dị ứng, viêm mũi dị ứng, ngứa. Giá thành rất thấp. Lưu ý gây buồn ngủ, không lái xe khi dùng.</p>',
 N'Kháng histamine Chlorpheniramine 4mg, trị dị ứng ngứa mề đay. Giá tiết kiệm, hiệu quả cao.',
 18000, 0, GETDATE(), 7),

(N'Eumovate 0.05% Kem bôi da (15g)',
 80, NULL,
 N'<p><strong>Eumovate 0.05%</strong> (GlaxoSmithKline) – Kem bôi chứa Clobetasone butyrate 0.05%, corticoid nhẹ. Điều trị viêm da cơ địa, chàm, viêm da tiếp xúc, ngứa da. Phù hợp dùng ở mặt và vùng da nhạy cảm.</p>',
 N'Kem bôi Clobetasone 0.05%, trị viêm da chàm ngứa, nhẹ nhàng phù hợp da mặt và da nhạy cảm.',
 95000, 0, GETDATE(), 7),

(N'Nizoral Shampoo 2% Ketoconazole 100ml',
 100, NULL,
 N'<p><strong>Nizoral Shampoo 2%</strong> (Janssen) – Dầu gội chứa Ketoconazole 2%, điều trị gàu, viêm da tiết bã, nhiễm nấm da đầu. Dùng 2 lần/tuần trong 2-4 tuần.</p>',
 N'Dầu gội Ketoconazole 2%, điều trị gàu nhiều viêm da tiết bã da đầu. Thương hiệu Janssen.',
 128000, 10, GETDATE(), 7);

-- ==== LOẠI 8: Thực phẩm chức năng ====
IF NOT EXISTS (SELECT 1 FROM SANPHAM WHERE ten_sanpham = N'Glucosamine 1500mg (hộp 60 viên)')
INSERT INTO SANPHAM (ten_sanpham, soluong, hinh, mota, motangan, gia, giamgia, ngaytao, id_loai) VALUES
(N'Glucosamine 1500mg (hộp 60 viên)',
 150, NULL,
 N'<p><strong>Glucosamine 1500mg</strong> – Bổ sung glucosamine sulfate tái tạo sụn khớp, giảm đau khớp mãn tính, phòng thoái hóa khớp. Cần dùng ít nhất 3 tháng để thấy hiệu quả.</p>',
 N'Glucosamine 1500mg tái tạo sụn khớp, giảm đau khớp mãn tính, phòng thoái hóa khớp.',
 285000, 20, GETDATE(), 8),

(N'Coenzyme Q10 100mg (hộp 30 viên)',
 100, NULL,
 N'<p><strong>Coenzyme Q10 100mg</strong> – CoQ10 chống oxy hóa mạnh, bảo vệ tim mạch, tăng năng lượng tế bào, làm chậm lão hóa. Đặc biệt cần thiết cho người dùng statin lâu dài.</p>',
 N'CoQ10 100mg chống oxy hóa bảo vệ tim, tăng năng lượng, làm chậm lão hóa tế bào.',
 385000, 25, GETDATE(), 8),

(N'Collagen Peptide 5000mg (hộp 30 gói)',
 120, NULL,
 N'<p><strong>Collagen Peptide 5000mg</strong> – Collagen thủy phân loại I+III từ cá biển sâu, phân tử nhỏ dễ hấp thu. Giúp da đàn hồi, giảm nếp nhăn, tốt cho xương khớp và tóc móng.</p>',
 N'Collagen peptide 5000mg từ cá biển, tăng đàn hồi da, giảm nhăn, tốt cho xương khớp tóc móng.',
 425000, 15, GETDATE(), 8),

(N'Probiotics Florastor 250mg (hộp 20 viên)',
 80, NULL,
 N'<p><strong>Florastor 250mg</strong> – Men vi sinh Saccharomyces boulardii CNCM I-745 phòng và điều trị tiêu chảy do kháng sinh, viêm ruột, hội chứng ruột kích thích (IBS). Chịu được nhiệt độ phòng.</p>',
 N'Men vi sinh Saccharomyces boulardii, phòng tiêu chảy do kháng sinh, cân bằng hệ vi sinh ruột.',
 345000, 0, GETDATE(), 8),

(N'Melatonin 5mg (hộp 60 viên)',
 150, NULL,
 N'<p><strong>Melatonin 5mg</strong> – Hormone điều hòa giấc ngủ tự nhiên, hỗ trợ giấc ngủ sâu không gây nghiện. Phù hợp cho người mất ngủ nhẹ, lệch múi giờ, làm ca đêm.</p>',
 N'Melatonin 5mg hỗ trợ giấc ngủ tự nhiên, không gây nghiện, phù hợp lệch múi giờ, làm đêm.',
 185000, 10, GETDATE(), 8);

PRINT 'Seeded LOAI and SANPHAM successfully!';
