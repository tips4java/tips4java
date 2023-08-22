import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.net.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MotionWithKeyBindings
{
	private JComponent component;

	public MotionWithKeyBindings(JComponent component)
	{
		this.component = component;
	}

	//  Move the component to its new location. The component will stop
	//  moving when it reaches the bounds of its container

	public void move(int deltaX, int deltaY)
	{
		int componentWidth = component.getSize().width;
		int componentHeight = component.getSize().height;

		Dimension parentSize = component.getParent().getSize();
		int parentWidth  = parentSize.width;
		int parentHeight = parentSize.height;

		//  Determine next X position

		int nextX = Math.max(component.getLocation().x + deltaX, 0);

		if ( nextX + componentWidth > parentWidth)
		{
			nextX = parentWidth - componentWidth;
		}

		//  Determine next Y position

		int nextY = Math.max(component.getLocation().y + deltaY, 0);

		if ( nextY + componentHeight > parentHeight)
		{
			nextY = parentHeight - componentHeight;
		}

		//  Move the component

		component.setLocation(nextX, nextY);
	}

	public MotionAction addAction(String name, int deltaX, int deltaY)
	{
		MotionAction action = new MotionAction(name, deltaX, deltaY);

		KeyStroke pressedKeyStroke = KeyStroke.getKeyStroke(name);
		InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(pressedKeyStroke, name);
		component.getActionMap().put(name, action);

		return action;
	}

	private class MotionAction extends AbstractAction
	{
		private int deltaX;
		private int deltaY;

		public MotionAction(String name, int deltaX, int deltaY)
		{
			super(name);
			putValue(Action.ACTION_COMMAND_KEY, name);


			this.deltaX = deltaX;
			this.deltaY = deltaY;
		}

		public void actionPerformed(ActionEvent e)
		{
			move(deltaX, deltaY);
		}
	}

	private static JButton addMotionSupport(JComponent component)
	{
		int delta = 3;
		MotionWithKeyBindings motion = new MotionWithKeyBindings(component);
		motion.addAction("LEFT", -delta,  0);
		motion.addAction("RIGHT", delta,  0);
		motion.addAction("UP",    0, -delta);
		motion.addAction("DOWN",  0,  delta);

		Action left = component.getActionMap().get("LEFT");
		return new JButton(left);
	}

	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				//  Create a panel with a component to be moved

				JPanel content = new JPanel();
				content.setLayout(null);

				JLabel component = new JLabel( new ColorIcon(Color.BLUE, 40, 40) );
				component.setSize( component.getPreferredSize() );
				component.setLocation(100, 100);
				content.add(component);

				JButton left = addMotionSupport( component );

				JFrame.setDefaultLookAndFeelDecorated(true);
				JFrame frame = new JFrame("Motion With Key Bindings");
				frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
				frame.add( content );
				frame.add(left, BorderLayout.PAGE_END);
				frame.setSize(600, 600);
				frame.setLocationByPlatform( true );
				frame.setVisible(true);

			}
		});
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
}
