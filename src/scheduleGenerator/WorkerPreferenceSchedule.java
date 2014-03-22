package scheduleGenerator;

import java.util.ArrayList;

public class WorkerPreferenceSchedule extends AbstractSchedule {

	public WorkerPreferenceSchedule(ArrayList<Day> daySlots, ArrayList<Worker> wrks) {
		this.workers = wrks;
		this.days = daySlots;
	}

	@Override
	protected void calculateNextMonth() {
		// TODO Auto-generated method stub
		
	}

}
