import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class HSLColorDemo extends JPanel
	implements ChangeListener
{
	private JSpinner red;
	private JSpinner green;
	private JSpinner blue;
	private JSpinner hue;
	private JSpinner saturation;
	private JSpinner luminance;
	private JPanel theColor;
	private HSLColor hslColor;
	private JTabbedPane tabbedPane;
	private JRadioButton useRGB;
	private JRadioButton useHSL;

	HSLColorDemo()
	{
		setBorder( new EmptyBorder(10, 10, 20, 10) );
		RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS, 20);
		rl.setBorderGap(0);
		setLayout( rl );

		JPanel north = createNorthPanel();

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Hue", (Component)null);
		tabbedPane.addTab("Saturation", (Component)null);
		tabbedPane.addTab("Luminance", (Component)null);
		tabbedPane.addTab("Tone", (Component)null);
		tabbedPane.addTab("Shade", (Component)null);

		add(north);
		add(tabbedPane);

		red.setValue(111);
		green.setValue(140);
		blue.setValue(222);
		red.addChangeListener( this );
		green.addChangeListener( this );
		blue.addChangeListener( this );
		hue.addChangeListener( this );
		saturation.addChangeListener( this );
		luminance.addChangeListener( this );
		useRGB.setSelected( true );
		updateHSL();
	}

	private JPanel createNorthPanel()
	{
		RelativeLayout rl = new RelativeLayout(RelativeLayout.X_AXIS, 10);
		JPanel panel = new JPanel( rl );

		//  Left for RGB

		RelativeLayout rlLeft = new RelativeLayout(RelativeLayout.Y_AXIS);
		rlLeft.setAlignment(RelativeLayout.LEADING);
		JPanel left = new JPanel( rlLeft );
		left.setBorder( new TitledBorder("RGB") );

		JPanel l1 = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		red = new JSpinner(new SpinnerNumberModel(0, 0, 255 ,10));
		l1.add( red );
		l1.add( new JLabel("Red") );
		left.add( l1 );

		JPanel l2 = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		green = new JSpinner(new SpinnerNumberModel(0, 0, 255 ,10));
		l2.add( green );
		l2.add( new JLabel("Green") );
		left.add( l2 );

		JPanel l3 = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		blue = new JSpinner(new SpinnerNumberModel(0, 0, 255 ,10));
		l3.add( blue );
		l3.add( new JLabel("Blue") );
		left.add( l3 );

		//  Center

		theColor = new JPanel();
		theColor.setBorder( new LineBorder(Color.BLACK) );
		theColor.setPreferredSize( new Dimension(50, 100) );

		//  Right for HSL

		RelativeLayout rlRight = new RelativeLayout(RelativeLayout.Y_AXIS);
		rlRight.setAlignment(RelativeLayout.LEADING);
		JPanel right = new JPanel( rlRight );
		right.setBorder( new TitledBorder("HSL") );

		JPanel r1 = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		hue = new JSpinner(new SpinnerNumberModel(0, 0, 360 ,10));
		r1.add( hue );
		r1.add( new JLabel("Hue") );
		right.add( r1 );

		JPanel r2 = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		saturation = new JSpinner(new SpinnerNumberModel(0, 0, 100 ,5));
		r2.add( saturation );
		r2.add( new JLabel("Saturation") );
		right.add( r2 );

		JPanel r3 = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		luminance = new JSpinner(new SpinnerNumberModel(0, 0, 100 ,5));
		r3.add( luminance );
		r3.add( new JLabel("Luminance") );
		right.add( r3 );

		//  Create radio buttons

		useRGB = new JRadioButton("Use RGB values");
		useHSL = new JRadioButton("Use HSL values");

		ButtonGroup bg = new ButtonGroup();
		bg.add( useRGB );
		bg.add( useHSL );

		//  Add components to the panel

		panel.add(Box.createGlue(), new Float(1));
		panel.add( useRGB );
		panel.add(left);
		panel.add(theColor);
		panel.add(right);
		panel.add( useHSL );
		panel.add(Box.createGlue(), new Float(1));
		return panel;
	}

	private void updateHSL()
	{
		Color rgb = new Color(
			(Integer)red.getValue(), (Integer)green.getValue(), (Integer)blue.getValue());
		theColor.setBackground( rgb );
		hslColor = new HSLColor( rgb );
		hue.setValue((int)hslColor.getHue());
		saturation.setValue((int)hslColor.getSaturation());
		luminance.setValue((int)hslColor.getLuminance());
		updateTabbedPane();
	}

	private void updateRGB()
	{
		hslColor = new HSLColor(
			(Integer)hue.getValue(), (Integer)saturation.getValue(), (Integer)luminance.getValue());
		Color rgb = hslColor.getRGB();
		theColor.setBackground( rgb );
		red.setValue((int)rgb.getRed());
		green.setValue((int)rgb.getGreen());
		blue.setValue((int)rgb.getBlue());
		updateTabbedPane();
	}

	private void updateTabbedPane()
	{
		updateHue();
		updateSaturation();
		updateLuminance();
		updateTone();
		updateShade();
	}

	private void updateHue()
	{
		JPanel main = new JPanel( new BorderLayout() );

		JPanel panel1 = new JPanel( new GridLayout(1, 0) );
		JPanel panel2 = new JPanel( new GridLayout(1, 0) );
		int width = 30;
		Dimension example = new Dimension(width, 25);
		Dimension label = new Dimension(width, 100);
		int saturation = (int)hslColor.getHue();

		for (int i = 0; i < 25; i++)
		{
			JLabel label1 = new JLabel();
			label1.setOpaque(true);
			int degrees = ((i * 15) + saturation) % 360;
			label1.setBackground( hslColor.adjustHue(degrees) );
			label1.setPreferredSize( label );
			label1.setToolTipText( label1.getBackground().toString() );
			panel1.add( label1 );

			JLabel label2 = new JLabel("" + degrees);
			label2.setHorizontalAlignment(JLabel.CENTER);
			label2.setPreferredSize( example );
			panel2.add( label2 );
		}

		main.add(panel1, BorderLayout.NORTH);
		main.add(panel2, BorderLayout.CENTER);
		tabbedPane.setComponentAt(0, main);
	}

	private void updateSaturation()
	{
		JPanel main = new JPanel( new BorderLayout() );

		JPanel panel1 = new JPanel( new GridLayout(1, 0) );
		JPanel panel2 = new JPanel( new GridLayout(1, 0) );
		int width = 30;
		Dimension example = new Dimension(width, 25);
		Dimension label = new Dimension(width, 100);

		for (int i = 100; i >= 0; i -= 5)
		{
			JLabel label1 = new JLabel();
			label1.setOpaque(true);
			label1.setBackground( hslColor.adjustSaturation(i) );
			label1.setPreferredSize( label );
			label1.setToolTipText( label1.getBackground().toString() );
			panel1.add( label1 );

			JLabel label2 = new JLabel("" + i);
			label2.setHorizontalAlignment(JLabel.CENTER);
			label2.setPreferredSize( example );
			panel2.add( label2 );
		}

		main.add(panel1, BorderLayout.NORTH);
		main.add(panel2, BorderLayout.CENTER);
		tabbedPane.setComponentAt(1, main);
	}

	private void updateLuminance()
	{
		JPanel main = new JPanel( new BorderLayout() );

		JPanel panel1 = new JPanel( new GridLayout(1, 0) );
		JPanel panel2 = new JPanel( new GridLayout(1, 0) );
		int width = 30;
		Dimension example = new Dimension(width, 25);
		Dimension label = new Dimension(width, 100);

		for (int i = 100; i >= 0; i -= 5)
		{
			JLabel label1 = new JLabel();
			label1.setOpaque(true);
			label1.setBackground( hslColor.adjustLuminance(i) );
			label1.setPreferredSize( label );
			label1.setToolTipText( label1.getBackground().toString() );
			panel1.add( label1 );

			JLabel label2 = new JLabel("" + i);
			label2.setHorizontalAlignment(JLabel.CENTER);
			label2.setPreferredSize( example );
			panel2.add( label2 );
		}

		main.add(panel1, BorderLayout.NORTH);
		main.add(panel2, BorderLayout.CENTER);
		tabbedPane.setComponentAt(2, main);
	}

	private void updateTone()
	{
		JPanel main = new JPanel( new BorderLayout() );

		JPanel panel1 = new JPanel( new GridLayout(1, 0) );
		JPanel panel2 = new JPanel( new GridLayout(1, 0) );
		int width = 30;
		Dimension example = new Dimension(width, 25);
		Dimension label = new Dimension(width, 100);

		for (int i = 1; i < 11; i++)
		{
			JLabel label1 = new JLabel();
			label1.setOpaque(true);
			int percent = i * 5;
			label1.setBackground( hslColor.adjustTone(percent) );
			label1.setPreferredSize( label );
			label1.setToolTipText( label1.getBackground().toString() );
			panel1.add( label1 );

			JLabel label2 = new JLabel(percent + "%");
			label2.setHorizontalAlignment(JLabel.CENTER);
			label2.setPreferredSize( example );
			panel2.add( label2 );
		}

		main.add(panel1, BorderLayout.NORTH);
		main.add(panel2, BorderLayout.CENTER);
		tabbedPane.setComponentAt(3, main);
	}

	private void updateShade()
	{
		JPanel main = new JPanel( new BorderLayout() );

		JPanel panel1 = new JPanel( new GridLayout(1, 0) );
		JPanel panel2 = new JPanel( new GridLayout(1, 0) );
		int width = 30;
		Dimension example = new Dimension(width, 25);
		Dimension label = new Dimension(width, 100);

		for (int i = 1; i < 11; i++)
		{
			JLabel label1 = new JLabel();
			label1.setOpaque(true);
			int percent = i * 5;
			label1.setBackground( hslColor.adjustShade(percent) );
			label1.setPreferredSize( label );
			label1.setToolTipText( label1.getBackground().toString() );
			panel1.add( label1 );

			JLabel label2 = new JLabel(percent + "%");
			label2.setHorizontalAlignment(JLabel.CENTER);
			label2.setPreferredSize( example );
			panel2.add( label2 );
		}

		main.add(panel1, BorderLayout.NORTH);
		main.add(panel2, BorderLayout.CENTER);
		tabbedPane.setComponentAt(4, main);
	}

	public void stateChanged(ChangeEvent e)
	{
		if (useRGB.isSelected())
			updateHSL();
		else
			updateRGB();
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
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("HSLColor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new HSLColorDemo() );
		frame.setSize(800, 360);
//		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
