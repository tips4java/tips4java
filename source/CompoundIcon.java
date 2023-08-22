import java.awt.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 *  The CompoundIcon will paint two, or more, Icons as a single Icon. The
 *  Icons are painted in the order in which they are added.
 *
 *  The Icons are layed out on the specified axis:
 * <ul>
 * <li>X-Axis (horizontally)
 * <li>Y-Axis (vertically)
 * <li>Z-Axis (stacked)
 * </ul>
 *
 */
public class CompoundIcon implements Icon
{
	public enum Axis
	{
		X_AXIS,
		Y_AXIS,
		Z_AXIS;
	}

	public final static float TOP = 0.0f;
	public final static float LEFT = 0.0f;
	public final static float CENTER = 0.5f;
	public final static float BOTTOM = 1.0f;
	public final static float RIGHT = 1.0f;

	private Icon[] icons;
	private Rectangle[] bounds;
	private HashMap<Component, Point> iconOffset = new HashMap<Component, Point>();

    private Axis axis;

	private int gap;

    private float alignmentX = CENTER;
    private float alignmentY = CENTER;

	/**
	 *  Convenience contructor for creating a CompoundIcon where the
	 *  icons are layed out on on the X-AXIS, the gap is 0 and the
	 *  X/Y alignments will default to CENTER.
	 *
	 *  @param icons  the Icons to be painted as part of the CompoundIcon
	 */
    public CompoundIcon(Icon... icons)
    {
    	this(Axis.X_AXIS, icons);
    }

	/**
	 *  Convenience contructor for creating a CompoundIcon where the
	 *  gap is 0 and the X/Y alignments will default to CENTER.
	 *
	 *  @param axis   the axis used to lay out the icons for painting.
	 *                Must be one of the Axis enums: X_AXIS, Y_AXIS, Z_Axis.
	 *  @param icons  the Icons to be painted as part of the CompoundIcon
	 */
    public CompoundIcon(Axis axis, Icon... icons)
    {
    	this(axis, 0, icons);
    }

	/**
	 *  Convenience contructor for creating a CompoundIcon where the
	 *  X/Y alignments will default to CENTER.
	 *
	 *  @param axis   the axis used to lay out the icons for painting
	 *                Must be one of the Axis enums: X_AXIS, Y_AXIS, Z_Axis.
	 *  @param gap    the gap between the icons
	 *  @param icons  the Icons to be painted as part of the CompoundIcon
	 */
	public CompoundIcon(Axis axis, int gap, Icon... icons)
	{
    	this(axis, gap, CENTER, CENTER, icons);
	}

	/**
	 *  Create a CompoundIcon specifying all the properties.
	 *
	 *  @param axis        the axis used to lay out the icons for painting
	 *                     Must be one of the Axis enums: X_AXIS, Y_AXIS, Z_Axis.
	 *  @param gap         the gap between the icons
	 *  @param alignmentX  the X alignment of the icons. Common values are
	 *                     LEFT, CENTER, RIGHT. Can be any value between 0.0 and 1.0
	 *  @param alignmentY  the Y alignment of the icons. Common values are
	 *                     TOP, CENTER, BOTTOM. Can be any value between 0.0 and 1.0
	 *  @param icons       the Icons to be painted as part of the CompoundIcon
	 */
	public CompoundIcon(Axis axis, int gap, float alignmentX, float alignmentY, Icon... icons)
	{
		this.axis = axis;
		this.gap = gap;
		this.alignmentX = alignmentX > 1.0f ? 1.0f : alignmentX < 0.0f ? 0.0f : alignmentX;
		this.alignmentY = alignmentY > 1.0f ? 1.0f : alignmentY < 0.0f ? 0.0f : alignmentY;

		for (int i = 0; i < icons.length; i++)
		{
			if (icons[i] == null)
			{
				String message = "Icon (" + i + ") cannot be null";
				throw new IllegalArgumentException( message );
			}
		}

		this.icons = icons;

		determineIconBounds();
	}

	/**
	 *  Get the Axis along which each icon is painted.
	 *
	 *  @return the Axis
	*/
	public Axis getAxis()
	{
		return axis;
	}

	/**
	 *  Get the gap between each icon
	 *
	 *  @return the gap in pixels
	 */
	public int getGap()
	{
		return gap;
	}

	/**
	 *  Get the alignment of the icon on the x-axis
	 *
	 *  @return the alignment
	 */
	public float getAlignmentX()
	{
		return alignmentX;
	}

	/**
	 *  Get the alignment of the icon on the y-axis
	 *
	 *  @return the alignment
	 */
	public float getAlignmentY()
	{
		return alignmentY;
	}

	/**
	 *  Get the number of Icons contained in this CompoundIcon.
	 *
	 *  @return the total number of Icons
	 */
	public int getIconCount()
	{
		return icons.length;
	}

	/**
	 *  Get the Icon at the specified index.
	 *
	 *  @param index  the index of the Icon to be returned
	 *  @return  the Icon at the specifed index
	 *  @exception IndexOutOfBoundsException  if the index is out of range
	 */
	public Icon getIcon(int index)
	{
		return icons[ index ];
	}

	/**
	 *  Get IconInformation at the specified point for a given component. The
	 *  icon information will include the icon, the bounds of the icon and the
	 *  Point relative to the icon. null is returned when no icon is found at
	 *  the specified point.
	 *
	 *  @param point  A specific point of the component.
	 *  @param component the component which is displaying the CompoundIcon
	 *  @return  IconInfo of the Icon at the specifed point or null.
	 */
	public IconInfo getIconInfoAtPoint(Point point, Component component)
	{
		//  An Icon can be shared by multiple components. Get the offset where
		//  this Icon was painted.

		Point offset = iconOffset.get(component);

		if (offset != null)
		{
			point.x -= offset.x;
			point.y -= offset.y;
		}

		//  Start searching Icon bound from the last Icon painted in case
		//  Icons overlap one another.

		for (int i = bounds.length - 1; i >= 0; i--)
		{
			Rectangle r = bounds[i];

			if ( r.contains(point) )
			{
				Icon icon = icons[i];

				//  Recursively invoke this method, this time with a null
				//  component, since the offset has been recalculated.

				if (icon instanceof CompoundIcon)
				{
					CompoundIcon compound = (CompoundIcon)icon;
					return compound.getIconInfoAtPoint(new Point(point.x - r.x, point.y - r.y), null);
				}
				else
				{
                	Point p = new Point(point.x - r.x, point.y - r.y);
                	return new IconInfo(icon, r, p);
				}
			}
		}

		return null;
	}


	/*
	 *  Get IconInfo for every Icon
	 *
	 *  @return  IconInfo for every Icon.
	 *
	 */
	public List<IconInfo> getIconInfo()
	{
		ArrayList<IconInfo> list = new ArrayList<IconInfo>();
		Point offset = new Point(0, 0);
        getIconInfo(list, offset);

		return list;
	}

	private void getIconInfo(ArrayList<IconInfo> list, Point offset)
	{
		for (int i = 0; i < icons.length; i++)
		{
			Rectangle r = bounds[i];

			if (icons[i] instanceof CompoundIcon)
			{
				CompoundIcon compound = (CompoundIcon)icons[i];
				compound.getIconInfo(list, new Point(offset.x + r.x, offset.y + r.y));
			}
			else
			{
				r = new Rectangle(r.x + offset.x, r.y + offset.y, r.width, r.height);
				list.add( new IconInfo(icons[i], r, null) );
			}
		}
	}

	/*
	 *  Helper class to contain information about an Icon contained in the
	 *  CompoundIcon. The bounds will always be returned for the specified
	 *  Icon. The relative point is only returned when a lookup for a single
	 *  Icon was done for a specific point. Otherwise (0, 0) is returned.
	 */
	static class IconInfo
	{
		private Icon icon;
		private Rectangle bounds;
		private Point point;

		public IconInfo(Icon icon, Rectangle bounds, Point point)
		{
			this.icon = icon;
			this.bounds = bounds;
			this.point = point;
		}

		public Icon getIcon()
		{
			return icon;
		}

		public Rectangle getBounds()
		{
			return bounds;
		}

		public Point getPoint()
		{
			return point;
		}
	}
//
//  Implement the Icon Interface
//
	/**
	 *  Gets the width of this icon.
	 *
	 *  @return the width of the icon in pixels.
	 */
	@Override
    public int getIconWidth()
    {
		int width = 0;

		//  Add the width of all Icons while also including the gap

    	if (axis == Axis.X_AXIS)
    	{
    		width += (icons.length - 1) * gap;

			for (Icon icon : icons)
				width += icon.getIconWidth();
    	}
		else  //  Just find the maximum width
		{
			for (Icon icon : icons)
				width = Math.max(width, icon.getIconWidth());
		}

		return width;
    }

	/**
	 *  Gets the height of this icon.
	 *
	 *  @return the height of the icon in pixels.
	 */
	@Override
    public int getIconHeight()
    {
		int height = 0;

		//  Add the height of all Icons while also including the gap

    	if (axis == Axis.Y_AXIS)
    	{
    		height += (icons.length - 1) * gap;

			for (Icon icon : icons)
				height += icon.getIconHeight();
    	}
		else  //  Just find the maximum height
		{
			for (Icon icon : icons)
				height = Math.max(height, icon.getIconHeight());
		}

		return height;
    }

   /**
    *  Paint the icons of this compound icon at the specified location
    *
    *  @param c The component on which the icon is painted
    *  @param g the graphics context
    *  @param x the X coordinate of the icon's top-left corner
    *  @param y the Y coordinate of the icon's top-left corner
    */
	@Override
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
    	//  An Icon can be used by multiple components. The Icon can be
    	//  painted at a different offset in each of these components.
    	//  Save the offset for each component.

		iconOffset.put(c, new Point(x, y));

		for (int i = 0; i < icons.length; i++)
		{
			Icon icon = icons[i];
			Rectangle r = bounds[i];
			icon.paintIcon(c, g, r.x + x, r.y + y);
		}
    }

	/*
	 *	Determine the bounds of each Icon contained within the CompoundIcon
	 */
	private void determineIconBounds()
	{
		bounds = new Rectangle[icons.length];
		int index = 0;

    	if (axis == Axis.X_AXIS)
    	{
    		int x = 0;
    		int height = getIconHeight();

			for (Icon icon : icons)
			{
				int iconY = getOffset(height, icon.getIconHeight(), alignmentY);
				bounds[index] = new Rectangle(x, iconY, icon.getIconWidth(), icon.getIconHeight());
				x += icon.getIconWidth() + gap;
				index++;
			}
    	}
    	else if (axis == Axis.Y_AXIS)
    	{
    		int y = 0;
    		int width = getIconWidth();

			for (Icon icon : icons)
			{
				int iconX = getOffset(width, icon.getIconWidth(), alignmentX);
				bounds[index] = new Rectangle(iconX, y, icon.getIconWidth(), icon.getIconHeight());
				y += icon.getIconHeight() + gap;
				index++;
			}
    	}
    	else // must be Z_AXIS
    	{
    		int width = getIconWidth();
    		int height = getIconHeight();

			for (Icon icon : icons)
			{
				int iconX = getOffset(width, icon.getIconWidth(), alignmentX);
				int iconY = getOffset(height, icon.getIconHeight(), alignmentY);
				bounds[index] = new Rectangle(iconX, iconY, icon.getIconWidth(), icon.getIconHeight());
				index++;
			}
    	}
	}

	/*
	 *  When the icon value is smaller than the maximum value of all icons the
	 *  icon needs to be aligned appropriately. Calculate the offset to be used
	 *  when painting the icon to achieve the proper alignment.
	 */
	private int getOffset(int maxValue, int iconValue, float alignment)
	{
		float offset = (maxValue - iconValue) * alignment;
		return Math.round(offset);
	}
}
