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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class Menu extends JPanel implements ActionListener {
	
	static final String PLUS = " + ";
	static final String MOINS = " -  ";

	Grapher grapher;
	JTable table;
	JToolBar toolbar;
	
	public Menu(String[] expressions, Grapher grapher) {
		this.grapher = grapher;
		
		//Composant 1 : Table
		
		// initialisation
		String[] columnNames = {"Expression", "Color"};
		Object[][] data = {};
		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		table = new JTable(model);
		for(String expression : expressions) {
			model.addRow(new Object[]{expression, Color.BLACK});
		}
		// rendu couleur pour la colonne Color
		TableColumn colorColumn = table.getColumnModel().getColumn(1);
		colorColumn.setCellEditor(new ColorEditor());
		colorColumn.setCellRenderer(new ColorRenderer(true));
		
		// affichage
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);
		
		// listener : selection
	    ListSelectionModel cellSelectionModel = table.getSelectionModel();
	    cellSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);//TODO : REPLACE BY MULTIPLE
	    cellSelectionModel.addListSelectionListener(new MyListSelectionListener());
	    
	    // listener : edition
	    table.getModel().addTableModelListener(new MyCellEditionListener());
		
	    	    
		//Composant 2 : Boutons
		
		this.toolbar = new JToolBar();
		// bouton Ajout
	    JButton buttonPlus = new JButton(PLUS);
	    buttonPlus.setHorizontalAlignment(SwingConstants.CENTER);
	    buttonPlus.addActionListener(this);
	    this.toolbar.add(buttonPlus);
	    // bouton Suppression
	    JButton buttonMin = new JButton(MOINS);
	    buttonMin.setHorizontalAlignment(SwingConstants.CENTER);
	    buttonMin.addActionListener(this);
	    this.toolbar.add(buttonMin);
	    
	    
	    //Composant final : 1 + 2
	       
	    this.setLayout(new BorderLayout());
	    JScrollPane scrollPane = new JScrollPane(table);
	    this.add(scrollPane, BorderLayout.CENTER);
	    this.add(toolbar, BorderLayout.SOUTH);
	}

	@Override //Button event
	public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
	        case PLUS:
	        	String newExp = JOptionPane.showInputDialog(this.getParent(),"Nouvelle expression");
	        	if(newExp == null) break;//close or cancel button has been clicked
	        	try {
	        		//update graph
		        	grapher.add(newExp);
		        	//update menu
		        	DefaultTableModel model = (DefaultTableModel) table.getModel();
		        	model.addRow(new Object[]{newExp, Color.BLACK});
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
	
	public class MyCellEditionListener implements TableModelListener  {

		@Override
		public void tableChanged(TableModelEvent e) {
			
			//cancel event if triggered by adding/removing a row
			if(e.getType() != TableModelEvent.UPDATE) return;
			
			int row = e.getFirstRow();
	        int col = e.getColumn();
	        TableModel model = (TableModel)e.getSource();
	        
	        String columnName = model.getColumnName(col);
	        Object data = model.getValueAt(row, col);
	        
	        try {
	        	grapher.edit(row, col, data);
        	} catch (Exception ex) {
        		JOptionPane.showMessageDialog(new JPanel(),"Expression invalide", "Erreur", 0);
        	}
		}
	}
}
