import java.awt.*;
import java.beans.*;
import javax.swing.*;

/**
 *	The FormScroller will ensure that when a component gains focus it will
 *  always be visible within the viewport of the scrollpane.
 */
public class FormScroller implements PropertyChangeListener
{
	public enum Type
	{
		COMPONENT,
		PARENT,
		CHILD;
	}

	private JScrollPane scrollPane;
	private Type type;
	private Insets scrollInsets = new Insets(0, 0, 0, 0);
	private boolean scrollingEnabled;

	private Point lastLocation = new Point();

	/**
	 *  Convenience constructor that set the scroll Type to COMPONENT
	 */
	public FormScroller(JScrollPane scrollPane)
	{
		this(scrollPane, Type.COMPONENT);
	}

	/**
	 *   Create a FormScroller for the specified scroll Type
	 */
	public FormScroller(JScrollPane scrollPane, Type type)
	{
		this.scrollPane = scrollPane;
		setType( type );
		setScrollingEnabled( true );
	}

	/**
	 *  Get the Type of scrolling to be attempted by the scroller
	 */
	public Type getType()
	{
		return type;
	}

	/**
	 *  Set the Type of scrolling to be done by the scroller
	 *
	 *  Type.COMPONENT - the focused component should be visible in the viewport.
	 *  Type.PARENT - the parent Container of the focus component should be
	 *                visible in the viewport. If the parent Container does not
	 *                fit completely then use Type.COMPONENT.
	 *  Type.CHILD - the child Contaier of the viewport view component which
	 *               contains the focused component should be visible in the
	 *               viewport. If the child Container does not fit completely
	 *               then use Type.PARENT.
	 *
	 *  @param Type - controls scrolling of the viewport (values given above)
	 */
	public void setType(Type type)
	{
		this.type = type;
	}

	/**
	 *  Get the scroll insets.
	 *
	 *	@return the scroll insets
	 */
	public Insets getScrollInsets()
	{
		return scrollInsets;
	}

	/**
	 *  Set the scroll insets. The scroller will attempt to leave a gap between
     *  the scrolled Container and the edge of the scrollpane.
     *
     *  @paran scrollInsets - Insets for the gap for each edge of the scrollpane
	 */
	public void setScrollInsets(Insets scrollInsets)
	{
		this.scrollInsets = scrollInsets;
	}

	/**
	 *
	 */
	 public boolean isScrollingEnabled()
	 {
	 	return scrollingEnabled;
	 }

	/**
	 *  Enable automatic scrolling on the form.
	 *
	 *  @param scrollingEnabled enable/disable scrolling
	 */
	public void setScrollingEnabled(boolean scrollingEnabled)
	{
		this.scrollingEnabled = scrollingEnabled;

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
			.removePropertyChangeListener("permanentFocusOwner", this);

		if (scrollingEnabled)
		{
			KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addPropertyChangeListener("permanentFocusOwner", this);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		Component component = (Component) evt.getNewValue();

		if (component == null) return;

		//  Make sure the component with focus is in the viewport

		JComponent view = (JComponent)scrollPane.getViewport().getView();

		if (! SwingUtilities.isDescendingFrom(component, view)) return;

		//  Scroll the viewport

		Rectangle bounds = determineScrollBounds(component, view);
		view.scrollRectToVisible(bounds);
	}

	/**
	 *  Determine the bounds that must fit into the viewport of the scrollpane
	 *
	 *  @param component the component that currently has focus
	 *  @param view the component added to the viewport of the scrollpane
	 *  @return a Rectangle representing the bounds to be scrolled
	 */
	protected Rectangle determineScrollBounds(Component component, JComponent view)
	{
		//  Determine the scroll bounds based on the scroll Type

		Rectangle bounds = null;

		if (type == Type.COMPONENT)
			bounds = determineComponentBounds(component, view);
		else if (type == Type.PARENT)
			bounds = determineParentBounds(component, view);
		else
			bounds = determineChildBounds(component, view);

		//  Adjust bounds to take scroll insets into consideration

		Point location = component.getLocation();
		location = SwingUtilities.convertPoint(component.getParent(), location, view);

		if (location.x < lastLocation.x)
			bounds.x -= scrollInsets.left;
		else
			bounds.width += scrollInsets.right;

		if (location.y < lastLocation.y)
			bounds.y -= scrollInsets.top;
		else
			bounds.height += scrollInsets.bottom;

		lastLocation = location;

		return bounds;
	}

	private Rectangle determineComponentBounds(Component component, JComponent view)
	{
		// Use the bounds of the focused component

		Rectangle bounds = component.getBounds();
		bounds = SwingUtilities.convertRectangle(component.getParent(), bounds, view);

		return bounds;
	}

	private Rectangle determineParentBounds(Component component, JComponent view)
	{
/*
		//  When focusing on a component in a tabbed pane attempt to keep the tabs
		//  in the viewport by treating the tabbed pane as the component with focus

		Component tabbedPane = (Component)SwingUtilities.getAncestorOfClass(JTabbedPane.class, component);

		if (tabbedPane != null)
			component = tabbedPane;
*/
		//  Use the bounds of the parent Container

		Component parent = component.getParent();
		Rectangle bounds = parent.getBounds();
		bounds = SwingUtilities.convertRectangle(parent.getParent(), bounds, view);

		//  Make sure the Container will fit into the viewport

		if (rectangleFits(bounds))
			return bounds;
		else
			return determineComponentBounds(component, view);
	}

	private Rectangle determineChildBounds(Component component, JComponent view)
	{
		//  Search each child Component of the view to find the Container which
		//  contains the component which current has focus

		Component child = null;

		for (Component c: view.getComponents())
		{
			if (SwingUtilities.isDescendingFrom(component, c))
			{
				child = c;
				break;
			}
		}

		Rectangle bounds = child.getBounds();
		bounds = SwingUtilities.convertRectangle(child.getParent(), bounds, view);

		//  Make sure this container will fit into the viewport

		if (rectangleFits(bounds))
			return bounds;
		else
			return determineParentBounds(component, view);
	}

	private boolean rectangleFits(Rectangle bounds)
	{
		Dimension viewport = scrollPane.getViewport().getSize();

		if (bounds.width > viewport.width || bounds.height > viewport.height)
			return false;
		else
			return true;
	}
}
