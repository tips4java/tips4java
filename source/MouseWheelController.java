import java.awt.event.*;
import javax.swing.*;

/**
 *  This class allows you to control the units scrolled for each mouse wheel
 *  rotation relative to the unit increment value of the scroll bar. Specifying
 *  a scroll amount of 1, is equivalent to clicking the unit scroll button  of
 *  the scroll bar once.
 */
public class MouseWheelController
	implements MouseWheelListener
{
	private JScrollPane scrollPane;
	private int scrollAmount = 0;
	private MouseWheelListener[] realListeners;

	/**
	 *  Convenience constructor to create the class with a scroll amount of 1.
	 *
	 *  @param scrollPane  the scroll pane being used by the mouse wheel
	 */
	public MouseWheelController(JScrollPane scrollPane)
	{
		this(scrollPane, 1);
	}

	/**
	 *  Create the class with the specified scroll amount.
	 *
	 *  @param scrollAmount  the scroll amount to by used for this scroll pane
	 *  @param scrollPane  the scroll pane being used by the mouse wheel
	 */
	public MouseWheelController(JScrollPane scrollPane, int scrollAmount)
	{
		this.scrollPane = scrollPane;
		setScrollAmount( scrollAmount );
		install();
	}

	/**
	 *  Get the scroll amount
	 *
	 *  @returns the scroll amount.
	 */
	public int getScrollAmount()
	{
		return scrollAmount;
	}

	/**
	 *  Set the scroll amount. Controls the amount the scrollpane will scroll
	 *  for each mouse wheel rotation. The amount is relative to the unit
	 *  increment value of the scrollbar being scrolled.
	 *
	 *  @param scrollAmount  an integer value. A value of zero will use the
	 *      default scroll amount for your OS.
	 */
	public void setScrollAmount(int scrollAmount)
	{
		this.scrollAmount = scrollAmount;
	}

	/**
	 *  Install this class as the default listener for MouseWheel events.
	 */
	public void install()
	{
		if (realListeners != null) return;

		//  Keep track of original listeners so we can use them to
		//  redispatch an altered MouseWheelEvent

		realListeners = scrollPane.getMouseWheelListeners();

		for (MouseWheelListener mwl : realListeners)
		{
			scrollPane.removeMouseWheelListener(mwl);
		}

		//  Intercept events so they can be redispatched

		scrollPane.addMouseWheelListener(this);
	}

	/**
	 *  Remove the class as the default listener and reinstall the original
	 *  listeners.
	 */
 	public void uninstall()
 	{
 		if (realListeners == null) return;

		//  Remove this class as the default listener

		scrollPane.removeMouseWheelListener( this );

		//  Install the default listeners

		for (MouseWheelListener mwl : realListeners)
		{
			scrollPane.addMouseWheelListener( mwl );
		}

		realListeners = null;
 	}

//  Implement MouseWheelListener interface

	/**
	 *  Redispatch a MouseWheelEvent to the real MouseWheelListeners
	 */
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		//  Create an altered event to redispatch

		if (scrollAmount != 0)
		{
			e = createScrollAmountEvent( e );
		}

		//  Redispatch the event to original MouseWheelListener

		for (MouseWheelListener mwl : realListeners)
		{
			mwl.mouseWheelMoved( e );
		}
	}

	private MouseWheelEvent createScrollAmountEvent(MouseWheelEvent e)
	{
		//  Reset the scroll amount

		MouseWheelEvent mwe = new MouseWheelEvent(
			e.getComponent(),
			e.getID(),
			e.getWhen(),
			e.getModifiers(),
			e.getX(),
			e.getY(),
			e.getXOnScreen(),
			e.getYOnScreen(),
			e.getClickCount(),
			e.isPopupTrigger(),
			e.getScrollType(),
			scrollAmount,
			e.getWheelRotation());

		return mwe;
	}
}
