package vn.edu.qlchuyenxe;

import javax.swing.SwingUtilities;

import vn.edu.qlchuyenxe.db.ConnectionManager;
import vn.edu.qlchuyenxe.service.QuanLyChuyenXeService;
import vn.edu.qlchuyenxe.ui.MainFrame;
import vn.edu.qlchuyenxe.ui.UiTheme;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        UiTheme.install();
        SwingUtilities.invokeLater(() -> {
            QuanLyChuyenXeService service = QuanLyChuyenXeService.create(new ConnectionManager());
            new MainFrame(service).setVisible(true);
        });
    }
}
