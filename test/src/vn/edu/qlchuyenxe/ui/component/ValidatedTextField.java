package vn.edu.qlchuyenxe.ui.component;

import java.awt.Toolkit;
import java.util.function.Predicate;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import vn.edu.qlchuyenxe.ui.UiTheme;

/**
 * Ô văn bản tự kiểm tra: chặn ký tự sai ngay khi gõ từ bàn phím và không cho
 * rời control khi dữ liệu còn trống hoặc không hợp lệ.
 */
public class ValidatedTextField extends JTextField {

    private final Predicate<String> rule;
    private final String errorMessage;
    private boolean verifyOnFocusLost = true;
    private boolean reportingError;

    public ValidatedTextField(int columns, Predicate<String> rule, String errorMessage) {
        super(columns);
        this.rule = rule;
        this.errorMessage = errorMessage;
        setFont(UiTheme.FIELD_FONT);
        setInputVerifier(new RuleVerifier());
    }

    public static ValidatedTextField digits(int columns, int maxLength, Predicate<String> rule, String message) {
        ValidatedTextField field = new ValidatedTextField(columns, rule, message);
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new CharacterFilter("[0-9]*", maxLength));
        return field;
    }

    public static ValidatedTextField pattern(int columns, String regex, int maxLength,
                                             Predicate<String> rule, String message) {
        ValidatedTextField field = new ValidatedTextField(columns, rule, message);
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new CharacterFilter(regex, maxLength));
        return field;
    }

    public void setVerifyOnFocusLost(boolean verifyOnFocusLost) {
        this.verifyOnFocusLost = verifyOnFocusLost;
    }

    public boolean isValidValue() {
        return rule.test(getText());
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getTrimmedText() {
        return getText() == null ? "" : getText().trim();
    }

    private final class RuleVerifier extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {
            return !verifyOnFocusLost || isValidValue();
        }

        @Override
        public boolean shouldYieldFocus(JComponent source, JComponent target) {
            if (verify(source) || reportingError) {
                return true;
            }
            reportingError = true;
            try {
                Toolkit.getDefaultToolkit().beep();
                Dialogs.error(ValidatedTextField.this, errorMessage);
            } finally {
                reportingError = false;
            }
            return false;
        }
    }

    /** Lọc ký tự nhập từ bàn phím theo biểu thức và giới hạn độ dài. */
    private static final class CharacterFilter extends DocumentFilter {

        private final String regex;
        private final int maxLength;

        private CharacterFilter(String regex, int maxLength) {
            this.regex = regex;
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                throws BadLocationException {
            replace(fb, offset, 0, text, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String candidate = current.substring(0, offset) + (text == null ? "" : text)
                    + current.substring(offset + length);
            if (candidate.length() > maxLength || !candidate.matches(regex)) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            super.replace(fb, offset, length, text, attr);
        }
    }
}
