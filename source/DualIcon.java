/**
 * @(#)DualIcon.java	1.0 03/18/09
 */
package darrylbu.renderer;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * A composite Icon class used to compose two Icon objects into a single Icon
 * by painting the icons in turn at the precomputed offsets.  For example,
 * this class may be used to add a custom icon to a component like JCheckBox
 * or JRadioButton, in addition to the default icon provided by the
 * Look and Feel.
 * 
 * @version 1.0 03/11/09
 * @author Darryl
 */
public class DualIcon implements Icon, SwingConstants {

   Icon icon1;
   Icon icon2;
   private int width;
   private int height;
   private int icon1HOffset;
   private int icon1VOffset;
   private int icon2HOffset;
   private int icon2VOffset;
   private int iconIconGap;

   /**
    * Creates a <CODE>DualIcon</CODE> with the specified icons, the default
    * horizontal and vertical positioning and default gap.
    * <CODE>icon2</CODE> is positioned to the right of <CODE>icon1</CODE>
    * with a 4 pixel gap, and the vertical centers of the icons are aligned.
    * 
    * @param icon1 the first icon
    * @param icon2 the second icon
    */
   public DualIcon(Icon icon1, Icon icon2) {
      this(icon1, icon2, RIGHT, CENTER, 4);
   }

   /**
    * Creates a <CODE>DualIcon</CODE> with the specified icons, the specified
    * horizontal and vertical positioning and the specified gap.
    * 
    * @param icon1 the first icon
    * @param icon2 the second icon
    * @param horizontalPosition of the second icon relative to the first.
    * <BR>
    * One of the following values:
    * <ul>
    * <li>{@code SwingConstants.LEFT}
    * <li>{@code SwingConstants.CENTER}
    * <li>{@code SwingConstants.RIGHT}
    * </ul>
    * <P>
    * @param verticalPosition of the second icon relative to the first.
    * <BR>
    * One of the following values:
    * <ul>
    * <li>{@code SwingConstants.TOP}
    * <li>{@code SwingConstants.CENTER}
    * <li>{@code SwingConstants.BOTTOM}
    * </ul>
    * <P>
    * @param iconIconGap the gap between the icons in pizels, ignored if the
    * horizontalPosition and verticalPosition are both
    * <CODE>SwingConstants.CENTER</CODE>.
    */
   public DualIcon(Icon icon1, Icon icon2,
         int horizontalPosition, int verticalPosition, int iconIconGap) {
      if (icon1 == null || icon2 == null) {
         throw new NullPointerException("Icons cannot be null");
      }
      horizontalPosition = checkHorizontalKey(horizontalPosition,
            "horizontalPosition");
      verticalPosition = checkVerticalKey(verticalPosition,
            "verticalPosition");

      this.icon1 = icon1;
      this.icon2 = icon2;
      this.iconIconGap = iconIconGap;

      if (horizontalPosition == CENTER) {
         width = Math.max(icon1.getIconWidth(),
               icon2.getIconWidth());
      } else {
         width = icon1.getIconWidth() + iconIconGap +
               icon2.getIconWidth();
      }

      if (verticalPosition == CENTER || horizontalPosition != CENTER) {
         height = Math.max(icon1.getIconHeight(),
               icon2.getIconHeight());
      } else {
         height = icon1.getIconHeight() + iconIconGap +
               icon2.getIconHeight();
      }

      switch (horizontalPosition) {
         case LEFT:
            icon1HOffset = 0;
            icon2HOffset = icon1.getIconWidth() + iconIconGap;
            break;
         case CENTER:
            icon1HOffset = (width - icon1.getIconWidth()) / 2;
            icon2HOffset = (width - icon2.getIconWidth()) / 2;
            break;
         case RIGHT:
            icon1HOffset = icon2.getIconWidth() + iconIconGap;
            icon2HOffset = 0;
            break;
      }

      if (verticalPosition == CENTER) {
         icon1VOffset = (height - icon1.getIconHeight()) / 2;
         icon2VOffset = (height - icon2.getIconHeight()) / 2;
      } else {
         if (horizontalPosition == CENTER) {
            icon1VOffset = verticalPosition == TOP
                  ? 0 : icon2.getIconHeight() + iconIconGap;
            icon2VOffset = verticalPosition == TOP
                  ? icon1.getIconHeight() + iconIconGap : 0;
         } else {
            icon1VOffset = verticalPosition == TOP
                  ? 0 : height - icon1.getIconHeight();
            icon2VOffset = verticalPosition == TOP
                  ? 0 : height - icon2.getIconHeight();
         }
      }
   }

   /**
    * Paints the icons of this compound icon at the specified location
    * with the precomputed offsets.
    * 
    * @param c The component to which the icon is painted, which may be used
    * to get properties useful for painting, e.g. the foreground or background
    * color or selection status.
    * @param g the graphics context
    * @param x the X coordinate of the compound icon's top-left corner
    * @param y the Y coordinate of the compound icon's top-left corner
    */
   @Override
   public void paintIcon(Component c, Graphics g, int x, int y) {
      icon1.paintIcon(c, g,
            x + icon1HOffset, y + icon1VOffset);
      icon2.paintIcon(c, g,
            x + icon2HOffset, y + icon2VOffset);
   }

   /**
    * Gets the width of the bounding rectangle of this
    * <CODE>DualIcon</CODE>.
    * 
    * @return the width in pixels
    */
   @Override
   public int getIconWidth() {
      return width;
   }

   /**
    * Gets the height of the the bounding rectangle of this
    * <CODE>DualIcon</CODE>.
    * 
    * @return the height in pixels
    */
   @Override
   public int getIconHeight() {
      return height;
   }

   private int checkHorizontalKey(int key, String exception) {
      if ((key == LEFT) || (key == CENTER) || (key == RIGHT)) {
         return key;
      } else {
         throw new IllegalArgumentException(exception);
      }
   }

   private int checkVerticalKey(int key, String exception) {
      if ((key == TOP) || (key == CENTER) || (key == BOTTOM)) {
         return key;
      } else {
         throw new IllegalArgumentException(exception);
      }
   }
}
