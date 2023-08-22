import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *  Note: Normally the ButtonPanel and DrawingArea would not be static classes.
 *  This was done for the convenience of posting the code in one class and to
 *  highlight the differences between the two approaches. All the differences
 *  are found in the DrawingArea class.
 */
public class DrawOnComponent
{
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI()
	{
		DrawingArea drawingArea = new DrawingArea();
		drawingArea.addRectangle(new Rectangle(10, 10, 200, 100), Color.RED);
		drawingArea.addRectangle(new Rectangle(210, 110, 20, 100), Color.BLUE);
		ButtonPanel buttonPanel = new ButtonPanel( drawingArea );

		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Draw On Component");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.getContentPane().add(drawingArea);
		frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		frame.setSize(400, 400);
		frame.setLocationRelativeTo( null );
		frame.setVisible(true);
	}

	static class ButtonPanel extends JPanel implements ActionListener
	{
		private DrawingArea drawingArea;

		public ButtonPanel(DrawingArea drawingArea)
		{
			this.drawingArea = drawingArea;

			add( createButton("	", Color.BLACK) );
			add( createButton("	", Color.RED) );
			add( createButton("	", Color.GREEN) );
			add( createButton("	", Color.BLUE) );
			add( createButton("	", Color.ORANGE) );
			add( createButton("	", Color.YELLOW) );
			add( createButton("Clear Drawing", null) );
		}

		private JButton createButton(String text, Color background)
		{
			JButton button = new JButton( text );
			button.setBackground( background );
			button.addActionListener( this );

			return button;
		}

		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton)e.getSource();

			if ("Clear Drawing".equals(e.getActionCommand()))
				drawingArea.clear();
			else
				drawingArea.setForeground( button.getBackground() );
		}
	}

	static class DrawingArea extends JPanel
	{
		private final static int AREA_SIZE = 400;
		private ArrayList<ColoredRectangle> coloredRectangles = new ArrayList<ColoredRectangle>();
		private Rectangle shape;

		public DrawingArea()
		{
			setBackground(Color.WHITE);

			MyMouseListener ml = new MyMouseListener();
			addMouseListener(ml);
			addMouseMotionListener(ml);
		}

		@Override
		public Dimension getPreferredSize()
		{
			return isPreferredSizeSet() ?
				super.getPreferredSize() : new Dimension(AREA_SIZE, AREA_SIZE);
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			//  Custom code to paint all the Rectangles from the List

			Graphics2D g2d = (Graphics2D)g;
			Color foreground = g2d.getColor();

			g2d.setColor( Color.BLACK );
			g2d.drawString("Add a rectangle by doing mouse press, drag and release!", 40, 15);

			for (DrawingArea.ColoredRectangle cr : coloredRectangles)
			{
				g2d.setColor( cr.getForeground() );
				g2d.draw( cr.getRectangle() );
			}

			//  Paint the Rectangle as the mouse is being dragged

			if (shape != null)
			{
				g2d.setColor( foreground );
				g2d.draw( shape );
			}
		}

		public void addRectangle(Rectangle rectangle, Color color)
		{
			//  Add the Rectangle to the List so it can be repainted

			ColoredRectangle cr = new ColoredRectangle(color, rectangle);
			coloredRectangles.add( cr );
			repaint();
		}

		public void clear()
		{
			coloredRectangles.clear();
			repaint();
		}

		class MyMouseListener extends MouseInputAdapter
		{
			private Point startPoint;

			public void mousePressed(MouseEvent e)
			{
				startPoint = e.getPoint();
				shape = new Rectangle();
			}

			public void mouseDragged(MouseEvent e)
			{
				int x = Math.min(startPoint.x, e.getX());
				int y = Math.min(startPoint.y, e.getY());
				int width = Math.abs(startPoint.x - e.getX());
				int height = Math.abs(startPoint.y - e.getY());

				shape.setBounds(x, y, width, height);
				repaint();
			}

			public void mouseReleased(MouseEvent e)
			{
				if (shape.width != 0 || shape.height != 0)
				{
					addRectangle(shape, e.getComponent().getForeground());
				}

				shape = null;
			}
		}

		class ColoredRectangle
		{
			private Color foreground;
			private Rectangle rectangle;

			public ColoredRectangle(Color foreground, Rectangle rectangle)
			{
				this.foreground = foreground;
				this.rectangle = rectangle;
			}

			public Color getForeground()
			{
				return foreground;
			}

			public void setForeground(Color foreground)
			{
				this.foreground = foreground;
			}

			public Rectangle getRectangle()
			{
				return rectangle;
			}
		}
	}
}
