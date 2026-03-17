-- Tạo bảng WISHLIST
CREATE TABLE WISHLIST (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    id_user     VARCHAR(50)  NOT NULL,
    id_sanpham  INT          NOT NULL,
    ngaytao     DATETIME     NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_WISHLIST_USER    FOREIGN KEY (id_user)    REFERENCES USERS(id_user),
    CONSTRAINT FK_WISHLIST_SANPHAM FOREIGN KEY (id_sanpham) REFERENCES SANPHAM(id_sanpham),
    CONSTRAINT UQ_WISHLIST         UNIQUE (id_user, id_sanpham)
);
