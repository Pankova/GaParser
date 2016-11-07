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


    BoxWindow()
    {
        super("GaParser"); //Заголовок окна
        setBounds(100, 100, 200, 200);

        //чтобы при закрытии окна закрывалась и программа (не висела в процессах)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(1200,800));


        //panel with testcase, part of log with last session and test report
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.X_AXIS));


        //style panels, where styled text will be placed
        JTextPane caseStylePane = new JTextPane();
        JTextPane logStylePane = new JTextPane();
        JTextPane reportStylePane = new JTextPane();

        JScrollPane caseScrollPanel = new JScrollPane(caseStylePane);
        JScrollPane reportScrollPanel = new JScrollPane(reportStylePane);
        JScrollPane logScrollPanel = new JScrollPane(logStylePane);

        caseScrollPanel.setMinimumSize(new Dimension(350, caseScrollPanel.getPreferredSize().height) );
        reportScrollPanel.setMinimumSize(new Dimension(450, reportScrollPanel.getPreferredSize().height) );
        logScrollPanel.setMinimumSize(new Dimension(450, logScrollPanel.getPreferredSize().height) );

        caseScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        reportScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //styledtext docs will be in style panels
        final StyledDocument caseStyleDoc = caseStylePane.getStyledDocument();
        /*final StyledDocument docLog = logStylePane.getStyledDocument();
        final StyledDocument docReport = reportStylePane.getStyledDocument();*/

        //user can not edit panels
        caseStylePane.setEditable(false);
        reportStylePane.setEditable(false);
        logStylePane.setEditable(false);


        dataPanel.add(caseScrollPanel);
        dataPanel.add(reportScrollPanel);
        dataPanel.add(logScrollPanel);


        // buttons listeners
        ActionListener loadCaseButtonListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                JFileChooser caseFileOpen = new JFileChooser();

                FileNameExtensionFilter caseFileFilter = new FileNameExtensionFilter("TXT files", "txt");
                caseFileOpen.setFileFilter(caseFileFilter);

                int pushButtonResult = caseFileOpen.showOpenDialog(caseStylePane);

                if (pushButtonResult == JFileChooser.APPROVE_OPTION)
                {
                    caseStylePane.setText("");
                    File caseFile = caseFileOpen.getSelectedFile();
                    outFile(caseFile, caseStyleDoc);
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

                int pushButtonResult = logFileOpen.showOpenDialog(logStylePane);

                if (pushButtonResult == JFileChooser.APPROVE_OPTION)
                {
                    reportStylePane.setText("");
                    logStylePane.setText("");
                    getLogFile = logFileOpen.getSelectedFile();
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
                    reportStylePane.setText("");
                    logStylePane.setText("");
                    GaParse parseProcess = new GaParse(testCaseFile, getLogFile, caseStylePane, logStylePane, reportStylePane); //, caseStyleDoc, docLog, docReport);
                    parseProcess.run();
                    reportStylePane.setCaretPosition(0);
                    logStylePane.setCaretPosition(0);
                }
            }
        };


        //button panel

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        final JButton loadCaseButton = new JButton("Case load");
        final JButton loadLogButton = new JButton("Log load");
        final JButton doCheckButton = new JButton("  Check  ");

        final Dimension buttonSize = new Dimension(300,35);

        loadCaseButton.setPreferredSize(buttonSize);
        loadLogButton.setPreferredSize(buttonSize);
        doCheckButton.setPreferredSize(buttonSize);

        buttonPanel.add(loadCaseButton);
        buttonPanel.add(loadLogButton);
        buttonPanel.add(doCheckButton);

        loadCaseButton.addActionListener(loadCaseButtonListener);
        loadLogButton.addActionListener(loadLogButtonListener);
        doCheckButton.addActionListener(checkButtonListener);


        panel.add(dataPanel);
        panel.add(buttonPanel);


        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args)
    {
        BoxWindow app = new BoxWindow();
        app.setVisible(true); //c этого момента приложение запущено
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
                if (!outLine.equals(""))
                    doc.insertString(doc.getLength(), " " + outLine + "\n", null);

                outLine = input.readLine();
            }
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
