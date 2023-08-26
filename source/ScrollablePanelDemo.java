import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ScrollablePanelDemo extends JPanel
	implements ActionListener
{
	private JScrollPane scrollPane;
	private JSpinner gridColumns;
	private JComboBox<ScrollablePanel.ScrollableSizeHint> scrollableHeight;
	private JComboBox<ScrollablePanel.ScrollableSizeHint> scrollableWidth;
	private JRadioButton verticalUnitPercent;
	private JRadioButton verticalUnitPixels;
	private JSpinner verticalUnitAmount;
	private JRadioButton verticalBlockPercent;
	private JRadioButton verticalBlockPixels;
	private JSpinner verticalBlockAmount;
	private JRadioButton horizontalUnitPercent;
	private JRadioButton horizontalUnitPixels;
	private JSpinner horizontalUnitAmount;
	private JRadioButton horizontalBlockPercent;
	private JRadioButton horizontalBlockPixels;
	private JSpinner horizontalBlockAmount;

	public ScrollablePanelDemo()
	{
		setBorder( new EmptyBorder(5, 5, 5, 5) );
		setLayout( new BorderLayout(5, 5) );

		scrollPane = new JScrollPane();
		add(scrollPane);

		add(createControlPanel(), BorderLayout.EAST);

		createLayout();
	}

	private JPanel createControlPanel()
	{
		RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS);
		rl.setGap(5);
		rl.setAlignment(RelativeLayout.LEADING);
		rl.setFill( true );
		JPanel panel = new JPanel( rl );
		panel.setBorder( new TitledBorder("Configure Panel") );

		JPanel r1 = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		r1.add( new JLabel("Grid Columns:") );
		gridColumns = new JSpinner(new SpinnerNumberModel(3, 1, 40 ,1));
		r1.add( gridColumns );
		panel.add( r1 );

		JPanel r2 = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		r2.add( new JLabel("Scrollable Height Hint:") );
		scrollableHeight = new JComboBox<>();
		scrollableHeight.addItem( ScrollablePanel.ScrollableSizeHint.NONE );
		scrollableHeight.addItem( ScrollablePanel.ScrollableSizeHint.FIT );
		scrollableHeight.addItem( ScrollablePanel.ScrollableSizeHint.STRETCH );
		r2.add( scrollableHeight );
		panel.add( r2 );

		JPanel r3 = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		r3.add( new JLabel("Scrollable Width Hint:") );
		scrollableWidth = new JComboBox<>();
		scrollableWidth.addItem( ScrollablePanel.ScrollableSizeHint.NONE );
		scrollableWidth.addItem( ScrollablePanel.ScrollableSizeHint.FIT );
		scrollableWidth.addItem( ScrollablePanel.ScrollableSizeHint.STRETCH );
		r3.add( scrollableWidth );
		panel.add( r3 );


		JPanel r4 = new JPanel( new GridLayout(1, 0, 5, 5) );
		r4.setBorder( new TitledBorder("Vertical Unit Increment:") );
		verticalUnitPercent = new JRadioButton("Percent");
		verticalUnitPercent.setSelected( true );
		r4.add( verticalUnitPercent );
		verticalUnitPixels = new JRadioButton("Pixels");
		r4.add( verticalUnitPixels );
		ButtonGroup vuGroup = new ButtonGroup();
		vuGroup.add( verticalUnitPercent );
		vuGroup.add( verticalUnitPixels );
		verticalUnitAmount = new JSpinner(new SpinnerNumberModel(10, 0, 500 ,5));
		r4.add( verticalUnitAmount );
		panel.add( r4 );

		JPanel r5 = new JPanel( new GridLayout(1, 0, 5, 5) );
		r5.setBorder( new TitledBorder("Vertical Block Increment:") );
		verticalBlockPercent = new JRadioButton("Percent");
		verticalBlockPercent.setSelected( true );
		r5.add( verticalBlockPercent );
		verticalBlockPixels = new JRadioButton("Pixels");
		r5.add( verticalBlockPixels );
		ButtonGroup vbGroup = new ButtonGroup();
		vbGroup.add( verticalBlockPercent );
		vbGroup.add( verticalBlockPixels );
		verticalBlockAmount = new JSpinner(new SpinnerNumberModel(100, 0, 500 ,5));
		r5.add( verticalBlockAmount );
		panel.add( r5 );

		JPanel r6 = new JPanel( new GridLayout(1, 0, 5, 5) );
		r6.setBorder( new TitledBorder("Horizontal Unit Increment:") );
		horizontalUnitPercent = new JRadioButton("Percent");
		horizontalUnitPercent.setSelected( true );
		r6.add( horizontalUnitPercent );
		horizontalUnitPixels = new JRadioButton("Pixels");
		r6.add( horizontalUnitPixels );
		ButtonGroup huGroup = new ButtonGroup();
		huGroup.add( horizontalUnitPercent );
		huGroup.add( horizontalUnitPixels );
		horizontalUnitAmount = new JSpinner(new SpinnerNumberModel(10, 0, 500 ,5));
		r6.add( horizontalUnitAmount );
		panel.add( r6 );

		JPanel r7 = new JPanel( new GridLayout(1, 0, 5, 5) );
		r7.setBorder( new TitledBorder("Horizontal Block Increment:") );
		horizontalBlockPercent = new JRadioButton("Percent");
		horizontalBlockPercent.setSelected( true );
		r7.add( horizontalBlockPercent );
		horizontalBlockPixels = new JRadioButton("Pixels");
		r7.add( horizontalBlockPixels );
		ButtonGroup hvGroup = new ButtonGroup();
		hvGroup.add( horizontalBlockPercent );
		hvGroup.add( horizontalBlockPixels );
		horizontalBlockAmount = new JSpinner(new SpinnerNumberModel(100, 0, 500 ,5));
		r7.add( horizontalBlockAmount );
		panel.add( r7 );

		panel.add(Box.createGlue(), Float.valueOf(1));


		JButton button = new JButton("Recreate Scrollable Panel");
		button.addActionListener( this );
		panel.add( button );

		return panel;
	}

	private void createLayout()
	{
		int columns = (Integer)gridColumns.getValue();
		ScrollablePanel panel = new ScrollablePanel( new GridLayout(0, columns) );

		panel.setScrollableHeight( (ScrollablePanel.ScrollableSizeHint)scrollableHeight.getSelectedItem() );
		panel.setScrollableWidth( (ScrollablePanel.ScrollableSizeHint)scrollableWidth.getSelectedItem() );

		ScrollablePanel.IncrementType type;
		type = verticalUnitPercent.isSelected() ? ScrollablePanel.IncrementType.PERCENT : ScrollablePanel.IncrementType.PIXELS;
		panel.setScrollableUnitIncrement(ScrollablePanel.VERTICAL, type, (Integer)verticalUnitAmount.getValue());
		type = verticalBlockPercent.isSelected() ? ScrollablePanel.IncrementType.PERCENT : ScrollablePanel.IncrementType.PIXELS;
		panel.setScrollableBlockIncrement(ScrollablePanel.VERTICAL, type, (Integer)verticalBlockAmount.getValue());
		type = horizontalUnitPercent.isSelected() ? ScrollablePanel.IncrementType.PERCENT : ScrollablePanel.IncrementType.PIXELS;
		panel.setScrollableUnitIncrement(ScrollablePanel.HORIZONTAL, type, (Integer)horizontalUnitAmount.getValue());
		type = horizontalBlockPercent.isSelected() ? ScrollablePanel.IncrementType.PERCENT : ScrollablePanel.IncrementType.PIXELS;
		panel.setScrollableBlockIncrement(ScrollablePanel.HORIZONTAL, type, (Integer)horizontalBlockAmount.getValue());

		createPanel( panel );
	}

	private void createPanel(JPanel panel)
	{
		for ( int i = 0; i < 40; i++ )
			panel.add( new JButton( "Button" + i ) );

		scrollPane.setViewportView(panel);
	}

	public void actionPerformed(ActionEvent e)
	{
		createLayout();
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
		JFrame frame = new JFrame("Scrollable Panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new ScrollablePanelDemo() );
		frame.setSize(560, 440);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
