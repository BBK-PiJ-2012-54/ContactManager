package contactManager;

import org.junit.*;
import junit.framework.Assert;


public class TestContactManager extends TestSetUp
{
	// the one instance of a ContactManager
	ContactManager cmInst = ContactManagerImpl.CONTACT_MANAGER;
	
	@Before
	public void setUp()
	{
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
		Assert.assertEquals(meetID, 1);

		cmInst.getPastMeeting(1);	
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
		Assert.assertEquals(meetID, 2);
		
		FutureMeeting fm = cmInst.getFutureMeeting(meetID);
		Assert.assertEquals(futureDate, fm.getDate());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetFutureMeetingAsPast()
	{
		PastMeeting pm = cmInst.getPastMeeting(1);
	}
}