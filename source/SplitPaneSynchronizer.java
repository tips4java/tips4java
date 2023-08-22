import java.util.*;
import java.beans.*;
import javax.swing.*;

/*
 *  Convenience class to synchronize the divider location of related
 *  JSplitPanes. Any number of split panes can be synchronized.
 *
 *  Synchronization will occur when:
 *  <ul>
 *  <li>the divider is dragged
 *  <li>the one touch expandable feature is used
 *  <li>the setDividerLocation(...) method is used
 *  </ul>
 */
public class SplitPaneSynchronizer implements PropertyChangeListener
{
	private final static String DIVIDER_LOCATION = "dividerLocation";

	private ArrayList<JSplitPane> splitPanes;

	/*
	 *  Create a SplitPaneSynchronizer for the specified JSplitPanes.
	 *
	 *  @param splitPanes  the JSplitPanes to be synchronized
	 */
	public SplitPaneSynchronizer(JSplitPane... splitPanes)
	{
		this.splitPanes = new ArrayList<JSplitPane>(splitPanes.length);

		for (JSplitPane splitPane : splitPanes)
			addSplitPane( splitPane );
	}

	/*
	 *  Specify a JSplitPane to be automatically synchronized
	 *
	 *  @param splitPane  a JSplitPane to be synchronized
	 */
	public void addSplitPane(JSplitPane splitPane)
	{
		splitPane.addPropertyChangeListener(DIVIDER_LOCATION, this);
		splitPanes.add( splitPane );
	}

	/*
	 *  Remove a JSplitPane from automatic synchronization
	 *
	 *  @param splitPane  a JSplitPane to be synchronized
	 */
	public void removeSplitPane(JSplitPane splitPane)
	{
		splitPane.removePropertyChangeListener(DIVIDER_LOCATION, this);
		splitPanes.remove( splitPane );
	}
//
//  Implement the PropertyChangeListener interface
//
	public void propertyChange(PropertyChangeEvent e)
	{
		//  Get the new divider location of the split pane

		Object source = e.getSource();
		int location = ((Integer)e.getNewValue()).intValue();

		//  Update the divider location of related split panes

		for (JSplitPane splitPane : splitPanes)
		{
			if (splitPane != source
			&&  splitPane.getDividerLocation() != location)
				splitPane.setDividerLocation( location );
		}
	}
}
