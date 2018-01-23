/**
 * 
 */
package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.Controller;
import model.RegexSystem;

/**
 * @author mashichao
 *
 */
public class ListTabPanel extends JPanel{
	
	private JTextField input;
	private JList<String> output;
	DefaultListModel<String> model;
	private boolean isReminder; 
	private RegexSystem aRegexSystem;
	
	public ListTabPanel(RegexSystem aRegexSystem, boolean isReminder){
		//instantiate controller 
		Controller aController=new Controller(this, aRegexSystem);
		
		this.aRegexSystem = aRegexSystem;
		this.isReminder = isReminder;
		model = new DefaultListModel<String>();
		output = new JList<String>(model);
		output.setPreferredSize(new Dimension(600, 300));
		output.addMouseListener(aController);
		input = new JTextField();
		input.addActionListener(aController);
		this.setLayout(new BorderLayout());
		this.add(output, BorderLayout.CENTER);
		this.add(input, BorderLayout.SOUTH);
		
		if(isReminder){
			this.initialiseTheReminderHistoryList();
		}else{
			this.initialiseTheCalendarHistoryList();
		}
	}
	
	//Some methods used in Controller
	public String getStringFromField(){
		return input.getText();
	}
	public void addStringToArea(String s){
		model.addElement(s);
	}
	public void cleanTheField(){
		input.setText("");
	}
	//To identify this is the a calendar tab or a reminder tab
	public boolean getIsReminder(){
		return isReminder;
	}
	public int getOutputIndex(MouseEvent e){
		return output.locationToIndex(e.getPoint());
	}
	//used to delete data in JList
	public void deleteALineOfOutPut(int i){
		model.remove(i);
	}
	//used to initialise the data in JList when we reopen the progarm
	public void initialiseTheCalendarHistoryList(){
		for(int i = 0; i < aRegexSystem.getCalendarHistoryListSize(); i++){
			model.addElement(aRegexSystem.getCalendarHistoryList(i));
		}
	}
	public void initialiseTheReminderHistoryList(){
		for(int i = 0; i < aRegexSystem.getReminderHistoryListSize(); i++){
			model.addElement(aRegexSystem.getReminderHistoryList(i));
		}
	}

}
