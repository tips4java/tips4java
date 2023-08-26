import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.UIManager.*;

public class BoundsPopupMenuListenerDemo extends JPanel
	implements ChangeListener, ItemListener
{
	private BoundsPopupMenuListener listener;
	private JCheckBox scrollBarRequired;
	private JCheckBox popupWider;
	private JSpinner maximumWidth;
	private JCheckBox popupAbove;
	private JComboBox<String> comboBox;

	BoundsPopupMenuListenerDemo()
	{
		setLayout( new BorderLayout(10, 10) );
		setBorder( new EmptyBorder(20, 20, 20, 20) );

		JComponent left = createLeftPanel();
		JComponent right = createRightPanel();

		add(left, BorderLayout.CENTER);
		add(right, BorderLayout.EAST);
	}

	private JComponent createLeftPanel()
	{
		listener = new BoundsPopupMenuListener(false, false, -1, false);

		String[] items =
		{
			"Item1",
			"Item2",
			"Item3",
			"Item4 contains longer text to display",
			"Item5",
			"Item6",
			"Item7",
			"Item8"
		};

		comboBox = new JComboBox<>( items );
		comboBox.setPrototypeDisplayValue("ItemWWW");
		comboBox.setMaximumRowCount(5);
		comboBox.addPopupMenuListener( listener );
		Dimension d = comboBox.getPreferredSize();
		d.width = 1024;
		comboBox.setMaximumSize( d );

		Box content = new Box( BoxLayout.Y_AXIS );
		content.add( comboBox );
		content.add( Box.createVerticalGlue() );

		return content;
	}

	public JPanel createRightPanel()
	{
		RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS);
		rl.setAlignment(RelativeLayout.LEADING);
		rl.setFill( true );
		JPanel main = new JPanel( rl );
		main.setBorder( new TitledBorder("Change Properties:") );

		scrollBarRequired = new JCheckBox("ScrollBar Required");
		scrollBarRequired.addItemListener( this );
		main.add( scrollBarRequired );

		popupWider = new JCheckBox("Popup Wider");
		popupWider.addItemListener( this );
		main.add( popupWider );

		JPanel width = new JPanel();
		maximumWidth = new JSpinner(new SpinnerNumberModel(-1, -1, 599 ,50));
		maximumWidth.addChangeListener( this );
		width.add( maximumWidth );
		width.add( new JLabel("Maximum Width") );
		main.add( width );

		popupAbove = new JCheckBox("Popup Above");
		popupAbove.addItemListener( this );
		main.add( popupAbove );

		return main;
	}

	public void itemStateChanged(ItemEvent e)
	{
		JCheckBox checkBox = (JCheckBox)e.getSource();

		if (checkBox == scrollBarRequired)
			listener.setScrollBarRequired( checkBox.isSelected() );
		else if (checkBox == popupWider)
			listener.setPopupWider( checkBox.isSelected() );
		else if (checkBox == popupAbove)
			listener.setPopupAbove( checkBox.isSelected() );

	}

	public void stateChanged(ChangeEvent e)
	{
		listener.setMaximumWidth( (Integer)maximumWidth.getValue() );
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
		JFrame frame = new JFrame("Bounds Popup Menu Listener");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new BoundsPopupMenuListenerDemo() );
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}
}
