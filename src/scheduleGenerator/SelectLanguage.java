package scheduleGenerator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SelectLanguage extends JFrame{

	//SWAP 3 Team 10
	// ENHANCEMENT FROM REFACTORING
	// This is the dialog that allows the user to select the language.  Right now it just has hard code for US english and Spanish from Spain.  
	// If you wanted to add more languages, you would probably want to add a new resource file and import these that way
	public SelectLanguage() {
		JPanel panel = new JPanel();
		JButton english = new JButton();
		english.setText("English");
		english.addActionListener(new LanguageSelector("en", "US"));
		
		JButton spanish = new JButton();
		spanish.setText("Español");
		spanish.addActionListener(new LanguageSelector("es", "ES"));
		
		panel.add(spanish);
		panel.add(english);
		this.add(panel);
		this.pack();
	}
	
	private class LanguageSelector implements ActionListener {
		private String language;
		private String country;
		public LanguageSelector(String language, String country) {
			this.language = language;
			this.country = country;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Main.setLocale(new Locale(this.language, this.country));
			setVisible(false);
			Main.run();
		}
		
	}
}
