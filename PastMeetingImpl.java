package contactManager;

import java.util.Calendar;
import java.util.Set;

/**
 * A meeting that was held in the past.
 *
 * It includes your notes about what happened and what was agreed.
 */
public class PastMeetingImpl extends MeetingImpl implements PastMeeting
{
	private String notes;
	
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
	 * @param fm
	 * @param notes
	 * @param id
	 */
	public PastMeetingImpl(FutureMeetingImpl fm, String notes, int id) 
	{
		super(fm.getContacts(), fm.getDate(), id);
		addNotes(notes);
	}

	/**
	 * Returns the notes from the meeting.
	 *
	 * If there are no notes, the empty string is returned.
	 *
	 * @return the notes from the meeting.
	 */
	public String getNotes()
	{
		return notes;
	}
	
	void addNotes(String notes)
	{
		this.notes = notes;
	}

	private String _toString(String delim)
	{
		String ret = Integer.toString(this.getId()) + delim + this.getDate() + delim;
		
		// inefficient but perhaps acceptable here
		for(Contact contact : this.getContacts())
		{
			ret += delim + contact.getId();
		}
		return ret;		
	}
}