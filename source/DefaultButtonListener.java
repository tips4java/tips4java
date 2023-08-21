import java.awt.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;

/*
 *  Keep track of the default button and the temporary default button
 *  for all root panes in an application.
 *
 *  The listener is installed for the entire application by using the static
 *  install() method. The listener can be removed for the application
 *  by using the static unInstall() method.
 *
 *  This implies you could enable the listener for specific Windows only by
 *  using the above methods as a Window is activated and deactivated.
 */
public class DefaultButtonListener implements PropertyChangeListener
{
	private static String PERMANENT_FOCUS_OWNER = "permanentFocusOwner";

	//  Keep track of the default button for each root pane in the application
	private HashMap<JRootPane, JButton> rootPanes = new HashMap<JRootPane, JButton>();

	//  Store the oldValue until the second PropertyChangeEvent is received
	private Component oldValue;

	/*
	 *	Multiple property change events will be generated
	 *
	 *  a) the first will contain the last component to have focus
	 *  b) the second will contain the component that currently has focus
	 *
	 *  We need information from both events in order to proceed.
	 */
	public void propertyChange(PropertyChangeEvent e)
	{
	 	// Wait until we have both pieces of information

		if (e.getOldValue() != null)
			oldValue = (Component)e.getOldValue();

		if (e.getNewValue() == null) return;

		//  When focus remains on the same root pane and
		//  when leaving a button and not going to a different button
		//  we need to restore the original default button

		Component newValue = (Component)e.getNewValue();
		JRootPane oldRootPane = SwingUtilities.getRootPane(oldValue);
		JRootPane newRootPane = SwingUtilities.getRootPane(newValue);

		if (newRootPane == oldRootPane)
		{
			if (   oldValue instanceof JButton
			&&  ! (newValue instanceof JButton))
			{
				restoreDefaultButton(newRootPane);
			}
		}

		//	Make this button the new default button for the root pane

		if (newValue instanceof JButton)
		{
			setDefaultButton(newRootPane, (JButton)newValue);
		}
	}

	/*
	 *  Restore the root pane to its original default button
	 */
	private void restoreDefaultButton(JRootPane rootPane)
	{
		if (rootPanes.containsKey(rootPane))
		{
			JButton savedDefaultButton = rootPanes.get(rootPane);
			rootPane.setDefaultButton(savedDefaultButton);
		}
	}

	/*
	 *  Temporarily change the default button of a root pane
	 */
	private void setDefaultButton(JRootPane rootPane, JButton button)
	{
		//	Save the original default button for the root pane

		if (! rootPanes.containsKey(rootPane))
		{
			rootPanes.put(rootPane, rootPane.getDefaultButton());
		}

		//  Set the current button to temporarily be the default button

		rootPane.setDefaultButton( button );
	}

	/*
	 *  Installing the listener will affect the entire application
	 */
	static DefaultButtonListener install()
	{
		DefaultButtonListener listener = new DefaultButtonListener();
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
			.addPropertyChangeListener(PERMANENT_FOCUS_OWNER, listener);

		return listener;
	}

	/*
	 *  Uninstalling the listener will affect the entire application
	 */
	static void unInstall(DefaultButtonListener listener)
	{
		listener.rootPanes.clear();

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
			.removePropertyChangeListener(PERMANENT_FOCUS_OWNER, listener);
	}
}
