package view;


import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import model.RegexSystem;

public class MainWindow extends JFrame{
	
	private ListTabPanel calendarPanel;
	private ListTabPanel remindersPanel;
	
    //The constructor
	public MainWindow(RegexSystem aRegexTransformer) {
		//Instantiate the tabbedpane and panels
        JTabbedPane tabbedPane = new JTabbedPane();
        calendarPanel = new ListTabPanel(aRegexTransformer, false);
        remindersPanel = new ListTabPanel(aRegexTransformer, true);
        //Add panels to the tabbedpane
        tabbedPane.addTab("Calendar", calendarPanel);
        tabbedPane.addTab("Reminders", remindersPanel);
        //Add the tabbedpane to the window
        add(tabbedPane);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
