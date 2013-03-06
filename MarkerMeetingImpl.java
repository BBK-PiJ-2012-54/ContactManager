package contactManager;

import java.util.Calendar;
import java.util.TreeSet;

/**
 * A dummy meeting with no contacts.
 * Used as a date marker to extract the meetings within 
 * a given date range.
 */
public class MarkerMeetingImpl extends MeetingImpl
{
	private static final TreeSet<Contact> emptySet = new TreeSet<Contact>();
	
	// explicit constructor needed.
	public MarkerMeetingImpl(Calendar date) 
	{
		super(emptySet, date);
	}
}