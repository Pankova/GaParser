import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

/**
 * Created by Мария on 20.08.2016.
 */
public class LogString
{
	String logString;
	DefaultStyledDocument outArea;

	LogString(String str, DefaultStyledDocument area)
	{
		logString = str;
		outArea = area;
	}

	public String getString(){ return logString; }

	private long getLongData()
	{
		if(! logString.contains("GA "))
		{
			out("Sorry, it's incorrect string without GA events");
			return -1;

		}

		Date data = parseDataFromString();

		//we get time 01.01.1970
		long dataLong = data.getTime();

		return dataLong;
	}

	public String getStringData()
    {
        String [] dataArray = logString.split(" ");
        String dataString = dataArray[2];

		dataArray = dataString.split(":");
		if (dataArray.length != 3)
		{
			out("The data is incorrect in String");
			return "";
		}

        return dataString;
    }

    public void out (String data)
	{
		try
		{
			outArea.insertString(outArea.getLength(), data, null);
		}
		catch (BadLocationException e)
		{

		}
	}

    public String incStringData()
	{
		Date date = parseDataFromString();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);

		if (seconds == 59)
		{
			seconds = 00;
			if (minutes == 59)
			{
				minutes = 00;
				hours++;
			}
			else
			{
				minutes++;
			}
		}
		else
		{
			seconds++;
		}

		return hours + ":" + minutes + ":" + seconds;
	}

	public long getDataDifference(LogString logString)
	{
			return Math.abs(logString.getLongData() - getLongData()) / 1000;

	}

	public Date parseDataFromString()
	{
		//I 2016.06.10 12:03:06 UTC  0 DGSGlobalDelegate.m:489$ Application did become active
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", new Locale("en"));
		try
		{
			Date data = dateFormat.parse(getStringData());
			return data;
		}
		catch (ParseException e)
		{
			out("Sorry, i can't parse data from string");
			return null;
		}
	}

}
