package vn.edu.qlchuyenxe.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import vn.edu.qlchuyenxe.model.ChiTietChuyenXe;
import vn.edu.qlchuyenxe.util.Formats;

/** Khung hiển thị dữ liệu chi tiết: STT là cột đầu tiên theo yêu cầu mục g. */
public class ChiTietTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "STT", "Mã sản phẩm", "Tên sản phẩm", "Đơn giá", "Ngày chạy", "Số lượng", "Thành tiền"
    };

    private final List<ChiTietChuyenXe> rows = new ArrayList<>();

    public void setRows(List<ChiTietChuyenXe> data) {
        rows.clear();
        rows.addAll(data);
        fireTableDataChanged();
    }

    public ChiTietChuyenXe getRow(int index) {
        return index < 0 || index >= rows.size() ? null : rows.get(index);
    }

    public List<ChiTietChuyenXe> getRows() {
        return new ArrayList<>(rows);
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
                return row.getTenSP();
            case 3:
                return Formats.money(row.getDonGia());
            case 4:
                return Formats.date(row.getNgayChay());
            case 5:
                return row.getSoLuong();
            case 6:
                return Formats.money(row.getThanhTien());
            default:
                return null;
        }
    }
}
