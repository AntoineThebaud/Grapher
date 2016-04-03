package grapher.ui;

import java.util.List;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Menu extends JList implements ListSelectionListener {

	Grapher grapher;
	
	public Menu(String[] expressions, Grapher grapher) {
		super(expressions);
		this.grapher = grapher;
		this.addListSelectionListener(this);
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) { 
            final List<String> selectedValuesList = this.getSelectedValuesList(); 
            grapher.activeFunctions(selectedValuesList);
            grapher.repaint();
        } 
	}
}
