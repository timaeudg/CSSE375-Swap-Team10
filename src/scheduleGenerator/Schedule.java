package scheduleGenerator;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Used to store predicted days and generate new days.
 * 
 * @author schneimd. Created Oct 18, 2012.
 */
public class Schedule extends Thread implements Serializable {

	private ArrayList<Worker> workers;
	private ArrayList<Day> days;
	private TreeMap<String, TreeMap<String, Worker>> schedule;
	private GregorianCalendar cal;
	private HashMap<Integer, ArrayList<Worker>> workerIndices;
	private boolean workerForEveryJob = true;
	private ArrayList<Date> holidays;

	/**
	 * Used to construct an initial schedule, used if one does not exist.
	 * 
	 * @param daySlots
	 * @param wrks
	 */
	public Schedule(ArrayList<Day> daySlots, ArrayList<Worker> wrks, ArrayList<Date> holidayList) {
		holidays = holidayList;
		this.workers = wrks;
		this.days = daySlots;
		this.workerIndices = new HashMap<Integer, ArrayList<Worker>>();
		for (int i = 1; i <= 7; i++) {
			this.workerIndices.put(i, new ArrayList<Worker>());
		}
		this.generateIndices();

		// Key is year/month/day format and item is a hashmap with key nameOfJob
		// and item Worker
		this.schedule = new TreeMap<String, TreeMap<String, Worker>>();

		this.cal = new GregorianCalendar();

		this.calculateNextMonth();
	}

	@Override
	public void run() {
		this.calculateNextMonth();
	}

	/**
	 * returns workers in schedule.
	 * 
	 * @return workers
	 */
	public ArrayList<Worker> getWorkers() {
		return this.workers;
	}

	private void generateIndices() {
		for (int i = 0; i < this.workers.size(); i++) {
			for (Day day : this.workers.get(i).getDays()) {
				int numDay = this.numForName(day.getNameOfDay());
				this.workerIndices.get(numDay).add(this.workers.get(i));
			}
		}
	}

	/**
	 * Calculates another month of schedule based on workers availability.
	 * 
	 */
	
	/* SWAP 1, TEAM 7
	 * Quality Changes
	 * The calculateNextMonth method had too many responsibilities, so we have separated it into several smaller methods
	 * The first part of the method is now in generateNextMonth. This part of the original method was not did not have any
	 * local variables accessed by the rest of the method and only updated the calendar
	 *  
	 * For each day, we assign workers to the jobs for that day. We moved this behavior from the inside of the for loop 
	 * to its own method to improve clarity. We left the counters for daysInMonth and numOfJobs in calculateNextMonth 
	 * as they did not have any impact on the rest of the behavior. 
	 * 
	 * Within assignWorkersToJobs we have to find the workers for the day's jobs. We moved the process of finding workers 
	 * to its own method. The rest of assignWorkersToJobs uses the result of this method to add that assignment to the 
	 * calendar or display that no worker was found, depending if the findWorkersForJob returns an empty array or not.
	 */
	/*
	 * SWAP 1, TEAM 7 
	 * Smell: Feature Envy
	 * Even after refactoring these methods, we still have the problem of Schedule accessing basically everything in worker 
	 * and day. We could correct this by moving some of the functionality in findWorkersForJob to the worker class. This would 
	 * make the Schedule class clearer and allow us to more easily add additional worker restrictions or preferences later on.
	 */
	/*
	 * SWAP 1, TEAM 7 
	 * Smell: Divergent Change
	 * Along with the feature envy listed above, Schedule contains all of the logic about workers' preferences, special calendar
	 * conditions, and available workers and days. We would need to alter the scheduling methods if any of this were to change. 
	 * To mitigate this effect, we should move some of the logic to other classes, such as moving worker preferences to the 
	 * worker class.
	 */
	/*
	 * SWAP2 Team 8
	   // REFACTORING FOR ENHANCEMENT FROM BAD SMELL
	 * Used Move Method
	 * Fixed the feature envy
	 * In an attempt to further separate the responsibility of worker and schedule
	 * we moved more of the find worker method into the worker class but in order to fully
	 * remove this cross-of-resposibilties between these two class an extensive structure change will
	 * be needed. But otherwise the refactor was moderatly successful. More of such refactoring would hpefully make the
	 * modification of the worker and how they function a simpler task. Such as giving one worker priority for certain tasks or shifts.
	 */
	
	private ArrayList<Worker> findWorkersForJob(String job,String dayName, ArrayList<String> workersWorking)
	{
		ArrayList<Worker> workersForJob = new ArrayList<Worker>();
		
		int currDate = cal.get(Calendar.DAY_OF_MONTH);
		int currMonth = cal.get(Calendar.MONTH) + 1;
		
		for (Worker worker : this.workerIndices.get(this.numForName(dayName))) {
			if (worker.isWorkerAvailable(job, dayName, workersWorking, currDate, currMonth)) {
				workersForJob.add(worker);
			}
		}
		return workersForJob;

	}

	
	private void generateNextMonth() 	{
		// If the schedule has already been generated
		
		String lastDateMade = this.schedule.lastKey();
		String[] parts = lastDateMade.split("/");
		int year = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1]) - 1;
		int day = Integer.parseInt(parts[2]);
		this.cal = new GregorianCalendar(year, month, day);
		int tempNum = this.cal.get(Calendar.MONTH);
		while (tempNum == this.cal.get(Calendar.MONTH)) {
			this.cal.add(Calendar.DATE, 1);
		}
	}
	
	// SWAP 1, TEAM 7
	// BONUS FEATURE
	// We added the functionality that each person will be scheduled at least
	// once before repeating a person if possible. We did this by prioritizing the
	// person that has worked the least when picking a worker for a day. To accomplish
	// this we first extracted from the long method assignWorkersToJobs, creating a method
	// to contain the logic for selecting a worker from a list of all workers. We then added
	// our logic to the method.
	
	private Worker assignWorkerToJob(ArrayList<Worker> workersForJob, String job) {
		Worker workerForJob = workersForJob.get(new Random().nextInt(workersForJob.size()));
		for (Worker w : workersForJob) {
			if (w.numWorkedForJob(job) < workerForJob.numWorkedForJob(job)) {
				workerForJob = w;
			}
		}
		for (Worker w : workersForJob) {
			if(w.getTotalTimeWorked() < workerForJob.getTotalTimeWorked()) {
				workerForJob = w;
			}
		}
		return workerForJob;
	}
	
	private void assignWorkersToJobs(Day day, ArrayList<String> jobsInOrder){
		/*	SWAP 1, Team 7
		 *  Additional Feature
		 *  Our additional feature is holidays to be selected, where no jobs are performed
		 *  and no workers are assigned to those jobs. In order to accomplish this, we first 
		 *  have to change assignWorkersToJobs so that it only assigns workers if the given
		 *  day is not a holiday. We also had to overcome the Middle Man of the Main and 
		 *  WorkerSetup classes containing the list of days. After refactoring we adding the 
		 *  functionality to input holidays and create schedules without those holidays.
		 *  
		 *  
		 */
		int currDate = cal.get(Calendar.DAY_OF_MONTH);
		int currMonth = cal.get(Calendar.MONTH) + 1;
		System.out.println("Info " + currMonth + "/" + currDate);
		
		for(Date d: Main.getHolidayDates()) {
			if(d.getDate() == currDate && d.getMonth() == currMonth)
				return;
		}
		
		
		// Used for html later

		TreeMap<String, Worker> jobsWithWorker = new TreeMap<String, Worker>();
		ArrayList<String> workersWorking = new ArrayList<String>();

		for (String job : jobsInOrder) {
			String dayName = day.getNameOfDay();
			ArrayList<Worker> workersForJob = findWorkersForJob(job,dayName,workersWorking);
			
			if (workersForJob.size() > 0) {
				// Extract
				Worker workerForJob = assignWorkerToJob(workersForJob, job);
				jobsWithWorker.put(job, workerForJob);
				workersWorking.add(workerForJob.getName());
				workerForJob.addWorkedJob(job);
			} else {
				jobsWithWorker.put(job, new Worker("Empty",
						new ArrayList<Day>()));
				JOptionPane
						.showMessageDialog(
								new JFrame(),
								"No workers are able to work as a(n) "
										+ job + " on "
										+ dayName);
				this.workerForEveryJob = false;
				break;
			}
		}
		String date = this.cal.get(Calendar.YEAR)
				+ "/"
				+ String.format("%02d",
						(this.cal.get(Calendar.MONTH) + 1))
				+ "/"
				+ String.format("%02d",
						this.cal.get(Calendar.DAY_OF_MONTH));
		this.schedule.put(date, jobsWithWorker);
	}	
	
	
	private synchronized void calculateNextMonth() {
		int initialSize = this.schedule.size();
		if (this.schedule.size() > 0) {			generateNextMonth();
		}	
		
		// Used to see if month changes
		int currentMonth = this.cal.get(Calendar.MONTH);

		int daysInMonth = 0;
		ArrayList<Integer> numOfJobs = new ArrayList<Integer>();

		// While still in the current month generate a schedule for each day
		while (currentMonth == this.cal.get(Calendar.MONTH)) {
			
			for (Day day : this.days) {
				if (this.cal.get(Calendar.DAY_OF_WEEK) == this.numForName(day.getNameOfDay())) {	
					daysInMonth++;
					ArrayList<String> jobsInOrder = day.getJobs();
					numOfJobs.add(jobsInOrder.size());
					assignWorkersToJobs(day,jobsInOrder);
					break; // Breaks so it doesn't check the other days
				}
			}
			this.cal.add(Calendar.DATE, 1);
		}
		HTMLGenerator.makeTable(daysInMonth, numOfJobs);
		// Calls itself if there aren't many days generated
		// For instance if the date it was created is the last day of the
		// month it would only makes one day of schedule.
		if (this.schedule.size() - initialSize < 2 && !this.workerForEveryJob) {
			this.calculateNextMonth();
		}

		Main.dumpConfigFile();
	}
	/* 	SWAP 1, TEAM 7
	 * 	Smell: Shotgun Surgery
	 * 	This conversion between string and int description of days is used several times throughout 
	 *  the system. We should move this behavior to the Day class in order to have it in one place.
	 *  This would make some additional features, such as multiple language support, easier to implement
	 *  as we would not need to edit the conversions in many different classes. 
	 *  
	 *  SWAP 2, TEAM 8
	 * // REFACTORING FOR ENHANCEMENT FROM BAD SMELL
	 * To keep consistent with other changes made in the system i have generalized this conversion of a String nameOfDay to be compatible with any calendar type
	 * We can now use any type of calendar and they system will still function as intended essentailly internationalizing the system. 
	 *  This refactoring was fairly simple so it was pretty successful
	 */
	private int numForName(String nameOfDay) {
		
		String[] days=new DateFormatSymbols().getWeekdays();
		int dayNum = 0;
		for (String day: days){
			if (day.equals(nameOfDay)){
				return dayNum;
			}else{
				dayNum++;
			}
		}
		return 0;
		
		
//		if (nameOfDay.equals("Sunday")) {
//			dayNum = 1;
//		} else if (nameOfDay.equals("Monday")) {
//			dayNum = 2;
//		} else if (nameOfDay.equals("Tuesday")) {
//			dayNum = 3;
//		} else if (nameOfDay.equals("Wednesday")) {
//			dayNum = 4;
//		} else if (nameOfDay.equals("Thursday")) {
//			dayNum = 5;
//		} else if (nameOfDay.equals("Friday")) {
//			dayNum = 6;
//		} else if (nameOfDay.equals("Saturday")) {
//			dayNum = 7;
//		}
//		return dayNum;
	}

	// /**
	// * Returns the month/day/year of next date with the name of day.
	// *
	// * @param nameOfDay
	// * @return string of year/month/day format
	// */
	// private String getNextDate(String nameOfDay) {
	// int dayNum = numForName(nameOfDay);
	// GregorianCalendar tempCal = (GregorianCalendar) this.cal.clone();
	//
	// tempCal.add(Calendar.DATE, 1);
	// while (tempCal.get(Calendar.DAY_OF_WEEK) != dayNum) {
	// tempCal.add(Calendar.DATE, 1);
	// }
	// return String.valueOf(tempCal.get(Calendar.YEAR)) + "/" +
	// String.valueOf(tempCal.get(Calendar.MONTH)) + "/"
	// + String.valueOf(tempCal.get(Calendar.DAY_OF_MONTH));
	// }

	/**
	 * Returns the schedule.
	 * 
	 * @return HashMap schedule
	 */
	public TreeMap<String, TreeMap<String, Worker>> getSchedule() {
		return this.schedule;
	}

}
