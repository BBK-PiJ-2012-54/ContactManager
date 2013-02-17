package contactManager;

import java.util.Calendar;
import java.util.Set;

/**
 * A meeting to be held in the future
 */
public class FutureMeetingImpl extends MeetingImpl
{
	// explicit constructor needed.
	public FutureMeetingImpl(Set<Contact> contacts, Calendar date) 
	{
		super(contacts, date);
	}

	// No methods here, this is just a naming interface
	// (i.e. only necessary for type checking and/or downcasting)
}