import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class FormScrollerDemo extends JPanel
	implements ActionListener, ChangeListener, ItemListener
{
	private FormScroller scroller;
	private JSpinner top;
	private JSpinner bottom;
	private JSpinner left;
	private JSpinner right;

	FormScrollerDemo()
	{
		setLayout( new BorderLayout(10, 10) );
		setBorder( new EmptyBorder(10, 10, 10, 10) );

		JComponent left = createLeftPanel();
		JComponent right = createRightPanel();

		add(left, BorderLayout.CENTER);
		add(right, BorderLayout.EAST);
	}

	private JComponent createLeftPanel()
	{
		Box content = new Box( BoxLayout.Y_AXIS );
		Random random = new Random();
		int groups = random.nextInt(10) + 5;

		for (int i = 0; i < groups; i++)
		{
			content.add( createGroup(i) );
		}

		JScrollPane scrollPane = new JScrollPane( content );
		scroller = new FormScroller( scrollPane );
		scroller.setScrollInsets( new Insets(5, 0, 5, 0) );

		return scrollPane;
	}

	private JComponent createGroup(int title)
	{
		Box group = new Box( BoxLayout.Y_AXIS );
		group.setBorder( new CompoundBorder(new LineBorder(Color.RED), new TitledBorder("Group " + title)) );
		Random random = new Random();
		int details = random.nextInt(3) + 1;

		for (int i = 0; i < details; i++)
		{
			group.add( createDetail(i) );
		}

		return group;
	}

	private JComponent createDetail(int title)
	{
		JPanel detail = new JPanel( new GridLayout(0, 2, 5, 5) );
		detail.setBorder( new TitledBorder("Detail " + title) );
		Random random = new Random();
		int rows = random.nextInt(3) + 1;

		for (int i = 0; i < rows; i++)
		{
			detail.add(new JLabel("Label " + i));
			detail.add(new JTextField(5));
		}

		return detail;
	}

	public JPanel createRightPanel()
	{
		RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS);
		rl.setAlignment(RelativeLayout.LEADING);
		rl.setFill( true );
		JPanel main = new JPanel( rl );

		JPanel type = new JPanel();
		type.setBorder( new TitledBorder("Scroll Type") );

		JComboBox<FormScroller.Type> comboBox = new JComboBox<>();
		comboBox.addItem( FormScroller.Type.COMPONENT );
		comboBox.addItem( FormScroller.Type.PARENT );
		comboBox.addItem( FormScroller.Type.CHILD );
		comboBox.addActionListener( this );
		type.add( comboBox );
		main.add( type );

		JPanel insets = new JPanel( new GridLayout(0, 2, 5, 5) );
		insets.setBorder( new TitledBorder("Scroll Insets:") );
		top = new JSpinner(new SpinnerNumberModel(5, 0, 100 ,5));
		top.addChangeListener( this );
		insets.add( top );
		insets.add( new JLabel("Top") );
		bottom = new JSpinner(new SpinnerNumberModel(5, 0, 100 ,5));
		bottom.addChangeListener( this );
		insets.add( bottom );
		insets.add( new JLabel("Bottom") );
		left = new JSpinner(new SpinnerNumberModel(0, 0, 100 ,5));
		left.addChangeListener( this );
		insets.add( left );
		insets.add( new JLabel("Left") );
		right = new JSpinner(new SpinnerNumberModel(0, 0, 100 ,5));
		right.addChangeListener( this );
		insets.add( right );
		insets.add( new JLabel("Right") );
		main.add( insets );

		JCheckBox scrollingEnabled = new JCheckBox("Scrolling Enabled");
		scrollingEnabled.setSelected( true );
		scrollingEnabled.addItemListener( this );
		main.add( scrollingEnabled );

		return main;
	}


	public void actionPerformed(ActionEvent e)
	{
		JComboBox comboBox = (JComboBox)e.getSource();
		scroller.setType( (FormScroller.Type)comboBox.getSelectedItem() );
	}

	public void itemStateChanged(ItemEvent e)
	{
		JCheckBox checkBox = (JCheckBox)e.getSource();
		scroller.setScrollingEnabled( checkBox.isSelected() );
	}


	public void stateChanged(ChangeEvent e)
	{
		Insets scrollInsets = new Insets(
			(Integer)top.getValue(), (Integer)left.getValue(), (Integer)bottom.getValue(), (Integer)right.getValue());
		scroller.setScrollInsets( scrollInsets );
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
		JFrame frame = new JFrame("Form Scroller");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new FormScrollerDemo() );
		frame.setSize(400, 300);
//		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
