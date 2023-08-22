import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

/**
 *  The UpperCaseDocumentFilter converts all characters to upper case before
 *  the characters are inserted into the Document.
 */
public class UpperCaseDocumentFilter extends ChainedDocumentFilter
{
	/**
	 *  Standard constructor for stand alone usage
	 */
	public UpperCaseDocumentFilter()
	{
	}

	/**
	 *	Constructor used when further filtering is required after this
	 *  filter has been applied.
	 */
	public UpperCaseDocumentFilter(DocumentFilter filter)
	{
		super(filter);
	}

	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a)
		throws BadLocationException
	{
		replace(fb, offs, 0, str, a);
	}

	public void replace(FilterBypass fb, final int offs, final int length, final String text, final AttributeSet a)
		throws BadLocationException
	{
		if (text != null)
		{
			super.replace(fb, offs, length, text.toUpperCase(), a);
		}
	}
}
