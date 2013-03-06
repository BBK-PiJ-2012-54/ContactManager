package contactManager;

import java.io.BufferedReader;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A class to manage your contacts and meetings.
 */
public class ContactManagerImpl implements ContactManager
{
	/**
	 * In this implementation we only have one instance of ContactManager.
	 */
	public static final ContactManager CONTACT_MANAGER = new ContactManagerImpl();

	private static Calendar now = Calendar.getInstance();

	public static final String COMMASPACE = new String(", ");
	public static final String CSVDELIM = new String("%");
	public static final String SAVEFILE = "ContactManager.dat";
	
	// this holds all contacts that have been created
	private static List<Contact> contactList = new ArrayList<Contact>();
	
	private static List<Meeting> meetingList = new ArrayList<Meeting>();	
	
	// a sorted set of all meetings in chronological order
	private static TreeSet<Meeting> meetingSet = new TreeSet<Meeting>(MeetingComparator.getInstance());
	
	private static File saveFile;
	
	private enum ObjectTypes{CONTACT_TYPE, MEETING_TYPE};
	
	private static ObjectTypes objectType = null;

	/**
	 * static method which initialises things at startup
	 */
	static
	{
		// restore any contacts and meetings from file
		restoreFromFile();
	}
	
	public static ContactManager getInstance()
	{
		return new ContactManagerImpl();
	}
	
	private static void restoreFromFile()
	{
		BufferedReader saveReader = null;
		
		saveFile = new File(SAVEFILE);
		if(!saveFile.exists() || !saveFile.canRead())
		{
			System.out.println("File " + SAVEFILE + " does not exist or is not readable.");
			return;
		}
		
		try
		{
			saveReader = new BufferedReader(new FileReader(saveFile));
			String line;
			while ((line = saveReader.readLine()) != null) 
			{
				processSaveLine(line);
			}
		}
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
		}
		finally
		{
			closeReader(saveReader);
		}
	}
	
	private static void processSaveLine(String line)
	{
		if(line.charAt(0) == '#')
		{
			// may switch object type or be a comment
			if(line.startsWith("Contacts", 1))
			{
				objectType = ObjectTypes.CONTACT_TYPE;
			}
			else if(line.startsWith("Meetings", 1))
			{
				objectType = ObjectTypes.MEETING_TYPE;
			}
		}
		else
		{
			// a csv line of the current object type
			String[] fields = line.split(ContactManagerImpl.CSVDELIM);
			
			if(objectType == ObjectTypes.CONTACT_TYPE)
			{
				CONTACT_MANAGER.addNewContact(fields[1], fields[2]);
			}
			else if(objectType == ObjectTypes.MEETING_TYPE)
			{
				long millis = Long.parseLong(fields[1]);
				Calendar date = Calendar.getInstance();
				date.setTimeInMillis(millis);
				
				Set<Contact> cSet = new TreeSet<Contact>();
				
				int[] ids = new int[fields.length - 2];
				
				for(int i = 4; i < fields.length; i++)
				{
					ids[i - 4] = Integer.parseInt(fields[i]);
				}
				
				cSet.addAll(CONTACT_MANAGER.getContacts(ids));
				
				if(fields[2].equals("F"))
				{
					CONTACT_MANAGER.addFutureMeeting(cSet, date);
				}
				else
				{
					CONTACT_MANAGER.addNewPastMeeting(cSet, date, fields[3]);
				}
			}
		}
	}
	
	private static void closeReader(Reader reader) 
	{
		try 
		{
			if (reader != null) 
			{
				reader.close();
			}
		}
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
		}
	}
	
	private static void closeWriter(Writer writer) 
	{
		try 
		{
			if (writer != null) 
			{
				writer.close();
			}
		}
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Add a new meeting to be held in the future.
	 *
	 * @param contacts a list of contacts that will participate in the meeting
	 * @param date the date on which the meeting will take place
	 * @return the ID for the meeting
	 * @throws IllegalArgumentException if the meeting is set for a time in the past,
	 * of if any contact is unknown / non-existent
	 */
	public int addFutureMeeting(Set<Contact> contacts, Calendar date) throws IllegalArgumentException
	{
		now.setTime(new Date());
		if(date.before(now))
		{
			throw new IllegalArgumentException("date should not be in the past");
		}
		
		for(Contact contact : contacts)
		{
			try
			{
				contactList.get(contact.getId());
			}
			catch(IndexOutOfBoundsException iobe)
			{
				throw new IllegalArgumentException("contact id " + contact.getId() + " is unknown");
			}
		}
		
		MeetingImpl fm = new FutureMeetingImpl(contacts, date);
		meetingList.add(fm);
		
		meetingSet.add(fm);

		for(Contact c : contacts)
		{
			((ContactImpl)c).addMeeting(fm);
		}

		return fm.getId();
	}

	/**
	 * Returns the PAST meeting with the requested ID, or null if it there is none.
	 *
	 * @param id the ID for the meeting
	 * @return the meeting with the requested ID, or null if it there is none.
	 * @throws IllegalArgumentException if there is a meeting with that ID happening in the future
	 */
	public PastMeeting getPastMeeting(int id) throws IllegalArgumentException
	{
		Meeting m = getMeeting(id);
		if(m == null)
		{
			return (PastMeeting)m;
		}
		
		now.setTime(new Date());

		if(now.before(m.getDate()))
		{
				throw new IllegalArgumentException("Meeting is in the future");
		}
		return (PastMeeting)m;
	}

	/**
	 * Returns the FUTURE meeting with the requested ID, or null if there is none.
	 *
	 * @param id the ID for the meeting
	 * @return the meeting with the requested ID, or null if it there is none.
	 * @throws IllegalArgumentException if there is a meeting with that ID happening in the past
	 */
	public FutureMeeting getFutureMeeting(int id) throws IllegalArgumentException
	{
		Meeting m = getMeeting(id);
		if(m == null)
		{
			return (FutureMeeting)m;
		}
		
		now.setTime(new Date());

		if(now.after(m.getDate()))
		{
				throw new IllegalArgumentException("Meeting is in the past");
		}
		
		return (FutureMeeting)m;
	}

	/**
	 * Returns the meeting with the requested ID, or null if it there is none.
	 *
	 * @param id the ID for the meeting
	 * @return the meeting with the requested ID, or null if it there is none.
	 */
	public Meeting getMeeting(int id) 
	{
		if(id >= meetingList.size())
		{
			return null;
		}
		
		return meetingList.get(id);
	}

	/**
	 * Returns the list of future meetings scheduled with this contact.
	 *
	 * If there are none, the returned list will be empty. Otherwise,
	 * the list will be chronologically sorted and will not contain any
	 * duplicates.
	 *
	 * @param contact one of the user�s contacts
	 * @return the list of future meeting(s) scheduled with this contact (maybe empty).
	 * @throws IllegalArgumentException if the contact does not exist
	 */
	public List<Meeting> getFutureMeetingList(Contact contact) throws IllegalArgumentException
	{
		if(contact == null ||
		   contact.getId() >= meetingList.size() ||
		   contact.getId()  < 0)
		{
			throw new IllegalArgumentException("Contact does not exist");
		}

		
		// contact supplied might have a valid id, but not be the one we added 
		Set<Contact> set = getContacts(contact.getId());
		if(!set.contains(contact))
		{
			throw new IllegalArgumentException("Contact does not exist");			
		}
		
		now.setTime(new Date());

		// create a dummy meeting with the start date.
		Meeting todayMarker = new MarkerMeetingImpl(now);

		Set<Meeting> futureSet = meetingSet.tailSet(todayMarker);
		
		return listForContact(futureSet, contact);
	}
	
	/**
	 * Returns the list of meetings that are scheduled for, or that took
	 * place on, the specified date
	 *
	 * If there are none, the returned list will be empty. Otherwise,
	 * the list will be chronologically sorted and will not contain any
	 * duplicates.
	 *
	 * @param date the date
	 * @return the list of meetings
	 */
	public List<Meeting> getFutureMeetingList(Calendar date) 
	{
		/* ASSUME given date has any time within the required day.
		 * Also ASSUME that this should work for any meeting whether
		 * in the future or the past.
		 */
		
		// create a date for the start (midnight) of that given date.
		Calendar startDate = (Calendar)date.clone();		
		startDate.set(Calendar.HOUR, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);

		// and an end date exactly one day later
		Calendar endDate = (Calendar)startDate.clone();		
		endDate.add(Calendar.DATE, 1);

		// create dummy meetings with the start and end dates.
		Meeting startMarker = new MarkerMeetingImpl(startDate);
		Meeting endMarker = new MarkerMeetingImpl(endDate);

		return new ArrayList<Meeting>(meetingSet.subSet(startMarker, endMarker));
	}

	/**
	 * Returns the list of past meetings in which this contact has participated.
	 *
	 * If there are none, the returned list will be empty. Otherwise,
	 * the list will be chronologically sorted and will not contain any
	 * duplicates.
	 *
	 * @param contact one of the user�s contacts
	 * @return the list of future meeting(s) scheduled with this contact (maybe empty).
	 * @throws IllegalArgumentException if the contact does not exist
	 */
	@SuppressWarnings("unchecked")
	public List<PastMeeting> getPastMeetingList(Contact contact) throws IllegalArgumentException
	{
		if(contact == null ||
				contact.getId() >= contactList.size() ||
				contact.getId()  < 0)
		{
			throw new IllegalArgumentException("Contact does not exist");
		}

		now.setTime(new Date());

		// create dummy meeting for today.
		Meeting todayMarker = new MarkerMeetingImpl(now);

		Set<Meeting> pastSet = meetingSet.headSet(todayMarker);
		
		// I cannot find a way to handle this with generics.
		@SuppressWarnings("rawtypes")
		List list = listForContact(pastSet, contact);	
		return list;
	}

	/**
	 * extract meetings from the set that include contact
	 * 
	 * @param meetingSet
	 * @param contact
	 * @return a List of the matching meetings
	 * 
	 */
	private List<Meeting> listForContact(Set<Meeting> meetingSet, Contact contact)
	{
		List<Meeting> list = new ArrayList<Meeting>();
		
		for(Meeting meeting : meetingSet)
		{
			if(meeting.getContacts().contains(contact))
			{
				list.add(meeting);
			}
		}
		return list;
	}

	/**
	 * Create a new record for a meeting that took place in the past.
	 *
	 * @param contacts a list of participants
	 * @param date the date on which the meeting took place
	 * @param text messages to be added about the meeting.
	 * @throws IllegalArgumentException if the list of contacts is
	 * empty, or any of the contacts does not exist
	 * @throws NullPointerException if any of the arguments is null
	 */
	public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) throws NullPointerException
	{		
		now.setTime(new Date());

		if(date.after(now))
		{
			throw new IllegalArgumentException("date should not be in the future");
		}

		for(Contact contact : contacts)
		{
			try
			{
				contactList.get(contact.getId());
			}
			catch(IndexOutOfBoundsException iobe)
			{
				throw new IllegalArgumentException("contact id " + contact.getId() + " is unknown");
			}
		}
		
		MeetingImpl pm = new PastMeetingImpl(contacts, date);
		
		((PastMeetingImpl)pm).addNotes(text);
		
		meetingList.add(pm);

		meetingSet.add(pm);

		for(Contact c : contacts)
		{
			((ContactImpl)c).addMeeting(pm);
		}
	}

	/**
	 * Add notes to a meeting.
	 *
	 * This method is used when a future meeting takes place, and is
	 * then converted to a past meeting (with notes).
	 *
	 * It can be also used to add notes to a past meeting at a later date.
	 *
	 * @param id the ID of the meeting
	 * @param text messages to be added about the meeting.
	 * @throws IllegalArgumentException if the meeting does not exist
	 * @throws IllegalStateException if the meeting is set for a date in the future
	 * @throws NullPointerException if the notes are null
	 */
	public void addMeetingNotes(int id, String text)  throws IllegalArgumentException, IllegalStateException, NullPointerException
	{
		if(text == null)
			throw new NullPointerException("notes supplied are null");
		Meeting fm = getMeeting(id);
		if(fm == null)
			throw new IllegalArgumentException("there is no meeting with this ID");
		
		now.setTime(new Date());
		if(now.before(fm.getDate()))
			throw new IllegalStateException("can't add notes to a meeting in the future");
		if(fm instanceof FutureMeeting)
		{
			PastMeetingImpl pm = new PastMeetingImpl((FutureMeetingImpl)fm, text, id);
			
			// and add the new past meeting to the meeting list
			// with the same id, replacing the future meeting.
			meetingList.set(id, pm);
			
			// replace the future meeting with the new past meeting 
			// in the chronological sorted set
			meetingSet.remove(fm);
			meetingSet.add(pm);
		}
		else if(fm instanceof PastMeeting)
		{
			// just add the notes to the existing object
			((PastMeetingImpl)fm).addNotes(text);
		}
	}

	/**
	 * Create a new contact with the specified name and notes.
	 *
	 * @param name the name of the contact.
	 * @param notes notes to be added about the contact.
	 * @throws NullPointerException if the name or the notes are null
	 */
	public void addNewContact(String name, String notes) throws NullPointerException
	{
		if(name == null)
		{
			throw new NullPointerException("name should not be null");
		}

		if(notes == null)
		{
			throw new NullPointerException("notes should not be null");
		}
		
		Contact contact = new ContactImpl(name);
		contact.addNotes(notes);
		contactList.add(contact);
	}

	/**
	 * Returns a list containing the contacts that correspond to the IDs.
	 *
	 * @param ids an arbitrary number of contact IDs
	 * @return a list containing the contacts that correspond to the IDs.
	 * @throws IllegalArgumentException if any of the IDs does not correspond to a real contact
	 */
	public Set<Contact> getContacts(int... ids) throws IllegalArgumentException
	{
		if(ids.length == 0)
			throw new IllegalArgumentException("No contact Ids were supplied");
		
		Set<Contact> contactSet = new TreeSet<Contact>();
		
		for(int id : ids)
		{
			try
			{
				Contact contact = contactList.get(id);
				if(contact == null)
					throw new IllegalArgumentException("Contact ID " + id + " does not exist");

				contactSet.add(contact);
			}
			catch (IndexOutOfBoundsException iobe)
			{
				throw new IllegalArgumentException("Contact ID " + id + " does not exist");
			}
		}
		return contactSet;
	}

	/**
	 * Returns a list with the contacts whose name contains that string.
	 *
	 * @param name the string to search for
	 * @return a list with the contacts whose name contains that string.
	 * @throws NullPointerException if the parameter is null
	 */
	public Set<Contact> getContacts(String name) throws NullPointerException
	{
		if(name == null)
			throw new NullPointerException("name given is null");
		
		Set<Contact> contactSet = new TreeSet<Contact>();
		
		for(Contact contact : contactList)
		{
			if(contact.getName().contains(name))
			{
				contactSet.add(contact);
			}
		}
		return contactSet;
	}

	/**
	 * Save all data to disk.
	 *
	 * This method must be executed when the program is
	 * closed and when/if the user requests it.
	 */
	public void flush() 
	{
		BufferedWriter saveWriter = null;
		
		try
		{
			saveWriter = new BufferedWriter(new FileWriter(saveFile));
			
			saveWriter.write("# Automatically generated file; DO NOT EDIT\n");

			saveWriter.write("#Contacts\n");
			for(Contact contact : contactList)
			{
				saveWriter.write(((ContactImpl)contact).toCSV() + "\n");
			}

			saveWriter.write("#\n");
			saveWriter.write("#Meetings\n");
			for(Meeting meeting : meetingList)
			{
				saveWriter.write(((MeetingImpl)meeting).toCSV() + "\n");
			}
		}
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
		}
		finally
		{
			closeWriter(saveWriter);
		}
	}
	
	/**
	 * method used ONLY for unit tests, to be commented out when tested.
	 */
	public void clearForTesting()
	{
		ContactImpl.resetNextIdForTesting();
		MeetingImpl.resetNextIdForTesting();
		contactList.clear();
		meetingList.clear();
		meetingSet.clear();
	}
	
	/**
	 *  Public wrapper, ONLY for unit tests, to be commented out when tested.
	 */
	public void restoreFromFileForTesting()
	{
		restoreFromFile();
	}
}