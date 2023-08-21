import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * The <code>RXCardLayout</code> provides some extensions to the
 * CardLayout class. In particular adding support for:
 *
 * a) setting focus on the card when it is displayed
 * b) getting the currently displayed Card
 * c) Next and Previous Actions
 *
 * This added support will only work when a JComponent is added as a Card.
 */
public class RXCardLayout extends CardLayout implements HierarchyListener
{
	private ArrayList<JComponent> cards = new ArrayList<JComponent>();
	private JComponent firstCard;
	private JComponent lastCard;
	private JComponent currentCard;
	private boolean isRequestFocusOnCard = true;
	private Action nextAction;
	private Action previousAction;

	/**
	 * Creates a new card layout with gaps of size zero.
	 */
	public RXCardLayout()
	{
		this(0, 0);
	}

	/**
	 * Creates a new card layout with the specified horizontal and
	 * vertical gaps. The horizontal gaps are placed at the left and
	 * right edges. The vertical gaps are placed at the top and bottom
	 * edges.
	 * @param	 hgap   the horizontal gap.
	 * @param	 vgap   the vertical gap.
	 */
	public RXCardLayout(int hgap, int vgap)
	{
		super(hgap, vgap);
	}

//  Overridden methods

	public void addLayoutComponent(Component comp, Object constraints)
	{
		super.addLayoutComponent(comp, constraints);

		if (! (comp instanceof JComponent)) return;

		JComponent component = (JComponent)comp;
		cards.add(component);

		if (firstCard == null)
			firstCard = component;

		lastCard = component;

		component.addHierarchyListener(this);
	}

	public void removeLayoutComponent(Component comp)
	{
		super.removeLayoutComponent(comp);

		if (! (comp instanceof JComponent)) return;

		JComponent component = (JComponent)comp;
		component.removeHierarchyListener(this);
		cards.remove(component);

		if (component.equals(firstCard)
		&&  cards.size() > 0)
		{
			firstCard = cards.get(0);
		}

		if (component.equals(lastCard)
		&&  cards.size() > 0)
		{
			lastCard = cards.get(cards.size() - 1);
		}

	}

//  New methods

	public JComponent getCurrentCard()
	{
		return currentCard;
	}

	public Action getNextAction()
	{
		return getNextAction("Next");
	}

	public Action getNextAction(String name)
	{
		if (nextAction == null)
		{
			nextAction = new CardAction(name, true);
			nextAction.putValue(Action.MNEMONIC_KEY, (int)name.charAt(0));
			nextAction.setEnabled( isNextCardAvailable() );
		}

		return nextAction;
	}

	public Action getPreviousAction()
	{
		return getPreviousAction("Previous");
	}

	public Action getPreviousAction(String name)
	{
		if (previousAction == null)
		{
			previousAction = new CardAction(name, false);
			previousAction.putValue(Action.MNEMONIC_KEY, (int)name.charAt(0));
			previousAction.setEnabled( isNextCardAvailable() );
		}

		return previousAction;
	}

	public boolean isNextCardAvailable()
	{
		return currentCard != lastCard;
	}

	public boolean isPreviousCardAvailable()
	{
		return currentCard != firstCard;
	}

	public boolean isRequestFocusOnCard()
	{
		return isRequestFocusOnCard;
	}

	public void setRequestFocusOnCard(boolean isRequestFocusOnCard)
	{
		this.isRequestFocusOnCard = isRequestFocusOnCard;
	}

//  Implement Hierarchy Listener

	@Override
	public void hierarchyChanged(HierarchyEvent e)
	{
		JComponent component = (JComponent)e.getSource();

		if ((HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags()) != 0
		&&  component.isShowing())
		{
			currentCard = component;

			if (isRequestFocusOnCard)
				currentCard.transferFocus();

			if (nextAction != null)
				nextAction.setEnabled( isNextCardAvailable() );

			if (previousAction != null)
        		previousAction.setEnabled( isPreviousCardAvailable() );
		}
	}

	class CardAction extends AbstractAction
	{
		private boolean isNext;

		public CardAction(String text, boolean isNext)
		{
			super(text);
			this.isNext = isNext;
			putValue( Action.SHORT_DESCRIPTION, getValue(Action.NAME) );
		}

		public void actionPerformed(ActionEvent e)
		{
			Container parent = getCurrentCard().getParent();

			if (isNext)
				next(parent);
			else
				previous(parent);
		}
	}
}
