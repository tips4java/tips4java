import java.awt.*;
import javax.swing.*;

/**
 *  The CircularAnimatedIcon will display a series of Icons in a circular
 *  sequence. That is, the cycle will be completed when the Icons have been
 *  displayed from first to last and then back down to the first again.
 */
public class CircularAnimatedIcon extends AnimatedIcon
{
	private int direction = 1;

	/**
	 *  Create a CircularAnimatedIcon that will continuously cycle with the
	 *  default delay (500ms).
	 *
	 *  @param component  the component the icon will be painted on
	 *  @param icons	  the Icons to be painted as part of the animation
	 */
	public CircularAnimatedIcon(JComponent component, Icon... icons)
	{
		super(component, icons);
	}

	/**
	 *  Create an CircularAnimatedIcon that will continuously cycle
	 *
	 *  @param component  the component the icon will be painted on
	 *  @param delay      the delay between painting each icon, in milli seconds
	 *  @param icons	  the Icons to be painted as part of the animation
	 */
	public CircularAnimatedIcon(JComponent component, int delay, Icon... icons)
	{
		super(component, delay, icons);
	}

	/**
	 *  Create an CircularAnimatedIcon specifying all the properties.
	 *
	 *  @param component  the component the icon will be painted on
	 *  @param delay      the delay between painting each icon, in milli seconds
	 *  @param cycles     the number of times to repeat the animation sequence
	 *  @param icons	  the Icons to be painted as part of the animation
	 */
	public CircularAnimatedIcon(JComponent component, int delay, int cycles, Icon... icons)
	{
		super(component, delay, cycles, icons);
	}

	/**
	 *  Get the index of the next Icon to be displayed.
	 *
	 *  The cycle starts at the first Icon, continues to the last and then
	 *  continues back down to the first.
	 *
	 *  @param index  the index of the Icon currently displayed
	 *  @param iconCount the number of Icons to be displayed
	 *  @return  the index of the next Icon to be displayed
	 */
	protected int getNextIconIndex(int currentIndex, int iconCount)
	{
		if (iconCount == 1) return 0;

		currentIndex += direction;

		//  Reached the end, head back towards the start

		if (currentIndex == iconCount)
		{
			currentIndex -= 2;
			direction = -1;
		}

		//  Reached the start, head back toward the end

		if (currentIndex == 0)
		{
			currentIndex = 0;
			direction = 1;
		}

		return currentIndex;
	}

	/**
	 *  This implemention checks if the first icon is currently displayed.
	 *  If so this indicates all icons have been displayed in a forwards and
	 *  reverse order so the cycle is complete.
	 *
	 *  @param index  the index of the Icon currently displayed
	 *  @param iconCount the number of Icons to be displayed
	 *  @return  the index of the next Icon to be displayed
	 */
	protected boolean isCycleCompleted(int currentIndex, int iconCount)
	{
		return ((currentIndex == 0) && (direction == 1));
	}
}
