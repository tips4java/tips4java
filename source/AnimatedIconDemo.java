import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class AnimatedIconDemo extends JPanel
{
	public AnimatedIconDemo()
	{
		setLayout( new BorderLayout() );

		JPanel panel = new JPanel(  );

		JLabel label1 = new JLabel();
		AnimatedIcon icon1 = new AnimatedIcon(label1, 250, 3);

		Icon anIcon = new ColorIcon(Color.BLUE, 70, 20 );
		// Use image for better demo
//		Icon anIcon = new ImageIcon( "???" );
		icon1.addIcon( anIcon );

		for (int angle = 45; angle < 360; angle += 45)
		{
		    icon1.addIcon( new RotatedIcon(anIcon, angle) );
		}

		icon1.setShowFirstIcon( true );
		label1.setIcon( icon1 );

		panel.add(createPanel(label1, "Three Cycles"), BorderLayout.WEST);

		JLabel label2 = new JLabel("Processing ");
		label2.setHorizontalTextPosition( JLabel.LEADING );
		AnimatedIcon icon2 = new AnimatedIcon( label2 );
		icon2.setAlignmentX( AnimatedIcon.LEFT );
		icon2.addIcon( new TextIcon(label2, ".") );
		icon2.addIcon( new TextIcon(label2, "..") );
		icon2.addIcon( new TextIcon(label2, "...") );
		icon2.addIcon( new TextIcon(label2, "....") );
		icon2.addIcon( new TextIcon(label2, ".....") );
		label2.setIcon( icon2 );

		Border title2 = new TitledBorder("Continuous Animation");
		label2.setBorder( new CompoundBorder(title2, new EmptyBorder(0, 25, 0, 25)) );

		JLabel label3 = new JLabel();
		CircularAnimatedIcon icon3 = new CircularAnimatedIcon( label3 );

		int j = 1;

		for (int i = 255; i > 10; i -= 30)
		{
			icon3.addIcon( new ColorIcon( new Color(255-i, i, i), j*5, j*5) );
			j++;
		}

		label3.setIcon( icon3 );

		Border title3 = new TitledBorder("Circular Animation");
		label3.setBorder( new CompoundBorder(title3, new EmptyBorder(0, 25, 0, 25)) );

		add(panel, BorderLayout.WEST);
		add(label2, BorderLayout.CENTER);
		add(label3, BorderLayout.EAST);

		icon1.start();
		icon2.start();
		icon3.start();
	}

	private JPanel createPanel(JLabel label, String title)
	{
		final AnimatedIcon icon = (AnimatedIcon)label.getIcon();

		JPanel east = new JPanel( new GridLayout(0, 1) );

		JButton start = new JButton("start");
		start.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				icon.start();
			}
		});
		east.add(start);

		JButton restart = new JButton("restart");
		restart.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				icon.restart();
			}
		});
		east.add(restart);

		JButton pause = new JButton("pause");
		pause.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				icon.pause();
			}
		});
		east.add(pause);

		JButton stop = new JButton("stop");
		stop.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				icon.stop();
			}
		});
		east.add(stop);

		JPanel panel = new JPanel( new BorderLayout(10, 0) );
		panel.setBorder( new TitledBorder( title ) );
		panel.add(east, BorderLayout.EAST);
		panel.add(label);

		return panel;
	}

    static class ColorIcon implements Icon
    {
        private Color color;
        private int width;
        private int height;

        public ColorIcon(Color color, int width, int height)
        {
            this.color = color;
            this.width = width;
            this.height = height;
        }

        public int getIconWidth()
        {
            return width;
        }

        public int getIconHeight()
        {
            return height;
        }

        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            g.setColor(color);
    		g.fillRect(x, y, width, height);
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
		JFrame frame = new JFrame("Animated Icon");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new AnimatedIconDemo() );
		frame.setSize(450, 180);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
