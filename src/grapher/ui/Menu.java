package grapher.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Menu extends JPanel implements ListSelectionListener, ActionListener {
	
	static final String PLUS = " + ";
	static final String MOINS = " -  ";

	Grapher grapher;
	JList list;
	JToolBar toolbar;
	
	public Menu(String[] expressions, Grapher grapher) {
		this.grapher = grapher;
		
		//Composant 1 : Liste
		
		this.list = new JList(expressions);
		this.list.addListSelectionListener(this);
		this.list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		//Composant 2 : Boutons
		
		this.toolbar = new JToolBar();

	    JButton buttonPlus = new JButton(PLUS);
	    buttonPlus.setHorizontalAlignment(SwingConstants.CENTER);
	    buttonPlus.addActionListener(this);
	    
	    JButton buttonMin = new JButton(MOINS);
	    buttonMin.setHorizontalAlignment(SwingConstants.CENTER);
	    buttonMin.addActionListener(this);
	    
	    this.toolbar.add(buttonPlus);
	    this.toolbar.add(buttonMin);
	    
	    //Composant final : 1 + 2
	    
	    this.setLayout(new BorderLayout());
	    this.add(list, BorderLayout.CENTER);
	    this.add(toolbar, BorderLayout.SOUTH);
	}

	@Override //List event
	public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) { 
            final List<String> selectedValuesList = list.getSelectedValuesList(); 
            grapher.activeFunctions(selectedValuesList);
            grapher.repaint();
        } 
	}

	@Override //Button event
	public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        switch (e.getActionCommand()) {
	        case PLUS:
	        	String s = JOptionPane.showInputDialog(this,"Nouvelle expression");
	        	System.out.println(s);
	        	//TODO : générer fonction à partir de la saisie
	        	break;
	        case MOINS:
	        	System.out.println("Je suis un moins.. gitanerie");
	        	//TODO : Suppresion des fonctions sélectionnées
	        	break;
	        default:
	        	assert(false);
        }
	}
}
