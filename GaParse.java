import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Мария on 13.06.2016.
 */
public class GaParse
{
	public static void main(String[] data)
	{

		String logName = data[0], test = data[1];

		try
		{
			File file = new File(logName + ".log");
			//списали лог
			BufferedReader inReal = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			String docString;
			String startEvent = "AppLoad, MapLoad", finishEvent = "ContactUs, FromSlidingMenu";

			List<Event> events = new ArrayList();

			//выделяем из лог файла нужный нам интервал событий
			while ((docString = inReal.readLine()) != null)
			{
				if (docString.contains("GA "))
					events.add(new Event(docString));
			}

			//считываем ожидаемые события
			File waitedEvents = new File(test + ".txt");
			BufferedReader inWait = new BufferedReader(new InputStreamReader(new FileInputStream(waitedEvents)));

			int size = events.size() - 1;

			//проверяем лог на валидность - ищем последнее ожидаемое событие
			if( ! events.get(size).getEventName().contains(finishEvent) )
			{
				System.out.println("Finish event is unexpected");
				return;
			}

			int localSize = size;

			//ищем позицию стартового события
			while( !events.get(localSize).getEventName().contains(startEvent) )
			{
				localSize--;

				if (localSize < 0)
				{
					System.out.println("Start event is not found");
					return;
				}
			}

			//выделили из всего лога по GA последнюю сессию
			events = events.subList(localSize, size + 1);


			for(Event ev : events)
				ev.print();

			String currentEvent = inWait.readLine();
			int mark = 0;

			while(currentEvent != null)
			{
				//если нашли ожидаемое событие в логе
				if(findEvent(mark, currentEvent, events) >= 0)
				{
					mark = findEvent(mark, currentEvent, events);
					//закрасили зеленым найденное ожидаемое событие
					events.get(mark).setColor(32);
					//если баг ожидаемый
					if(currentEvent.startsWith("w") )
					{
						//закрасили желтым известный баг
						events.get(mark).setColor(33);
					}
					mark++;
				}
				//иначе ожидаемое событие не нашли в логе - заносим его пропущенным синим цветом
				else
				{
					Event missed = new Event(currentEvent);
					//закрасили синим не регистрируемое событие и добавили его в вывод
					missed.setColor(34);

					//если отсутствие события ожидаемо
					if(currentEvent.startsWith("n") )
					{
						//закрасили желтым известный баг
						events.get(mark).setColor(35);
					}

					events.add(missed);
				}
				currentEvent = inWait.readLine();
			}

			for(Event elem: events)
			{
				//закрасили красным неизвестно откуда взявшиеся события
				if(elem.getEventColor() == 0)
				{
					elem.setColor(31);
				}
				elem.print();
			}



		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static int findEvent(int mark, String event, List<Event> logPart)
	{
		for(int i = mark; i < logPart.size(); i++)
		{
			Event logElem = logPart.get(i);
			String logElemName = logElem.getEventName();

			if(logElemName.contains(event))
			{
				return i;
			}
		}
		return -1;
	}
}
