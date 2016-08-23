import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Мария on 20.08.2016.
 */
public class LogString
{
	String logString;

	LogString(String str){ logString = str; }

	public String getString(){ return logString; }

	private long getData()
	{
		if(! logString.contains("GA "))
		{
			System.out.println("Sorry, it's incorrect string without GA events");
			return -1;
		}
		//I 2016.06.10 12:03:06 UTC  0 DGSGlobalDelegate.m:489$ Application did become active
		String [] dataArray = logString.split(" ");
		String dataString = dataArray[2];
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", new Locale("en"));
		try
		{
			Date data = dateFormat.parse(dataString);

			//we get time 01.01.1970
			long dataLong = data.getTime();
			return dataLong;
		}
		catch (ParseException e)
		{
			System.out.println("Sorry, i can't parse data from string");
		}
		return -1;
	}

	public long getDataDifference(LogString logString)
	{
			return Math.abs(logString.getData() - getData());

	}



}
