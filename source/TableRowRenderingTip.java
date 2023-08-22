import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

public class TableRowRenderingTip extends JPanel
{
	public TableRowRenderingTip()
	{
		Object[] columnNames = {"Type", "Company", "Shares", "Price", "Boolean"};
		Object[][] data =
		{
			{"Buy", "IBM", new Integer(1000), new Double(80.5), Boolean.TRUE},
			{"Sell", "Dell", new Integer(2000), new Double(6.25), Boolean.FALSE},
			{"Short Sell", "Apple", new Integer(3000), new Double(7.35), Boolean.TRUE},
			{"Buy", "MicroSoft", new Integer(4000), new Double(27.50), Boolean.FALSE},
			{"Short Sell", "Cisco", new Integer(5000), new Double(20), Boolean.TRUE}
		};

		DefaultTableModel model = new DefaultTableModel(data, columnNames)
		{
			public Class getColumnClass(int column)
			{
				return getValueAt(0, column).getClass();
			}
		};

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Alternating", createAlternating(model));
		tabbedPane.addTab("Border", createBorder(model));
		tabbedPane.addTab("Data", createData(model));
		add( tabbedPane );
	}

	private JComponent createAlternating(DefaultTableModel model)
	{
		JTable table = new JTable( model )
		{
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component c = super.prepareRenderer(renderer, row, column);

				//  Alternate row color

				if (!isRowSelected(row))
					c.setBackground(row % 2 == 0 ? getBackground() : Color.LIGHT_GRAY);

				return c;
			}
		};

		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.changeSelection(0, 0, false, false);
		return new JScrollPane( table );
	}

	private JComponent createBorder(DefaultTableModel model)
	{

		JTable table = new JTable( model )
		{
			private Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
			private Border inside = new EmptyBorder(0, 1, 0, 1);
			private Border highlight = new CompoundBorder(outside, inside);

			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component c = super.prepareRenderer(renderer, row, column);
				JComponent jc = (JComponent)c;

				// Add a border to the selected row

				if (isRowSelected(row))
					jc.setBorder( highlight );

				return c;
			}
		};

		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.changeSelection(0, 0, false, false);

		return new JScrollPane( table );
	}

	private JComponent createData(DefaultTableModel model)
	{
		JTable table = new JTable( model )
		{
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component c = super.prepareRenderer(renderer, row, column);

				//  Color row based on a cell value

				if (!isRowSelected(row))
				{
					c.setBackground(getBackground());
					int modelRow = convertRowIndexToModel(row);
					String type = (String)getModel().getValueAt(modelRow, 0);
					if ("Buy".equals(type)) c.setBackground(Color.GREEN);
					if ("Sell".equals(type)) c.setBackground(Color.YELLOW);
				}

				return c;
			}
		};

		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.changeSelection(0, 0, false, false);
        table.setAutoCreateRowSorter(true);
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
		try
		{
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) { }

		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Table Row Rendering");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new TableRowRenderingTip() );
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
