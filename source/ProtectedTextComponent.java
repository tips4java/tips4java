import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/*
 *  Class to manage the protection of text and to highlight the protected
 *  text within a text component. This class has two functions:
 *
 *  a) notify the ProtectedDocument class of the text to be protected.
 *  b) notify the ProtectedHighlighter class of the text to be protected.
 */
public class ProtectedTextComponent
{
	private JTextComponent component;
	private ProtectedHighlighter highlighter;
	private ProtectedDocument document;

	/**
	 *  Specify the component to be protected. The text will be highlighted
	 *  using the default color
	 */
	public ProtectedTextComponent(JTextComponent component)
	{
		this(component, null);
	}

	/**
	 *  Specify the component to be protected. The text will be highlighted
	 *  using the specified color
	 */
	public ProtectedTextComponent(JTextComponent component, Color color)
	{
		this.component = component;

		// Handles updates to the Document and caret movement

		document = new ProtectedDocument(component);

		//  Handles highlighting of the protected text

		highlighter = new ProtectedHighlighter(component, color);
	}

	/**
	 *  Protect a range of characters
	 *
	 *  @param start  starting offset
	 *  @param end    ending offset
	 */
	public void protectText(int start, int end)
	{
		document.protect(start, end);
		highlighter.addHighlight(start, end + 1);
	}

	/**
	 *  Protect an entire line
	 *
	 *  @param line  the line to protect
	 */
	public void protectLine(int line)
	{
		protectLines(line, line);
	}

	/**
	 *  Protect a range of lines
	 *
	 *  @param firstLine  first line in the range
	 *  @param lastLine   last line in the range
	 */
	public void protectLines(int firstLine, int lastLine)
	{
		Element root = component.getDocument().getDefaultRootElement();

		firstLine = Math.max(firstLine, 0);
		firstLine = Math.min(firstLine, root.getElementCount() - 1);
		Element firstElement = root.getElement( firstLine );

		lastLine = Math.max(lastLine, 0);
		lastLine = Math.min(lastLine, root.getElementCount() - 1);
		Element lastElement = root.getElement( lastLine );

		int start = firstElement.getStartOffset();
		int end = lastElement.getEndOffset();

		document.protect(start - 1, end - 1);
		highlighter.addHighlight(start, end);
	}
}
