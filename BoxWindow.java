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
import java.util.prefs.*;

public class BoxWindow extends JFrame
{
    private boolean isCaseFile = false;
    private boolean isLogFile = false;

    private File testCaseFile;
    private File logFile;

    private Preferences prefFolder;

    BoxWindow()
    {
        super("GaParser 0.3"); //Заголовок окна
        //setBounds(100, 100, 200, 200);

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
        JTextPane legendStylePane = new JTextPane();
        JTextPane logStylePane = new JTextPane();
        JTextPane reportStylePane = new JTextPane();

        JPanel caseAndLegendPanel = new JPanel();

        JSplitPane caseAndLegendSplitPane = new JSplitPane();
        caseAndLegendPanel.setLayout(new BoxLayout(caseAndLegendPanel, BoxLayout.Y_AXIS));
        //caseAndLegendPanel.setMaximumSize(new Dimension(350, caseAndLegendPanel.getPreferredSize().height));

        JScrollPane caseScrollPanel = new JScrollPane(caseStylePane);
        JScrollPane reportScrollPanel = new JScrollPane(reportStylePane);
        JScrollPane logScrollPanel = new JScrollPane(logStylePane);
        JScrollPane legendScrollPanel = new JScrollPane(legendStylePane);

        caseScrollPanel.setMinimumSize(new Dimension(350, caseScrollPanel.getPreferredSize().height) );
        legendScrollPanel.setMinimumSize(new Dimension(350, legendScrollPanel.getPreferredSize().height));
        reportScrollPanel.setMinimumSize(new Dimension(450, reportScrollPanel.getPreferredSize().height) );
        logScrollPanel.setMinimumSize(new Dimension(450, logScrollPanel.getPreferredSize().height) );

        caseScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        legendScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        reportScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //styledtext docs will be in style panels
        final StyledDocument caseStyleDoc = caseStylePane.getStyledDocument();
        final StyledDocument legendStyleDoc = legendStylePane.getStyledDocument();
        final StyledDocument logStyleDoc = logStylePane.getStyledDocument();
        final StyledDocument reportStyleDoc = reportStylePane.getStyledDocument();

        //user can not edit panels except casePanel
        caseStylePane.setEditable(false);
        legendStylePane.setEditable(false);
        reportStylePane.setEditable(false);
        logStylePane.setEditable(false);


        caseAndLegendPanel.add(caseScrollPanel);
        caseAndLegendPanel.add(legendScrollPanel);

        dataPanel.add(caseAndLegendPanel);
        dataPanel.add(reportScrollPanel);
        dataPanel.add(logScrollPanel);

        //remember last used folder
        prefFolder = Preferences.userNodeForPackage(this.getClass());

        // buttons listeners
        ActionListener loadCaseButtonListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                String lastDir = prefFolder.get("LAST_CASE_FOLDER", "");
                JFileChooser caseFileOpen = new JFileChooser();
                caseFileOpen.setCurrentDirectory(new File(lastDir));

                FileNameExtensionFilter caseFileFilter = new FileNameExtensionFilter("TXT files", "txt");
                caseFileOpen.setFileFilter(caseFileFilter);

                int pushButtonResult = caseFileOpen.showOpenDialog(caseStylePane);

                if (pushButtonResult == JFileChooser.APPROVE_OPTION)
                {
                    caseStylePane.setText("");
                    File caseFile = caseFileOpen.getSelectedFile();
                    prefFolder.put("LAST_CASE_FOLDER", caseFile.getAbsolutePath());
                    outFile(caseFile, caseStyleDoc);
                    caseStylePane.setCaretPosition(0);
                    testCaseFile = caseFile;
                    caseStylePane.setEditable(true);
                    isCaseFile = true;
                }
            }
        };

        //filedrop listener from the Robert Harder library http://www.iharder.net/current/java/filedrop/
        new  FileDrop(caseStylePane, new FileDrop.Listener()
        {
            public void  filesDropped (java.io.File[] files)
                {
                    for( int i = 0; i < files.length; i++ )
                    {
                        caseStylePane.setText("");
                        File caseFile = files[0];
                        prefFolder.put("LAST_CASE_FOLDER", caseFile.getAbsolutePath());
                        testCaseFile = caseFile;
                        outFile(caseFile, caseStyleDoc);
                        caseStylePane.setCaretPosition(0);
                        caseStylePane.setEditable(true);
                        isCaseFile = true;
                    }
                }
            }
        );

        ActionListener loadLogButtonListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                String lastDir = prefFolder.get("LAST_LOG_FOLDER", "");
                JFileChooser logFileOpen = new JFileChooser(lastDir);

                FileNameExtensionFilter logFileFilter = new FileNameExtensionFilter("TXT and LOG files", "txt", "log");
                logFileOpen.setFileFilter(logFileFilter);


                int pushButtonResult = logFileOpen.showOpenDialog(logStylePane);

                if (pushButtonResult == JFileChooser.APPROVE_OPTION)
                {
                    reportStylePane.setText(" Йоу, лог загружен!");
                    logStylePane.setText("");
                    logFile = logFileOpen.getSelectedFile();
                    prefFolder.put("LAST_LOG_FOLDER", logFile.getAbsolutePath());
                    isLogFile = true;
                }
            }
        };

        //filedrop listener from the Robert Harder library http://www.iharder.net/current/java/filedrop/
        new  FileDrop(logStylePane, new FileDrop.Listener()
            {
                public void  filesDropped (java.io.File[] files)
                {
                    for( int i = 0; i < files.length; i++ )
                    {
                        logFile = files[0];
                        prefFolder.put("LAST_LOG_FOLDER", logFile.getAbsolutePath());
                        isLogFile = true;
                        reportStylePane.setText(" Йоу, лог загружен!");
                        logStylePane.setText("");
                    }
                }
            }
        );

        ActionListener checkButtonListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (isCaseFile && isLogFile)
                {
                    reportStylePane.setText("");
                    logStylePane.setText("");
                    legendStylePane.setText("");
                    //выводим легенду
                    outLegend(legendStylePane, legendStyleDoc);

                    try
                    {
                        //get edited text in casefile and save it to chosen file
                        String finalTestCase = caseStyleDoc.getText(0, caseStyleDoc.getLength()-1);

                        //19 - cut off const string in the start "Проверяемый кейс:\n\n"
                        finalTestCase = finalTestCase.substring(19);

                        String caseFileName = testCaseFile.getName();
                        PrintWriter out = new PrintWriter(caseFileName);
                        out.println(finalTestCase);
                        out.close();

                        testCaseFile = new File (caseFileName);
                    }
                    catch (BadLocationException e)
                    {
                        System.out.println("Problem in getting final testcase text. Please load testcase again.");
                    }
                    catch (FileNotFoundException e)
                    {
                        System.out.println("BoxWindow class. Problem in saving new test case file.");
                    }

                    GaParse parseProcess = new GaParse(testCaseFile, logFile, caseStylePane, logStylePane, reportStylePane); //, caseStyleDoc, docLog, docReport);
                    parseProcess.run();
                    reportStylePane.setCaretPosition(0);
                    logStylePane.setCaretPosition(0);
                }
                else if (!isCaseFile && !isLogFile)
                {
                    reportStylePane.setText("Ты не загрузил ни файла с тест-кейсом, ни файла с логом, чего же ты ждешь?");
                }
                else if (isCaseFile && !isLogFile)
                {
                    reportStylePane.setText("Ты не загрузил файла с логом, я слишком ограниченный, чтобы самому " +
                            "придумать фактический результат.");
                }
                else if (!isCaseFile && isLogFile)
                {
                    reportStylePane.setText("Ты не загрузил файла с тест-кейсом, с чем же мне сравнивать?");
                }
            }
        };

        ActionListener cleanButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                testCaseFile = null;
                logFile = null;
                isCaseFile = false;
                isLogFile = false;
                caseStylePane.setText("");
                logStylePane.setText("");
                reportStylePane.setText("");
                legendStylePane.setText("");
                setDefaultText(caseStylePane, logStylePane, reportStylePane, legendStylePane,
                        caseStyleDoc, logStyleDoc, reportStyleDoc, legendStyleDoc);
                caseStylePane.setEditable(false);
            }
        };

        //button panel

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        final JButton loadCaseButton = new JButton("Case load");
        final JButton loadLogButton = new JButton("Log load");
        final JButton doCheckButton = new JButton("  Check  ");
        final JButton cleanButton = new JButton("Clean all");

        final Dimension buttonSize = new Dimension(300,35);

        loadCaseButton.setPreferredSize(buttonSize);
        loadLogButton.setPreferredSize(buttonSize);
        doCheckButton.setPreferredSize(buttonSize);
        cleanButton.setPreferredSize(buttonSize);

        buttonPanel.add(loadCaseButton);
        buttonPanel.add(loadLogButton);
        buttonPanel.add(doCheckButton);
        buttonPanel.add(cleanButton);

        loadCaseButton.addActionListener(loadCaseButtonListener);
        loadLogButton.addActionListener(loadLogButtonListener);
        doCheckButton.addActionListener(checkButtonListener);
        cleanButton.addActionListener(cleanButtonListener);

        panel.add(dataPanel);
        panel.add(buttonPanel);

        // заполняем окна дефолтными записями с инструкцией и легендой
        setDefaultText(caseStylePane, logStylePane, reportStylePane, legendStylePane,
                caseStyleDoc, logStyleDoc, reportStyleDoc, legendStyleDoc);
        // с этой строкой программа почему-то запускается раз через три, как быстрое решение, ставлю ее отрисовку позже
        // надеюсь придумать что-нибудь умное в будущем
        //outLegend(legendStylePane, legendStyleDoc);

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
                    doc.insertString(doc.getLength(), outLine + "\n", null);

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

    private void outLegend(JTextPane legendStylePane, StyledDocument legendStyleDoc)
    {
        try
        {
            legendStyleDoc.insertString(legendStyleDoc.getLength(), "\nЛегенда:\n\n", null);
        }
        catch (BadLocationException e)
        {
            System.out.println("Problem in out legend in loadCaseButtonListener");
        }
        StyledDocOut legendOutStyle = new StyledDocOut(legendStylePane, legendStyleDoc);
        legendOutStyle.printWithStyle("Bug / Ошибка\n", 31); //red
        legendOutStyle.printWithStyle("Expected event / Событие из кейса\n", 32); //green
        legendOutStyle.printWithStyle("Known bug / Известный баг\n", 33); //yellow
        legendOutStyle.printWithStyle("- перед известным багом напишите в тест-кейсе символ w (от waited)\n", 1);
        legendOutStyle.printWithStyle("Missing event / Событие из кейса отсутствует\n", 34); //blue
        legendOutStyle.printWithStyle("Expected missing event / Известное отсутствующее событие\n", 35); //pink
        legendOutStyle.printWithStyle("- перед известным отсутствующим событием напишите в тест-кейсе символ n " +
                "(от no)\n", 1);

    }

    private void setDefaultText(JTextPane caseStylePane, JTextPane logStylePane, JTextPane reportStylePane,
                                JTextPane legendStylePane, StyledDocument caseDoc, StyledDocument logDoc,
                                StyledDocument reportDoc, StyledDocument legendDoc)
    {
        StyledDocOut caseStyle = new StyledDocOut(caseStylePane, caseDoc);
        StyledDocOut logStyle = new StyledDocOut(logStylePane, logDoc);
        StyledDocOut reportStyle = new StyledDocOut(reportStylePane, reportDoc);
        StyledDocOut legendStyle = new StyledDocOut(legendStylePane, legendDoc);

        //Заполняем окна стартовыми инструкциями:
        String caseWindowDefaultString = "Загрузите по кнопке 'Case load' или перетащите мышкой из окна файл в " +
                "формате .txt с тест-кейсом который хотите проверить.\n\n\n";
        caseStyle.printWithStyle(caseWindowDefaultString, 1);

        String logWindowDefaultString = "Загрузите по кнопке 'Log load' или перетащите мышкой из окна файл с логом в " +
                "формате .txt или .log, содержащий последнюю сессию приложения (а именно строку 'Starting shell'). " +
                "После нажатия Check сюда будет выведена последняя сессия приложения, дабы можно было убедиться, " +
                "что алгоритм сравнения сработал корректно.";
        logStyle.printWithStyle(logWindowDefaultString, 1);

        String reportWindowDefaultString = "Здесь будет сформирован отчет сравнения тест-кейса и логфайла по легенде " +
                "приведенной слева снизу в окошке, после нажатия кнопки Check.";
        reportStyle.printWithStyle(reportWindowDefaultString, 1);

        String legendWindowDefaultString = "Место будущей легенды.";
        legendStyle.printWithStyle(legendWindowDefaultString, 1);    }
}
