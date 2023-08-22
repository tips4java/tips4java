import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 *  The ScrollBarToolTip class allows you to simulate the display of a
 *  tooltip as the scrollbar thumb is being dragged. This may be usefull when
 *  scrolling through a large amount of data in a scrollpane.
 *
 *  The STYLE of the tooltip can be specified. A "fixed" style implies that
 *  the tooltip is fixed at a certain spot when the mouse is pressed on the
 *  toolbar thumb. A "floating" style implies that the tooltip will move as the
 *  toolbar thumb is dragged.
 *
 *  The POSITION of the tooltip indicates how the tooltip is placed relative to
 *  the  scrollbar.
 *
 *  The default tooltip text shows the relationship of the current position of
 *  the scrollbar to the maximum scrollbar value as a percentage.
 */
public class ScrollBarToolTip
	implements MouseListener, MouseMotionListener
{
	public enum Style
	{
		FIXED_PRESSED,
		FIXED_START,
		FIXED_END,
		FLOAT_START,
		FLOAT_END;

		/*
		 *  When using a RTL orientation we need to translate a LTR position
		 *  to its corresponding RTL position.
		 */
		public Style getOrientationStyle(JScrollBar scrollBar)
		{
			//  No translation required

			if (scrollBar.getComponentOrientation().isLeftToRight()
			||  scrollBar.getOrientation() == JScrollBar.VERTICAL)
				return this;

			//  Translate the following styles

			switch (this)
			{
				case FIXED_START: return Style.FIXED_END;
				case FIXED_END: return Style.FIXED_START;
				case FLOAT_START: return Style.FLOAT_END;
				case FLOAT_END: return Style.FLOAT_START;
				default: return this;
			}
		}
	}

	public enum Position
	{
		CENTER,
		INSIDE,
		INSIDE_EDGE,
		OUTSIDE,
		OUTSIDE_EDGE;

		/*
		 *  When using a RTL orientation we need to translate a LTR position
		 *  to its corresponding RTL position.
		 */
		public Position getOrientationPosition(JScrollBar scrollBar)
		{
			//  No translation required

			if (scrollBar.getComponentOrientation().isLeftToRight()
			||  scrollBar.getOrientation() == JScrollBar.HORIZONTAL)
				return this;

			//  Translate the following positions

			switch (this)
			{
				case INSIDE: return Position.OUTSIDE;
				case OUTSIDE: return Position.INSIDE;
				case INSIDE_EDGE: return Position.OUTSIDE_EDGE;
				case OUTSIDE_EDGE: return Position.INSIDE_EDGE;
				default: return this;
			}
		}
	}

	private final static int GAP = 5;
	private final static int ADJUSTMENT = 20;

	private JScrollBar scrollBar;
	private Style style;
	private Position position;

	private Point pressedPoint;
	private Popup popup;
	private JToolTip toolTip;

	/**
	 *  Convenience constructor with a Style enum of FIXED_PRESSED and a
	 *  Position enum of INSIDE.
	 *
	 *  @param scrollBar  the JScrollBar the tooltip will be displayed for
	 */
	public ScrollBarToolTip(JScrollBar scrollBar)
	{
		this(scrollBar, Style.FIXED_PRESSED, Position.INSIDE);
	}

	/**
	 *  Create a ScrollBarToolTip
	 *
	 *  @param scrollBar  the JScrollBar the tooltip will be displayed for
	 *  @param style      the style of the tooltip (see setStyle() for values)
	 *  @param position   the position of the tooltip (see setPosition()
	 *                    for values)
	 */
	public ScrollBarToolTip(JScrollBar scrollBar, Style style, Position position)
	{
		this.scrollBar = scrollBar;
		setStyle( style );
		setPosition( position );

		scrollBar.addMouseListener(this);
		scrollBar.addMouseMotionListener(this);

		toolTip = scrollBar.createToolTip();
	}

	/**
	 *  Get the Style tooltip
	 *
	 *  @return the tooltip Style
	 */
	public Style getStyle()
	{
		return style;
	}

	/**
	 *  Set the Style of the tooltip.
	 *
	 *  The valid Styles are:
	 *
	 *  <ul>
	 *  <li>Fixed Pressed - the tooltip is fixed at the position where the
	 *      mouse was pressed to begin dragging
	 *  <li>Fixed Start - the tooltip is fixed at the start of the scrollbar
	 *      (top or left, depending on the orientation)
	 *  <li>Fixed End - the tooltip is fixed at the end of the scrollbar
	 *      (bottom or right, depending on the orientation)
	 *  <li>Float Start - the tooltip floats between the start of the scrollbar
	 *      and the scrollbar thumb as the thumb is dragged
	 *  <li>Float End - the tooltip floats between the end of the scrollbar
	 *      and the scrollbar thumb as the thumb is dragged
	 *  </ul>
	 *
	 *  @param style  the Style of the tooltip
	 */
	public void setStyle(Style style)
	{
		this.style = style;
	}

	/**
	 *  Get the Position tooltip
	 *
	 *  @return the tooltip Position
	 */
	public Position getPosition()
	{
		return position;
	}

	/**
	 *  Set the Position of the tooltip relative to the scrollbar.
	 *
	 *  The valid Positions are:
	 *
	 *  <ul>
	 *  <li>Center - the tooltip is centered over the scrollbar
	 *  <li>Inside - the tooltip is located inside the scrollpane the
	 *      scrollbar belongs to
	 *  <li>Inside Edge - the tooltip is justified to the inside edge of the
	 *      scrollbar
	 *  <li>Outside - the tooltip is located outside the scrollpane the
	 *      scrollbar belongs to
	 *  <li>Outside Edge - the tooltip is justified to the outside edge of the
	 *      scrollbar
	 *  </ul>
	 *
	 *  @param position  the Position of the tooltip
	 */
	public void setPosition(Position position)
	{
		this.position = position;
	}

	/*
	 *	Invoked for every MousePressed or MouseDragged event. The tooltip
	 *  text is determined and the toolip location is determined based on
	 *  the Style and Position properties
	 *
	 *  @param event  the MouseEvent
	 */
	protected void showToolTip(MouseEvent event)
	{
		//  Determine tooltip text and location

		toolTip.setTipText( getToolTipText(event) );
		Point toolTipLocation = getToolTipLocation(event);

		//  Trick is to hide a previous popup before showing a new one

		if (popup != null) popup.hide();

		PopupFactory factory = PopupFactory.getSharedInstance();
		popup = factory.getPopup(scrollBar, toolTip, toolTipLocation.x, toolTipLocation.y);
		popup.show();
	}

	/*
	 *	Determine the tooltip text for the every MouseEvent. Default
	 *  implentation is to return the current scrollbar position as
	 *  a percentage of the maximum scrollbar value.
	 *
	 *  @param event  the MouseEvent
	 *  @return the tooltip text
	 */
	protected String getToolTipText(MouseEvent event)
	{
		BoundedRangeModel model = scrollBar.getModel();
		int percent = model.getValue() * 100 / (model.getMaximum() - model.getExtent());
		return percent + "%";
	}

	/*
	 *	Determine the location on the screen to display the tooltip. First
	 *  determine the preferred location of the tooltip using the Style and
	 *  Position properties. Then make sure the tooltip will be fully
	 *  contained in its parent window, adjusting the location if necessary.
	 *
	 *  @param event  the MouseEvent
	 *  @return the tooltip location on the screen
	 */
	protected Point getToolTipLocation(MouseEvent event)
	{
		//  Determine preferred tooltip location based on the tooltip
		//  Style and Position properties

		Point toolTipLocation;

		if (scrollBar.getOrientation() == JScrollBar.HORIZONTAL)
			toolTipLocation = new Point(getHorizontalX(event), getHorizontalY(event));
		else
			toolTipLocation = new Point(getVerticalX(event), getVerticalY(event));

		//  Don't allow the tool tip to extend outside the containing Window

		Point scrollBarLocation = scrollBar.getLocationOnScreen();
		int x = scrollBarLocation.x + toolTipLocation.x;
		int y = scrollBarLocation.y + toolTipLocation.y;

		Window window = SwingUtilities.windowForComponent(scrollBar);
		Rectangle r = window.getBounds();
		Insets insets = window.getInsets();
		Dimension d = toolTip.getPreferredSize();

		int xMax = r.x + r.width - d.width - insets.right;
		int yMax = r.y + r.height - d.height - insets.bottom;

		x = Math.min(Math.max(x, r.x + insets.left), xMax);
		y = Math.min(Math.max(y, r.y + insets.top), yMax);
		toolTipLocation.setLocation(x, y);

		return toolTipLocation;
	}
//
//  Implement MouseListener Interface
//
	@Override
	public void mousePressed(MouseEvent e)
	{
		pressedPoint = e.getPoint();
		showToolTip(e);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		popup.hide();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
//
//  Implement MouseMotionListener Interface
//
	/**
	 *  Update the tooltip text and/or position as the mouse is dragged
	 */
	@Override
	public void mouseDragged(MouseEvent e)
	{
		showToolTip(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
//
//	Private methods
//
	/*
	 *  Get the X position when using a horizontal scrollbar
	 */
	private int getHorizontalX(MouseEvent e)
	{
		//  Handle the various "fixed" styles

		Style orientationStyle = style.getOrientationStyle(scrollBar);

		if (orientationStyle == Style.FIXED_PRESSED)
			return pressedPoint.x;
		else if (orientationStyle == Style.FIXED_START)
			return 0;
		else if (orientationStyle == Style.FIXED_END)
			return scrollBar.getWidth() - toolTip.getPreferredSize().width;

		//  Handle the various "floating" styles

		Position orientationPosition = position.getOrientationPosition(scrollBar);
		int x = e.getX();

		if (orientationPosition == Position.INSIDE_EDGE
		||  orientationPosition == Position.CENTER
		||  orientationPosition == Position.OUTSIDE_EDGE)
			if (orientationStyle == Style.FLOAT_START)
				x -= (ADJUSTMENT + toolTip.getPreferredSize().width);
			else
				x += ADJUSTMENT;

		x = Math.min(Math.max(x, 0), scrollBar.getWidth() - toolTip.getPreferredSize().width - 1);

		return x;
	}

	/*
	 *  Get the X position when using a vertical scrollbar
	 */
	private int getVerticalX(MouseEvent e)
	{
		Position orientationPosition = position.getOrientationPosition(scrollBar);
		int x = 0;

		switch (orientationPosition)
		{
			case CENTER:
				x = (scrollBar.getWidth() - toolTip.getPreferredSize().width) / 2;
				break;
			case INSIDE:
				x = -GAP - toolTip.getPreferredSize().width;
				break;
			case OUTSIDE:
				x = scrollBar.getWidth() + GAP;
				break;
			case OUTSIDE_EDGE:
				x = scrollBar.getWidth() - toolTip.getPreferredSize().width;
				break;
		}

		return x;
	}

	/*
	 *  Get the Y position when using a horzontal scrollbar
	 */
	private int getHorizontalY(MouseEvent e)
	{
		Position orientationPosition = position.getOrientationPosition(scrollBar);
		int y = 0;

		switch (orientationPosition)
		{
			case CENTER:
				y = (scrollBar.getHeight() - toolTip.getPreferredSize().height) / 2;
				break;
			case INSIDE:
				y = -GAP - toolTip.getPreferredSize().height;
				break;
			case OUTSIDE:
				y = scrollBar.getHeight() + GAP;
				break;
			case OUTSIDE_EDGE:
				y = scrollBar.getHeight() - toolTip.getPreferredSize().height;
				break;
		}

		return y;
	}

	/*
	 *  Get the Y position when using a vertical scrollbar
	 */
	private int getVerticalY(MouseEvent e)
	{
		//  Handle the various "fixed" styles

		Style orientationStyle = style.getOrientationStyle(scrollBar);

		if (orientationStyle == Style.FIXED_PRESSED)
			return pressedPoint.y;
		else if (orientationStyle == Style.FIXED_START)
			return 0;
		else if (orientationStyle == Style.FIXED_END)
			return scrollBar.getHeight() - toolTip.getPreferredSize().height;

		//  Handle the various "floating" styles

		Position orientationPosition = position.getOrientationPosition(scrollBar);
		int y = e.getY();

		if (orientationPosition == Position.INSIDE_EDGE
		||  orientationPosition == Position.CENTER
		||  orientationPosition == Position.OUTSIDE_EDGE)
			if (orientationStyle == Style.FLOAT_START)
				y -= (ADJUSTMENT + toolTip.getPreferredSize().height);
			else
				y += ADJUSTMENT;

		y = Math.min(Math.max(y, 0), scrollBar.getHeight() - toolTip.getPreferredSize().height - 1);

		return y;
	}
}
