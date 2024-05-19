import javax.swing.JTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class PlaceholderTextField extends JTextField {
    private String placeholderContent;

    public PlaceholderTextField(String placeholderContent) {
        super(placeholderContent);
        this.placeholderContent = placeholderContent;
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholderContent)) {
                    setText("");
                }
            }
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText(placeholderContent);
                }
            }
        });
    }

    public String getPlaceholderContent() {
        return placeholderContent;
    }
}
