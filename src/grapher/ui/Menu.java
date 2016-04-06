package grapher.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class Menu extends JPanel implements ActionListener {
	
	static final String PLUS = " + ";
	static final String MOINS = " -  ";

	Grapher grapher;
	//JList<String> list;
	JTable table;
	JToolBar toolbar;
	
	public Menu(String[] expressions, Grapher grapher) {
		this.grapher = grapher;
		
		//Composant 1 : Table
		
		String[] columnNames = {"Expression", "Color"};
		
		//Todo : boucle qui ajoute les fonctions avec couleur
		Object[][] data = {
	        {expressions[0], "Red"},
	        {expressions[1], "Blue"},
	    };
		
		table = new JTable(data, columnNames);
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);
		table.setCellSelectionEnabled(true);
	    ListSelectionModel cellSelectionModel = table.getSelectionModel();
	    cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//TODO : REPLACE BY MULTIPLE
	    cellSelectionModel.addListSelectionListener(new MenuListener());
		
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
	    JScrollPane scrollPane = new JScrollPane(table);
	    this.add(scrollPane, BorderLayout.CENTER);
	    this.add(toolbar, BorderLayout.SOUTH);
	}

//	@Override //List event
//	public void valueChanged(ListSelectionEvent e) {
//        if(!e.getValueIsAdjusting()) { 
//            final List<String> selectedValuesList = list.getSelectedValuesList(); 
//            grapher.activeFunctions(selectedValuesList);
//            grapher.repaint();
//        } 
//	}

	@Override //Button event
	public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        switch (e.getActionCommand()) {
	        case PLUS:
	        	String newExp = JOptionPane.showInputDialog(this,"Nouvelle expression");
	        	System.out.println(newExp);
	        	//update menu
	        	//TODO
	        	//update graph
	        	grapher.add(newExp);
	        	break;
	        case MOINS:
	        	System.out.println("Je suis un moins.. gitanerie");
	        	//TODO : Suppression des fonctions sélectionnées
	        	break;
	        default:
	        	assert(false);
        }
	}
	
	public class MenuListener implements TableModelListener, ListSelectionListener {

		@Override
		public void tableChanged(TableModelEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting()) { 
				int[] selectedRow = table.getSelectedRows();
		        int[] selectedColumns = table.getSelectedColumns();
		        //TODO : temp code just to make it work
		        String s = (String) table.getValueAt(selectedRow[0], selectedColumns[0]);
		        final List<String> list = new ArrayList<String>();
		        list.add(s);
				grapher.activeFunctions(list);
				grapher.repaint();
			}
		}
		
	}
}
