package vn.edu.qlchuyenxe.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import vn.edu.qlchuyenxe.model.ChiTietChuyenXe;
import vn.edu.qlchuyenxe.model.Xe;

/** Ghi nội dung Khung hiển thị dữ liệu chi tiết ra file txt. */
public final class TxtExporter {

    private static final String LINE =
            "+-----+------------+------------------------------+----------------+------------+----------+------------------+";
    private static final String ROW_FORMAT = "| %-3s | %-10s | %-28s | %14s | %-10s | %8s | %16s |%n";

    private TxtExporter() {
    }

    public static void export(Path target, Xe xe, List<ChiTietChuyenXe> rows) throws IOException {
        Path parent = target.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (Writer writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
            write(writer, xe, rows);
        }
    }

    static void write(Writer writer, Xe xe, List<ChiTietChuyenXe> rows) {
        PrintWriter out = new PrintWriter(writer);
        out.println("BẾN XE MIỀN TÂY");
        out.println("DANH SÁCH SẢN PHẨM THEO CHUYẾN XE");
        out.printf("Ngày xuất: %s%n",
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
        out.println();
        if (xe != null) {
            out.printf("Mã xe    : %s%n", xe.getMaXe());
            out.printf("Tên xe   : %s%n", xe.getTenXe());
            out.printf("Giá tuyến: %s%n", Formats.money(xe.getGiaTuyen()));
            out.println();
        }
        out.println(LINE);
        out.printf(ROW_FORMAT, "STT", "Mã SP", "Tên sản phẩm", "Đơn giá", "Ngày chạy", "Số lượng", "Thành tiền");
        out.println(LINE);

        long tongThanhTien = 0;
        int stt = 0;
        for (ChiTietChuyenXe row : rows) {
            stt++;
            tongThanhTien += row.getThanhTien();
            out.printf(ROW_FORMAT,
                    stt,
                    row.getMaSP(),
                    row.getTenSP(),
                    Formats.money(row.getDonGia()),
                    Formats.date(row.getNgayChay()),
                    row.getSoLuong(),
                    Formats.money(row.getThanhTien()));
        }
        out.println(LINE);
        out.println();
        out.printf("Số lượng sản phẩm: %d%n", rows.size());
        out.printf("Tổng thành tiền  : %s%n", Formats.money(tongThanhTien));
        out.flush();
    }
}
