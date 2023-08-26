import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class ComponentBorderDemo extends JPanel
	implements ActionListener
{
	private JRadioButton top;
	private JRadioButton left;
	private JRadioButton bottom;
	private JRadioButton right;

	private JRadioButton leading;
	private JRadioButton center;
	private JRadioButton trailing;

	private JSpinner gap;

	private JCheckBox adjustInsets;

	ComponentBorderDemo()
	{
		setLayout( new BoxLayout(this,BoxLayout.X_AXIS) );
		setBorder( new EmptyBorder(10, 10, 10, 10) );

		JComponent east = createEastPanel();
		JComponent center = createCenterPanel();

		add(center);
		add( Box.createHorizontalStrut(10) );
		add(east);
	}

	private Box createCenterPanel()
	{
		Box box = Box.createVerticalBox();

		JPanel first = new JPanel();
		first.setBorder( new TitledBorder("Normal Approach") );
		JTextField textField1= new JTextField("text", 15);
		first.add( textField1 );
		JButton button1= new JButton("?");
		button1.addActionListener( new NameActionListener(textField1) );
		first.add( button1 );
		box.add( first );

		JPanel second = new JPanel();
		second.setBorder( new TitledBorder("Alternative Approach") );
		JTextField textField2 = new JTextField("text", 15);
		JButton button2= new JButton("?");
		button2.addActionListener( new NameActionListener(textField2) );
		JPanel panel = new JPanel();
		panel.setLayout( new FlowLayout(FlowLayout.CENTER, 0, 0) );
		panel.setBackground( textField2.getBackground() );
		panel.setBorder( textField2.getBorder() );
		textField2.setBorder(null);
		panel.add(textField2);
		panel.add(button2);
		second.add(panel);
		box.add(second);

		JPanel third = new JPanel();
		third.setBorder( new TitledBorder("Component Border Approach") );
		JTextField textField3 = new JTextField("text", 15);
		JButton button3 = new JButton("?");
		button3.setMargin(new Insets(1, 1, 1, 1) );
		ComponentBorder cb = new ComponentBorder(button3);
		button3.addActionListener( new NameActionListener(textField3) );

		if (top.isSelected())
			cb.setEdge(ComponentBorder.Edge.TOP);
		if (left.isSelected())
			cb.setEdge(ComponentBorder.Edge.LEFT);
		if (bottom.isSelected())
			cb.setEdge(ComponentBorder.Edge.BOTTOM);
		if (right.isSelected())
			cb.setEdge(ComponentBorder.Edge.RIGHT);

		if (leading.isSelected())
			cb.setAlignment(ComponentBorder.LEADING);
		if (center.isSelected())
			cb.setAlignment(ComponentBorder.CENTER);
		if (trailing.isSelected())
			cb.setAlignment(ComponentBorder.TRAILING);

		cb.setGap( (Integer)gap.getValue() );

		cb.setAdjustInsets( adjustInsets.isSelected() );

		cb.install(textField3);
		third.add( textField3 );
		box.add( third );

		return box;
	}

	private JPanel createEastPanel()
	{
		JPanel panel = new JPanel( new BorderLayout() );
		panel.setBorder( new TitledBorder("Component Border Properties") );

		Box box = Box.createVerticalBox();
		panel.add(box);

		//  Create Edge radio buttons

		top = new JRadioButton("Top");
		left = new JRadioButton("Left");
		bottom = new JRadioButton("Bottom");
		right = new JRadioButton("Right");
		right.setSelected( true );

		ButtonGroup bg1 = new ButtonGroup();
		bg1.add( top );
		bg1.add( left );
		bg1.add( bottom );
		bg1.add( right );

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.setBorder( new TitledBorder("Edge") );
		p1.add( top );
		p1.add( left );
		p1.add( bottom );
		p1.add( right );
		box.add( p1 );

		//  Create Alignment radio buttons

		leading = new JRadioButton("Leading");
		center = new JRadioButton("Center");
		trailing = new JRadioButton("Trailing");
		center.setSelected(true);

		ButtonGroup bg2 = new ButtonGroup();
		bg2.add( leading );
		bg2.add( center );
		bg2.add( trailing );

		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p2.setBorder( new TitledBorder("Edge") );
		p2.add( leading );
		p2.add( center );
		p2.add( trailing );
		box.add( p2 );

		//  Create Gap

		JPanel p3 = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		gap = new JSpinner(new SpinnerNumberModel(5, 0, 50, 5));
		p3.add( gap );
		p3.add( new JLabel("Gap") );
		box.add( p3 );

		//  Create adjust insets

		JPanel p4 = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		adjustInsets = new JCheckBox("Adjust Insets");
		adjustInsets.setAlignmentX(0.5f);
		adjustInsets.setSelected(true);
		p4.add(adjustInsets);
		box.add(p4);

		JButton button = new JButton("Recreate Component Border");
		button.addActionListener( this );
		panel.add(button, BorderLayout.SOUTH);

		return panel;
	}

	public void actionPerformed(ActionEvent e)
	{
		remove(0);
		add(createCenterPanel(), 0);
		revalidate();
		repaint();
	}

	static class NameActionListener implements ActionListener
	{
		JTextField textField;

		NameActionListener(JTextField textField)
		{
			this.textField = textField;
		}

		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton)e.getSource();

			String text = JOptionPane.showInputDialog(
				button,
            "Enter Name",
			textField.getText()
            );

			textField.setText( text );
		}
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
		JFrame frame = new JFrame("Component Border");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new ComponentBorderDemo() );
		frame.setSize(600, 360);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
