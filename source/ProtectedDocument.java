import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.text.*;

/*
 *  Class to manage protected text within a Document. There are two different
 *  but related functions provided by this class:
 *
 *  a) protect the text in the Document from being changed or deleted
 *  b) control caret navigation so that the caret is only ever positioned on
 *     text that can be changed.
 *
 *  Note: this class was designed to be used the the ProtectedTextComponent
 *  class, although it can be used on its own when highlighting of the
 *  protected text is not a requirement.
 */
public class ProtectedDocument
{
	private AbstractDocument doc;
	private Map<Position, Position> positions = new HashMap<Position, Position>();

	public ProtectedDocument(JTextComponent component)
	{
		doc = (AbstractDocument)component.getDocument();
		doc.setDocumentFilter( new ProtectedDocumentFilter() );
		component.setNavigationFilter( new ProtectedNavigationFilter(component) );
	}

	/*
	 *  Specify a portion of text to be protected
	 *
	 *  Note: when protecting and entire line the start offset should be the line
	 *        start - 1. This prevents a character from being inserted at the
	 *        start of the line.
	 */
	public void protect(int start, int end)
	{
		try
		{
			positions.put(doc.createPosition(start), doc.createPosition(end));
		}
		catch(BadLocationException ble)
		{
			System.out.println(ble);
		}
	}

	/*
	 *  Class to prevent the removal or changing of text in protected areas
	 *  of the Document.
	 */
	class ProtectedDocumentFilter extends DocumentFilter
	{
		/*
		 *	Prevent inserts by the program
		 */
		public void insertString(FilterBypass fb, int offset, String str, AttributeSet a)
			throws BadLocationException
		{
			if (isInsertProtected(offset))
			{
				Toolkit.getDefaultToolkit().beep();
			}
			else
			{
				super.insertString(fb, offset, str, a);
			}
		}

		/*
		 *  Prevent removal and insertions by the GUI components
		 */
		public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet a)
			throws BadLocationException
		{
			if (length != 0  && isRemoveProtected(offset, length))
			{
				Toolkit.getDefaultToolkit().beep();
			}
			else if (isInsertProtected(offset))
			{
				Toolkit.getDefaultToolkit().beep();
			}
			else
			{
				super.replace(fb, offset, length, str, a);
			}
		}

		/*
		 *  Prevent removal of text
		 */
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
			throws BadLocationException
		{
			if (length == 0) length++;

			if (isRemoveProtected(offset, length))
				Toolkit.getDefaultToolkit().beep();
			else
				super.remove(fb, offset, length);
		}

		/*
		 *	Check if we are attempting to remove protected text. Don't
		 *  remove when:
		 *
		 *  a) the starting offset is contained within a protected block
		 *  b) the ending offset is contained within a protected block
		 *  c) the start and end offsets span a contained block
		 */
		private boolean isRemoveProtected(int start, int length)
		{
			int end = start + length - 1;

			for (Map.Entry<Position, Position> me: positions.entrySet())
			{
				int positionStart = me.getKey().getOffset();
				int positionEnd = me.getValue().getOffset();

				if (start >= positionStart && start <= positionEnd)
					return true;

				if (end >= positionStart && end <= positionEnd)
					return true;

				if (start < positionStart && end > positionEnd)
					return true;
			}

			return false;
		}

		/*
		 *  Check if we are attempting to insert text in the middle of a
		 *  protected block. This should never happen since the navigation
		 *  filter should prevent the caret from being positioned here.
		 *  However, the program could invoke an insertString(...) method.
		 *
		 *  Offset 0, is a special case because the Position object managed by
		 *  the Document will never update its start position, so the size of
		 *  protected area would continue to grow as text is inserted.
		 */
		private boolean isInsertProtected(int start)
		{
			for (Map.Entry<Position, Position> me: positions.entrySet())
			{
				int positionStart = me.getKey().getOffset();
				int positionEnd = me.getValue().getOffset();

				if (start == 0 && positionStart == 0)
					return true;

				if (start > positionStart && start <= positionEnd)
					return true;
			}

			return false;
		}

	}	// end ProtectedDocumentFilter

	/*
	 *  This class will control the navigation of the caret. The caret will
	 *  skip over protected pieces of text and position itself at the
	 *  next unprotected text.
	 */
	class ProtectedNavigationFilter extends NavigationFilter implements MouseListener
	{
		private JTextComponent component;
		private boolean isMousePressed = false;
		private int mouseDot = -1;
		private int lastDot = -1;

		public ProtectedNavigationFilter(JTextComponent component)
		{
			this.component = component;
			this.component.addMouseListener( this );
		}

		/*
		 *  Override for normal caret movement
		 */
		public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias)
		{
			// Moving forwards in the Document

			if (dot > lastDot)
			{
				dot = getForwardDot(dot);
				super.setDot(fb, dot, bias);
			}
			else  // Moving backwards in the Document
			{
				dot = getBackwardDot(dot);
				super.setDot(fb, dot, bias);
			}

			lastDot = dot;
		}

		/*
		 *  Override for text selection as the caret is moved
		 */
		public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias)
		{
			//  The mouse dot is used when dragging the mouse to prevent flickering

			lastDot = isMousePressed ? mouseDot : lastDot;

			//  Moving forwards in the Document

			if (dot > lastDot)
			{
				lastDot = dot;
				dot = getForwardDot(dot);
				super.moveDot(fb, dot, bias);
			}
			else  //  Moving backwards in the Document
			{
				lastDot = dot;
				dot = getBackwardDot(dot);
				super.moveDot(fb, dot, bias);
			}

			lastDot = dot;
		}

		/*
		 *  Attempting to move the caret forward in the Document. Skip forward
		 *  when we attempt to position the caret at a protected offset.
		 */
		private int getForwardDot(int dot)
		{
			for (Map.Entry<Position, Position> me: positions.entrySet())
			{
				int positionStart = me.getKey().getOffset();
				int positionEnd = me.getValue().getOffset();

				if (dot > positionStart && dot <= positionEnd)
					return positionEnd + 1;
			}

			return dot;
		}

		/*
		 *  Attempting to move the caret forward in the Document. Skip forward
		 *  when we attempt to position the caret at a protected offset.
		 */
		private int getBackwardDot(int dot)
		{
			for (Map.Entry<Position, Position> me: positions.entrySet())
			{
				int positionStart = me.getKey().getOffset();
				int positionEnd = me.getValue().getOffset();

				if (dot <= positionEnd && dot >= positionStart)
					return positionStart;
			}

			return dot;
		}

	//  Implement the MouseListener

		public void mousePressed( MouseEvent e )
		{
			//  Track the caret position so it is easier to determine whether
			//  we are moving forwards/backwards when dragging the mouse.

			isMousePressed = true;
			mouseDot = component.getCaretPosition();
		}

		public void mouseReleased( MouseEvent e )
		{
			isMousePressed = false;
		}

		public void mouseEntered( MouseEvent e ) {}
		public void mouseExited( MouseEvent e ) {}
		public void mouseClicked( MouseEvent e ) {}
	}	//  end ProtectedNavigationFilter
}	// end ProtectedDocument
