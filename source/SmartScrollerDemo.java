import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

public class SmartScrollerDemo extends JPanel
	implements ActionListener
{
	private JRadioButton horizontal;
	private JRadioButton vertical;
	private JRadioButton start;
	private JRadioButton end;

	private	JScrollPane textAreaSP;
	private JScrollPane listSP;
	private JTextArea textArea;
	private JList list;

	private Timer timer;
	private SmartScroller textAreaScroller;
	private SmartScroller listScroller;

	public SmartScrollerDemo()
	{
		setBorder( new EmptyBorder(5, 5, 5, 5) );
		setLayout( new BorderLayout(10, 10) );

		add(createNorthPanel(), BorderLayout.NORTH);
		add(createCenterPanel());
		add(createSouthPanel(), BorderLayout.SOUTH);

		resetScrolling();
	}

	private JPanel createNorthPanel()
	{
		JPanel panel = new JPanel( new GridLayout(1, 0, 5, 5) );

		JLabel textArea = new JLabel( "JTextArea" );
		textArea.setHorizontalAlignment( JLabel.CENTER );
		panel.add( textArea );

		JLabel list = new JLabel( "JList" );
		list.setHorizontalAlignment( JLabel.CENTER );
		panel.add( list );

		return panel;
	}

	private JPanel createCenterPanel()
	{
		JPanel panel = new JPanel( new GridLayout(1, 0, 5, 5) );

		JTextArea textArea = new JTextArea();
		textArea.setEditable( false );
		textAreaSP = new JScrollPane( textArea );
		panel.add( textAreaSP );

		JList<String> list = new JList<String>( new DefaultListModel<String>() );
		list.setVisibleRowCount(1);
		listSP = new JScrollPane( list );
		panel.add( listSP );

		return panel;
	}

	private JPanel createSouthPanel()
	{
		JPanel panel = new JPanel( new GridLayout(1, 0, 5, 5) );
		TitledBorder border = new TitledBorder("Change properties then click Reset");
		border.setTitleJustification( TitledBorder.CENTER );
		panel.setBorder( border );

		JPanel r1 = new JPanel( new GridLayout(0, 1) );
		r1.setBorder( new TitledBorder("Scrolling Direction:") );
		horizontal = new JRadioButton("Horizontal");
		r1.add( horizontal );
		vertical = new JRadioButton("Vertical");
		vertical.setSelected( true );
		r1.add( vertical );
		ButtonGroup vbGroup = new ButtonGroup();
		vbGroup.add( horizontal );
		vbGroup.add( vertical );
		panel.add( r1 );

		JPanel r2 = new JPanel( new GridLayout(0, 1) );
		r2.setBorder( new TitledBorder("Viewport Position:") );
		start = new JRadioButton("Start");
		r2.add( start );
		end = new JRadioButton("End");
		end.setSelected( true );
		r2.add( end );
		ButtonGroup huGroup = new ButtonGroup();
		huGroup.add( start );
		huGroup.add( end );
		panel.add( r2 );

		JButton button = new JButton("Reset Smart Scroller");
		button.addActionListener( this );
		panel.add( button );

		return panel;
	}

	@SuppressWarnings("unchecked")
	private void resetScrolling()
	{
		if (timer != null)
			timer.stop();

		if (textAreaScroller != null)
		{
			textAreaSP.getVerticalScrollBar().removeAdjustmentListener(textAreaScroller);
			textAreaSP.getHorizontalScrollBar().removeAdjustmentListener(textAreaScroller);
			listSP.getVerticalScrollBar().removeAdjustmentListener(listScroller);
			listSP.getHorizontalScrollBar().removeAdjustmentListener(listScroller);
		}

		JTextArea textArea = (JTextArea)textAreaSP.getViewport().getView();
		textArea.setText("");
		final Document doc = textArea.getDocument();
		final String prefix = vertical.isSelected() ? "\nplus some longer text and longer and longer" : "\tand some more text";

		JList list = (JList)listSP.getViewport().getView();
		list.setLayoutOrientation(vertical.isSelected() ? JList.VERTICAL : JList.HORIZONTAL_WRAP);
		final DefaultListModel model = (DefaultListModel)list.getModel();
		model.clear();

		int scrollDirection = vertical.isSelected() ? SmartScroller.VERTICAL : SmartScroller.HORIZONTAL;
		int	position = end.isSelected() ? SmartScroller.END : SmartScroller.START;

		textAreaScroller = new SmartScroller(textAreaSP, scrollDirection, position);
		listScroller = new SmartScroller(listSP, scrollDirection, position);

		timer = new Timer(1000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					Format formatter = DateFormat.getTimeInstance();
					String now = formatter.format( new Date() );

					int offset = end.isSelected() ? doc.getLength() : 0;
					doc.insertString(offset, prefix + now.toString(), null);

					int index = end.isSelected() ? model.getSize() : 0;
					model.insertElementAt(now + "  ", index);

				}
				catch (BadLocationException e1) {}
			}
		});
		timer.start();
	}

	public void actionPerformed(ActionEvent e)
	{
		resetScrolling();
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
		JFrame frame = new JFrame("Smart Scroller");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new SmartScrollerDemo() );
		frame.setSize(560, 260);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
