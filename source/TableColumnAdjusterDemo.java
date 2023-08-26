import java.awt.*;
import java.awt.event.*;
//import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class TableColumnAdjusterDemo extends JPanel
	implements ItemListener
{
	private TableColumnAdjuster tca;

	TableColumnAdjusterDemo()
	{
		setLayout( new BorderLayout(10, 10) );
		setBorder( new EmptyBorder(10, 10, 10, 10) );

		JComponent center = createCenterPanel();
		JComponent south = createSouthPanel();

		add(center, BorderLayout.CENTER);
		add(south, BorderLayout.SOUTH);
	}

	public JComponent createCenterPanel()
	{
    	String[] columnNames = {"First Name", "Last Name", "Age", "Address"};
    	DefaultTableModel model = new DefaultTableModel(columnNames, 0)
    	{
    		@Override
    		public Class getColumnClass(int column)
    		{
    			return (column == 4) ? Boolean.class : String.class;
    		}
    	};

    	String[] row1 = {"Homer", "Simpson", "54", "Somewhere in Hollywoodasfsdf"};
    	String[] row2 = {"Herman", "Schwarzenegger", "50", "California"};

		for	(int i = 0; i < 1; i++)
		{
			model.addRow(row1);
			model.addRow(row2);
		}

		model.setRowCount(5);

		JTable table = new JTable( model );
//		table.setColumnSelectionAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(25);
		tcm.getColumn(1).setPreferredWidth(25);
		tcm.getColumn(2).setPreferredWidth(25);
		tcm.getColumn(3).setPreferredWidth(25);

//		JTableHeader header = table.getTableHeader();
//		header.setPreferredSize(new Dimension(0, 0));
		tca = new TableColumnAdjuster( table, 6 );
		tca.setColumnDataIncluded(false);
		tca.adjustColumns();
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
//		TableColumn tc = tcm.getColumn(1);
//		tc.setWidth(tc.getWidth() + 25);

		table.setPreferredScrollableViewportSize(table.getPreferredSize());
        JScrollPane scrollPane= new JScrollPane( table );

        table.setAutoCreateRowSorter(true);

		return scrollPane;
	}

	private JComponent createSouthPanel()
	{
		JPanel panel = new JPanel( new GridLayout(0, 2) );

		JCheckBox columnHeaderIncluded = new JCheckBox("Column Header Included");
		columnHeaderIncluded.setSelected(true);
		columnHeaderIncluded.addItemListener( this );
		panel.add( columnHeaderIncluded );

		JCheckBox onlyAdjustLarger = new JCheckBox( "Only Adjust Larger" );
		onlyAdjustLarger.setSelected(true);
		onlyAdjustLarger.addItemListener( this );
		panel.add( onlyAdjustLarger );

		JCheckBox columnDataIncluded = new JCheckBox( "Column Data Included" );
		columnDataIncluded.setSelected(true);
		columnDataIncluded.addItemListener( this );
		panel.add( columnDataIncluded );

		JCheckBox dynamicAdjustment = new JCheckBox( "Dynamic Adjustment" );
		dynamicAdjustment.addItemListener( this );
		panel.add( dynamicAdjustment );

		return panel;
	}

	public void itemStateChanged(ItemEvent e)
	{
		JCheckBox checkBox = (JCheckBox)e.getSource();
		String command = checkBox.getText();

		if ("Column Header Included".equals(command))
			tca.setColumnHeaderIncluded( checkBox.isSelected() );

		if ("Column Data Included".equals(command))
			tca.setColumnDataIncluded( checkBox.isSelected() );

		if ("Only Adjust Larger".equals(command))
			tca.setOnlyAdjustLarger( checkBox.isSelected() );

		if ("Dynamic Adjustment".equals(command))
			tca.setDynamicAdjustment( checkBox.isSelected() );
	}


	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public static void createAndShowGUI()
	{
		JFrame frame = new JFrame("Table Column Adjuster");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new TableColumnAdjusterDemo() );
		frame.setSize(500, 300);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
