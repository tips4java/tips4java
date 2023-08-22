import java.awt.*;
import javax.swing.*;

/**
 *  The DragLayout is used as a layout manager for a Container that supports
 *  random dragging of components with the Container.
 *
 *  The location of the component will not be managed by the layout manager.
 *
 *	The size of the component can be either the preferred size of the component,
 *  or the actual size of the component.
 *
 *  The preferred size of the container will calculated normally based on the
 *  actual location and size of each component.
 *
 *  A Component cannot have a location outside the bounds of the parent container.
 *  That is the x/y coordinate must be withing the Insets of the Container. If
 *  any component is outside the bounds, then the location of all components
 *  will be adjusted by the same amount.
 */
public class DragLayout implements LayoutManager, java.io.Serializable
{
	private boolean usePreferredSize;

	/**
	 *  Convenience constructor
	 */
	public DragLayout()
	{
		this(true);
	}

	/**
	 *  Create a DragLayout and indicate how the component size is determined.
	 *
	 *  @param usePreferred size see setPreferredSize() for values.
	 */
	public DragLayout(boolean usePreferredSize)
	{
		setUsePreferredSize( usePreferredSize );
	}

	/**
	 *  Set the use preferred size property
	 *
	 *  @param usePreferredSize when true, use the preferred size of the component
	 *                          in layout calculations. When false, use the size
	 *                          of the component, unless the size is 0, in which
	 *                          case use the preferred size as a default.
	 */
	public void setUsePreferredSize(boolean usePreferredSize)
	{
		this.usePreferredSize = usePreferredSize;
	}

	/**
	 *  Get the use Preferred Size property
	 *
	 *@return the use preferred size property
	 */
	public boolean isUsePreferredSize()
	{
		return usePreferredSize;
	}

	/**
	 * Adds the specified component with the specified name to the layout.
	 * @param name the name of the component
	 * @param comp the component to be added
	 */
	@Override
	public void addLayoutComponent(String name, Component comp) {}


	/**
	 * Removes the specified component from the layout.
	 *
	 * @param comp the component to be removed
	 */
	@Override
	public void removeLayoutComponent(Component component)
	{
	}

	/**
	 *	Determine the minimum size on the Container
	 *
	 *  @param	 target   the container in which to do the layout
	 *  @return	 the minimum dimensions needed to lay out the
	 *			 subcomponents of the specified container
	 */
	@Override
	public Dimension minimumLayoutSize(Container parent)
	{
		synchronized (parent.getTreeLock())
		{
			return preferredLayoutSize(parent);
		}
	}

	/**
	 *	Determine the preferred size on the Container
	 *
	 *  @param	 parent   the container in which to do the layout
	 *  @return  the preferred dimensions to lay out the
	 *	         subcomponents of the specified container
	 */
	@Override
	public Dimension preferredLayoutSize(Container parent)
	{
		synchronized (parent.getTreeLock())
		{
			return getLayoutSize(parent);
		}
	}

	/*
	 *  The calculation for minimum/preferred size is the same.
	 *
	 *  @param	 parent  the container in which to do the layout
	 */
	private Dimension getLayoutSize(Container parent)
	{
		Insets parentInsets = parent.getInsets();
		int x = parentInsets.left;
		int y = parentInsets.top;
		int width = 0;
		int height = 0;

		//  Get extreme values of the components on the container.
		//  The x/y values represent the starting point relative to the
		//  top/left of the container. The width/height values represent
		//  the bottom/right value within the container.

		for (Component component: parent.getComponents())
		{
			if (component.isVisible())
			{
				Point p = component.getLocation();
				Dimension d = getActualSize(component);
				x = Math.min(x, p.x);
				y = Math.min(y, p.y);
				width = Math.max(width, p.x + d.width);
				height = Math.max(height, p.y + d.height);
			}
		}

		// Width/Height is adjusted if any component is outside left/top edge

		if (x < parentInsets.left)
			width += parentInsets.left - x;

		if (y < parentInsets.top)
			height += parentInsets.top - y;

		//  Adjust for insets

		width += parentInsets.right;
		height += parentInsets.bottom;
		Dimension d = new Dimension(width, height);

		return d;
	}

	private Dimension getActualSize(Component component)
	{
		if (usePreferredSize)
			return component.getPreferredSize();

		//  Use the preferred size as a default when a size has not been set.

		Dimension d = component.getSize();

		if (d.width == 0 || d.height == 0)
			return component.getPreferredSize();
		else
			return d;
	}

	/**
	 * Lays out the specified container using this layout.
	 *
	 * @param	  target   the container in which to do the layout
	 */
	@Override
	public void layoutContainer(Container parent)
	{
	synchronized (parent.getTreeLock())
	{
		Insets parentInsets = parent.getInsets();

		int x = parentInsets.left;
		int y = parentInsets.top;

		//  Get x/y location of any component outside the bounds of the panel.
		//  All components will be adjust by the x/y values, if necessary.

		for (Component component: parent.getComponents())
		{
			if (component.isVisible())
			{
				Point location = component.getLocation();
				x = Math.min(x, location.x);
				y = Math.min(y, location.y);
			}
		}

		x = (x < parentInsets.left) ? parentInsets.left - x : 0;
		y = (y < parentInsets.top) ? parentInsets.top - y : 0;

		//  Set bounds of each component

		for (Component component: parent.getComponents())
		{
			if (component.isVisible())
			{
				Point p = component.getLocation();
				Dimension d = getActualSize(component);

				component.setBounds(p.x + x, p.y + y, d.width, d.height);
			}
		}
	}}

	/**
	 * Returns the string representation of this column layout's values.
	 *
	 * @return	 a string representation of this layout
	 */
	@Override
	public String toString()
	{
		return "["
			+ getClass().getName()
			+ "]";
	}
}
