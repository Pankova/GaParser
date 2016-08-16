import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Мария on 13.06.2016.
 */
public class GaParse
{
	public static void main(String[] data)
	{

		String logName = data[0], testCase = data[1];

		try
		{
			//списали лог
			File logFile = new File(logName + ".log");
			BufferedReader inLog = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));


			//считываем ожидаемые события
			File caseFile = new File(testCase + ".txt");
			BufferedReader inCase = new BufferedReader(new InputStreamReader(new FileInputStream(caseFile)));

			String startEvent = inCase.readLine(); // = "AppLoad, MapLoad", finishEvent = "ContactUs, FromSlidingMenu";


			//what happened
			List<Event> happenedEvents = new ArrayList();

			//what will be output
			List<Event> outputEvents = new ArrayList();

			//выделяем из лог файла нужный нам интервал событий
			String docString;
			while ((docString = inLog.readLine()) != null)
			{
				if (docString.contains("GA "))
					happenedEvents.add(new Event(docString));
			}



			int size = happenedEvents.size() - 1;

			if(size < 0)
			{
				System.out.println("There aren't GA events in the log");
				return;
			}

			/*//проверяем лог на валидность - ищем последнее ожидаемое событие
			if( ! events.get(size).getEventName().contains(finishEvent) )
			{
				System.out.println("Finish event is unexpected");
				return;
			}*/

			int localSize = size;

			//ищем позицию стартового события
			while( !happenedEvents.get(localSize).getEventName().contains(startEvent) )
			{
				localSize--;

				if (localSize < 0)
				{
					System.out.println("Start event is not found");
					return;
				}
			}

			//выделили из всего лога по GA последнюю сессию от запуска до выхода из приложения
			happenedEvents = happenedEvents.subList(localSize, size + 1);


			for(Event ev : happenedEvents)
				ev.print();

			System.out.println();

			//начинаем считываем события,ожидаемые в кейсе
			String currentEvent = startEvent, nextEvent = inCase.readLine();
			int mark = 0;



			while(currentEvent != null)
			{
				//happenedEvents.add(new Event(currentEvent));
				//если нашли ожидаемое событие в логе
				if(findEvent(mark, currentEvent, nextEvent, happenedEvents) >= 0)
				{
					mark = findEvent(mark, currentEvent, nextEvent, happenedEvents);

					//закрасили зеленым найденное ожидаемое событие
					outputEvents.add(new Event(currentEvent,32) );
					//events.get(mark).setColor(32);
					//если баг ожидаемый
					if(currentEvent.startsWith("w") )
					{
						//закрасили желтым известный баг
						outputEvents.get(outputEvents.size()-1).setColor(33);
						//events.get(mark).setColor(33);
					}
					mark++;
				}
				//иначе ожидаемое событие не нашли в логе - заносим его пропущенным синим цветом
				else
				{
					//Event missed = new Event(currentEvent);
					//закрасили синим не регистрируемое событие и добавили его в вывод
					outputEvents.add(new Event(currentEvent, 34 ));
					//missed.setColor(34);
					//если отсутствие события ожидаемо
					if(currentEvent.startsWith("n") )
					{
						//закрасили сиреневым известный баг
						outputEvents.get(outputEvents.size()-1).setColor(35);
						//events.get(mark).setColor(35);
					}

					//events.add(mark, missed);
					//mark++;
				}
				currentEvent = nextEvent;
				nextEvent = inCase.readLine();
			}



			for(int i = 0; i < outputEvents.size(); i++)
			{
				for(int j = 0; j < happenedEvents.size(); j++)
				{
					if(happenedEvents.get(j).getEventName().contains(outputEvents.get(i).getEventName()) && happenedEvents.get(j).getEventColor() == 0)
					{
						happenedEvents.get(j).setColor(outputEvents.get(i).getEventColor());
						break;
					}
				}
				//happenedEvents.add(i, outputEvents.get(i));
			}



			/*Iterator<Event> out = outputEvents.iterator();
			while(out.hasNext())
			{
				Iterator<Event> hap = happenedEvents.iterator();
				Event hapEv = hap.next();
				if(hapEv.getEventName().contains(out.next().getEventName()) && hapEv.getEventColor() == 0)
				{
						hapEv.setColor(out.next().getEventColor());
						out.remove();//outputEvents.remove(out);
						break;
				}
				else
				{
					hap.hasNext();
				}
			}*/



			for(Event elem: happenedEvents)
			{
				//закрасили красным неизвестно откуда взявшиеся события
				if(elem.getEventColor() == 0)
				{
					elem.setColor(31);
				}
				//elem.print();
			}

			for(int i = 0; i < outputEvents.size(); i++)
			{
				for(int j = 0; j < happenedEvents.size(); j++)
				{
					if(happenedEvents.get(j).getEventName().contains(outputEvents.get(i).getEventName()) &&
							happenedEvents.get(j).getEventColor() == 32 && outputEvents.get(i).getEventColor() == 32)
					{
						happenedEvents.get(j).print();
						happenedEvents.get(j).setColor(0);
						outputEvents.get(i).setColor(0);
						break;
					}
					if(happenedEvents.get(j).getEventColor() == 31)
					{
						happenedEvents.get(j).print();
						happenedEvents.get(j).setColor(0);
						i--;
						break;
					}
					if(outputEvents.get(i).getEventColor() == 33 || outputEvents.get(i).getEventColor() == 34 || outputEvents.get(i).getEventColor() == 35)
					{
						outputEvents.get(i).print();
						outputEvents.get(i).setColor(0);
						break;
					}
				}
			}
			/*if(outputEvents.size() < happenedEvents.size())
			{
				for(int i = outputEvents.size(); i < happenedEvents.size(); i++)
					happenedEvents.get(i).print();
			}*/



		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static int findEvent(int startPosition, String event, String finishEvent, List<Event> logPart)
	{
		/*if(startPosition == logPart.size())
		{
			return -1;
		}*/
		int i = startPosition;
		String currentEvent = logPart.get(i).getEventName();

		if(finishEvent == null)
		{
			while(currentEvent != null)
			{
				if(currentEvent.contains(event)&& logPart.get(i).getEventColor() == 0)
				{
					return i;
				}
				i++;
				currentEvent = logPart.get(i).getEventName();
			}
		}
		while(! currentEvent.contains(finishEvent))//(finishEvent == null || ! meetedEvent.contains(finishEvent) || i == startPosition)
		{
			if(currentEvent.contains(event) && logPart.get(i).getEventColor() == 0)
			{
				return i;
			}
			/*if(finishEvent == null)
			{
				return -1;
			}*/
			i++;
			currentEvent = logPart.get(i).getEventName();

			if(currentEvent == null)
			{
				return -1;
			}

		}

		if(currentEvent.contains(event) && logPart.get(i).getEventColor() == 0)
		{
			return i;
		}

		/*for(int i = startPosition; i < logPart.size(); i++)
		{
			Event logElem = logPart.get(i);
			String logElemName = logElem.getEventName();

			if(logElemName.contains(event))
			{
				return i;
			}
		}*/
		return -1;
	}
}
