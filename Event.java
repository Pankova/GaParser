/**
 * Created by Мария on 13.06.2016.
 */
public class Event
{
	private String eventName;
	private int eventColor = 0;

	Event(String name){ eventName = name; }

	public final void setColor(int color){ eventColor = color; }

	public int getEventColor(){ return eventColor; }

	public String getEventName(){ return eventName; }

	public void print()
	{
		System.out.println((char) 27 + "[" + eventColor + "m" + eventName);
	}
}
