package contactManager;

import org.junit.*;
import junit.framework.Assert;

import java.util.Calendar;

public class TestCal
{
	/*
	 * Doesn't seem to matter if smaller fields are uninitd -
	 * assume they are initd to same value.
	 * So, can use getInstance() with no initing.
	 */
	@Test
	public void doit()
	{
			Calendar base = Calendar.getInstance();
			// base.set(0,0,0,0,0);
			// base.set(Calendar.MILLISECOND, 0);
			
			// Calendar cal1 = (Calendar) base.clone();
			Calendar cal1 = Calendar.getInstance();
			cal1.set(2013, 02, 14);
			
			Calendar cal2 = (Calendar) base.clone();
			cal2.set(2013, 02, 14);
			
			Calendar cal3 = (Calendar) base.clone();
			cal3.set(2013, 02, 15);
			
			Assert.assertEquals(0, cal1.compareTo(cal2));
			Assert.assertEquals(-1, cal1.compareTo(cal3));
	}
    
    public static void main(String argv[])
    {
        TestCal tc = new TestCal();
        tc.doit();
    }
}