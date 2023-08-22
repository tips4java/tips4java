/**
 * @(#)ToggleButtonIcon.java	1.0 03/18/09
 */
package darrylbu.renderer;

import javax.swing.Icon;
import javax.swing.JToggleButton;

/**
 * The base class for displying an additional custom icon on a JCheckBox or
 * JRadioButton.  Subclasses should furnish the default Icon provided by the
 * Look and Feel using UIManager.getIcon(...).
 * <P>
 * The gap between the icons is always equal to the gap between the button's
 * icon and text.
 * <P>
 * Extends {@link DualIcon}.
 * @version 1.0 03/11/09
 * @author Darryl
 */
public abstract class ToggleButtonIcon extends DualIcon {

   /**
    * Creates a <CODE>ToggleButtonIcon</CODE> with the specified horizontal
    * and vertical positioning.
    * 
    * @param button the button
    * @param customIcon the icon
    * @param defaultIcon the default icon for the button
    * @param horizontalPosition the horizontal position of the custom icon
    * relative to the default icon.  One of the following values:
    * <ul>
    * <li>{@code SwingConstants.RIGHT}
    * <li>{@code SwingConstants.LEFT}
    * <li>{@code SwingConstants.CENTER}
    * <li>{@code SwingConstants.LEADING}
    * <li>{@code SwingConstants.TRAILING}
    * </ul>
    * LEADING and TRAILING will be interpreted in the context of the button's
    * ComponentOrientation.
    * <P>
    * @param verticalPosition the vertical position of the custom icon
    * relative to the default icon
    * One of the following values:
    * <ul>
    * <li>{@code SwingConstants.CENTER}
    * <li>{@code SwingConstants.TOP}
    * <li>{@code SwingConstants.BOTTOM}
    * </ul>
    */
   protected ToggleButtonIcon(JToggleButton button,
         Icon customIcon, Icon defaultIcon,
         int horizontalPosition, int verticalPosition) {
      super(customIcon, defaultIcon,
            getHorizontalPosition(button, horizontalPosition),
            verticalPosition, button.getIconTextGap());
   }

   protected static int getHorizontalPosition(JToggleButton button) {
      return getHorizontalPosition(button, button.getHorizontalTextPosition());
   }

   private static int getHorizontalPosition(JToggleButton button,
         int horizontalPosition) {
      if (button.getComponentOrientation().isLeftToRight()) {
         if (horizontalPosition == LEADING) {
            return LEFT;
         }
         if (horizontalPosition == TRAILING) {
            return RIGHT;
         }
      } else {
         if (horizontalPosition == LEADING) {
            return RIGHT;
         }
         if (horizontalPosition == TRAILING) {
            return LEFT;
         }
      }
      return horizontalPosition;
   }
}
