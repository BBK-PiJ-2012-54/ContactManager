package contactManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.*;

import junit.framework.Assert;


public class TestContactManager extends TestSetUp
{
	// the one instance of a ContactManager
	ContactManager cmInst = ContactManagerImpl.CONTACT_MANAGER;
	
	private static Calendar now;
	
	@Before
	public void setUp()
	{
		((ContactManagerImpl)cmInst).clearForTesting();
		
		testSetUp();
	}
	
	/**
	 * create a meeting with an invalid contact set
	 * expect IllegalArgumentException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddFutureMeeting1()
	{
		cmInst.addFutureMeeting(invalidContactSet, futureDate);
	}

	/**
	 * create a meeting with a date in the past
	 * expect IllegalArgumentException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddFutureMeeting2()
	{
		cmInst.addFutureMeeting(testContactSet, pastDate);		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetPastMeeting1()
	{
		int meetID = cmInst.addFutureMeeting(testContactSet, futureDate);

		cmInst.getPastMeeting(meetID);	
	}
	
	@Test
	public void testGetPastMeeting2()
	{
		Assert.assertNull(cmInst.getPastMeeting(9999));	
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetFutureMeeting1()
	{
		int meetID = cmInst.addFutureMeeting(testContactSet, pastDate);
		Assert.assertEquals(meetID, 2);

		cmInst.getFutureMeeting(2);	
	}
	
	@Test
	public void testGetFutureMeeting2()
	{
		Assert.assertNull(cmInst.getFutureMeeting(9999));	
	}
	
	@Test
	public void testGetMeeting()
	{
		Assert.assertNull(cmInst.getMeeting(9999));	
	}
	
	/**
	 * create a valid meeting 
	 */
	@Test
	public void testFutureMeeting()
	{		
		int meetID = cmInst.addFutureMeeting(testContactSet, futureDate);
		
		FutureMeeting fm = cmInst.getFutureMeeting(meetID);
		Assert.assertEquals(futureDate, fm.getDate());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetFutureMeetingAsPast()
	{
		Calendar fDate = Calendar.getInstance();
		fDate.setTime(new Date());
		fDate.add(Calendar.SECOND, 1);
		int meetID = cmInst.addFutureMeeting(testContactSet, fDate);

		try {
			Thread.sleep(1100);
		} catch (InterruptedException e) {
			Assert.fail("sleep was interrupted, please rerun tests.");
		}
		
		Meeting pm = cmInst.getPastMeeting(meetID);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetPastMeetingAsFuture()
	{
		// create a new Past Meeting
		cmInst.addNewPastMeeting(testContactSet, pastDate, "notes");
		
		// need this to get hold of it
		Meeting pm = ((ContactManagerImpl)cmInst).getLastMeetingAddedForTesting();
		
		// check that you can't get it as a FutureMeeting
		Meeting fm = cmInst.getFutureMeeting(pm.getId());
	}
	
	@Test
	public void testGetPastMeetingList1()
	{
		cmInst.addNewPastMeeting(testContactSet, pastDate, "added as NewPastMeeting");
		Calendar fDate = Calendar.getInstance();
		fDate.setTime(new Date());
		fDate.add(Calendar.SECOND, 1);
		int fmId = cmInst.addFutureMeeting(testContactSet, fDate);
		try {
			Thread.sleep(1100);
		} catch (InterruptedException e) {
			Assert.fail("sleep was interrupted, please rerun tests.");
		}
		cmInst.addMeetingNotes(fmId, "future meeting convd to Past");
		cmInst.addFutureMeeting(testContactSet, futureDate); // should not show up in list
		Set<Contact> cSet = cmInst.getContacts(3);
		for(Contact testContact : cSet)
		{
			List<PastMeeting> list = cmInst.getPastMeetingList(testContact);
			Assert.assertEquals("added as NewPastMeeting", list.get(0).getNotes());
			Assert.assertEquals("future meeting convd to Past", list.get(1).getNotes());			
			Assert.assertEquals(2, list.size());
		}
		
		cSet = cmInst.getContacts(6);
		for(Contact testContact : cSet)
		{
			List<PastMeeting> list = cmInst.getPastMeetingList(testContact);
			Assert.assertEquals(0, list.size());
		}
	}

	/**
	 * test persistence
	 * 
	 * Run testPersistence1 and testPersistence2 manually
	 * as we need to restart between them.
	 */
	@Test
	public void testPersistence1()
	{		
		testSetUp();
		
		Calendar date = (Calendar)futureDate.clone();
		date.set(Calendar.HOUR, 8);
		cmInst.addFutureMeeting(testContactSet, date);
		
		cmInst.addNewPastMeeting(testContactSet, pastDate, "these are the notes");
		now = Calendar.getInstance();
		now.setTime(new Date());

		date = (Calendar)now.clone();
		int id = cmInst.addFutureMeeting(testContactSet, date);
		cmInst.addMeetingNotes(id, "making past from future");
		
		cmInst.flush();
	}
	
	/**
	 * test persistence
	 * 
	 * Run testPersistence1 and testPersistence2 manually
	 * as we need to restart between them.
	 */
	@Test
	public void testPersistence2()
	{		
		setUpDates();
		((ContactManagerImpl)cmInst).clearForTesting();
		((ContactManagerImpl)cmInst).restoreFromFileForTesting();
		
		Meeting m = cmInst.getFutureMeeting(0);
		Assert.assertTrue(m instanceof FutureMeetingImpl);
		Assert.assertEquals(8, m.getDate().get(Calendar.HOUR));		
		
		List<Meeting> ml = cmInst.getFutureMeetingList(futureDate);
		Assert.assertEquals(1, ml.size());
		
		now = Calendar.getInstance();
		now.setTime(new Date());

		m = cmInst.getPastMeeting(1);
		Assert.assertTrue(m instanceof PastMeetingImpl);
		Assert.assertEquals(now.get(Calendar.HOUR), m.getDate().get(Calendar.HOUR));	
		Assert.assertEquals("these are the notes", ((PastMeetingImpl)m).getNotes());

		m = cmInst.getPastMeeting(2);
		Assert.assertTrue(m instanceof PastMeetingImpl);
		Assert.assertEquals(now.get(Calendar.HOUR), m.getDate().get(Calendar.HOUR));	
		Assert.assertEquals("making past from future", ((PastMeetingImpl)m).getNotes());
	}
	
	/**
	 * test assignment of contacts
	 */
	@Test
	public void testContactIDs()
	{
		testSetUp();
		cmInst.addNewContact("new contact", "new contact notes");
		Set<Contact> thisSet = cmInst.getContacts(2, 8);
		Set<Contact> thatSet = cmInst.getContacts("Syd");
		
		// IDs 2 and 8 are two contacts
		Assert.assertEquals(2, thisSet.size());
		
		// two contacts contain "Syd"
		Assert.assertEquals(2, thatSet.size());
		
		for(Contact c: thatSet)
		{
			Assert.assertTrue(thisSet.contains(c));
		}
	}

	/**
	 * test assignment of IDs doesn't change
	 */
	@Test
	public void testAddFutureMeetingIDs()
	{
		// add several future dates all on one day
		Calendar date = (Calendar)futureDate.clone();
		date.set(Calendar.HOUR, 8);
		cmInst.addFutureMeeting(testContactSet, date);
		date.set(Calendar.HOUR, 9);
		cmInst.addFutureMeeting(testContactSet, date);
		date.set(Calendar.HOUR, 10);
		cmInst.addFutureMeeting(testContactSet, date);
		date.set(Calendar.HOUR, 11);
		cmInst.addFutureMeeting(testContactSet, date);
		date.set(Calendar.HOUR, 12);
		cmInst.addFutureMeeting(testContactSet, date);
		
		// add a date in the past
		cmInst.addNewPastMeeting(testContactSet, pastDate, "some notes");
		
		// add two more on different days
		date.add(Calendar.DATE, 1);
		cmInst.addFutureMeeting(testContactSet, date);
		
		date.add(Calendar.DATE, -2);
		cmInst.addFutureMeeting(testContactSet, date);
		
		for(Contact c: testContactSet)
		{
			List<Meeting> l = cmInst.getFutureMeetingList(c);
			Assert.assertEquals(l.size(), 7);
		}
		
		List<Meeting> l = cmInst.getFutureMeetingList(futureDate);
		Assert.assertEquals(l.size(), 5);
		
		Meeting m = cmInst.getFutureMeeting(1);
		Assert.assertEquals(9, m.getDate().get(Calendar.HOUR));
		
		cmInst.flush();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetFutureMeetingInvalidContact()
	{
		cmInst.getFutureMeetingList(contactInval);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetFutureMeetingBadIDContact()
	{
		cmInst.getFutureMeetingList(contactBadID);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetPastMeetingInvalidContact()
	{
		cmInst.getPastMeetingList(contactInval);
	}

	@Test (expected = NullPointerException.class)
	public void testGetContactByNullName()
	{
		Set<Contact> set = cmInst.getContacts((String)null);
	}
}