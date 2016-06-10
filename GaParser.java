import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Мария on 05.06.2016.
 */
public class GaParser
{
	public static void main(String [] data)
	{
		/*String a = "A";
		System.out.println((char) 27 + "[31m" + a);//
		//System.out.println((char) 27 + "[38mWarning! ");
		System.out.println((char) 27 + "[33mWarning! ");
		System.out.println((char) 27 + "[32mWarning! ");//
		System.out.println((char) 27 + "[34mWarning! ");//
		System.out.println((char) 27 + "[35mWarning! ");*/
		//30 - черный. 31 - красный. 32 - зеленый. 33 - желтый. 34 - синий. 35 - пурпурный. 36 - голубой. 37 - белый.

		String filename = data[0];
		File file = new File(filename + ".log");

		try
		{
			BufferedReader inReal = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			String docString;
			String startEvent = "AppLoad, MapLoad", finishEvent = "ContactUs, FromSlidingMenu";

			List<String> events = new ArrayList();

			while ((docString = inReal.readLine()) != null)
			{
				if(docString.contains("GA "))
					events.add(docString);
			}

			int size = events.size() - 1;

			if( ! events.get(size).contains(finishEvent) )
			{
				System.out.println("Finish event is unexpected");
				return;
			}

			int localSize = size;
			while( !events.get(localSize).contains(startEvent) )
			{
				localSize--;

				if (localSize < 0)
				{
					System.out.println("Start event is not found");
					return;
				}
			}

			List<String> localEvents = events.subList(localSize + 1, size);

			for(String ev : localEvents)
			{
				System.out.println(ev);
			}

			File waitedEvents = new File("wait.txt");

			BufferedReader inWait = new BufferedReader(new InputStreamReader(new FileInputStream(waitedEvents)));

			String currentEvent, prevEvent;

			currentEvent = inWait.readLine();
			prevEvent = currentEvent;
			while ( (currentEvent = inWait.readLine()) != null)
			{
				ParsePair(prevEvent, currentEvent, localEvents);
				prevEvent = currentEvent;
			}




		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void ParsePair(String ev1, String ev2, List<String> evs)
	{
		if( ! evs.get(0).contains(ev1))
		{
			System.out.println("Unexpected inside err");
			return;
		}

		System.out.println((char) 27 + "[32m" + ev1);
		evs.remove(0);

		while( ! evs.get(0).contains(ev2) )
		{
			if( ! evs.get(0).contains(ev2) )
				System.out.println((char) 27 + "[31m" + ev1);
			evs.remove(0);
		}
	}
}
