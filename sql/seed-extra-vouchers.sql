-- Voucher giam gia khong co dieu kien
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
SELECT 'GIAM30K', 'FIXED', 30000, NULL, NULL, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'GIAM30K');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'GIAM50K', 'FIXED', 50000, NULL, 200000, 300, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'GIAM50K');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'NEWUSER', 'PERCENT', 25, 150000, NULL, 9999, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'NEWUSER');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'MEDISALE5', 'PERCENT', 5, 30000, NULL, 9999, 3, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'MEDISALE5');

-- Freeship khong co dieu kien toi thieu
INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'SHIPFREE', 'FIXED', 20000, NULL, NULL, 9999, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'SHIPFREE');

INSERT INTO VOUCHER (code, type, value, max_discount, min_order_value, usage_limit, usage_per_user, total_used, start_date, end_date, active)
SELECT 'SHIPNHANH', 'FIXED', 50000, NULL, NULL, 500, 1, 0, GETDATE(), DATEADD(YEAR,1,GETDATE()), 1
WHERE NOT EXISTS (SELECT 1 FROM VOUCHER WHERE code = 'SHIPNHANH');

PRINT 'Done';
