import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/*
 *  Highlight only the area occupied by text, not the entire background
 */
class SelectAllRenderer extends DefaultTableCellRenderer
{
	private Color selectionBackground;
	private Border editBorder = BorderFactory.createLineBorder(Color.BLACK);
	private boolean isPaintSelection;

	public SelectAllRenderer()
	{
		this( UIManager.getColor("TextField.selectionBackground") );
	}

	public SelectAllRenderer(Color selectionBackground)
	{
		this.selectionBackground = selectionBackground;
	}

	public Component getTableCellRendererComponent(
		JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		//  This variable is used by the paintComponent() method to control when
		//  the background painting (to mimic the text selection) is done.

		if (hasFocus
		&&  table.isCellEditable(row, column)
		&&  ! getText().equals(""))
			isPaintSelection = true;
		else
			isPaintSelection = false;

		return this;
	}

	/*
	 *	Override to control the background painting of the renderer
	 */
	protected void paintComponent(Graphics g)
	{
		//  Paint the background manually so we can simulate highlighting the text,
		//	therefore we need to make the renderer non-opaque

		if (isPaintSelection)
		{
			setBorder( editBorder );
			g.setColor( UIManager.getColor("Table.focusCellBackground") );
			g.fillRect(0, 0, getSize().width, getSize().height);
			g.setColor( selectionBackground );
			g.fillRect(0, 0, getPreferredSize().width, getSize().height);
			setOpaque( false );
		}

		super.paintComponent(g);
		setOpaque( true );
	}
}
