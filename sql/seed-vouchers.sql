-- ============================================================
--  Seed vouchers: Freeship (standard + express)
--  Chỉ insert nếu code chưa tồn tại
-- ============================================================

-- ── FREESHIP TIÊU CHUẨN (FIXED 20.000₫, đơn tối thiểu 50k) ──
INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP01', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP01');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP02', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP02');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP03', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP03');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP04', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP04');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP05', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP05');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP06', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP06');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP07', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP07');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP08', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP08');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP09', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP09');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP10', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP10');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP11', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP11');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP12', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP12');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP13', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP13');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP14', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP14');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP15', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP15');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP16', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP16');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP17', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP17');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP18', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP18');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP19', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP19');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'FREESHIP20', 'FIXED', 20000, NULL, 50000, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'FREESHIP20');

-- ── FREESHIP HỎA TỐC (FIXED 50.000₫, đơn tối thiểu 150k) ──
INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'HOATOC01', 'FIXED', 50000, NULL, 150000, 200, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'HOATOC01');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'HOATOC02', 'FIXED', 50000, NULL, 150000, 200, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'HOATOC02');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'HOATOC03', 'FIXED', 50000, NULL, 150000, 200, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'HOATOC03');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'HOATOC04', 'FIXED', 50000, NULL, 150000, 200, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'HOATOC04');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'HOATOC05', 'FIXED', 50000, NULL, 150000, 200, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'HOATOC05');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'HOATOC06', 'FIXED', 50000, NULL, 150000, 200, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'HOATOC06');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'HOATOC07', 'FIXED', 50000, NULL, 150000, 200, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'HOATOC07');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'HOATOC08', 'FIXED', 50000, NULL, 150000, 200, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'HOATOC08');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'HOATOC09', 'FIXED', 50000, NULL, 150000, 200, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'HOATOC09');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'HOATOC10', 'FIXED', 50000, NULL, 150000, 200, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'HOATOC10');

-- ── VOUCHER FREESHIP KHÔNG CÓ ĐIỀU KIỆN TỐI THIỂU ──
INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'SHIPFREE', 'FIXED', 20000, NULL, NULL, 9999, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'SHIPFREE');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'SHIPNHANH', 'FIXED', 50000, NULL, NULL, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'SHIPNHANH');

-- ── VOUCHER GIẢM GIÁ ──
INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'MEDISALE5', 'PERCENT', 5, 30000, NULL, 9999, 3, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'MEDISALE5');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'SALE10', 'PERCENT', 10, 50000, NULL, 1000, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'SALE10');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'SALE15', 'PERCENT', 15, 75000, NULL, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'SALE15');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'SALE20', 'PERCENT', 20, 100000, NULL, 300, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'SALE20');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'NEWUSER', 'PERCENT', 25, 150000, NULL, 9999, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'NEWUSER');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'GIAM30K', 'FIXED', 30000, NULL, NULL, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'GIAM30K');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'GIAM50K', 'FIXED', 50000, NULL, 200000, 300, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'GIAM50K');

PRINT 'Seed vouchers completed.';
