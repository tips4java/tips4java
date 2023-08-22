import java.awt.Component;
import java.awt.KeyboardFocusManager;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.JTextComponent;

/**
 *  A DocumentFilter that allows you to control the maximum number of
 *  characters that can be added to the Document. When the Document is
 *  full you can optionally tab to the next component to speed data entry.
 *
 *  This class can also be used as a generic size filter for JTextFields. In
 *  this case when a size of 0 is speicifed for the size of the Document the
 *	getColumns() method of JTextField will be used to determine the size
 *  restriction.
 */
public class SizeDocumentFilter extends ChainedDocumentFilter
{
	private int size;
	private boolean autoTab = true;

	/**
	 *  Generic constructor for use with JTextFields only. The size of the
	 *  Document will be determined by the value of the getColumns() method.
	 */
	public SizeDocumentFilter()
	{
		this(0);
	}

	/**
	 *  Constructor to set the size for this filter
	 *
	 *  @param size maximum number of characters to be added to the Document
	 */
	public SizeDocumentFilter(int size)
	{
		this(size, null);
	}

	/**
	 *  Constructor to set the size for this filter as well as provide
	 *  additional filtering
	 *
	 *  @param size maximum number of characters to be added to the Document
	 *  @param filter another DocumentFilter to invoke
	 */
	public SizeDocumentFilter(int size, DocumentFilter filter)
	{
		super( filter );
		setSize( size );
	}

	/**
	 *  Get the auto tab property
	 *
	 *	@return the auto tab property
	 */
	public boolean getAutoTab()
	{
		return autoTab;
	}

	/**
	 *  Set the auto tab property
	 *
	 *  @param autoTab the default is true
	 */
	public void setAutoTab(boolean autoTab)
	{
		this.autoTab = autoTab;
	}

	/**
	 *  Get the maximum size for any Document using this filter
	 *
	 *  @return
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 *  Set maximum size for a Document using this filter. Dynamically changing
	 *  the size will not affect existing Documents. Characters will not be
	 *  removed from any Document. The filter will only be invoked on new
	 *  additions to the Document.
	 *
	 *  @param size the maximum number of character allowed in the Document
	 */
	public void setSize(int size)
	{
		this.size = size;
	}

	/**
	 *	Make sure the insertion of text will not cause the Document to exceed
	 *  its size limit. Also, potentially tab to next component when full.
	 */
	@Override
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a)
		throws BadLocationException
	{
		int possibleSize = fb.getDocument().getLength() + str.length();
		int allowedSize = getAllowedSize( fb );

		if (possibleSize <= allowedSize)
		{
			super.insertString(fb, offs, str, a);
			handleAutoTab(possibleSize, allowedSize, fb);
		}
		else
		{
			provideErrorFeedback();
		}
	}

	/**
	 *	Make sure the replacement of text will not cause the Document to exceed
	 *  its size limit. Also, potentially tab to next component when full.
	 */
	@Override
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a)
		throws BadLocationException
	{
		int possibleSize = fb.getDocument().getLength() + str.length() - length;
		int allowedSize = getAllowedSize( fb );

		if (possibleSize <= allowedSize)
		{
			super.replace(fb, offs, length, str, a);
			handleAutoTab(possibleSize, allowedSize, fb);
		}
		else
		{
			provideErrorFeedback();
		}
	}

	/**
	 *  When a size isn't specified then we assume the desired size can be
	 *  obtained from the associated text field. Otherwise, use the class
	 *  size property.
	 */
	private int getAllowedSize(FilterBypass fb)
	{
		return (size == 0) ? getColumns(fb) : size;
	}

	/*
	 *	Use the value returnd by invoking the getColumns() method of JTextField
	 */
	private int getColumns(FilterBypass fb)
	{
		//  Find the text field that currently has focus
		//  and make sure it is using the Document that will be updated

		Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

		if (c != null && c instanceof JTextField)
		{
			JTextField textField = (JTextField)c;
			Document doc = textField.getDocument();

			if (doc.equals( fb.getDocument() ))
			{
				return textField.getColumns();
			}
		}

		return 0;
	}

	/*
	 *  When the Document is full tab to the next component.
	 */
	protected void handleAutoTab(int possibleSize, int allowedSize, FilterBypass fb)
	{
		if (autoTab == false
		||  possibleSize != allowedSize)
			return;

		//  Find the text field that currently has focus
		//  and make sure it is using the Document that has been updated

		Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

		if (c != null && c instanceof JTextComponent)
		{
			JTextComponent component = (JTextComponent)c;
			Document doc = component.getDocument();

			if (doc.equals( fb.getDocument() ))
			{
				c.transferFocus();
			}
		}
	}
}
