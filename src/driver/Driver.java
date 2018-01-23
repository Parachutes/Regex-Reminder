package driver;

import model.RegexSystem;
import view.MainWindow;

public class Driver {
	public static void main(String[] args) {
		RegexSystem aRegexTransformer = new RegexSystem();
		new MainWindow(aRegexTransformer);
    }
}
