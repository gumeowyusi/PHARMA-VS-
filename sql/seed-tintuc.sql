-- ============================================================
-- SEED DATA: TIN_TUC - Bài viết y tế thật
-- ============================================================
USE STORE;

INSERT INTO TIN_TUC (tieu_de, tom_tat, the_loai, tac_gia, ngay_dang, nguon_bao, luot_xem, trang_thai, noi_dung) VALUES
(N'5 lưu ý quan trọng khi tự dùng thuốc cảm cúm tại nhà',
 N'Đọc nhãn kỹ, dùng đúng liều, đúng thời điểm. Không kết hợp nhiều thuốc cảm cùng lúc vì dễ trùng hoạt chất paracetamol.',
 N'huong-dan', N'DS. Nguyễn Thị Lan', DATEADD(day,-1,GETDATE()), N'ADMIN', 128, 1,
 N'<p>Khi bị cảm cúm nhẹ, nhiều người có thói quen tự mua thuốc điều trị tại nhà. Điều này hoàn toàn có thể thực hiện an toàn nếu bạn nắm được các nguyên tắc cơ bản sau.</p><h6>1. Đọc kỹ nhãn thuốc trước khi dùng</h6><p>Kiểm tra thành phần hoạt chất, đặc biệt chú ý hàm lượng Paracetamol. Nhiều loại thuốc cảm đều chứa Paracetamol — uống kết hợp dễ bị quá liều.</p><h6>2. Không uống quá 8 viên Paracetamol 500mg/ngày</h6><p>Giới hạn tối đa là 4g/ngày. Quá liều Paracetamol có thể gây suy gan nghiêm trọng.</p><h6>3. Uống đủ nước và nghỉ ngơi</h6><p>Uống ít nhất 2 lít nước mỗi ngày khi bị sốt để bù điện giải.</p><h6>4. Không dùng kháng sinh cho cảm cúm</h6><p>Cảm cúm do virus gây ra — kháng sinh hoàn toàn không có tác dụng.</p><h6>5. Khi nào cần đến bệnh viện?</h6><p>Sốt trên 39°C không giảm sau 48 giờ, khó thở, đau ngực, nôn liên tục cần đến cơ sở y tế ngay.</p>');

INSERT INTO TIN_TUC (tieu_de, tom_tat, the_loai, tac_gia, ngay_dang, nguon_bao, luot_xem, trang_thai, noi_dung) VALUES
(N'Kháng sinh đồ: Tại sao không được tự ý mua kháng sinh?',
 N'Sử dụng kháng sinh không đúng cách là nguyên nhân hàng đầu gây kháng kháng sinh toàn cầu. WHO cảnh báo đây là một trong những mối đe dọa sức khỏe nghiêm trọng nhất thế kỷ 21.',
 N'an-toan-thuoc', N'DS. Phạm Thị Mai', DATEADD(day,-3,GETDATE()), N'ADMIN', 245, 1,
 N'<p>Kháng sinh là loại thuốc chữa bệnh do vi khuẩn gây ra. Tuy nhiên, việc tự ý mua và sử dụng kháng sinh mà không có chỉ định của bác sĩ đang gây ra những hậu quả nghiêm trọng.</p><h6>Kháng kháng sinh là gì?</h6><p>Kháng kháng sinh xảy ra khi vi khuẩn thay đổi và không còn phản ứng với thuốc. Điều này khiến các bệnh nhiễm khuẩn trở nên khó điều trị hơn, làm tăng nguy cơ lây lan và tử vong.</p><h6>Những sai lầm thường gặp</h6><ul><li>Dùng kháng sinh để trị cảm lạnh, cúm (do virus — kháng sinh không có tác dụng)</li><li>Bỏ thuốc giữa chừng khi thấy đỡ bệnh</li><li>Dùng đơn thuốc cũ hoặc thuốc của người khác</li><li>Tự tăng giảm liều lượng</li></ul><h6>Khuyến cáo của WHO</h6><p>Chỉ dùng kháng sinh khi được bác sĩ hoặc dược sĩ có thẩm quyền kê đơn. Luôn uống đủ liều, đúng thời gian ngay cả khi cảm thấy khỏe hơn.</p>');

INSERT INTO TIN_TUC (tieu_de, tom_tat, the_loai, tac_gia, ngay_dang, nguon_bao, luot_xem, trang_thai, noi_dung) VALUES
(N'Vitamin D và miễn dịch: Sự thật bạn cần biết',
 N'Nghiên cứu từ Harvard cho thấy thiếu Vitamin D làm tăng nguy cơ mắc bệnh nhiễm khuẩn đường hô hấp lên 40%. Cách bổ sung Vitamin D đúng cách để tăng cường miễn dịch.',
 N'dinh-duong', N'TS. BS. Nguyễn Văn An', DATEADD(day,-7,GETDATE()), N'ADMIN', 512, 1,
 N'<p>Vitamin D không chỉ quan trọng cho xương mà còn đóng vai trò then chốt trong hệ miễn dịch. Nghiên cứu trên BMJ cho thấy bổ sung Vitamin D có thể giảm nguy cơ nhiễm khuẩn đường hô hấp cấp tính.</p><h6>Tại sao Vitamin D quan trọng với miễn dịch?</h6><p>Vitamin D kích thích sản xuất các peptide kháng khuẩn tự nhiên trong phổi và đường tiêu hóa. Nó cũng điều chỉnh phản ứng viêm, giúp cơ thể chiến đấu với vi khuẩn và virus mà không gây viêm quá mức.</p><h6>Ai dễ bị thiếu Vitamin D?</h6><ul><li>Người ít ra ngoài nắng (làm việc trong văn phòng)</li><li>Người cao tuổi</li><li>Người béo phì</li><li>Người ăn chay trường</li></ul><h6>Cách bổ sung hiệu quả</h6><p>Tắm nắng 15-30 phút/ngày trước 9h sáng. Ăn cá béo (cá hồi, cá thu), trứng, nấm. Nếu thiếu nặng cần bổ sung theo chỉ dẫn của bác sĩ.</p>');

INSERT INTO TIN_TUC (tieu_de, tom_tat, the_loai, tac_gia, ngay_dang, nguon_bao, luot_xem, trang_thai, noi_dung) VALUES
(N'Tăng huyết áp: Kẻ giết người thầm lặng và cách phòng ngừa',
 N'Theo thống kê của Bộ Y tế, có khoảng 12 triệu người Việt Nam mắc tăng huyết áp nhưng hơn 50% không biết mình bị bệnh.',
 N'phong-benh', N'PGS. TS. Trần Thị Hoa', DATEADD(day,-1,GETDATE()), N'ADMIN', 738, 1,
 N'<p>Tăng huyết áp được mệnh danh là kẻ giết người thầm lặng vì thường không có triệu chứng rõ ràng cho đến khi gây ra các biến chứng nghiêm trọng như đột quỵ, nhồi máu cơ tim, suy thận.</p><h6>Phân loại huyết áp</h6><ul><li><strong>Bình thường:</strong> dưới 120/80 mmHg</li><li><strong>Tiền tăng HA:</strong> 120-139/80-89 mmHg</li><li><strong>Tăng HA độ 1:</strong> 140-159/90-99 mmHg</li><li><strong>Tăng HA độ 2:</strong> ≥160/≥100 mmHg</li></ul><h6>Lối sống kiểm soát huyết áp</h6><p>Giảm muối (dưới 5g/ngày), tăng hoạt động thể lực ít nhất 30 phút/ngày, duy trì cân nặng hợp lý, không hút thuốc, hạn chế rượu bia. Theo dõi huyết áp tại nhà thường xuyên.</p>');

INSERT INTO TIN_TUC (tieu_de, tom_tat, the_loai, tac_gia, ngay_dang, nguon_bao, luot_xem, trang_thai, noi_dung) VALUES
(N'Omega-3: Lợi ích tim mạch được khoa học chứng minh',
 N'Hàng trăm nghiên cứu lâm sàng xác nhận Omega-3 (EPA và DHA) giảm triglyceride máu, chống viêm, bảo vệ tim mạch. Ai cần bổ sung và liều lượng thế nào là đủ?',
 N'dinh-duong', N'DS. Lê Minh Tuấn', DATEADD(day,-5,GETDATE()), N'ADMIN', 423, 1,
 N'<p>Axit béo Omega-3, đặc biệt là EPA (eicosapentaenoic acid) và DHA (docosahexaenoic acid), là những dưỡng chất thiết yếu mà cơ thể người không tự tổng hợp được.</p><h6>Lợi ích được chứng minh khoa học</h6><ul><li>Giảm triglyceride máu 20-50%</li><li>Giảm nguy cơ nhồi máu cơ tim</li><li>Chống viêm, giảm đau khớp</li><li>Hỗ trợ phát triển não bộ ở trẻ em</li><li>Cải thiện triệu chứng trầm cảm nhẹ</li></ul><h6>Nguồn cung cấp tốt nhất</h6><p>Cá hồi (2.260mg/100g), cá thu (2.670mg/100g), cá trích, cá mòi, hạt lanh, hạt chia, quả óc chó.</p>');

INSERT INTO TIN_TUC (tieu_de, tom_tat, the_loai, tac_gia, ngay_dang, nguon_bao, luot_xem, trang_thai, noi_dung) VALUES
(N'Loét dạ dày và H.pylori: Nguyên nhân, chẩn đoán và điều trị',
 N'Vi khuẩn Helicobacter pylori là nguyên nhân gây loét dạ dày tá tràng ở 80% trường hợp. Cách nhận biết triệu chứng và phác đồ điều trị chuẩn hiện nay.',
 N'huong-dan', N'BS. Nguyễn Thị Lan Anh', DATEADD(day,-10,GETDATE()), N'ADMIN', 896, 1,
 N'<p>Helicobacter pylori (H.pylori) là vi khuẩn sống trong lớp nhầy bảo vệ niêm mạc dạ dày. Khoảng 50% dân số thế giới nhiễm H.pylori, nhưng chỉ 10-15% phát triển thành loét dạ dày.</p><h6>Triệu chứng cần chú ý</h6><ul><li>Đau bụng vùng thượng vị, thường đau khi đói hoặc ban đêm</li><li>Đầy bụng, ợ chua, buồn nôn</li><li>Chán ăn, sụt cân không rõ nguyên nhân</li><li>Phân đen (dấu hiệu xuất huyết tiêu hóa — cần cấp cứu ngay)</li></ul><h6>Phác đồ điều trị chuẩn Triple Therapy</h6><p>PPI (Omeprazole/Pantoprazole) + Amoxicillin + Clarithromycin × 14 ngày. Tuân thủ đủ liều để đạt tỷ lệ tiệt trừ trên 90%.</p>');

INSERT INTO TIN_TUC (tieu_de, tom_tat, the_loai, tac_gia, ngay_dang, nguon_bao, luot_xem, trang_thai, noi_dung) VALUES
(N'Hướng dẫn bảo quản thuốc tại nhà đúng cách',
 N'Nhiều gia đình bảo quản thuốc không đúng cách khiến thuốc giảm hiệu quả hoặc hỏng trước hạn dùng. Những nguyên tắc vàng giúp bảo quản tủ thuốc gia đình an toàn.',
 N'huong-dan', N'DS. Vũ Thị Thu', DATEADD(day,-14,GETDATE()), N'ADMIN', 334, 1,
 N'<p>Việc bảo quản thuốc đúng cách không chỉ đảm bảo hiệu quả điều trị mà còn phòng tránh ngộ độc và các tai nạn không mong muốn, đặc biệt với trẻ nhỏ.</p><h6>Nguyên tắc bảo quản vàng</h6><ul><li><strong>Nhiệt độ:</strong> Hầu hết thuốc cần 15-25°C, tránh nơi nóng ẩm.</li><li><strong>Ánh sáng:</strong> Tránh ánh nắng trực tiếp, đặc biệt thuốc nhỏ mắt và insulin.</li><li><strong>Độ ẩm:</strong> Không để trong phòng tắm hay bếp.</li><li><strong>Trẻ em:</strong> Để thuốc ngoài tầm với của trẻ, khóa tủ thuốc.</li></ul><h6>Thuốc cần bỏ ngay</h6><p>Bỏ thuốc quá hạn, viên bị ẩm vón cục, thay đổi màu/mùi. Không đổ thuốc xuống toilet — mang trả nhà thuốc để xử lý đúng cách.</p>');

INSERT INTO TIN_TUC (tieu_de, tom_tat, the_loai, tac_gia, ngay_dang, nguon_bao, luot_xem, trang_thai, noi_dung) VALUES
(N'Mẹo giảm tác dụng phụ khi dùng thuốc kháng viêm NSAIDs',
 N'Ibuprofen, Diclofenac, Aspirin... hiệu quả nhưng dễ gây loét dạ dày. Những mẹo đơn giản giúp bạn dùng NSAIDs an toàn hơn mà không cần lo lắng về tác dụng phụ.',
 N'meo-hay', N'DS. Trần Văn Bình', DATEADD(day,-4,GETDATE()), N'ADMIN', 289, 1,
 N'<p>Thuốc kháng viêm không steroid (NSAIDs) như Ibuprofen, Diclofenac, Aspirin là những thuốc phổ biến nhất thế giới. Tuy nhiên, tác dụng phụ trên dạ dày là vấn đề đáng lo ngại nếu dùng không đúng cách.</p><h6>Tại sao NSAIDs gây hại dạ dày?</h6><p>NSAIDs ức chế enzyme COX-1, làm giảm tổng hợp prostaglandin — chất bảo vệ niêm mạc dạ dày. Kết quả là niêm mạc dễ bị tổn thương bởi acid dạ dày.</p><h6>Mẹo dùng NSAIDs an toàn</h6><ul><li><strong>Uống sau bữa ăn</strong> hoặc cùng với sữa</li><li><strong>Uống nhiều nước</strong> (ít nhất 1 ly đầy)</li><li>Không nằm ngay sau uống thuốc ít nhất 30 phút</li><li>Kết hợp với thuốc bảo vệ dạ dày (Omeprazole/Pantoprazole) nếu dùng dài ngày</li><li>Không uống kết hợp 2 loại NSAIDs khác nhau cùng lúc</li></ul>');

INSERT INTO TIN_TUC (tieu_de, tom_tat, the_loai, tac_gia, ngay_dang, nguon_bao, luot_xem, trang_thai, noi_dung) VALUES
(N'Cách tăng cường hệ miễn dịch tự nhiên không cần thuốc',
 N'Trước khi nghĩ đến việc uống thuốc bổ, hãy thực hiện 6 thói quen sống lành mạnh này. Khoa học chứng minh chúng hiệu quả hơn bất kỳ viên vitamin nào.',
 N'song-khoe', N'BS. Hoàng Thị Ngọc', DATEADD(day,-6,GETDATE()), N'ADMIN', 645, 1,
 N'<p>Hệ miễn dịch khỏe mạnh là chiếc khiên bảo vệ cơ thể tốt nhất. Thay vì phụ thuộc vào thuốc bổ, hãy xây dựng miễn dịch từ bên trong bằng lối sống lành mạnh.</p><h6>6 thói quen tăng cường miễn dịch</h6><ul><li><strong>Ngủ đủ 7-8 tiếng/đêm:</strong> Trong khi ngủ, cơ thể sản xuất cytokine — protein chống nhiễm khuẩn.</li><li><strong>Vận động 30 phút/ngày:</strong> Tập thể dục vừa sức tăng tuần hoàn bạch cầu.</li><li><strong>Ăn nhiều rau củ nhiều màu sắc:</strong> Đặc biệt tỏi, gừng, nghệ có tính kháng khuẩn mạnh.</li><li><strong>Giảm stress:</strong> Stress mãn tính ức chế hệ miễn dịch qua cortisol.</li><li><strong>Không hút thuốc lá:</strong> Nicotin làm giảm đáp ứng miễn dịch đến 50%.</li><li><strong>Hạn chế đường tinh luyện:</strong> Đường ức chế hoạt động của bạch cầu hạt.</li></ul>');

INSERT INTO TIN_TUC (tieu_de, tom_tat, the_loai, tac_gia, ngay_dang, nguon_bao, luot_xem, trang_thai, noi_dung) VALUES
(N'Tiểu đường type 2: Phòng ngừa bằng chế độ ăn và vận động',
 N'Tiểu đường type 2 ảnh hưởng đến hơn 3,5 triệu người Việt Nam. Tin tốt là 60% các trường hợp có thể phòng ngừa hoặc trì hoãn hoàn toàn bằng thay đổi lối sống.',
 N'phong-benh', N'PGS. TS. Nguyễn Minh Đức', DATEADD(day,-12,GETDATE()), N'ADMIN', 567, 1,
 N'<p>Tiểu đường type 2 là bệnh mãn tính do cơ thể không sử dụng insulin hiệu quả. Khác với type 1, tiểu đường type 2 phần lớn là hệ quả của lối sống và hoàn toàn có thể phòng ngừa.</p><h6>Yếu tố nguy cơ</h6><ul><li>Thừa cân, béo phì (đặc biệt béo bụng)</li><li>Ít vận động</li><li>Ăn nhiều tinh bột tinh luyện, đường</li><li>Tiền sử gia đình có bệnh tiểu đường</li><li>Tuổi trên 45</li></ul><h6>Chế độ ăn phòng tiểu đường</h6><p>Ưu tiên: Rau xanh, ngũ cốc nguyên cám, đậu đỗ, cá, thịt nạc. Hạn chế: Cơm trắng, bánh mì, nước ngọt, đồ chiên rán, rượu bia. Kiểm soát khẩu phần và ăn đúng giờ giúp ổn định đường huyết.</p>');

PRINT 'Inserted 10 TIN_TUC articles successfully!';
