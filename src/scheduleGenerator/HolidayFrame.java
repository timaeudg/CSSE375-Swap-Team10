package scheduleGenerator;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/***
 * GUI element for editing "holidays" or days off that should not
 * show up on the schedule and should not have people assigned to them.
 * @author nezumi
 *
 */
public class HolidayFrame extends JFrame {
	private static final long serialVersionUID = 6784775312617591784L;

	private JPanel topPanel;
	private JPanel bottomPanel;
	private JTextField monthInput = new JTextField();
	private JTextField dayInput = new JTextField();
	private JButton addDate = new JButton("Add Date");
	private JButton removeDate = new JButton("Remove Date");
	private JButton doneButton = new JButton("Done");
	private DefaultListModel listModel;
	private JList datesList;
	
	private ArrayList<Date> dates;
	
	
	public HolidayFrame(ArrayList<Date> dates) {
		super();
		
		this.dates = new ArrayList<Date>();
		
		this.topPanel = new JPanel();
		this.bottomPanel = new JPanel();
		
		this.getContentPane().setLayout(new BorderLayout());
		this.topPanel.setLayout(new BoxLayout(this.topPanel, BoxLayout.Y_AXIS));
		this.bottomPanel.setLayout(new BoxLayout(this.bottomPanel, BoxLayout.X_AXIS));
		
		JLabel dayLabel = new JLabel("Day: ");
		JLabel monthLabel = new JLabel("Month: ");
		JLabel directions = new JLabel("Add days that people should not work to the below list.");
		JLabel directions2 = new JLabel("Enter the month and day as their numerical representations.");
		
		this.listModel = new DefaultListModel();
		addDates(dates);
		this.datesList = new JList(listModel);
		
		this.datesList.setPreferredSize(new Dimension(datesList.getWidth(), 400));
		
		directions.setAlignmentX(CENTER_ALIGNMENT);
		directions2.setAlignmentX(CENTER_ALIGNMENT);
		doneButton.setAlignmentX(CENTER_ALIGNMENT);
		
		addActionListeners();
		
		this.topPanel.add(directions);
		this.topPanel.add(directions2);
		this.topPanel.add(doneButton);
		this.add(datesList, BorderLayout.CENTER);
		this.bottomPanel.add(monthLabel);
		this.bottomPanel.add(monthInput);
		this.bottomPanel.add(dayLabel);
		this.bottomPanel.add(dayInput);
		this.bottomPanel.add(addDate);
		this.bottomPanel.add(removeDate);
		
		this.removeDate.setEnabled(false);
		
		this.add(this.topPanel, BorderLayout.NORTH);
		this.add(this.bottomPanel, BorderLayout.SOUTH);
		
		this.pack();
		this.setVisible(true);
	}
	
	public HolidayFrame(){
		this(new ArrayList());
	}
	
	private void addActionListeners() {
		this.addDate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					int month = Integer.parseInt(monthInput.getText());
					int day = Integer.parseInt(dayInput.getText());
					if(month < 0 || month > 12 || day < 0 || day > 31) {
						throw new Exception("Out of bounds");
					}
					addDate(month, day);
				} catch (Exception e) { e.printStackTrace(); }
				monthInput.setText("");
				dayInput.setText("");
			}
		});
		
		this.removeDate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				listModel.remove(datesList.getSelectedIndex());
				if(listModel.size() <= 0) {
					removeDate.setEnabled(false);
				}
			}
		});
		
		this.doneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Main.setHolidayDates(dates);
				setVisible(false);
			}
		});
		
	}
	
	private void addDate(int month, int day) {
		listModel.addElement(month + "/" + day);
		dates.add(new Date(2014, month, day));
		if(listModel.size() > 0) {
			removeDate.setEnabled(true);
		}
	}
	
	private void addDates(ArrayList<Date> dates) {
		for(Date d : dates) {
			addDate(d.getMonth(), d.getDate());
		}
	}
	
	public ArrayList<Date> getDates(){
		return this.dates;
	}
	
	
	
}
