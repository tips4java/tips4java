import java.awt.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class OverlapLayoutDemo
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
		JFrame frame = new JFrame("Overlap Layout");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new DemoPanel() );
		frame.setSize(600, 340);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	static class DemoPanel extends JPanel
		implements ActionListener, MouseListener
	{
		private JScrollPane scrollPane;
		private JRadioButton above;
		private JRadioButton below;
		private JSpinner xOffset;
		private JSpinner yOffset;
		private JSpinner top;
		private JSpinner bottom;
		private JSpinner left;
		private JSpinner right;
		private JCheckBox includeInvisible;
		private ImageIcon duke;
		private OverlapLayout layout;

		public DemoPanel()
		{
			String path = "dukeWaveRed.gif";
			java.net.URL imgURL = getClass().getResource(path);
        	duke = new ImageIcon(imgURL, path);

			setLayout( new BorderLayout() );

			scrollPane = new JScrollPane();
			add(scrollPane);

			add(createControlPanel(), BorderLayout.EAST);

			JButton button = new JButton("Recreate Layout With Cards");
			button.addActionListener( this );
			add(button, BorderLayout.SOUTH);

			createLayout();
		}

		private JPanel createControlPanel()
		{
			RelativeLayout rl = new RelativeLayout(RelativeLayout.Y_AXIS);
			rl.setAlignment(RelativeLayout.LEADING);
			rl.setFill( true );
			JPanel panel = new JPanel( rl );
			panel.setBorder( new TitledBorder("Configure Layout") );

			JPanel r1 = new JPanel( new GridLayout(1, 0, 5, 5) );
			r1.setBorder( new TitledBorder("Overlap:") );
			ButtonGroup bg1 = new ButtonGroup();
			above = new JRadioButton("Above");
			above.setSelected( true );
			r1.add( above );
			bg1.add( above );
			below = new JRadioButton("Below");
			r1.add( below );
			bg1.add( below );
			panel.add( r1 );

			JPanel r2 = new JPanel( new GridLayout(1, 0, 5, 5) );
			r2.setBorder( new TitledBorder("Overlap Position:") );
			xOffset = new JSpinner(new SpinnerNumberModel(20, -100, 100 ,5));
			r2.add( xOffset );
			r2.add( new JLabel("X") );
			yOffset = new JSpinner(new SpinnerNumberModel(0, -100, 100 ,5));
			r2.add( yOffset );
			r2.add( new JLabel("Y") );
			panel.add( r2 );

			JPanel r3 = new JPanel( new GridLayout(2, 0, 5, 5) );
			r3.setBorder( new TitledBorder("Popup Insets:") );
			top = new JSpinner(new SpinnerNumberModel(20, 0, 100 ,5));
			r3.add( top );
			r3.add( new JLabel("Top") );
			bottom = new JSpinner(new SpinnerNumberModel(0, 0, 100 ,5));
			r3.add( bottom );
			r3.add( new JLabel("Bottom") );
			left = new JSpinner(new SpinnerNumberModel(0, 0, 100 ,5));
			r3.add( left );
			r3.add( new JLabel("Left") );
			right = new JSpinner(new SpinnerNumberModel(0, 0, 100 ,5));
			r3.add( right );
			r3.add( new JLabel("Right") );
			panel.add( r3 );

			includeInvisible = new JCheckBox("Include Invisible");
			includeInvisible.setSelected( true );
			panel.add( includeInvisible );

//			panel.add(Box.createGlue(), new Float(1));
			panel.add(Box.createGlue(), Float.valueOf(1));

			JLabel label = new JLabel("Note: click on card for \"popup\"");
			panel.add( label );

			return panel;
		}

		private void createLayout()
		{
			Point overlap = new Point((Integer)xOffset.getValue(), (Integer)yOffset.getValue());
			layout = new OverlapLayout(overlap, above.isSelected());
			Insets popupInsets = new Insets(
				(Integer)top.getValue(), (Integer)left.getValue(), (Integer)bottom.getValue(), (Integer)right.getValue());
			layout.setPopupInsets( popupInsets );
			layout.setIncludeInvisible( includeInvisible.isSelected() );

			createPanel( layout );
		}

		private void createPanel(OverlapLayout lm)
		{
			JPanel panel = new JPanel(lm);
			panel.setBorder( new EmptyBorder(10, 10, 10, 10) );

			createCard(panel, "1", duke, false) ;
			createCard(panel, "2", duke, false) ;
			createCard(panel, "3", duke, false) ;
			createCard(panel, "4", duke, true) ;
			createCard(panel, "5", duke, false) ;

			Component c = panel.getComponent( layout.convertIndex(1) );
			layout.addLayoutComponent(c, OverlapLayout.POP_UP);

			scrollPane.setViewportView(panel);
		}

		private void createCard(Container c, String text, Icon icon, boolean invisible)
		{
			JPanel card = createCard(text, icon);
			c.add(card);

			if (invisible)
			{
				card.setVisible(false);
			}
		}

		public JPanel createCard(String text, Icon icon)
		{
			JPanel card = new JPanel( new BorderLayout() );
			card.setToolTipText("Duke " + text);
			card.setBackground( Color.WHITE );
			card.setBorder( new LineBorder(Color.BLACK) );

			JLabel north = new JLabel( text );
			north.setHorizontalAlignment(JLabel.LEFT);
			JLabel center = new JLabel( icon );
			JLabel south = new JLabel( text );
			south.setHorizontalAlignment(JLabel.RIGHT);

			card.add(north, BorderLayout.NORTH);
			card.add(center);
			card.add(south, BorderLayout.SOUTH);

			card.setName(text);
			card.addMouseListener( this );

			return card;
		}

		public void actionPerformed(ActionEvent e)
		{
			createLayout();
		}

	    public void mousePressed(MouseEvent e)
	    {
			Component c = e.getComponent();
			Boolean constraint = layout.getConstraints(c);

			if (constraint == null || constraint == OverlapLayout.POP_DOWN)
				layout.addLayoutComponent(c, OverlapLayout.POP_UP);
			else
				layout.addLayoutComponent(c, OverlapLayout.POP_DOWN);

			((JComponent)c).revalidate();
	    }

       	public void mouseMoved(MouseEvent e) {}
	    public void mouseClicked(MouseEvent e) {}
   		public void mouseEntered(MouseEvent e) {}
	    public void mouseExited(MouseEvent e) {}
	    public void mouseReleased(MouseEvent e) {}
	}
}
