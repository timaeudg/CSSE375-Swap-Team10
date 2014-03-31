package scheduleGenerator;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;

public class WeekdayCheckBox extends JCheckBox {
	
	private Config parent;
	private final int weekdayIndex;
	private final String weekdayName;
	
	public WeekdayCheckBox(Config parent, String weekdayName, int weekdayIndex) {
		this.parent = parent;
		this.weekdayName = weekdayName;
		this.weekdayIndex = weekdayIndex;
		
		this.setText(weekdayName);
		this.setName(weekdayName.toLowerCase() + "Check"); // NOI18N
	}
	
	protected ArrayList<Day> nextButtonActionPerformed(ActionEvent e) {
		ArrayList<Day> days = new ArrayList<Day>();
    	if(this.isSelected())
        {
    		ArrayList<Object> list = new ArrayList<Object>();
    		List<Object> jobs = Arrays.asList(parent.models[weekdayIndex].toArray());
    		list.addAll(jobs);
        	days.add(new Day(weekdayName, list));
        }
    	return days;
	}
	
}
