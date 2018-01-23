/**
 * 
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import model.RegexSystem;
import view.ListTabPanel;

/**
 * @author mashichao
 *
 */
public class Controller implements ActionListener, MouseListener{
	//initialise a TabPanel and a CanlendarTransformer
	private ListTabPanel aListTabPanel;
	private RegexSystem aRegexTransformer;
	//The constructor
	public Controller(ListTabPanel aListTabPanel, RegexSystem aReminderTransformer){
		this.aListTabPanel = aListTabPanel;
		this.aRegexTransformer = aReminderTransformer;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		aRegexTransformer.setInput(aListTabPanel.getStringFromField());
		if(aListTabPanel.getIsReminder()){
			aListTabPanel.addStringToArea(aRegexTransformer.getOutputForReminder());
		}else{
			aListTabPanel.addStringToArea(aRegexTransformer.getOutputForCalendar());
		}
		aListTabPanel.cleanTheField();
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			//To renew the local history
			if(aListTabPanel.getIsReminder()){
				aRegexTransformer.deleteParticularLineInReminderHistory(aListTabPanel.getOutputIndex(e));
				aRegexTransformer.renewTheReminderHistory();
			}else{
				aRegexTransformer.deleteParticularLineInCalendarHistoryList(aListTabPanel.getOutputIndex(e));
				aRegexTransformer.renewTheCalendarHistory();
			}
			aListTabPanel.deleteALineOfOutPut(aListTabPanel.getOutputIndex(e));
        }
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
