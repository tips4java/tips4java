import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class ButtonColumnDemo
{
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
    	UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);

		String[] columnNames = {"First Name", "Last Name", ""};
		Object[][] data =
		{
			{"Homer", "Simpson", "delete Homer"},
			{"Madge", "Simpson", "delete Madge"},
			{"Bart",  "Simpson", "delete Bart"},
			{"Lisa",  "Simpson", "delete Lisa"},
		};

		DefaultTableModel model = new DefaultTableModel(data, columnNames);
		JTable table = new JTable( model );
		JScrollPane scrollPane = new JScrollPane( table );

		Action delete = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{

				JTable table = (JTable)e.getSource();
				int modelRow = Integer.valueOf( e.getActionCommand() );
				Object delete = table.getModel().getValueAt(modelRow, 2);
				Window window = SwingUtilities.windowForComponent(table);

				int result = JOptionPane.showConfirmDialog(
					window,
					"Are you sure you want to " + delete,
					"Delete Row Confirmation",
					JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.YES_OPTION)
				{
//					System.out.println( "Deleting row: " + modelRow);
					((DefaultTableModel)table.getModel()).removeRow(modelRow);
				}
			}
		};

		ButtonColumn buttonColumn = new ButtonColumn(table, delete, 2);
        buttonColumn.setMnemonic(KeyEvent.VK_D);

		JFrame frame = new JFrame("Table Button Column");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.add( scrollPane );
		frame.setSize(400, 160);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
    }
}
