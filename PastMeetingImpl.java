package contactManager;

import java.util.Calendar;
import java.util.Set;

/**
 * A meeting that was held in the past.
 *
 * It includes your notes about what happened and what was agreed.
 */
public class PastMeetingImpl extends MeetingImpl 
{
	String delim;
	
	/**
	 * never called, only here because the compiler wants this if 
	 * there's a super non-default constructor
	 * 
	 * @param contacts
	 * @param date
	 */
	public PastMeetingImpl(Set<Contact> contacts, Calendar date) 
	{
		super(contacts, date);
	}

	/**
	 * 'Converts' a future meeting into a past one by adding notes
	 * (i.e. constructs a past meeting and removes the future meeting
	 * from any collections.)
	 * 
	 * @param fmi
	 * @param notes
	 */
	public PastMeetingImpl(FutureMeetingImpl fmi, String notes) 
	{
		super(fmi.getContacts(), fmi.getDate());
		addNotes(notes);
		
		// now REMOVE the future meeting.  Create FutureMeeting.remove()
		// a fake destructor.
	}

	private String notes;
	
	/**
	 * Default constructor.
	 * 
	 * Explicitly sets the initial value of notes to the empty string.
	 */
	// NO, won't instantiate a PastMeeting directly.
	// In fact, don't you 
	// public PastMeetingImpl()
	// {
	// 		notes = new String("");
	// }
	
	/**
	 * Returns the notes from the meeting.
	 *
	 * If there are no notes, the empty string is returned.
	 *
	 * @return the notes from the meeting.
	 */
	String getNotes()
	{
		return notes;
	}
	
	void addNotes(String notes)
	{
		this.notes = notes;
	}
	
	public String toString()
	{
		delim = ContactManagerImpl.COMMASPACE;
		String ret = Integer.toString(this.getId()) + delim + this.getDate() + delim + this.getNotes() + delim;
		
		// inefficient but perhaps acceptable here
		for(Contact contact : this.getContacts())
		{
			ret += delim + contact.getId();
		}
		return ret;
	}
	
	public String toCSV()
	{
		delim = ContactManagerImpl.CSVDELIM;
		String ret = Integer.toString(this.getId()) + delim + this.getDate() + delim + this.getNotes() + delim;
		
		// inefficient but perhaps acceptable here
		for(Contact contact : this.getContacts())
		{
			ret += delim + contact.getId();
		}
		return ret;
	}
}