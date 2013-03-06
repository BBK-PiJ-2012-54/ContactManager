package contactManager;

import java.util.TreeSet;

/**
 * A contact is a person we are making business with or may do in the future.
 *
 * Contacts have an ID (unique), a name (probably unique, but maybe
 * not), and notes that the user may want to save about them.
 */
public class ContactImpl implements Contact, Comparable<ContactImpl>
{
	private static int nextId = 0;
	
	private int id;
	private String name;
	private String note;

	/**
	 * creates a contact with a name, assigning the next ID
	 * 
	 * @param name
	 */
	public ContactImpl(String name)
	{
		this.name = name;
		this.id = nextId++;
	}
	
	/**
	 * Returns the ID of the contact.
	 *
	 * @return the ID of the contact.
	 */
	public int getId() 
	{
		return id;
	}

	/**
	 * Returns the name of the contact.
	 *
	 * @return the name of the contact.
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * Returns our notes about the contact, if any.
	 *
	 * If we have not written anything about the contact, the empty
	 * string is returned.
	 *
	 * @return a string with notes about the contact, maybe empty.
	 */
	public String getNotes() 
	{
		return note;
	}
	
	/**
	 * Add notes about the contact.
	 *
	 * @param note the notes to be added
	 */
	public void addNotes(String note) 
	{
		this.note = note;
	}

	/**
	 * only used by unit tests
	 */
	public static void resetNextIdForTesting()
	{
		nextId = 0;
	}
	
	@Override
	public int compareTo(ContactImpl cmp) 
	{
		return (this.getId() - cmp.getId());
	}
	
	public String _toString(String delim)
	{
		return Integer.toString(id) + delim + name + delim + note;
	}
	
	public String toString()
	{
		return _toString(ContactManagerImpl.COMMASPACE);
	}
	
	public String toCSV()
	{
		return _toString(ContactManagerImpl.CSVDELIM);
	}	
	
	/*************************************************************
	 * The following method(s), for testing only, should be removed
	 * before release. 
	 *************************************************************/
	
	/**
	 * Create an invalid contact
	 * 
	 * @param name
	 * @param id
	 * 
	 * @return a contact not in the List or Set, and with the given ID
	 */
	public static ContactImpl getInvalidContactImplForTesting(String name, int id)
	{
		ContactImpl c = new ContactImpl(name);
		c.id = id;
		return c;
	}
}