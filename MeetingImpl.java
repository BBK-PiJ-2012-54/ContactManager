package contactManager;

import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

/**
 * A class to represent meetings
 *
 * Meetings have unique IDs, scheduled date and a list of participating contacts
 */
public abstract class MeetingImpl implements Meeting
{
	private static int nextId = 0;
	
	private int id;
	private Set<Contact> contacts;
	private Calendar date;
	
	/**
	 * Standard constructor for future meetings.
	 * Assigns the next id.
	 * 
	 * @param contacts
	 * @param date
	 */
	public MeetingImpl(Set<Contact> contacts, Calendar date)
	{
		id = nextId++;
		
		// need defensive copy of the set
		this.contacts = new TreeSet<Contact>();
		this.contacts.addAll(contacts);
		
		// need defensive copy of mutable object
		this.date = (Calendar)date.clone();
	}
	
	/**
	 * Standard constructor for future meetings.
	 * Sets the id to the one given.
	 * Used only when converting a future meeting to a past one in addMeetingNotes.
	 * 
	 * @param contacts
	 * @param date
	 * @param id
	 */
	public MeetingImpl(Set<Contact> contacts, Calendar date, int id)
	{
		this.id = id;
		
		// need defensive copy of the set
		this.contacts = new TreeSet<Contact>();
		this.contacts.addAll(contacts);
		
		// need defensive copy of mutable object
		this.date = (Calendar)date.clone();
	}
	
	/**
	 * Returns the id of the meeting.
	 *
	 * @return the id of the meeting.
	 */
	public int getId() 
	{
		return id;
	}

	/**
	 * Return the date of the meeting.
	 *
	 * @return the date of the meeting.
	 */
	public Calendar getDate() 
	{
		return date;
	}

	/**
	 * Return the details of people that attended the meeting.
	 *
	 * The list contains a minimum of one contact (if there were
	 * just two people: the user and the contact) and may contain an
	 * arbitrary number of them.
	 *
	 * @return the details of people that attended the meeting.
	 */
	public Set<Contact> getContacts() 
	{
		return contacts;
	}

	/**
	 * only used by unit tests
	 */
	public static void resetNextIdForTesting()
	{
		nextId = 0;
	}

	private String _toString(String delim)
	{
		String ret = Integer.toString(id) + delim + date.getTimeInMillis();
		
		// past/future flag and notes (empty for Future meetings)
		if(this instanceof FutureMeeting)
			ret += delim + "F" + delim;
		else
			ret += delim + "P" + delim + ((PastMeetingImpl)this).getNotes();
		
		// inefficient but perhaps acceptable here
		for(Contact contact : contacts)
		{
			ret += delim + contact.getId();
		}
		return ret;		
	}
	
	public String toString()
	{
		return _toString(ContactManagerImpl.COMMASPACE);
	}
	
	public String toCSV()
	{
		return _toString(ContactManagerImpl.CSVDELIM);
	}
}