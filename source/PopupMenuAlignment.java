import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *  Convenience class to align the text of JMenuItems within a specified
 *  JPopupMenu to be either CENTER or TRAILING aligned. The default for
 *  trailing would be right aligned  unless the component orientation is
 *  changed.
 *
 *  The class can be used in one of two ways. For a static popup menu you
 *  can just invoke the statick alignText() method once the menu has been
 *  created. For dynamic popup menus you can use this class as a
 *  ContainerListener on the popup menu so as menu items are added or
 *  removed the remaining items are adjusted accordingly.
 */
public class PopupMenuAlignment implements ContainerListener
{
	public static int CENTER = SwingConstants.CENTER;
	public static int TRAILING  = SwingConstants.TRAILING;

	private int alignment;

	/**
	 *  Use this class as a listener to handle dynamic changes to the
	 *  popup menu.
	 *
	 *  @param alignment  alignment of each menu item. Either
	 *                    CENTER or TRAILING
	 */
	public PopupMenuAlignment(int alignment)
	{
		this.alignment = alignment;
	}
//
//  Implement the ContainerListener
//
//  The menu items in the popup menu are dynamically aligned
//
	public void componentAdded(ContainerEvent e)
	{
		JPopupMenu popup = (JPopupMenu)e.getContainer();
		alignText(popup, alignment);
	}

	public void componentRemoved(ContainerEvent e)
	{
		JPopupMenu popup = (JPopupMenu)e.getContainer();
		alignText(popup, alignment);
	}

	/**
	 *  Align the menu items contained with the popup menu.
	 *
	 *  @param popup      the popup menu
	 *  @param alignment  alignment of each menu item. Either
	 *                    CENTER or TRAILING
	 */
	public static void alignText(JPopupMenu popup, int alignment)
	{
		int maxWidth = 0;

		//  Find the largest text string

		for (int i = 0; i < popup.getComponentCount(); i++)
		{
			Component c = popup.getComponent(i);

			if (c instanceof JMenuItem)
			{
				JMenuItem menuItem = (JMenuItem)c;
				FontMetrics fm = menuItem.getFontMetrics(menuItem.getFont());
				maxWidth = Math.max(maxWidth, fm.stringWidth(menuItem.getText().trim()));
			}
		}

		//  Align each menu item

		for (int i = 0; i < popup.getComponentCount(); i++)
		{
			Component c = popup.getComponent(i);

			if (! (c instanceof JMenuItem)) continue;

			JMenuItem menuItem = (JMenuItem)c;
			String actionCommand = menuItem.getActionCommand();
			String text = menuItem.getText().trim();
			ComponentOrientation orientation = menuItem.getComponentOrientation();

			StringBuffer sb = new StringBuffer();

			//  Padding will be added to the right, so add text now

			if (! orientation.isLeftToRight())
			{
				sb.append( text );
			}

			//  Calculate the number of padding spaces to add

			FontMetrics fm = menuItem.getFontMetrics(menuItem.getFont());
			int spaceWidth = fm.stringWidth(" ") * (alignment == CENTER ? 2 : 1);
			int spaces = (maxWidth - fm.stringWidth(text)) / spaceWidth;

			for (int j = 0; j < spaces; j++)
			{
				sb.append(" ");
			}

			//  Padding has been added to the left, so add text now

			if (orientation.isLeftToRight())
			{
				sb.append( text );
			}

			menuItem.setText(sb.toString());
			menuItem.setActionCommand(actionCommand);
		}
	}
}
