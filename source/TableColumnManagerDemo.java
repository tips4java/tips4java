import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class TableColumnManagerDemo extends JPanel
{
	private TableColumnManager marquee;
	private JSpinner scrollFrequency;
	private JSpinner scrollAmount;
	private JSpinner preferredWidth;
	private JSpinner wrapAmount;

	TableColumnManagerDemo()
	{
		setLayout( new BorderLayout(10, 10) );
		setBorder( new EmptyBorder(10, 10, 10, 10) );

		JComponent center = createCenterPanel();
		add(center, BorderLayout.CENTER);
	}

	public JComponent createCenterPanel()
	{
		JTable table = new JTable( new DefaultTableModel(15, 15) )
		{
			public Class getColumnClass(int column)
			{
				int modelColumn = convertColumnIndexToModel( column );
				return (modelColumn == 0) ? Integer.class : Object.class;
			}
		};
		table.setPreferredScrollableViewportSize(table.getPreferredSize());

		TableColumnManager tcm = new TableColumnManager(table);
		tcm.hideColumn(2);
		tcm.hideColumn("E");
//		tcm.showColumn("C");
		tcm.showColumn(2);

		return new JScrollPane( table );
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
		JFrame frame = new JFrame("Table Column Manager");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new TableColumnManagerDemo() );
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
