/**
 * @(#)RadioButtonIcon.java	1.0 03/18/09
 */
package darrylbu.renderer;

import javax.swing.Icon;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

public class RadioButtonIcon extends ToggleButtonIcon {

   /**
    * Creates a <CODE>RadioButtonIcon</CODE> for a JRadioButton with the
    * horizontal and vertical positioning derived from the horizontal and
    * vertical positions of the radio button's text relative to its icon.
    * 
    * @param radioButton the radio button
    * @param customIcon the icon
    */
   public RadioButtonIcon(JRadioButton radioButton, Icon customIcon) {
      this(radioButton, customIcon,
            ToggleButtonIcon.getHorizontalPosition(radioButton),
            radioButton.getVerticalTextPosition());
   }

   /**
    * Creates a <CODE>RadioButtonIcon</CODE> for a JRadioButton with the
    * specified horizontal and vertical positioning.
    * 
    * @param radioButton the radio button
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
    * LEADING and TRAILING will be interpreted in the context of the radio
    * button's ComponentOrientation.
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
   public RadioButtonIcon(JRadioButton radioButton, Icon customIcon,
         int horizontalPosition, int verticalPosition) {
      super(radioButton, customIcon, UIManager.getIcon("RadioButton.icon"),
            horizontalPosition, verticalPosition);
   }
}
