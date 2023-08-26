import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class ComponentMoverDemo extends JPanel
	implements ChangeListener
{
	private JSpinner top;
	private JSpinner bottom;
	private JSpinner left;
	private JSpinner right;
	private JSpinner snapX;
	private JSpinner snapY;
	private ComponentMover cm1;
	private ComponentMover cm2;

	ComponentMoverDemo()
	{
		// drag event is passed from registered component to the Window
		cm1 = new ComponentMover(Window.class);

		// drag event is handled by registered component
		cm2 = new ComponentMover();

		setLayout( new BorderLayout() );
        setBorder( new MatteBorder(4, 4, 4, 4, Color.BLUE) );
		JComponent top = createTopPanel();
		add(top, BorderLayout.NORTH);

		JPanel center = new JPanel( new BorderLayout() );
		center.setLayout( new BorderLayout(10, 10) );
		center.setBorder( new EmptyBorder(10, 10, 10, 10) );
		add(center, BorderLayout.CENTER);

		JComponent left = createLeftPanel();
		JComponent right = createRightPanel();

		center.add(left, BorderLayout.CENTER);
		center.add(right, BorderLayout.EAST);

		JLabel label = new JLabel( "Tab off spinner for change to take effect" );
		label.setHorizontalAlignment( JLabel.CENTER );
		center.add(label, BorderLayout.SOUTH);
	}

	private JComponent createTopPanel()
	{
		JPanel titleBar = new JPanel( new BorderLayout() );
		titleBar.setBackground( UIManager.getColor("InternalFrame.activeTitleBackground" ) );
		cm1.registerComponent( titleBar );

		JLabel title = new JLabel( "     Componenent Mover - Dragable Title Bar" );
		titleBar.add(title, BorderLayout.CENTER);

		JButton close = new JButton( "X" );
		close.addActionListener( new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		titleBar.add(close, BorderLayout.EAST);

		return titleBar;
	}

	private JComponent createLeftPanel()
	{
		JPanel panel = new JPanel( null );
		panel.setBorder( new LineBorder(Color.BLUE) );

		for (int i = 1; i < 4; i++)
		{
			JLabel label = new JLabel("Dragable Label " + i);
			label.setSize( label.getPreferredSize() );
			label.setLocation(20, 50 * i);
			label.setOpaque(true);
			label.setBackground( UIManager.getColor("InternalFrame.activeTitleBackground" ) );
			panel.add( label );
			cm2.registerComponent( label );
		}

		return panel;
	}

	public JPanel createRightPanel()
	{
		RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS);
		rl.setAlignment(RelativeLayout.LEADING);
		rl.setFill( true );
		JPanel main = new JPanel( rl );

		JPanel snap = new JPanel( new GridLayout(0, 2, 5, 5) );
		snap.setBorder( new TitledBorder("Snap Size:") );
		snapX = new JSpinner(new SpinnerNumberModel(1, 1, 50 ,1));
		snapX.addChangeListener( this );
		snap.add( snapX );
		snap.add( new JLabel("X") );
		snapY = new JSpinner(new SpinnerNumberModel(1, 1, 50 ,1));
		snapY.addChangeListener( this );
		snap.add( snapY );
		snap.add( new JLabel("Y") );
		main.add( snap );

		JPanel insets = new JPanel( new GridLayout(0, 2, 5, 5) );
		insets.setBorder( new TitledBorder("Edge Insets:") );
		top = new JSpinner(new SpinnerNumberModel(0, -50, 50 ,5));
		top.addChangeListener( this );
		insets.add( top );
		insets.add( new JLabel("Top") );
		left = new JSpinner(new SpinnerNumberModel(0, -50, 50 ,5));
		left.addChangeListener( this );
		insets.add( left );
		insets.add( new JLabel("Left") );
		bottom = new JSpinner(new SpinnerNumberModel(0, -50, 50 ,5));
		bottom.addChangeListener( this );
		insets.add( bottom );
		insets.add( new JLabel("Bottom") );
		right = new JSpinner(new SpinnerNumberModel(0, -50, 50 ,5));
		right.addChangeListener( this );
		insets.add( right );
		insets.add( new JLabel("Right") );
		main.add( insets );

		return main;
	}

	public void stateChanged(ChangeEvent e)
	{
		Dimension snapDimension = new Dimension((Integer)snapX.getValue(), (Integer)snapY.getValue());
		cm1.setSnapSize( snapDimension );
		cm2.setSnapSize( snapDimension );

		Insets boundsInsets = new Insets(
			(Integer)top.getValue(), (Integer)left.getValue(), (Integer)bottom.getValue(), (Integer)right.getValue());
		cm1.setEdgeInsets( boundsInsets );
		cm2.setEdgeInsets( boundsInsets );
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
		JFrame frame = new JFrame("Component Mover");
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new ComponentMoverDemo() );
		frame.setSize(400, 300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// drag frame from anywhere a MouseEvent is not handled

		new ComponentMover(frame, frame.getRootPane());

	}
}
