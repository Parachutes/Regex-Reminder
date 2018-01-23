/**
 * 
 */
package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Observable;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mashichao
 *
 */
public class RegexSystem extends Observable{

	private String inPut;
	private String outPut;
	private TreeMap<String, Integer> daysInAWeek;
	
	private Pattern patternForReminder;
	private Matcher matcherForReminder;
	//Used in date
	private Pattern patternForDateFormat1;
	private Pattern patternForDateFormat2;
	private Pattern patternForDateFormat3;
	private Matcher matcherForDate;
	//Used in time
	private Pattern patternForTimeFormat1;
	private Pattern patternForTimeFormat2;
	private Pattern patternForTimeFormat3;
	private Matcher matcherForTime;
	//Used in location
	private Pattern patternForLocation;
	private Matcher matcherForLocation;
	///<<<<<<<<<<<<<<<<<<
	//File Writer
	private FileWriter fwReminder;
	private BufferedWriter bwReminder;
	private FileWriter fwCalendar;
	private BufferedWriter bwCalendar;
	///<<<<<<<<<<<<<<<<<<
	//File Reader
	private FileReader frReminder;
	private BufferedReader brReminder;
	private FileReader frCalendar;
	private BufferedReader brCalendar;
	///<<<<<<<<<<<<<<<<<<
	private ArrayList<String> calendarHistoryList;
	private ArrayList<String> reminderHistoryList;
	//Constructor
	public RegexSystem(){
		///<<<<<<<<<<<<<<<<<< instantiate filewriter and bufferedwriter to record history in the local file
		try{
			fwReminder = new FileWriter("src/model/DataOfReminder.txt", true);
			fwCalendar = new FileWriter("src/model/DataOfCalendar.txt", true);
		}catch(IOException e) {
			e.printStackTrace();
		}
		bwReminder = new BufferedWriter(fwReminder);
		bwCalendar = new BufferedWriter(fwCalendar);
		///<<<<<<<<<<<<<<<<<<
		try{
			frReminder = new FileReader("src/model/DataOfReminder.txt");
			frCalendar = new FileReader("src/model/DataOfCalendar.txt");
		}catch(IOException e) {
			e.printStackTrace();
		}
		brReminder = new BufferedReader(frReminder);
		brCalendar = new BufferedReader(frCalendar);
		///<<<<<<<<<<<<<<<<<<
		calendarHistoryList = new ArrayList<String>();
		reminderHistoryList = new ArrayList<String>();
		daysInAWeek = new TreeMap<String, Integer>();
		initialiseDaysOfAWeek();
		this.retrieveCalendarHistoryToList();
		this.retrieveReminderHistoryToList();
	}
	public void setInput(String input){
		this.inPut = input;
	}	
	/**
	 * the regex part for reminder
	 * ===============================================================================================================================
	 */
	public String getOutputForReminder(){
		patternForReminder = Pattern.compile("(R|r)(emind me )(to|of|about )(.*)");
		matcherForReminder = patternForReminder.matcher(inPut);
		//initialise the output so when input is unqualified the output will be the same as input
		outPut = inPut;
		if(matcherForReminder.find()){
			inPut = inPut.replaceAll("(R|r)(emind me )(to|of|about )", "");
			//I'm using the same methods I used in calendar part
			String reminderTime = this.calendarTime();
			if(reminderTime.equals("-")){
				reminderTime = "";
			}else{
				reminderTime = " |Time: "+reminderTime;
			}
			String reminderDate = this.calendarDate();
			if(reminderDate.equals("-")){
				reminderDate = "";
			}else{
				reminderDate = " |Date: "+reminderDate;
			}
			outPut = inPut.trim()+reminderTime+reminderDate;
			
		}
		///<<<<<<<<<<<<<<<<<< record history
		try {
			bwReminder.write(outPut+"\n");
			bwReminder.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		///<<<<<<<<<<<<<<<<<<
		reminderHistoryList.add(outPut);		
		return outPut;
	}
	/**
	 * the regex part for calendar
	 * 
	 * ===============================================================================================================================
	 */
	
	public String getOutputForCalendar(){
		String date = this.calendarDate();
		String time = this.calendarTime();
		String location = this.calendarLocation();
		String event = this.calendarEvent();
		outPut = "Event: "+event+"|Date: "+date+"|Time: "+time+ "|Location: "+location;
		
		///<<<<<<<<<<<<<<<<<< record history
		try {
			bwCalendar.write(outPut+"\n");
			bwCalendar.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		///<<<<<<<<<<<<<<<<<<
		
		calendarHistoryList.add(outPut);
		
		return outPut;
	}
	/**
	 * the regex part to determine the event of the input
	 * ===============================================================================================================================
	 * this part can be obtained through deleting all other parts in input
	 */
	private String calendarEvent(){
		//remove the space at the beginning and the end
		inPut = inPut.trim(); 
		//Other complements have been deleted after they were matched, so all we need to do is to return inPut
		//if there is noting in inPut, this component will return "-"
		if(inPut.matches("\\s*")){
			return "-";
		}else{
			return inPut;
		}	
	}
	/**
	 * the regex part to determine the date of the input
	 * ===============================================================================================================================
	 */
	private String calendarDate(){
		//First of all, determine the type and format of input information(format 1)
		patternForDateFormat1 = Pattern.compile("(on )?(\\d\\d)/(\\d\\d)/(\\d\\d\\d\\d)");
		matcherForDate = patternForDateFormat1.matcher(inPut);
		if(matcherForDate.find()){
			///&//used in event<<<
			inPut = inPut.replaceAll("(on )?(\\d\\d)/(\\d\\d)/(\\d\\d\\d\\d)", "");
			return inputContainsNumbers();
		}
		
		//Secondly, check whether input contains month and date(format 2)
		Set<String> keys = daysInAWeek.keySet();
		for(String key: keys){
			for(int i = 0; i < 12; i++){
				patternForDateFormat2 = Pattern.compile("(on )?"+(key)+"( \\d{1,2})(\\D\\D) "+getTheStringMonth(i));
				matcherForDate = patternForDateFormat2.matcher(inPut);
				if(matcherForDate.find()){
					///&//used in event<<<
					inPut = inPut.replaceAll("(on )?"+(key)+"( \\d{1,2})(\\D\\D) "+getTheStringMonth(i), "");
					return key+matcherForDate.group(2)+matcherForDate.group(3)+" "+getTheStringMonth(i);
				}
			}
		}
		//Thirdly, if input only has the day of a week(format 3)
		return inputOnlyContainsDay();
	}
	public String inputContainsNumbers(){
		Calendar calendar = Calendar.getInstance(Locale.UK);
		calendar.set(Integer.parseInt(matcherForDate.group(4)), Integer.parseInt(matcherForDate.group(3))-1, Integer.parseInt(matcherForDate.group(2)));
		Set<String> keys = daysInAWeek.keySet();
		String theDayOfWeek="";
		for(String key: keys){
			if(daysInAWeek.get(key)==calendar.get(calendar.DAY_OF_WEEK)){
				theDayOfWeek = key;
			}
		}
		return theDayOfWeek+" "+matcherForDate.group(2)+setSuffix(Integer.parseInt(matcherForDate.group(2)))+getTheStringMonth(Integer.parseInt(matcherForDate.group(3))-1);
	}
	public String inputOnlyContainsDay(){
		Calendar calendar = Calendar.getInstance(Locale.UK); 
		Set<String> keys = daysInAWeek.keySet();
		for(String key: keys){
			patternForDateFormat3 = Pattern.compile("((O|o)n )?((N|n)ext )?"+(key));
			matcherForDate = patternForDateFormat3.matcher(inPut);
			if(matcherForDate.find()){
				int weekOfYear = calendar.get(calendar.WEEK_OF_YEAR);
				if(daysInAWeek.get(key)<=calendar.get(calendar.DAY_OF_WEEK) && daysInAWeek.get(key)!=1){
					if(matcherForDate.group(3)!=null){
						calendar.setWeekDate(calendar.getWeekYear(), weekOfYear+2, daysInAWeek.get(key));
					}else{
						calendar.setWeekDate(calendar.getWeekYear(), weekOfYear+1, daysInAWeek.get(key));
					}
				}else if(daysInAWeek.get(key)==calendar.get(calendar.DAY_OF_WEEK)){
					if(matcherForDate.group(3)!=null){
						calendar.setWeekDate(calendar.getWeekYear(), weekOfYear+2, daysInAWeek.get(key));
					}else{
						calendar.setWeekDate(calendar.getWeekYear(), weekOfYear+1, daysInAWeek.get(key));
					}
				}else{
					if(matcherForDate.group(3)!=null){
						calendar.setWeekDate(calendar.getWeekYear(), weekOfYear+1, daysInAWeek.get(key));
					}else{
						calendar.setWeekDate(calendar.getWeekYear(), weekOfYear, daysInAWeek.get(key));
					}
				}
				int i = calendar.get(calendar.DAY_OF_MONTH);
				///&//used in event<<<
				inPut = inPut.replaceAll("((O|o)n )?((N|n)ext )?"+(key), "");
				return key+" "+i+setSuffix(i)+getTheStringMonth(calendar.MONTH);
			}
		}		
		return "-";
	}
	//The tree map of days in a week <<<< inputOnlyContainsDay()
	private void initialiseDaysOfAWeek(){
		daysInAWeek.put("Monday", 2);
		daysInAWeek.put("Tuesday", 3);
		daysInAWeek.put("Wednesday", 4);
		daysInAWeek.put("Thursday", 5);
		daysInAWeek.put("Friday", 6);
		daysInAWeek.put("Saturday", 7);
		daysInAWeek.put("Sunday", 1);
	}
	//Set suffix for a date <<<< inputOnlyContainsDay() and inputContainsNumber()
	public String setSuffix(int i){
		if(i == 1 || i == 11 || i==21 || i == 31){
        	return "st ";
        }else if(i == 2 || i == 12 || i==22){
        	return "nd ";
        }else if(i == 3 || i == 13 || i==23){
        	return "rd ";
        }
		return "th ";
	}
	//To get the month(String) from the month(Integer) <<<< inputOnlyContainsDay() and inputContainsNumber()
		private String getTheStringMonth(int i){
			DateFormatSymbols dfs = new DateFormatSymbols();
	        String[] months = dfs.getMonths();
	        return months[i];
		}
	/**
	 * the regex part to determine the time of the input
	 * ===============================================================================================================================
	 */
	private String calendarTime(){
		//Time format 1
		patternForTimeFormat1 = Pattern.compile("(at )?([0-1]?[0-9]|2[0-3]):([0-5][0-9])");
		matcherForTime = patternForTimeFormat1.matcher(inPut);
		if(matcherForTime.find()){
			///&//used in event<<<
	        inPut = inPut.replaceAll("(at )?([0-1]?[0-9]|2[0-3]):([0-5][0-9])", "");
			return matcherForTime.group(2)+":"+matcherForTime.group(3);
		}
		//Time format 2
		patternForTimeFormat2 = Pattern.compile("(at )?(\\d{1,2})(pm|am)");
		matcherForTime = patternForTimeFormat2.matcher(inPut);
		if(matcherForTime.find()){
			if(matcherForTime.group(3).equals("am")){
				///&//used in event<<<
		        inPut = inPut.replaceAll("(at )?(\\d{1,2})(pm|am)", "");
				return matcherForTime.group(2)+":00";
			}else{
				///&//used in event<<<
		        inPut = inPut.replaceAll("(at )?(\\d{1,2})(pm|am)", "");
				return (Integer.parseInt(matcherForTime.group(2))+12)+":00";
			}
		}
		//Time format 3
		patternForTimeFormat3 = Pattern.compile("((I|i)n the)?\\s?(((M|m)orning)|((E|e)vening))");
		matcherForTime = patternForTimeFormat3.matcher(inPut);
		if(matcherForTime.find()){
			///&//used in event<<<
	        inPut = inPut.replaceAll("((I|i)n the)?\\s?(((M|m)orning)|((E|e)vening))", "");
			if(matcherForTime.group(3).matches("(M|m)orning")){
				return "09:00";
			}else{
				return "20:00";
			}
		}

		return "-";
	}
	/**
	 * the regex part to determine the location of the input
	 * ===============================================================================================================================
	 */
	private String calendarLocation(){
		patternForLocation = Pattern.compile("(at )(\\S*)(\\s)?");
		matcherForLocation = patternForLocation.matcher(inPut); 
		if(matcherForLocation.find()){
			///&//used in event<<<
	        inPut = inPut.replaceAll("(at )(\\S*)(\\s)?", "");
			return matcherForLocation.group(2);
		}
		return "-";
	}
	
	/**
	 * the end of regex part
	 * =================================================================================================================================
	 */
	
	/**
	 * Read data from local file
	 * used to retrieve data when we reopen the program
	 */
	//For calendar
	public void retrieveCalendarHistoryToList(){
		try {
			String line = brCalendar.readLine();
			while(line!=null){
				calendarHistoryList.add(line);
				line = brCalendar.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public int getCalendarHistoryListSize(){
		return calendarHistoryList.size();
	}
	public String getCalendarHistoryList(int i){
		return calendarHistoryList.get(i);
	}
	//For reminder
	public void retrieveReminderHistoryToList(){
		try {
			String line = brReminder.readLine();
			while(line!=null){
				reminderHistoryList.add(line);
				line = brReminder.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public int getReminderHistoryListSize(){
		return reminderHistoryList.size();
	}
	public String getReminderHistoryList(int i){
		return reminderHistoryList.get(i);
	}
	
	
	/**
	 * Used to delete selected line when double click it
	 */
	//For Calendar
	public void deleteParticularLineInCalendarHistoryList(int i){
		calendarHistoryList.remove(i);
	}
	public void renewTheCalendarHistory(){
		try {
			bwCalendar = new BufferedWriter(new FileWriter("src/model/DataOfCalendar.txt", false));
			bwCalendar.write("");
			bwCalendar.flush();
			bwCalendar = new BufferedWriter(fwCalendar);
			for(String str: calendarHistoryList){
				bwCalendar.write(str+"\n");
				bwCalendar.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//For Reminder
	public void deleteParticularLineInReminderHistory(int i){
		reminderHistoryList.remove(i);
	}
	public void renewTheReminderHistory(){
		try {
			bwReminder = new BufferedWriter(new FileWriter("src/model/DataOfReminder.txt", false));
			bwReminder.write("");
			bwReminder.flush();
			bwReminder = new BufferedWriter(fwReminder);
			for(String str: reminderHistoryList){
				bwReminder.write(str+"\n");
				bwReminder.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}