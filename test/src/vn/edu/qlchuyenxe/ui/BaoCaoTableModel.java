package vn.edu.qlchuyenxe.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import vn.edu.qlchuyenxe.model.ChiTietChuyenXe;
import vn.edu.qlchuyenxe.util.Formats;

/** Bảng của Cửa sổ Báo cáo: STT, Mã sản phẩm, Ngày chạy, Số lượng, Ghi chú. */
public class BaoCaoTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"STT", "Mã sản phẩm", "Ngày chạy", "Số lượng", "Ghi chú"};

    private final List<ChiTietChuyenXe> rows = new ArrayList<>();

    public BaoCaoTableModel(List<ChiTietChuyenXe> data) {
        rows.addAll(data);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ChiTietChuyenXe row = rows.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
            case 1:
                return row.getMaSP();
            case 2:
                return Formats.date(row.getNgayChay());
            case 3:
                return row.getSoLuong();
            case 4:
                return row.getGhiChu() == null ? "" : row.getGhiChu();
            default:
                return null;
        }
    }
}
