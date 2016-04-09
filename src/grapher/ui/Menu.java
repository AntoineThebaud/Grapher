package grapher.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

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

		Object[][] data = {};
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		table = new JTable(model);
		for(String expression : expressions) {
			model.addRow(new Object[]{expression, new Color(1)});
		}
		
		//ajout couleurs :
		TableColumn colorColumn = table.getColumnModel().getColumn(1);
		colorColumn.setCellEditor(new ColorEditor());
		colorColumn.setCellRenderer(new ColorRenderer(true));
		
		//table = new JTable(data, columnNames);
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);

	    ListSelectionModel cellSelectionModel = table.getSelectionModel();
	    cellSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);//TODO : REPLACE BY MULTIPLE
	    cellSelectionModel.addListSelectionListener(new MyListSelectionListener());
		
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

	@Override //Button event
	public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        switch (e.getActionCommand()) {
	        case PLUS:
	        	String newExp = JOptionPane.showInputDialog(this.getParent(),"Nouvelle expression");
	        	if(newExp == null) break;//close or cancel button has been clicked
	        	System.out.println(newExp);
	        	try {
	        		//update graph
		        	grapher.add(newExp);
		        	//update menu
		        	DefaultTableModel model = (DefaultTableModel) table.getModel();
		        	model.addRow(new Object[]{newExp, "GREEN"});
	        	} catch (Exception ex) {
	        		JOptionPane.showMessageDialog(this.getParent(),"Expression invalide", "Erreur", 0);
	        	}
	        	break;
	        case MOINS:
	        	int[] selectedRows = table.getSelectedRows();
	        	//update graph
	        	grapher.remove(selectedRows);
	        	//update menu
	        	table.clearSelection();
	        	DefaultTableModel model = (DefaultTableModel) table.getModel();
	        	for(int i = 0; i < selectedRows.length; ++i) {
	        		model.removeRow(selectedRows[i] - i);
	        		//" - i" car la table réduit de 1 à chaque suppression
	        	}
	        	break;
	        default:
	        	assert(false);
        }
	}
	
	public class MyListSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting()) {
				grapher.changeActiveFunctions(table.getSelectedRows());
				grapher.repaint();
			}
		}
	}
}
