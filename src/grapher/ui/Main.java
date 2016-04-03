package grapher.ui;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

public class Main extends JFrame {
	Main(String title, String[] expressions) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//Graph
		Grapher grapher = new Grapher();		
		for(String expression : expressions) {
			grapher.add(expression);
		}
		
		//Menu
		Menu dataList = new Menu(expressions, grapher);
		
		//SplitPane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,dataList,grapher);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(100);
		
		this.add(splitPane);
		
		pack();
	}

	public static void main(String[] argv) {
		final String[] expressions = argv;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				new Main("grapher", expressions).setVisible(true); 
			}
		});
	}
}
