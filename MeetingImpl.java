package contactManager;

import java.util.Calendar;
import java.util.Set;

/**
 * A class to represent meetings
 *
 * Meetings have unique IDs, scheduled date and a list of participating contacts
 */
public abstract class MeetingImpl implements Meeting
{
	private static int nextId = 1;
	
	private int id;
	private Set<Contact> contacts;
	private Calendar date;
	
	private String delim;
	
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
		this.contacts = contacts;
		this.date = date;
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
	
	public String toString()
	{
		delim = ContactManagerImpl.COMMASPACE;
		String ret = Integer.toString(id) + delim + date + delim;
		
		// inefficient but perhaps acceptable here
		for(Contact contact : contacts)
		{
			ret += delim + contact.getId();
		}
		return ret;
	}
	
	public String toCSV()
	{
		delim = ContactManagerImpl.CSVDELIM;
		String ret = Integer.toString(id) + delim + date + delim;
		
		// inefficient but perhaps acceptable here
		for(Contact contact : contacts)
		{
			ret += delim + contact.getId();
		}
		return ret;
	}
}