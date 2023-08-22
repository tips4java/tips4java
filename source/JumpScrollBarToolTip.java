/**
 * @(#)JumpScrollBarToolTip.java 1.0.0 04/08/09
 */
package darrylbu.renderer;

import camickr.ScrollBarToolTip;
import darrylbu.model.JumpScrollBarModel;
import java.awt.event.MouseEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * This class extends Rob Camick's ScrollBarToolTip to provide support for
 * a scroll bar that has a JumpScrollBarModel.  The sole difference is that
 * the tool tip text is computed on the basis of the model's true value as
 * returned by the getTrueValue() method instead of the value returned by
 * getValue().
 * <P>
 * An attempt to use this class with a scroll bar that does not have an
 * appropriate model will result in a ClassCastException.
 * 
 * @author Darryl
 * @see ScrollBarToolTip
 * @see JumpScrollBarModel
 */
public class JumpScrollBarToolTip extends ScrollBarToolTip {

   /**
    * Creates a <code>JumpScrollBarToolTip</code> for a JScrollPane's vertical
    * scroll bar, which must have a <code>JumpScrollBarModel</code>, with
    * the default Style (<code>FIXED_PRESSED</code>) and Position
    * (<code>INSIDE</code>).
    * <P>
    * @param scrollPane the scroll pane
    */
   public JumpScrollBarToolTip(JScrollPane scrollPane) {
      this(scrollPane.getVerticalScrollBar(), Style.FIXED_PRESSED,
            Position.INSIDE);
   }

   /**
    * Creates a <code>JumpScrollBarToolTip</code> for a scroll bar that has a
    * <code>JumpScrollBarModel</code>, with the default Style
    * (<code>FIXED_PRESSED,</code>) and Position (<code>INSIDE</code>).
    * 
    * @param scrollBar the scroll bar
    */
   public JumpScrollBarToolTip(JScrollBar scrollBar) {
      this(scrollBar, Style.FIXED_PRESSED, Position.INSIDE);
   }

   /**
    * Creates a <code>JumpScrollBarToolTip</code> for a scroll bar that has a
    * <code>JumpScrollBarModel</code>, with the specified Style and Position.
    * <P>
    * @param scrollBar the scroll bar
    * @param style the style of the tooltip (see ScrollBarToolTip.setStyle()
    * for values)
    * @param position the position of the tooltip (see
    * ScrollBarToolTip.setPosition() for values)
    * @see ScrollBarToolTip#setStyle(ScrollBarToolTip.Style)
    * @see ScrollBarToolTip#setPosition(ScrollBarToolTip.Position) 
    */
   public JumpScrollBarToolTip(JScrollBar scrollBar, Style style,
         Position position) {
      super(scrollBar, style, position);
   }

   /**
    *	Determine the tooltip text for the every MouseEvent. This implementation
    * returns the current true value, based on the position of the scroll thumb,
    * as an integer percentage of the maximum scrollbar value.
    * <P>
    * Subclasses should override this method to return the desired tooltip text,
    * for example the row number of a JList or JTable in a scroll pane.
    * <P>
    * @param event  the MouseEvent
    * @return the tooltip text
    */
   @Override
   protected String getToolTipText(MouseEvent event) {
      JScrollBar bar = (JScrollBar) event.getComponent();
      JumpScrollBarModel model = (JumpScrollBarModel) bar.getModel();
      int percent = model.getTrueValue() * 100 /
            (model.getMaximum() - model.getExtent());
      return percent + "%";
   }
}
