
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 * The package-private class javax.swing.JTable.IconRenderer, which is the
 * default renderer for columns of class Icon and ImageIcon, is known to
 * give rise to a ClassCastException when attempting to render an Icon whose
 * implementation of paintIcon(Component c, Graphics g, int x, int y) requires
 * a cast of its first parameter to a specific subclass of Component.  Many
 * icons obtained from standard JDK classes trigger this ClassCastException.
 * <P>
 * IconTableCellRenderer addresses this issue in the following manner:
 * <UL>
 * <LI> First, attempt to paint the Icon to the Graphics context of a
 * BufferedImage with a reference to itself as the Component parameter.</LI>
 * <LI> In case of a ClassCastException, the desired class is identified by
 * parsing the exception's message.  An attempt is made to instantiate a
 * Component of the desired class and use it as a valid reference.</LI>
 * <LI> In case of an InstantiationException, which is thrown if the desired
 * class is abstract, a final attempt is made to draw the icon with a
 * concretized AbstractButton as the Component parameter.</LI>
 * <LI> All else failing, the icon is rendered as a crossed rectangle of the
 * same size as the original icon, or the default icon if supplied as a
 * parameter to the constructor.</LI>
 * <P>
 * Extends DefaultTableCellRenderer
 *
 * @see DefaultTableCellRenderer
 * @author Darryl
 */
public class IconTableCellRenderer extends DefaultTableCellRenderer {

  Icon defaultIcon;
  static AbstractButton button = new AbstractButton() {
  };

  static {
    button.setModel(new DefaultButtonModel());
  }

  /**
   * Constructs an IconTableCellRenderer which will use a crossed rectangle as
   * default icon
   */
  public IconTableCellRenderer() {
  }

  /**
   * Constructs an IconTableCellRenderer which will use the supplied
   * default icon, which must be an icon that is capable of being painted
   * to any JComponent.
   *
   * @param defaultIcon
   */
  public IconTableCellRenderer(Icon defaultIcon) {
    this.defaultIcon = defaultIcon;
  }

  /**
   * @see DefaultTableCellRenderer#getTableCellRendererComponent(JTable,
   * Object, boolean, boolean, int, int)
   */
  @Override
  public Component getTableCellRendererComponent(final JTable table,
          Object value, boolean isSelected, boolean hasFocus,
          int row, int column) {
    Icon icon = (Icon) value;
    setIcon(null);
    setText("width=" + icon.getIconWidth()
            + ", height=" + icon.getIconHeight());
    setBackground(getCorrectBackground(table, isSelected));

    try {
      createIcon(icon, this);
    } catch (ClassCastException cce) {
      // some paintIcon implementations cast the component to a
      // specific subclass of JComponent
      try {
        String className = cce.getMessage();
        className = className.substring(className.lastIndexOf(" ") + 1);
        Class clazz = Class.forName(className);
        createIcon(icon, (JComponent) clazz.newInstance());
      } catch (InstantiationException ie) {
        // OceanTheme.IFIcon#paintIcon casts the component to
        // AbstractButton
        try {
          createIcon(icon, button);
        } catch (ClassCastException cce2) {
        } catch (InstantiationException ie2) {
        }
      } catch (IllegalAccessException iae) {
      } catch (ClassNotFoundException cnfe) {
      }
    } catch (InstantiationException ie3) {
    }
    if (getIcon() == null) {
      try {
        createIcon(defaultIcon == null
                ? new XIcon(icon.getIconWidth(), icon.getIconHeight())
                : defaultIcon, null);
      } catch (Exception ex) {
      }
    }
    setBackground(getCorrectBackground(table, isSelected));

    return this;
  }

  /**
   * Paints the icon to a BufferedImage for use as the renderer's Icon.
   * Also sets the height of the table row to accommodate the icon.
   *
   * @param table the JTable
   * @param row row number
   * @param icon the icon to be painted
   * @param component the component it represents
   * @throws java.lang.ClassCastException may originate in paintIcon
   * @throws java.lang.InstantiationException for a default abstract class
   */
  private void createIcon(Icon icon, JComponent component)
          throws ClassCastException, InstantiationException {
    BufferedImage image = new BufferedImage(icon.getIconWidth() + 2,
            icon.getIconHeight() + 2, BufferedImage.TYPE_INT_ARGB);
    Graphics g = image.createGraphics();
    icon.paintIcon(component, g, 2, 2);
    setIcon(new ImageIcon(image));
  }

  /**
   * Obtains the correct background as per the selection status
   *
   * @param table the JTable
   * @param isSelected selection status
   * @return the correct background Color
   */
  private Color getCorrectBackground(JTable table, boolean isSelected) {
    return isSelected ? table.getSelectionBackground() : table.getBackground();
  }

  public static void setRowHeightsForIcons(JTable table) {
    TableModel model = table.getModel();

    for (int col = 0; col < table.getColumnCount(); col++) {
      if (Icon.class.isAssignableFrom(table.getColumnClass(col))
              && table.getDefaultRenderer(Icon.class) instanceof IconTableCellRenderer) {
        for (int row = 0; row < model.getRowCount(); row++) {
          Icon icon = (Icon) model.getValueAt(row, col);
          table.setRowHeight(table.convertRowIndexToView(row),
                  Math.max(table.getRowHeight(row), icon.getIconHeight() + 2));
        }
      }
    }
  }

  /**
   * The default icon used if none is supplied.  It is drawn as a rectangle
   * with diagonals
   */
  class XIcon implements Icon {

    int width;
    int height;

    XIcon(int width, int height) {
      this.width = width;
      this.height = height;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.drawRect(x, y, width, height);
      g.drawLine(x, y, x + width, y + height);
      g.drawLine(x + width, y, x, y + height);
    }

    public int getIconWidth() {
      return width;
    }

    public int getIconHeight() {
      return height;
    }
  }
}
