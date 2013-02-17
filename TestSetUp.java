package contactManager;

import org.junit.*;
import junit.framework.Assert;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * we need to run this for most test classes 
 */
public class TestSetUp
{
	public static Calendar pastDate;
	public static Calendar futureDate;
	
	public static Contact CONTACT1 = new ContactImpl("Lou Preager");
	public static Contact CONTACT2 = new ContactImpl("Henry Hall");
	public static Contact CONTACT3 = new ContactImpl("Syd Lipton");
	public static Contact CONTACT4 = new ContactImpl("Ennis Parkes");
	public static Contact CONTACT5 = new ContactImpl("Jack Payne");
	public static Contact CONTACT6 = new ContactImpl("John Smith");
	public static Contact CONTACT7 = new ContactImpl("Alexander Tsfasman");
	public static Contact CONTACT8 = new ContactImpl("Ivy Benson");
	public static Contact CONTACT9 = new ContactImpl("Sydney Kyte");
	public static Contact CONTACT10 = new ContactImpl("John Smith"); // duplicate name
	
	public static Set<Contact> testContactSet;
	public static Set<Contact> invalidContactSet;
	
	// this is the only sort of invalid contact I can create.
	// the constructor/setters won't let you add invalid data members
	// (if I needed to add extra validity tests I'd do it there)
	// and you can't use invalid objects.
	public static final ContactImpl CONTACTINVAL = (ContactImpl)null;
	
	public void testSetUp()
	{
		// and dates in the past and future
		Calendar pastDate = (Calendar) ContactManagerImpl.todayDate.clone();
		pastDate.add(Calendar.DATE, -7);
		
		Calendar futureDate = (Calendar) ContactManagerImpl.todayDate.clone();
		futureDate.add(Calendar.DATE, 7);
		
		testContactSet = new TreeSet<Contact>();
		testContactSet.add(CONTACT1);
		testContactSet.add(CONTACT5);
		testContactSet.add(CONTACT7);
		testContactSet.add(CONTACT10);
		testContactSet.add(CONTACT3);
		testContactSet.add(CONTACT2);

		invalidContactSet = new TreeSet<Contact>();
		invalidContactSet.add(CONTACT3);
		invalidContactSet.add(CONTACT4);
		invalidContactSet.add(CONTACT6);
		invalidContactSet.add(CONTACT10);
		invalidContactSet.add(CONTACT2);
		invalidContactSet.add(CONTACTINVAL);
	}
}