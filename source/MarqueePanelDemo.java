import java.awt.*;
import java.awt.event.*;
//import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class MarqueePanelDemo extends JPanel
	implements ActionListener, ChangeListener, ItemListener
{
	private MarqueePanel marquee;
	private JSpinner scrollFrequency;
	private JSpinner scrollAmount;
	private JSpinner preferredWidth;
	private JSpinner wrapAmount;

	MarqueePanelDemo()
	{
		setLayout( new BorderLayout(10, 10) );
		setBorder( new EmptyBorder(10, 10, 10, 10) );
		setBackground( Color.ORANGE );

		JComponent north = createNorthPanel();
		JComponent center = createCenterPanel();
		JComponent south = createSouthPanel();

		add(north, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(south, BorderLayout.SOUTH);
	}

	private JComponent createNorthPanel()
	{
		marquee = new MarqueePanel();
		marquee.setPreferredWidth( 199 );
		marquee.setOpaque(false);

		JLabel label1 = new JLabel();
		label1.setText("Here is a long string of text.");
	    Font f = (new Font("SansSerif", Font.PLAIN, 24));
		label1.setFont( f );
		marquee.add( label1 );

		//  add an image here

		String path = "dukeWaveRed.gif";
		java.net.URL imgURL = getClass().getResource(path);
        ImageIcon duke = new ImageIcon(imgURL, path);
		JLabel label2 = new JLabel( duke );
		marquee.add( label2 );

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.add( marquee );
		return panel;
	}

	public JPanel createCenterPanel()
	{
		RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS);
		rl.setAlignment(RelativeLayout.LEADING);
		rl.setFill( true );
		JPanel main = new JPanel( rl );

		JPanel insets = new JPanel( new GridLayout(0, 2, 5, 5) );
		scrollFrequency = new JSpinner(new SpinnerNumberModel(5, 1, 1000 ,2));
		scrollFrequency.addChangeListener( this );
		insets.add( scrollFrequency );
		insets.add( new JLabel("Scroll Frequency") );
		scrollAmount = new JSpinner(new SpinnerNumberModel(5, 1, 10 ,1));
		scrollAmount.addChangeListener( this );
		insets.add( scrollAmount );
		insets.add( new JLabel("Scroll Amount") );
		preferredWidth = new JSpinner(new SpinnerNumberModel(199, -1, 399 ,50));
		preferredWidth.addChangeListener( this );
		insets.add( preferredWidth );
		insets.add( new JLabel("Preferred Width") );
		main.add( insets );

		JCheckBox wrap = new JCheckBox("Wrap");
		wrap.setSelected( false );
		wrap.addItemListener( this );
		main.add( wrap );

		JPanel wrapPanel = new JPanel( new GridLayout(0, 2, 5, 5) );
		wrapAmount = new JSpinner(new SpinnerNumberModel(50, 0, 500 ,25));
		wrapAmount.setEnabled( false );
		wrapAmount.addChangeListener( this );
		wrapPanel.add( wrapAmount );
		wrapPanel.add( new JLabel("Wrap Amount") );
		main.add( wrapPanel );

		JCheckBox scrollWhenFocused = new JCheckBox("Scroll When Focused");
		scrollWhenFocused.setSelected( true );
		scrollWhenFocused.addItemListener( this );
		main.add( scrollWhenFocused );

		JPanel panel = new JPanel();
		panel.add( main );

		return panel;
	}

	private JComponent createSouthPanel()
	{
		JPanel panel = new JPanel();

		JButton start = new JButton( "Start" );
		start.addActionListener( this );
		panel.add( start );

		JButton stop = new JButton( "Stop" );
		stop.addActionListener( this );
		panel.add( stop );

		JButton pause = new JButton( "Pause" );
		pause.addActionListener( this );
		panel.add( pause );

		JButton resume = new JButton( "Resume" );
		resume.addActionListener( this );
		panel.add( resume );

		return panel;
	}


	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

		if ("Start".equals( command ) )
			marquee.startScrolling();

		if ("Stop".equals( command ) )
			marquee.stopScrolling();

		if ("Pause".equals( command ) )
			marquee.pauseScrolling();

		if ("Resume".equals( command ) )
			marquee.resumeScrolling();

	}

	public void itemStateChanged(ItemEvent e)
	{
		JCheckBox checkBox = (JCheckBox)e.getSource();
		String command = checkBox.getText();

		if ("Wrap".equals(command))
		{
			marquee.setWrap( checkBox.isSelected() );
			wrapAmount.setEnabled( checkBox.isSelected() );
		}
		else
			marquee.setScrollWhenFocused( checkBox.isSelected() );
	}


	public void stateChanged(ChangeEvent e)
	{
		marquee.setScrollFrequency( (Integer)scrollFrequency.getValue() );
		marquee.setScrollAmount( (Integer)scrollAmount.getValue() );
		marquee.setPreferredWidth( (Integer)preferredWidth.getValue() );
		marquee.setWrapAmount( (Integer)wrapAmount.getValue() );
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
		JFrame frame = new JFrame("Marquee Panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new MarqueePanelDemo() );
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
