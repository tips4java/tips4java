import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class ToolTipListenerDemo extends JPanel
	implements ActionListener
{
	private JRadioButton noListener;
	private JRadioButton mouseWheelListener;
	private JRadioButton adjustmentListener;
	private JRadioButton componentListener;

	ToolTipListenerDemo()
	{
		setLayout( new BoxLayout(this,BoxLayout.X_AXIS) );
		setBorder( new EmptyBorder(10, 10, 10, 10) );

		JComponent right = createRightPanel();
		JComponent left = createLeftPanel();

		add(left);
		add( Box.createHorizontalStrut(10) );
		add(right);
	}

	public JComponent createLeftPanel()
	{
        JTable table = new JTable(50, 50)
		{
			public String getToolTipText( MouseEvent e )
			{
				int row = rowAtPoint( e.getPoint() );
				int column = columnAtPoint( e.getPoint() );
                return "(" + row+ "," + column + ")";
			}
		};
		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

		JScrollPane scrollPane = new JScrollPane( table );
		scrollPane.setPreferredSize( new Dimension(300, 240) );
		scrollPane.getViewport().getView().requestFocusInWindow();

		ToolTipListener listener = new ToolTipListener();

		if (mouseWheelListener.isSelected())
		{
			scrollPane.addMouseWheelListener(listener);
		}
		if (adjustmentListener.isSelected())
		{
			scrollPane.getVerticalScrollBar().addAdjustmentListener( listener );
		}
		if (componentListener.isSelected())
		{
			table.addComponentListener( listener );
		}

		return scrollPane;
	}

	public JPanel createRightPanel()
	{
		noListener = new JRadioButton("No Listener");
		mouseWheelListener = new JRadioButton("MouseWheel Listener");
		adjustmentListener = new JRadioButton("Adjustment Listener");
		componentListener = new JRadioButton("Component Listener");
		noListener.setSelected( true );

		ButtonGroup bg1 = new ButtonGroup();
		bg1.add( noListener );
		bg1.add( mouseWheelListener );
		bg1.add( adjustmentListener );
		bg1.add( componentListener );

		JPanel p1 = new JPanel( new GridLayout(0, 1) );
		p1.setBorder( new TitledBorder("Select Listener to Use") );
		p1.add( noListener );
		p1.add( mouseWheelListener );
		p1.add( adjustmentListener );
		p1.add( componentListener );

		noListener.addActionListener(this);
		mouseWheelListener.addActionListener(this);
		adjustmentListener.addActionListener(this);
		componentListener.addActionListener(this);

		return p1;
	}


	public void actionPerformed(ActionEvent e)
	{
		remove(0);
		JComponent component = createLeftPanel();
		add(component, 0);
		revalidate();
		repaint();
		component.requestFocusInWindow();
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
		JFrame frame = new JFrame("ToolTip Listener");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new ToolTipListenerDemo() );
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}
}
