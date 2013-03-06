package contactManager;

import java.util.Comparator;

/**
 * A class to compare meetings to allow creation of a TreeSet<Meeting> with this order
 */
public class MeetingComparator implements Comparator<Meeting>
{
	private static MeetingComparator instance = new MeetingComparator();
	
	public static MeetingComparator getInstance()
	{
		return instance;
	}
	
	/**
	 * Is meeting1 before, after or at the some time as meeting2?
	 * 
	 * @param m1 first meeting
	 * @param m2 second meeting
	 * @return a positive, zero or negative int
	 */
	public int compare(Meeting m1, Meeting m2)
	{
		return m1.getDate().compareTo(m2.getDate());
	}
	
	/*
	 * The equals() method needs no change from the default Object.equals()
	 * and the javadoc says it's safe not to override it.
	 */
}
