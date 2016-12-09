import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * Created by mary on 23.10.16.
 */

public class StyledDocOut
{
    private StyledDocument doc;
    private Style style;

    StyledDocOut (JTextPane pane, StyledDocument paneDoc)
    {
        JTextPane outPane = pane;
        doc = paneDoc;
        style = pane.addStyle("DefStyle", null);

        Font font = new Font("Courier", Font.PLAIN, 14);
        outPane.setFont(font);
    }

    public StyledDocument getDoc () { return doc; }

    public void printWithStyle(String str, int colorStyle)
    {
        StyleConstants.setLineSpacing(style, -5.0f);

        switch (colorStyle)
        {
            case 31: //red
            {
                Color red = new Color (166, 0, 8);
                StyleConstants.setForeground(style, red);
                out(str);
                break;
            }
            case 32: //green
            {
                Color green = new Color(0, 166, 8);
                StyleConstants.setForeground(style, green);
                out(str);
                break;
            }
            case 33: //yellow
            {
                Color yellow = new Color(196, 150, 0);
                StyleConstants.setForeground(style, yellow);
                out(str);
                break;
            }
            case 34: //blue
            {
                Color blue = new Color(42, 45, 184);
                StyleConstants.setForeground(style, blue);
                out(str);
                break;
            }
            case 35: //purple
            {
                Color purple = new Color(184, 31, 110);
                StyleConstants.setForeground(style, purple);
                out(str);
                break;
            }
            default:
            {
                Color black = new Color(133,133,133);
                StyleConstants.setForeground(style, black);
                out(str);
                break;
            }
        }
    }

    private void out(String data)
    {
        try
        {
            doc.insertString(doc.getLength(), data, style);
        }
        catch (BadLocationException e)
        {
            System.out.println("Problem in out function of StyleDocOut class");
        }
    }
 }

