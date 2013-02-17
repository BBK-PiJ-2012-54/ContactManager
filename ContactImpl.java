package contactManager;

/**
 * A contact is a person we are making business with or may do in the future.
 *
 * Contacts have an ID (unique), a name (probably unique, but maybe
 * not), and notes that the user may want to save about them.
 */
public class ContactImpl implements Contact, Comparable<ContactImpl>
{
	private static int nextId = 1;
	
	private int id;
	private String name;
	private String note;
	
	private String delim;
	
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

	@Override
	public int compareTo(ContactImpl cmp) 
	{
		return (this.getId() - cmp.getId());
	}
	
	public String toString()
	{
		delim = ContactManagerImpl.COMMASPACE;
		return Integer.toString(id) + delim + name + delim + note;
	}
	
	public String toCSV()
	{
		delim = ContactManagerImpl.CSVDELIM;
		return Integer.toString(id) + delim + name + delim + note;
	}	
}