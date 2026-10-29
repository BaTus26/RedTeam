package vn.edu.qlchuyenxe.ui.component;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

import vn.edu.qlchuyenxe.ui.UiTheme;

/**
 * Biểu tượng điều hướng vẽ bằng Java2D thay cho ký tự Unicode, để không phụ
 * thuộc vào việc phông chữ hệ thống có glyph mũi tên hay không.
 */
public final class NavIcon implements Icon {

    public enum Kind { FIRST, PREVIOUS, NEXT, LAST }

    private static final int SIZE = 14;

    private final Kind kind;

    public NavIcon(Kind kind) {
        this.kind = kind;
    }

    @Override
    public int getIconWidth() {
        return SIZE;
    }

    @Override
    public int getIconHeight() {
        return SIZE;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c.isEnabled() ? UiTheme.LABEL : UiTheme.GRID_LINE);

        boolean pointsLeft = kind == Kind.FIRST || kind == Kind.PREVIOUS;
        boolean hasBar = kind == Kind.FIRST || kind == Kind.LAST;

        int barWidth = hasBar ? 3 : 0;
        int left = x + (pointsLeft ? barWidth + 1 : 0);
        int right = x + SIZE - (pointsLeft ? 0 : barWidth + 1);
        int top = y + 1;
        int bottom = y + SIZE - 1;
        int middle = y + SIZE / 2;

        int[] xs = pointsLeft
                ? new int[] {right, right, left}
                : new int[] {left, left, right};
        int[] ys = {top, bottom, middle};
        g2.fillPolygon(xs, ys, 3);

        if (hasBar) {
            int barX = pointsLeft ? x : x + SIZE - barWidth;
            g2.fillRect(barX, top, barWidth - 1, bottom - top);
        }
        g2.dispose();
    }
}
