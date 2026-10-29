package vn.edu.qlchuyenxe.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.edu.qlchuyenxe.dao.DaoBundle;
import vn.edu.qlchuyenxe.dao.DataAccessException;
import vn.edu.qlchuyenxe.dao.jdbc.JdbcChuyenXeDao;
import vn.edu.qlchuyenxe.dao.jdbc.JdbcSanPhamDao;
import vn.edu.qlchuyenxe.dao.jdbc.JdbcXeDao;
import vn.edu.qlchuyenxe.dao.memory.InMemoryChuyenXeDao;
import vn.edu.qlchuyenxe.dao.memory.InMemoryDatabase;
import vn.edu.qlchuyenxe.dao.memory.InMemorySanPhamDao;
import vn.edu.qlchuyenxe.dao.memory.InMemoryXeDao;
import vn.edu.qlchuyenxe.db.ConnectionManager;
import vn.edu.qlchuyenxe.db.ConnectionStatus;
import vn.edu.qlchuyenxe.model.ChiTietChuyenXe;
import vn.edu.qlchuyenxe.model.ChuyenXe;
import vn.edu.qlchuyenxe.model.SanPham;
import vn.edu.qlchuyenxe.model.Xe;
import vn.edu.qlchuyenxe.util.Validator;

/**
 * Toàn bộ nghiệp vụ của Cửa sổ chính: điều hướng bản ghi Xe, ràng buộc dữ liệu,
 * cập nhật chi tiết Chuyến xe và tính các control Thống kê.
 */
public class QuanLyChuyenXeService {

    public static final String LOI_MA_XE_TON_TAI = "Mã xe đã tồn tại, vui lòng nhập lại!";

    private final DaoBundle dao;
    private final ConnectionStatus connectionStatus;

    private final List<Xe> danhSachXe = new ArrayList<>();
    private final List<ChiTietChuyenXe> chiTietHienThoi = new ArrayList<>();
    private int viTriHienThoi = -1;

    public QuanLyChuyenXeService(DaoBundle dao, ConnectionStatus connectionStatus) {
        this.dao = dao;
        this.connectionStatus = connectionStatus;
    }

    /**
     * Kiểm tra kết nối Database Server trước; khi không kết nối được thì chuyển
     * sang nguồn dữ liệu ảo để Cửa sổ vẫn nạp được dữ liệu.
     */
    public static QuanLyChuyenXeService create(ConnectionManager connectionManager) {
        ConnectionStatus status = connectionManager.check();
        if (status.isConnected()) {
            DaoBundle jdbc = new DaoBundle(
                    new JdbcXeDao(connectionManager),
                    new JdbcSanPhamDao(connectionManager),
                    new JdbcChuyenXeDao(connectionManager));
            try {
                jdbc.xe().findAll();
                return new QuanLyChuyenXeService(jdbc, status);
            } catch (DataAccessException ex) {
                status = ConnectionStatus.failed(ex.getMessage());
            }
        }
        return new QuanLyChuyenXeService(dummyBundle(), status);
    }

    private static DaoBundle dummyBundle() {
        InMemoryDatabase db = InMemoryDatabase.withDummyRow();
        return new DaoBundle(new InMemoryXeDao(db), new InMemorySanPhamDao(db), new InMemoryChuyenXeDao(db));
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    /* ------------------------------ Nạp dữ liệu ------------------------------ */

    public void napDuLieu() {
        danhSachXe.clear();
        danhSachXe.addAll(dao.xe().findAll());
        viTriHienThoi = danhSachXe.isEmpty() ? -1 : 0;
        napChiTiet();
    }

    private void napChiTiet() {
        chiTietHienThoi.clear();
        Xe xe = getXeHienThoi();
        if (xe != null) {
            chiTietHienThoi.addAll(dao.chuyenXe().findByXe(xe.getMaXe()));
        }
    }

    public List<Xe> getDanhSachXe() {
        return Collections.unmodifiableList(danhSachXe);
    }

    public List<SanPham> getDanhSachSanPham() {
        return dao.sanPham().findAll();
    }

    public List<ChiTietChuyenXe> getChiTietHienThoi() {
        return Collections.unmodifiableList(chiTietHienThoi);
    }

    public Xe getXeHienThoi() {
        return viTriHienThoi < 0 || viTriHienThoi >= danhSachXe.size() ? null : danhSachXe.get(viTriHienThoi);
    }

    public ThongKe getThongKe() {
        return ThongKe.of(chiTietHienThoi);
    }

    /* ------------------------------ Điều hướng ------------------------------- */

    public void veDau() {
        diChuyenDen(0);
    }

    public void veTruoc() {
        diChuyenDen(viTriHienThoi - 1);
    }

    public void veSau() {
        diChuyenDen(viTriHienThoi + 1);
    }

    public void veCuoi() {
        diChuyenDen(danhSachXe.size() - 1);
    }

    public void diChuyenDen(int viTri) {
        if (danhSachXe.isEmpty()) {
            viTriHienThoi = -1;
        } else {
            viTriHienThoi = Math.max(0, Math.min(viTri, danhSachXe.size() - 1));
        }
        napChiTiet();
    }

    public int getViTriHienThoi() {
        return viTriHienThoi;
    }

    public int getTongSoBanGhi() {
        return danhSachXe.size();
    }

    /** Chỉ số dạng "1/3" của bản ghi hiện thời trên tổng số bản ghi table Xe. */
    public String getChiSo() {
        return danhSachXe.isEmpty() ? "0/0" : (viTriHienThoi + 1) + "/" + danhSachXe.size();
    }

    /* -------------------------------- Xe ------------------------------------- */

    public void kiemTraRangBuocXe(String maXe, String tenXe, String giaTuyen) {
        if (Validator.isBlank(maXe)) {
            throw new BusinessException("Mã xe không được để trống!");
        }
        if (!Validator.isCode(maXe)) {
            throw new BusinessException("Mã xe chỉ gồm chữ và số, tối đa 10 ký tự!");
        }
        if (Validator.isBlank(tenXe)) {
            throw new BusinessException("Tên xe không được để trống!");
        }
        if (!Validator.isName(tenXe)) {
            throw new BusinessException("Tên xe tối đa 50 ký tự!");
        }
        if (Validator.isBlank(giaTuyen)) {
            throw new BusinessException("Giá tuyến không được để trống!");
        }
        if (!Validator.isNonNegativeNumber(giaTuyen)) {
            throw new BusinessException("Giá tuyến phải là số nguyên không âm!");
        }
    }

    public Xe themXe(String maXe, String tenXe, String giaTuyen) {
        kiemTraRangBuocXe(maXe, tenXe, giaTuyen);
        if (dao.xe().exists(maXe.trim())) {
            throw new BusinessException(LOI_MA_XE_TON_TAI);
        }
        Xe xe = new Xe(maXe.trim(), tenXe.trim(), Long.parseLong(giaTuyen.trim()));
        dao.xe().insert(xe);
        napDuLieu();
        diChuyenDen(indexOf(xe.getMaXe()));
        return xe;
    }

    /** Xóa Xe hiện thời rồi đưa dữ liệu của Xe kế tiếp lên Cửa sổ. */
    public Xe xoaXeHienThoi() {
        Xe xe = getXeHienThoi();
        if (xe == null) {
            throw new BusinessException("Không có bản ghi Xe nào để xóa!");
        }
        int viTriCu = viTriHienThoi;
        dao.chuyenXe().deleteByXe(xe.getMaXe());
        dao.xe().delete(xe.getMaXe());
        danhSachXe.clear();
        danhSachXe.addAll(dao.xe().findAll());
        diChuyenDen(Math.min(viTriCu, danhSachXe.size() - 1));
        return getXeHienThoi();
    }

    private int indexOf(String maXe) {
        for (int i = 0; i < danhSachXe.size(); i++) {
            if (danhSachXe.get(i).getMaXe().equalsIgnoreCase(maXe)) {
                return i;
            }
        }
        return 0;
    }

    /* ---------------------------- Chi tiết chuyến xe -------------------------- */

    public void themChiTiet(String maSP, LocalDate ngayChay, String soLuong, String ghiChu) {
        Xe xe = yeuCauXeHienThoi();
        kiemTraRangBuocChiTiet(maSP, ngayChay, soLuong);
        if (dao.chuyenXe().exists(maSP, xe.getMaXe(), ngayChay)) {
            throw new BusinessException("Sản phẩm này đã được tải lên xe trong ngày đã chọn!");
        }
        dao.chuyenXe().insert(new ChuyenXe(maSP, xe.getMaXe(), ngayChay,
                Integer.parseInt(soLuong.trim()), ghiChu));
        napChiTiet();
    }

    public void suaChiTiet(ChiTietChuyenXe goc, String maSP, LocalDate ngayChay, String soLuong, String ghiChu) {
        Xe xe = yeuCauXeHienThoi();
        if (goc == null) {
            throw new BusinessException("Chưa chọn dòng dữ liệu cần sửa!");
        }
        kiemTraRangBuocChiTiet(maSP, ngayChay, soLuong);
        boolean doiKhoa = !goc.getMaSP().equalsIgnoreCase(maSP) || !goc.getNgayChay().equals(ngayChay);
        if (doiKhoa && dao.chuyenXe().exists(maSP, xe.getMaXe(), ngayChay)) {
            throw new BusinessException("Sản phẩm này đã được tải lên xe trong ngày đã chọn!");
        }
        ChuyenXe key = new ChuyenXe(goc.getMaSP(), xe.getMaXe(), goc.getNgayChay(),
                goc.getSoLuong(), goc.getGhiChu());
        ChuyenXe value = new ChuyenXe(maSP, xe.getMaXe(), ngayChay,
                Integer.parseInt(soLuong.trim()), ghiChu);
        dao.chuyenXe().update(key, value);
        napChiTiet();
    }

    public void xoaChiTiet(ChiTietChuyenXe row) {
        Xe xe = yeuCauXeHienThoi();
        if (row == null) {
            throw new BusinessException("Chưa chọn dòng dữ liệu cần xóa!");
        }
        dao.chuyenXe().delete(row.getMaSP(), xe.getMaXe(), row.getNgayChay());
        napChiTiet();
    }

    public void kiemTraRangBuocChiTiet(String maSP, LocalDate ngayChay, String soLuong) {
        if (Validator.isBlank(maSP)) {
            throw new BusinessException("Mã sản phẩm không được để trống!");
        }
        if (dao.sanPham().findById(maSP) == null) {
            throw new BusinessException("Mã sản phẩm không tồn tại trong bảng Sản phẩm!");
        }
        if (ngayChay == null) {
            throw new BusinessException("Ngày chạy không hợp lệ (dd/MM/yyyy)!");
        }
        if (!Validator.isPositiveNumber(soLuong)) {
            throw new BusinessException("Số lượng phải là số nguyên dương!");
        }
    }

    private Xe yeuCauXeHienThoi() {
        Xe xe = getXeHienThoi();
        if (xe == null) {
            throw new BusinessException("Chưa có Xe nào được chọn trên Cửa sổ!");
        }
        return xe;
    }

    /* --------------------------------- Báo cáo -------------------------------- */

    public List<ChiTietChuyenXe> locBaoCao(String maXe, LocalDate ngayChay) {
        return dao.chuyenXe().search(maXe, ngayChay);
    }

    public Xe timXe(String maXe) {
        for (Xe xe : danhSachXe) {
            if (xe.getMaXe().equalsIgnoreCase(maXe)) {
                return xe;
            }
        }
        return null;
    }
}
