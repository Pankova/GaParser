/**
 * Created by mary on 18.09.16.
 */

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class BoxWindow extends JFrame
{
    boolean isCaseFile = false;
    boolean isLogFile = false;

    File testCaseFile;
    File getLogFile;




    public BoxWindow()
    {


        super("GaParser"); //Заголовок окна
        setBounds(100, 100, 200, 200); //Если не выставить
        //размер и положение
        //то окно будет мелкое и незаметное
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //это нужно для того чтобы при
        //закрытии окна закрывалась и программа,
        //иначе она останется висеть в процессах

        JPanel buttonPanel = new JPanel();
        JPanel panel = new JPanel();
        JPanel dataPanel = new JPanel();

        //JTextArea caseArea = new JTextArea(20,10);
        //JTextArea reportArea = new JTextArea(20,10);
        //JTextArea logpartArea = new JTextArea(20,10);

        //честно пыталась понять зачем он, но так и не въехала - пишу, с муками совести, просто потому, что нужен
        StyleContext stCxt =  StyleContext.getDefaultStyleContext();




        JTextPane caseStyleArea = new JTextPane();
        JTextPane logStyleArea = new JTextPane();
        JTextPane reportStyleArea = new JTextPane();



        final StyledDocument docCase = caseStyleArea.getStyledDocument();
        /*final StyledDocument docLog = logStyleArea.getStyledDocument();
        final StyledDocument docReport = reportStyleArea.getStyledDocument();*/


        /*//стиль найденных багов (красный цвет)
        final Style bugStyle = stCxt.addStyle("Bug", null);

        //стиль ожидаемых событий (зеленый цвет)
        final Style caseStyle = stCxt.addStyle("Case", null);

        //стиль событий, которые есть в кейсе есть, но в логе не встретились (синий цвет)
        final Style notFoundStyle = stCxt.addStyle("NotFound", null);

        //стиль известных багов (желтый цвет)
        final Style waitBugStyle = stCxt.addStyle("WaitedBug", null);

        //стиль известных отсуствующих событий (сиреневый цвет)
        final Style waitNotFoundStyle = stCxt.addStyle("WaitNotFound", null);*/

        final JButton loadCaseButton = new JButton("Case load");
        final JButton loadLogButton = new JButton("Log load");
        final JButton doCheckButton = new JButton("  Check  ");

        JScrollPane casePanel = new JScrollPane(caseStyleArea);
        JScrollPane reportPanel = new JScrollPane(reportStyleArea);
        JScrollPane logpartPanel = new JScrollPane(logStyleArea);

        Dimension buttonSize = new Dimension(300,35);

        //задаем стили шрифтов

        //stCxt.addAttribute(Color);

        /*bugStyle.addAttribute(StyleConstants.Foreground, Color.red);
        caseStyle.addAttribute(StyleConstants.Foreground, Color.green);
        notFoundStyle.addAttribute(StyleConstants.Foreground, Color.blue);
        waitBugStyle.addAttribute(StyleConstants.Foreground, Color.yellow);
        waitNotFoundStyle.addAttribute(StyleConstants.Foreground, Color.magenta);*/

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.X_AXIS));
        dataPanel.setPreferredSize(new Dimension(1200,800));

        //caseStyleArea.setLineWrap(true);
        //reportStyleArea.setLineWrap(true);
        //logStyleArea.setLineWrap(true);

        caseStyleArea.setEditable(false);
        reportStyleArea.setEditable(false);
        logStyleArea.setEditable(false);

        casePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        reportPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logpartPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        dataPanel.add(casePanel, BorderLayout.WEST);
        dataPanel.add(reportPanel, BorderLayout.CENTER);
        dataPanel.add(logpartPanel, BorderLayout.EAST);


        loadCaseButton.setPreferredSize(buttonSize);
        loadLogButton.setPreferredSize(buttonSize);
        doCheckButton.setPreferredSize(buttonSize);

        ActionListener loadCaseButtonListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                JFileChooser caseFileOpen = new JFileChooser();

                FileNameExtensionFilter caseFileFilter = new FileNameExtensionFilter("TXT files", "txt");
                caseFileOpen.setFileFilter(caseFileFilter);

                int pushButtonResult = caseFileOpen.showOpenDialog(caseStyleArea);

                if (pushButtonResult == JFileChooser.APPROVE_OPTION)
                {

                    caseStyleArea.setText("");
                    File caseFile = caseFileOpen.getSelectedFile();
                    outFile(caseFile, docCase);
                    testCaseFile = caseFile;
                    isCaseFile = true;
                }
            }
        };

        ActionListener loadLogButtonListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                JFileChooser logFileOpen = new JFileChooser();

                FileNameExtensionFilter logFileFilter = new FileNameExtensionFilter("TXT and LOG files", "txt", "log");
                logFileOpen.setFileFilter(logFileFilter);

                int pushButtonResult = logFileOpen.showOpenDialog(logStyleArea);

                if (pushButtonResult == JFileChooser.APPROVE_OPTION)
                {
                    reportStyleArea.setText("");
                    logStyleArea.setText("");
                    File logFile = logFileOpen.getSelectedFile();
                    //outFile(logFile, logpartArea);
                    getLogFile = logFile;
                    isLogFile = true;
                }
            }
        };

        ActionListener checkButtonListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (isCaseFile && isLogFile)
                {
                    reportStyleArea.setText("");
                    GaParse parseProcess = new GaParse(testCaseFile, getLogFile, caseStyleArea, logStyleArea, reportStyleArea); //, docCase, docLog, docReport);
                    parseProcess.run();
                }
            }
        };

        loadCaseButton.addActionListener(loadCaseButtonListener);
        loadLogButton.addActionListener(loadLogButtonListener);
        doCheckButton.addActionListener(checkButtonListener);

        buttonPanel.add(loadCaseButton);
        buttonPanel.add(loadLogButton);
        buttonPanel.add(doCheckButton);

        panel.add(dataPanel);
        panel.add(buttonPanel);

        getContentPane().add(panel);
        //setPreferredSize(new Dimension(350, 200));
        pack();
        setLocationRelativeTo(null);


    }

    public static void main(String[] args)
    { //эта функция может быть и в другом классе
        BoxWindow app = new BoxWindow(); //Создаем экземпляр нашего приложения
        app.setVisible(true); //С этого момента приложение запущено!

    }

    public void outFile (File file, StyledDocument doc)
    {
        try
        {
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String outLine = input.readLine();
            doc.insertString(doc.getLength(), "Проверяемый кейс:\n\n", null);
            while (outLine != null)
            {
                doc.insertString(doc.getLength(), " " + outLine + "\n", null);
                outLine = input.readLine();
            }
            //doc.setCaretPosition(0);
        }
        catch (FileNotFoundException exc)
        {
            System.out.println("Пока Вы выбирали файл и решались нажать кнопку, файл куда-то делся.");
        }
        catch (IOException exc)
        {
            System.out.println("Есть что-то в вашем выбранном файле с тест-кейсом невыводимое.");
        }
        catch (NullPointerException e)
        {

        }
        catch (BadLocationException e)
        {

        }
    }
}
