import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/*
 *  Manage the focus when a new tab is selected. You can select a focus policy:
 *  a) Reset Focus - focus is reset to the first focusable component on the tab
 *  b) Retain Focus - focus returns to the last component with focus on the tab
 *
 *  In addition you add tabs that you want to exclude from the focus policy,
 *  in which case the other policy will be in effect.
 */
public class TabFocusHandler implements ChangeListener, PropertyChangeListener
{
	public final static int RESET_FOCUS = 0;
	public final static int RETAIN_FOCUS = 1;

	private HashMap<Component, Component> tabFocus = new HashMap<Component, Component>();
	private HashSet<Component> exceptions;
	private JTabbedPane tabbedPane;
	private int focusPolicy;

	/*
	 *  Create with the default Retain Focus policy
	 */
	public TabFocusHandler(JTabbedPane tabbedPane)
	{
		this(tabbedPane, RETAIN_FOCUS);
	}

	/*
	 *  Create using the specified focus policy
	 */
	public TabFocusHandler(JTabbedPane tabbedPane, int focusPolicy)
	{
		if (focusPolicy != RESET_FOCUS
		&&  focusPolicy != RETAIN_FOCUS)
			throw new IllegalArgumentException("Invalid focus policy");

		this.tabbedPane = tabbedPane;
		this.focusPolicy = focusPolicy;

		//  Add listeners to manage a tab change

		tabbedPane.addChangeListener( this );
		KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		focusManager.addPropertyChangeListener("permanentFocusOwner", this);
	}

	/*
	 *  Specify a tab with an exception to the focus policy rule
	 */
	public void addException(int index)
	{
		if (exceptions == null)
			exceptions = new HashSet<Component>();

		Component key = tabbedPane.getComponentAt( index );
		exceptions.add(key);
	}

	/*
	 *	Tab has changed. Focus on saved component for the given tab.
	 *  When there is no saved component, focus on the first component.
	 */
	public void stateChanged(ChangeEvent e)
	{
/*
		//  Check if the keyboard is being used to move from tab to tab,
		//  in which case focus should remain on the tab

		KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Component component = focusManager.getFocusOwner();

		if (component != null && component instanceof JTabbedPane) return;

		//  Make sure a component has been added to the tab
*/
		Component key = tabbedPane.getComponentAt( tabbedPane.getSelectedIndex() );

		if (key == null) return;

		Component value = tabFocus.get(key);

		//  First time selecting this tab or focus policy is RESET_FOCUS

		if (value == null)
		{
			key.transferFocus();
//			tabFocus.put(key, value);
		}
		else //  Use the saved component for focusing
		{
			value.requestFocusInWindow();
		}
	}

	/*
	 *  Track focus changes and update the current focus component
	 *  for the current tab
	 */
	public void propertyChange(PropertyChangeEvent e)
	{
		//  No need to track focus change

		if (exceptions == null && focusPolicy == RESET_FOCUS)
			return;

		//  Check for exceptions to the focus policy exist

		Component key = tabbedPane.getComponentAt( tabbedPane.getSelectedIndex() );

		if (exceptions != null)
		{
			if (focusPolicy == RESET_FOCUS && !exceptions.contains(key))
				return;

			if (focusPolicy == RETAIN_FOCUS && exceptions.contains(key))
				return;
		}

		// Track focus changes for the tab

		Component value = (Component)e.getNewValue();

		if (value != null
		&&  SwingUtilities.isDescendingFrom(value, key))

		{
			tabFocus.put(key, value);
		}
	}
}
