import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Мария on 13.06.2016.
 */
public class Event
{
	private String eventName;
	private int eventColor = 0;
	private String data = "::";
	private int listNumber = 0;
	StyledDocOut outArea;


	Event(String name, JTextPane area, StyledDocument doc)
	{
		eventName = name;
		outArea = new StyledDocOut(area, doc);
		data = makeData(name);
	}

	Event(String name, JTextPane area, StyledDocument doc, int color)
	{
		eventName = name;
		outArea =  new StyledDocOut(area, doc);
		data = makeData(name);
		setColor(color);
	}

	Event(String name, JTextPane pane, StyledDocument doc, int color, String fakeData)
	{
		eventName = name;
		outArea = new StyledDocOut(pane, doc);
		data = fakeData;
		setColor(color);
	}

	Event(String name, JTextPane  pane, StyledDocument doc, int color, String fakeData, int number)
	{
		eventName = name;
		outArea = new StyledDocOut(pane, doc);
		data = fakeData;
		setColor(color);
		listNumber = number;
	}

	Event(String name, JTextPane pane, StyledDocument doc, int color, int number)
	{
		eventName = name;
		outArea = new StyledDocOut(pane, doc);
		data = makeData(name);
		setColor(color);
		listNumber = number;
	}

	private String makeData(String name)
	{
		try
		{
			LogString logStr = new LogString(name, outArea.getDoc());
			return logStr.getStringData();
		}
		catch(ArrayIndexOutOfBoundsException | NullPointerException e)
		{
			return "No time";
		}

	}

	public int getListNumber(){ return listNumber; }

	public final void setColor(int color){ eventColor = color; }

	public int getEventColor(){ return eventColor; }

	public String getEventName(){ return eventName; }

	public String getStringData(){ return data; }

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

	public void out (String data)
	{
		try
		{
			outArea.getDoc().insertString(outArea.getDoc().getLength(), data, null);
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

	public boolean dataEquals(Event event)
	{
		Date thisData = parseDataFromString();
		Date evData = event.parseDataFromString();

		if (thisData.equals(evData))
		{
			return true;
		}
		return false;
		/*Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);*/

	}




	public void print()
	{

		String name = eventName;
		int a = name.indexOf("I d");
		int b = name.indexOf("GA ");
		try
		{
			String excess = name.substring(a, b);
			name = name.replace(excess, "");
		}
		catch (StringIndexOutOfBoundsException e)
		{
			//не было в строке указанных символов
		}

		outArea.printWithStyle(data + " " + name + "\n", eventColor);
	}
}
