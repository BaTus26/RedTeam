package vn.edu.qlchuyenxe.dao.jdbc;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import vn.edu.qlchuyenxe.dao.ChuyenXeDao;
import vn.edu.qlchuyenxe.db.ConnectionManager;
import vn.edu.qlchuyenxe.model.ChiTietChuyenXe;
import vn.edu.qlchuyenxe.model.ChuyenXe;

public class JdbcChuyenXeDao extends JdbcSupport implements ChuyenXeDao {

    private static final String SELECT_DETAIL =
            "SELECT cx.MaSP, sp.TenSP, sp.DonGia, cx.NgayGio, cx.SoLuong, cx.GhiChu "
          + "FROM dbo.Chuyen_Xe cx INNER JOIN dbo.San_Pham sp ON sp.MaSP = cx.MaSP ";

    public JdbcChuyenXeDao(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public List<ChiTietChuyenXe> findByXe(String maXe) {
        return query(SELECT_DETAIL + "WHERE cx.MaXe = ? ORDER BY cx.NgayGio, cx.MaSP",
                JdbcChuyenXeDao::map, maXe);
    }

    @Override
    public List<ChiTietChuyenXe> search(String maXe, LocalDate ngayChay) {
        StringBuilder sql = new StringBuilder(SELECT_DETAIL).append("WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        if (maXe != null && !maXe.trim().isEmpty()) {
            sql.append("AND cx.MaXe = ? ");
            params.add(maXe.trim());
        }
        if (ngayChay != null) {
            sql.append("AND cx.NgayGio = ? ");
            params.add(Date.valueOf(ngayChay));
        }
        sql.append("ORDER BY cx.MaXe, cx.NgayGio, cx.MaSP");
        return query(sql.toString(), JdbcChuyenXeDao::map, params.toArray());
    }

    @Override
    public boolean exists(String maSP, String maXe, LocalDate ngayGio) {
        return existsBy("SELECT 1 FROM dbo.Chuyen_Xe WHERE MaSP = ? AND MaXe = ? AND NgayGio = ?",
                maSP, maXe, Date.valueOf(ngayGio));
    }

    @Override
    public void insert(ChuyenXe chuyenXe) {
        execute("INSERT INTO dbo.Chuyen_Xe (MaSP, MaXe, NgayGio, SoLuong, GhiChu) VALUES (?, ?, ?, ?, ?)",
                chuyenXe.getMaSP(), chuyenXe.getMaXe(), Date.valueOf(chuyenXe.getNgayGio()),
                chuyenXe.getSoLuong(), chuyenXe.getGhiChu());
    }

    @Override
    public void update(ChuyenXe key, ChuyenXe value) {
        execute("UPDATE dbo.Chuyen_Xe SET MaSP = ?, NgayGio = ?, SoLuong = ?, GhiChu = ? "
                        + "WHERE MaSP = ? AND MaXe = ? AND NgayGio = ?",
                value.getMaSP(), Date.valueOf(value.getNgayGio()), value.getSoLuong(), value.getGhiChu(),
                key.getMaSP(), key.getMaXe(), Date.valueOf(key.getNgayGio()));
    }

    @Override
    public void delete(String maSP, String maXe, LocalDate ngayGio) {
        execute("DELETE FROM dbo.Chuyen_Xe WHERE MaSP = ? AND MaXe = ? AND NgayGio = ?",
                maSP, maXe, Date.valueOf(ngayGio));
    }

    @Override
    public void deleteByXe(String maXe) {
        execute("DELETE FROM dbo.Chuyen_Xe WHERE MaXe = ?", maXe);
    }

    private static ChiTietChuyenXe map(ResultSet rs) throws SQLException {
        Date ngay = rs.getDate("NgayGio");
        return new ChiTietChuyenXe(
                rs.getString("MaSP"),
                rs.getString("TenSP"),
                rs.getLong("DonGia"),
                ngay == null ? null : ngay.toLocalDate(),
                rs.getInt("SoLuong"),
                rs.getString("GhiChu"));
    }
}
