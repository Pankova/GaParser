import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;

/**
 * Created by mary on 23.10.16.
 */
public class StyledDocOut
{
    JTextPane outPane;
    DefaultStyledDocument doc;

    StyledDocOut (JTextPane pane)
    {
        outPane = pane;
        doc = pane.getStyledDocument();
        Style style;
    }
}
