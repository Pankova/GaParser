import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import java.io.*;
import java.util.*;

/**
 * Created by Мария on 13.06.2016.
 */
public class GaParse
{

	File caseFile;
	File logFile;

	JTextPane casePane;
	JTextPane logPane;
	JTextPane reportPane;

	DefaultStyledDocument caseOut;
	DefaultStyledDocument logOut;
	DefaultStyledDocument reportOut;

	GaParse(File file1, File file2, JTextPane pane1, JTextPane pane2, JTextPane pane3, DefaultStyledDocument doc1, DefaultStyledDocument doc2, DefaultStyledDocument doc3)
	{
		caseFile = file1;
		logFile = file2;
		casePane = pane1;
		logPane = pane2;
		reportPane = pane3;
		caseOut = doc1;
		logOut = doc2;
		reportOut = doc3;
	}

	public void run()
	{
		try
		{
			//doc.insertString(doc.getLength(), " " + outLine + "\n", null);

			out("Start\n", reportOut);

			//списали лог
			BufferedReader inLog = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));


			//считываем ожидаемые события

			BufferedReader inCase = new BufferedReader(new InputStreamReader(new FileInputStream(caseFile)));

			//начало новой сессии приложения в логе
			String startSessinString = "* Known files:";


			//сюда будем считывать GA события из лога
			List<Event> happenedEvents = new ArrayList();

			//сюда будем дописывать события, которых в логе не хватает (как бы странно это ни выглядело)
			List<Event> outputEvents = new ArrayList();

			//выделяем из лог файла нужный нам интервал событий
			String docString;
			while ((docString = inLog.readLine()) != null)
			{
				if (docString.contains("GA "))
					happenedEvents.add(new Event(docString, reportPane));
				//зафиксировали старт сессии
				if (docString.equals(startSessinString))
					happenedEvents.add(new Event("Start", reportPane));
			}


			int size = happenedEvents.size() - 1;

			if(size < 0)
			{
				out("There aren't GA events in the log", reportOut);
				return;
			}


			int localSize = size;

			//идем снизу вверх по логу, пока не нашли стартовое событие сессии
			while(localSize > 0 && !happenedEvents.get(localSize-1).getEventName().equals("Start"))
			{
				localSize--;
			}


			//выделили из всего лога по GA последнюю сессию от запуска до выхода из приложения
			happenedEvents = happenedEvents.subList(localSize, size + 1);


			for(Event ev : happenedEvents)
				out(ev.getEventName() + "\n", logOut);//);ev.print();

			out("\n", reportOut);

			//начинаем считываем события,ожидаемые в кейсе
			String currentEvent = inCase.readLine(), nextEvent = inCase.readLine();
			int mark = 0;



			while(currentEvent != null)
			{
				//если нашли ожидаемое событие в логе
				if(findEvent(mark, currentEvent, nextEvent, happenedEvents) >= 0)
				{
					mark = findEvent(mark, currentEvent, nextEvent, happenedEvents);
					happenedEvents.get(mark).setColor(32);

					//закрасили зеленым найденное ожидаемое событие
					outputEvents.add(new Event(happenedEvents.get(mark).getEventName(),32, 1, reportPane) );

					//если баг ожидаемый
					if(currentEvent.startsWith("w") )
					{
						//закрасили желтым известный баг
						outputEvents.get(outputEvents.size()-1).setColor(33);
					}
					mark++;
				}
				//иначе ожидаемое событие не нашли в логе - заносим его пропущенным синим цветом
				else
				{
					//закрасили синим не регистрируемое событие и добавили его в вывод
					Event prevEvent = outputEvents.get(outputEvents.size()-1);
					String fakeData = prevEvent.incStringData();
					outputEvents.add(new Event(currentEvent, 34, fakeData, 1, reportPane));

					//если отсутствие события ожидаемо
					if(currentEvent.startsWith("n") )
					{
						//закрасили сиреневым известный баг
						outputEvents.get(outputEvents.size()-1).setColor(35);
						//events.get(mark).setColor(35);
					}
				}
				currentEvent = nextEvent;
				nextEvent = inCase.readLine();
			}

			inCase.close();
			inLog.close();

			for(Event elem: happenedEvents)
			{
				//закрасили красным неизвестно откуда взявшиеся события
				if(elem.getEventColor() == 0)
				{
					elem.setColor(31);
				}
			}

			for(int i = 0; i < outputEvents.size(); i++)
			{
				for(int j = 0; j < happenedEvents.size(); j++)
				{
					if(happenedEvents.get(j).getEventName().contains(outputEvents.get(i).getEventName()) && happenedEvents.get(j).getEventColor() != 31)
					{
						happenedEvents.get(j).setColor(outputEvents.get(i).getEventColor());
						break;
					}
				}
			}



			//слили списки в один, выводит будем их объединение
			outputEvents.addAll(happenedEvents);
			Collections.sort(outputEvents, sortEventByDate);

			//костыль по причине того, что если события пришли в одно время, система их может переставить местами
			//и будут рядом одно и то же пропущенное событие и красный лишний баг
			for (int j = 1; j < outputEvents.size(); j++)
			{
				Event prev = outputEvents.get(j-1);
				Event curr = outputEvents.get(j);
				if (curr.getEventName().contains(prev.getEventName()) &&
						(curr.getEventColor() == 31 && prev.getEventColor() == 34))
				{
					outputEvents.remove(j - 1);
					outputEvents.get(j - 1).setColor(32);
				}

			}

				Event prevEvent = new Event("", reportPane);
			for (Event ev: outputEvents)
			{
				//выводим, если разные имена или события из одного списка
				if (!ev.getEventName().equals(prevEvent.getEventName()) || ev.getListNumber() == prevEvent.getListNumber())
					ev.print();
				prevEvent = ev;
			}


		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			out("ArrayIndexOutOfBoundsException. Sorry, empty data source.\n", reportOut);
		}
		catch (FileNotFoundException e)
		{

			out("FileNotFoundException. Sorry, i can't find one of files or both.\n", reportOut);
		}
		catch (IOException e)
		{
			out("IOException in input file.\n", reportOut);
		}
	}

	public static int findEvent(int startPosition, String event, String finishEvent, List<Event> logPart)
	{

		if(startPosition == logPart.size())
		{
			return -1;
		}

		try
		{
			if (event.startsWith("w") || event.startsWith("n"))
			{
				event = event.substring(2);
			}
			int i = startPosition;

			String currentEvent = logPart.get(i).getEventName();

			if (finishEvent == null)
			{
				while (currentEvent != null)
				{
					if (currentEvent.contains(event) && logPart.get(i).getEventColor() == 0)
					{
						return i;
					}
					i++;

					if (i != logPart.size())
						currentEvent = logPart.get(i).getEventName();
					else
						return -1;
				}
			}
			while (!currentEvent.contains(finishEvent))
			{
				if (currentEvent.contains(event) && logPart.get(i).getEventColor() == 0)
				{
					return i;
				}
				i++;
				currentEvent = logPart.get(i).getEventName();

				if (currentEvent == null)
				{
					return -1;
				}

			}

			if (currentEvent.contains(event) && logPart.get(i).getEventColor() == 0)
			{
				return i;
			}


		}
		catch (IndexOutOfBoundsException e)
		{
			System.out.println("Index out of bound logpart");
		}
		catch (NullPointerException e)
        {
            System.out.println("NullPointerException in method contain() in findEvent(): finishEvent is null.");
        }
		return -1;
	}

	public void out (String data, DefaultStyledDocument outArea)
	{
		try
		{
			outArea.insertString(outArea.getLength(), data, null);
		}
		catch (BadLocationException e)
		{

		}
	}


	static Comparator<Event> sortEventByDate = new Comparator<Event>()
	{
		@Override
		public int compare(Event ev1, Event ev2) {
			String[] date1 = ev1.getStringData().split(":");
			String[] date2 = ev2.getStringData().split(":");
			int result;

			result = Integer.compare(Integer.parseInt(date1[0]), Integer.parseInt(date2[0]));
			if (result != 0){ return result; }

			result = Integer.compare(Integer.parseInt(date1[1]), Integer.parseInt(date2[1]));
			if (result != 0){ return result; }

			result = Integer.compare(Integer.parseInt(date1[2]), Integer.parseInt(date2[2]));
			if (result != 0){ return result; }

			return  ev2.getEventName().compareTo(ev1.getEventName());

		}
	};

}
