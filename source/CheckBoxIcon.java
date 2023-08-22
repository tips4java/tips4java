/**
 * @(#)CheckBoxIcon.java	1.0 03/18/09
 */
package darrylbu.renderer;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.UIManager;

public class CheckBoxIcon extends ToggleButtonIcon {

   /**
    * Creates a <CODE>CheckBoxIcon</CODE> for a JCheckBox with the
    * horizontal and vertical positioning derived from the horizontal and
    * vertical positions of the check box's text relative to its icon.
    * 
    * @param checkBox the check box
    * @param customIcon the icon
    */
   public CheckBoxIcon(JCheckBox checkBox, Icon customIcon) {
      this(checkBox, customIcon,
            ToggleButtonIcon.getHorizontalPosition(checkBox),
            checkBox.getVerticalTextPosition());
   }

   /**
    * Creates a <CODE>CheckBoxIcon</CODE> for a JCheckBox with the
    * specified horizontal and vertical positioning.
    * 
    * @param checkBox the check box
    * @param customIcon the icon
    * @param horizontalPosition the horizontal position of the custom icon
    * relative to the default icon.  One of the following values:
    * <ul>
    * <li>{@code SwingConstants.RIGHT}
    * <li>{@code SwingConstants.LEFT}
    * <li>{@code SwingConstants.CENTER}
    * <li>{@code SwingConstants.LEADING}
    * <li>{@code SwingConstants.TRAILING}
    * </ul>
    * LEADING and TRAILING will be interpreted in the context of the
    * check box's ComponentOrientation.
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
   public CheckBoxIcon(JCheckBox checkBox, Icon customIcon,
         int horizontalPosition, int verticalPosition) {
      super(checkBox, customIcon, UIManager.getIcon("CheckBox.icon"),
            horizontalPosition, verticalPosition);
   }
}
