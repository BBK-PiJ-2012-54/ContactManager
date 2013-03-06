package contactManager;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * we need to run this for most test classes 
 */
public class TestSetUp
{
	public static Calendar pastDate;
	public static Calendar futureDate;
	
	public static Set<Contact> testContactSet;
	public static Set<Contact> invalidContactSet;
		
	public void testSetUp()
	{
		setUpContacts();
		
		setUpDates();
	}
	
	public void setUpDates()
	{
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		// and dates in the past and future
		pastDate = (Calendar) now.clone();
		pastDate.add(Calendar.DATE, -7);
		
		futureDate = (Calendar) now.clone();
		futureDate.add(Calendar.DATE, 7);
	}
	
	public void setUpContacts()
	{
		// get the instance
		ContactManager cm = ContactManagerImpl.CONTACT_MANAGER;

		// testing method only, should be removed from production code
		((ContactManagerImpl)cm).clearForTesting();
		
		cm.addNewContact("Lou Preager", "and his band");
		cm.addNewContact("Henry Hall", "and his band");
		cm.addNewContact("Syd Lipton", "and his band");
		cm.addNewContact("Ennis Parkes", "and her band");
		cm.addNewContact("Jack Payne", "and his band");
		cm.addNewContact("Bob Smith", "and his band");
		cm.addNewContact("Alexander Tsfasman", "and his band");
		cm.addNewContact("Ivy Benson", "and her band");
		cm.addNewContact("Sydney Kyte", "and his band");
		cm.addNewContact("Jack Payne", "the footballer");

		/* How to create an invalid contact.  Well, this is impossible 
		 * with the public interface methods.  But here I can create an
		 * instance without adding it to the list that I store.
		 */
		Contact contactInval = new ContactImpl("I don't exist");
		contactInval.addNotes("and I'm not notes");

		testContactSet = cm.getContacts(0, 1, 2, 3, 4, 5);

		invalidContactSet = cm.getContacts(1, 2, 3, 4, 5);
		invalidContactSet.add(contactInval); // the one that's not in the contact list
	}
}