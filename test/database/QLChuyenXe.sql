IF DB_ID(N'QLChuyenXe') IS NULL
    CREATE DATABASE QLChuyenXe;
GO

USE QLChuyenXe;
GO

IF OBJECT_ID(N'dbo.Chuyen_Xe', N'U') IS NOT NULL DROP TABLE dbo.Chuyen_Xe;
IF OBJECT_ID(N'dbo.San_Pham', N'U') IS NOT NULL DROP TABLE dbo.San_Pham;
IF OBJECT_ID(N'dbo.Xe',       N'U') IS NOT NULL DROP TABLE dbo.Xe;
GO

CREATE TABLE dbo.San_Pham (
    MaSP    VARCHAR(10)   NOT NULL,
    TenSP   NVARCHAR(50)  NOT NULL,
    DonGia  INT           NOT NULL,
    CONSTRAINT PK_San_Pham PRIMARY KEY (MaSP)
);
GO

CREATE TABLE dbo.Xe (
    MaXe      VARCHAR(10)  NOT NULL,
    TenXe     NVARCHAR(50) NOT NULL,
    GiaTuyen  INT          NOT NULL,
    CONSTRAINT PK_Xe PRIMARY KEY (MaXe)
);
GO

CREATE TABLE dbo.Chuyen_Xe (
    MaSP     VARCHAR(10)   NOT NULL,
    MaXe     VARCHAR(10)   NOT NULL,
    NgayGio  DATE          NOT NULL,
    SoLuong  INT           NOT NULL,
    GhiChu   NVARCHAR(100) NULL,
    CONSTRAINT PK_Chuyen_Xe PRIMARY KEY (MaSP, MaXe, NgayGio)
);
GO


ALTER TABLE dbo.Chuyen_Xe
    ADD CONSTRAINT FK_Chuyen_Xe_San_Pham FOREIGN KEY (MaSP)
        REFERENCES dbo.San_Pham (MaSP) ON UPDATE CASCADE;
GO

ALTER TABLE dbo.Chuyen_Xe
    ADD CONSTRAINT FK_Chuyen_Xe_Xe FOREIGN KEY (MaXe)
        REFERENCES dbo.Xe (MaXe) ON UPDATE CASCADE ON DELETE CASCADE;
GO

ALTER TABLE dbo.San_Pham ADD
    CONSTRAINT CK_San_Pham_TenSP  CHECK (LEN(LTRIM(RTRIM(TenSP))) > 0),
    CONSTRAINT CK_San_Pham_DonGia CHECK (DonGia >= 0);
GO

ALTER TABLE dbo.Xe ADD
    CONSTRAINT CK_Xe_TenXe    CHECK (LEN(LTRIM(RTRIM(TenXe))) > 0),
    CONSTRAINT CK_Xe_GiaTuyen CHECK (GiaTuyen >= 0);
GO

ALTER TABLE dbo.Chuyen_Xe ADD
    CONSTRAINT CK_Chuyen_Xe_SoLuong CHECK (SoLuong > 0);
GO

CREATE INDEX IX_Chuyen_Xe_MaXe_NgayGio ON dbo.Chuyen_Xe (MaXe, NgayGio);
GO


INSERT INTO dbo.San_Pham (MaSP, TenSP, DonGia) VALUES
    ('BAN001', N'Bàn gỗ',        1200000),
    ('LAP001', N'Laptop Dell',  15000000),
    ('GHE001', N'Ghế xoay',       850000),
    ('TUL001', N'Tủ lạnh',       7200000),
    ('QUA001', N'Quạt đứng',      450000);
GO

INSERT INTO dbo.Xe (MaXe, TenXe, GiaTuyen) VALUES
    ('KH001', N'Towner',     100),
    ('KH002', N'Hyundai HD', 250),
    ('KH003', N'Thaco Kia',  180);
GO

INSERT INTO dbo.Chuyen_Xe (MaSP, MaXe, NgayGio, SoLuong, GhiChu) VALUES
    ('BAN001', 'KH001', '2012-10-29', 3, NULL),
    ('LAP001', 'KH001', '2012-10-28', 1, N'Laptop'),
    ('GHE001', 'KH001', '2012-11-02', 5, N'Hàng dễ vỡ'),
    ('TUL001', 'KH002', '2012-11-05', 2, N'Giao tận nơi'),
    ('QUA001', 'KH002', '2012-11-06', 8, NULL),
    ('BAN001', 'KH003', '2012-11-10', 4, N'Đóng thùng');
GO


SELECT cx.MaSP, sp.TenSP, sp.DonGia, cx.NgayGio, cx.SoLuong,
       sp.DonGia * cx.SoLuong AS ThanhTien, cx.GhiChu
FROM   dbo.Chuyen_Xe cx
       INNER JOIN dbo.San_Pham sp ON sp.MaSP = cx.MaSP
WHERE  cx.MaXe = 'KH001'
ORDER  BY cx.NgayGio, cx.MaSP;
GO
