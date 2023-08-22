import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ScrollBarToolTipDemo
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
		JTable table = new JTable(2000, 100);
		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		JScrollPane scrollPane = new JScrollPane(table);

		final ScrollBarToolTip vtt = new ScrollBarToolTip(scrollPane.getVerticalScrollBar());
		final ScrollBarToolTip htt = new ScrollBarToolTip(scrollPane.getHorizontalScrollBar())
		{
			public String xxxgetToolTipText(MouseEvent e)
			{
				JScrollBar scrollBar = (JScrollBar)e.getComponent();
				JScrollPane scrollPane = (JScrollPane)scrollBar.getParent();
				JViewport viewport = scrollPane.getViewport();
				JTable table = (JTable)viewport.getView();
				int column = table.columnAtPoint( viewport.getViewPosition() );

				if (column != -1)
					return table.getColumnName(column);
				else
					return null;
			}
		};

		JPanel southPanel = new JPanel( new GridLayout(2, 0) );

		JPanel stylePanel = new JPanel( new GridLayout(1, 0) );
		stylePanel.setBorder( new TitledBorder("Style") );
		southPanel.add(stylePanel);
		String[] styles = {"Fixed Pressed", "Fixed Start", "Fixed End", "Float Start", "Float End"};

		ScrollBarToolTip.Style[] styles2 =
		{
			ScrollBarToolTip.Style.FIXED_PRESSED,
			ScrollBarToolTip.Style.FIXED_START,
			ScrollBarToolTip.Style.FIXED_END,
			ScrollBarToolTip.Style.FLOAT_START,
			ScrollBarToolTip.Style.FLOAT_END
		};
		final ButtonGroup styleGroup = new ButtonGroup();

		for (int i = 0; i < styles.length; i++)
		{
			JRadioButton button = new JRadioButton(styles[i]);
			button.putClientProperty("style", styles2[i]);
			stylePanel.add(button);
			styleGroup.add(button);
			button.addActionListener( new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					JRadioButton button = (JRadioButton)e.getSource();
					ScrollBarToolTip.Style style = (ScrollBarToolTip.Style)button.getClientProperty("style");
					vtt.setStyle( style );
					htt.setStyle( style );
				}
			});
			if (i == 0) button.setSelected( true );
		}


		JPanel locationPanel = new JPanel( new GridLayout(1, 0) );
		locationPanel.setBorder( new TitledBorder("Position") );
		southPanel.add(locationPanel);
		String[] locations = {"Center", "Inside", "Inside Edge", "Outside", "Outside Edge"};

		ScrollBarToolTip.Position[] locations2 =
		{
			ScrollBarToolTip.Position.CENTER,
			ScrollBarToolTip.Position.INSIDE,
			ScrollBarToolTip.Position.INSIDE_EDGE,
			ScrollBarToolTip.Position.OUTSIDE,
			ScrollBarToolTip.Position.OUTSIDE_EDGE
		};
		final ButtonGroup locationGroup = new ButtonGroup();

		for (int i = 0; i < locations.length; i++)
		{
			JRadioButton button = new JRadioButton(locations[i]);
			button.putClientProperty("location", locations2[i]);
			locationPanel.add(button);
			locationGroup.add(button);
			button.addActionListener( new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					JRadioButton button = (JRadioButton)e.getSource();
					ScrollBarToolTip.Position location = (ScrollBarToolTip.Position)button.getClientProperty("location");
					vtt.setPosition( location );
					htt.setPosition( location );
				}
			});
			if (i == 1) button.setSelected( true );
		}

		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Scrollbar Tooltip");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 300);
		frame.add(scrollPane, BorderLayout.WEST);
		frame.add(southPanel, BorderLayout.SOUTH);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		//  Uncomment for Right-to-Left test

//		table.setComponentOrientation( ComponentOrientation.RIGHT_TO_LEFT );
//		scrollPane.setComponentOrientation( ComponentOrientation.RIGHT_TO_LEFT );
//		frame.add(scrollPane, BorderLayout.EAST);
	}
}
