/**
 * @(#)VerticalTableHeaderCellRenderer.java	1.0 02/26/09
 */
package darrylbu.renderer;

import darrylbu.plaf.vertical.VerticalLabelUI;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.UIManager;

/**
 * A renderer for a JTableHeader with text rotated 90° counterclockwise.
 * <P>
 * Extends {@link DefaultTableHeaderCellRenderer}.
 * 
 * @see VerticalLabelUI
 * @author Darryl
 */
public class VerticalTableHeaderCellRenderer
        extends DefaultTableHeaderCellRenderer {

  /**
   * Constructs a <code>VerticalTableHeaderCellRenderer</code>.
   * <P>
   * The horizontal and vertical alignments and text positions are set as
   * appropriate to a vertical table header cell.
   */
  public VerticalTableHeaderCellRenderer() {
    setHorizontalAlignment(LEFT);
    setHorizontalTextPosition(CENTER);
    setVerticalAlignment(CENTER);
    setVerticalTextPosition(TOP);
    setUI(new VerticalLabelUI());
  }

  /**
   * Overridden to return a rotated version of the sort icon.
   *
   * @param table the <code>JTable</code>.
   * @param column the colummn index.
   * @return the sort icon, or null if the column is unsorted.
   */
  @Override
  protected Icon getIcon(JTable table, int column) {
    SortKey sortKey = getSortKey(table, column);
    if (sortKey != null && table.convertColumnIndexToView(sortKey.getColumn()) == column) {
      SortOrder sortOrder = sortKey.getSortOrder();
      switch (sortOrder) {
        case ASCENDING:
          return VerticalSortIcon.ASCENDING;
        case DESCENDING:
          return VerticalSortIcon.DESCENDING;
      }
    }
    return null;
  }

  /**
   * An icon implementation to paint the contained icon rotated 90° clockwise.
   * <P>
   * This implementation assumes that the L&F provides ascending and
   * descending sort icons of identical size.
   */
  private enum VerticalSortIcon implements Icon {

    ASCENDING(UIManager.getIcon("Table.ascendingSortIcon")),
    DESCENDING(UIManager.getIcon("Table.descendingSortIcon"));
    private final Icon icon;// = ;

    private VerticalSortIcon(Icon icon) {
      this.icon = icon;
    }

    /**
     * Paints an icon suitable for the header of a sorted table column,
     * rotated by 90° clockwise.  This rotation is applied to compensate
     * the rotation already applied to the passed in Graphics reference
     * by the VerticalLabelUI.
     * <P>
     * The icon is retrieved from the UIManager to obtain an icon
     * appropriate to the L&F.
     *
     * @param c the component to which the icon is to be rendered
     * @param g the graphics context
     * @param x the X coordinate of the icon's top-left corner
     * @param y the Y coordinate of the icon's top-left corner
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      int maxSide = Math.max(getIconWidth(), getIconHeight());
      Graphics2D g2 = (Graphics2D) g.create(x, y, maxSide, maxSide);
      g2.rotate((Math.PI / 2));
      g2.translate(0, -maxSide);
      icon.paintIcon(c, g2, 0, 0);
      g2.dispose();
    }

    /**
     * Returns the width of the rotated icon.
     *
     * @return the <B>height</B> of the contained icon
     */
    @Override
    public int getIconWidth() {
      return icon.getIconHeight();
    }

    /**
     * Returns the height of the rotated icon.
     *
     * @return the <B>width</B> of the contained icon
     */
    @Override
    public int getIconHeight() {
      return icon.getIconWidth();
    }
  }
}
