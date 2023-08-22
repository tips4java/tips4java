/*
 * @(#)JumpScrollBarModel.java 1.0.0 04/07/09
 */
package darrylbu.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * This class is used to create a scroll bar model which continues to return the
 * initial value as long as the scroll thumb is being dragged, and returns the
 * updated value when it is released, or optionally after a specified delay.
 * <P>
 * This can be useful in two perceived situations:
 * <UL>
 *   <LI>When the rendering of the scrollable component is time consuming, so as
 *       to render the component once at the end of the scrolling instead of
 *       several times over.</LI>
 *   <LI>When detail to be displayed in different areas of the scrollable
 *       component is dynamically loaded at runtime, and it is desirable to load
 *       only the data for the final visible rectangle.</LI>
 * </UL>
 * <P>
 * If the <code>JumpScrollBarModel</code> is constructed with a reference to
 * the scroll bar or scroll pane to which it is attached, the original model
 * can be restored by invoking the class's dispose() method.
 * @see #dispose
 * <P>
 * The model can be set to jump when the scroll bar's slider is paused for
 * a specified period by setting a positive jump interval.
 * @see #setJumpInterval(int)
 * <P>
 * Extends {@link javax.swing.DefaultBoundedRangeModel}.
 * @author Darryl
 */
public class JumpScrollBarModel extends DefaultBoundedRangeModel
      implements SwingConstants {

   private JScrollBar scrollBar;
   private BoundedRangeModel oldModel;
   private int oldValue;
   private Timer timer = new Timer(1000, new ActionListener() {

      int lastValue = getTrueValue();

      public void actionPerformed(ActionEvent e) {
         if (lastValue != getTrueValue()) {
            lastValue = getTrueValue();
            timer.restart();
            return;
         }

         int newValue = getTrueValue();
         if (oldValue != newValue) {
            oldValue = newValue;
            fireStateChanged();
         }
      }
   });

   /**
    * Creates a <code>JumpScrollBarModel</code> with all of the properties
    * set to default values.   Those values are:
    * <UL>
    *   <LI>value = 0</LI>
    *   <LI>extent = 0</LI>
    *   <LI>minimum = 0</LI>
    *   <LI>maximum = 100</LI>
    *   <LI>adjusting = false</LI>
    * </UL>
    */
   public JumpScrollBarModel() {
      super();
   }

   /**
    * Creates a <code>JumpScrollBarModel</code> with the specified value,
    * extent, minimum and maximum.   Adjusting is false.
    * <P>
    * Throws an IllegalArgumentException if the following constraints aren't
    * satisfied:
    * <code>min <= value <= value+extent <= max</code>
    * 
    * @param value the value
    * @param extent the extent
    * @param min the minimum of the range
    * @param max the maximum of the range
    */
   public JumpScrollBarModel(int value, int extent, int min, int max) {
      super(value, extent, min, max);
   }

   /**
    * Creates a <code>JumpScrollBarModel</code> for the vertical scroll bar
    * of a JScrollPane.  The properties are initialized from the scroll bar's
    * original model.
    * 
    * @param scrollPane the JScrollPane
    */
   public JumpScrollBarModel(JScrollPane scrollPane) {
      this(scrollPane, VERTICAL);
   }

   /**
    * Creates a <code>JumpScrollBarModel</code> for the specified scroll bar
    * of a JScrollPane.  The properties are initialized from the scroll bar's
    * original model.
    * 
    * @param scrollPane the JScrollPane
    * @param scrollBarType should be either of
    * <UL>
    *   <LI>SwingConstants.HORIZONTAL</LI>
    *   <LI>SwingConstants.VERTICAL</LI>
    * </UL>
    * <P>
    * In case of an illegal value, the vertical scroll bar will be selected
    * by default.
    */
   public JumpScrollBarModel(JScrollPane scrollPane, int scrollBarType) {
      this(scrollBarType == HORIZONTAL
            ? scrollPane.getHorizontalScrollBar()
            : scrollPane.getVerticalScrollBar());
   }

   /**
    * Creates a <code>JumpScrollBarModel</code> for a JScrollBar.  The
    * properties are initialized from the scroll bar's original model.
    * 
    * @param scrollBar the JScrollBar
    */
   public JumpScrollBarModel(JScrollBar scrollBar) {
      this.scrollBar = scrollBar;
      oldModel = scrollBar.getModel();

      setValue(oldModel.getValue());
      setExtent(oldModel.getExtent());
      setMinimum(oldModel.getMinimum());
      setMaximum(oldModel.getMaximum());
   }

   /**
    * Returns a <code>JumpScrollBarModel</code> for the vertical scroll bar
    * of a JScrollPane.  The properties are initialized from the scroll bar's
    * original model.
    * 
    * @param scrollPane the JScrollPane
    * @return the <code>JumpScrollBarModel</code>
    */
   public static JumpScrollBarModel setJumpScrollBarModel(JScrollPane scrollPane) {
      return setJumpScrollBarModel(scrollPane, VERTICAL);
   }

   /**
    * Returns a <code>JumpScrollBarModel</code> for the specified scroll bar
    * of a JScrollPane.  The properties are initialized from the scroll bar's
    * original model.
    * 
    * @param scrollPane the JScrollPane
    * @param scrollBarType should be either of
    * <UL>
    *   <LI>SwingConstants.HORIZONTAL</LI>
    *   <LI>SwingConstants.VERTICAL</LI>
    * </UL>
    * <P>
    * In case of an illegal value, the vertical scroll bar will be selected
    * by default.
    * 
    * @return the <code>JumpScrollBarModel</code>
    */
   public static JumpScrollBarModel setJumpScrollBarModel(JScrollPane scrollPane,
         int scrollBarType) {
      return setJumpScrollBarModel(scrollBarType == HORIZONTAL
            ? scrollPane.getHorizontalScrollBar()
            : scrollPane.getVerticalScrollBar());
   }

   /**
    * Returns a <code>JumpScrollBarModel</code> for a JScrollBar.  The
    * properties are initialized from the scroll bar's original model.
    * 
    * @param scrollBar the JScrollBar
    * @return the <code>JumpScrollBarModel</code>
    */
   public static JumpScrollBarModel setJumpScrollBarModel(JScrollBar scrollBar) {
      JumpScrollBarModel model = new JumpScrollBarModel(scrollBar);
      scrollBar.setModel(model);
      return model;
   }

   /**
    * Overridden to return the previous value while the scroll bar's value is
    * adjusting, and to return the current value only when not adjusting.
    * 
    * @return the model's value, adjusted as noted.
    */
   @Override
   public int getValue() {
      int newValue = getTrueValue();
      if (!getValueIsAdjusting() && (oldValue != newValue)) {
         oldValue = newValue;
         fireStateChanged();
      }
      return oldValue;
   }

   /**
    * Returns the true value of the scroll bar, as retrieved from the
    * superclass.  This method is provided as a convenience for consumers
    * that require the true value, for example to post a tool tip or status
    * message while adjusting.
    * 
    * @return the true value
    */
   public int getTrueValue() {
      return super.getValue();
   }

   /**
    * Returns the jump interval in milliseconds, or -1 if the model does not
    * auto jump.
    * 
    * @return the jump interval, or -1 if the model does not auto jump.
    */
   public int getJumpInterval() {
      if (timer.isRunning()) {
         return timer.getDelay();
      } else {
         return -1;
      }
   }

   /**
    * Sets the interval  in milliseconds after which the scrollbar will
    * jump if paused at one point.  A value of -1 will stop the auto jump.
    * <P>
    * Any other negative value or 0 will do nothing.
    * 
    * @param interval the jump interval, or -1 to stop auto jump.
    */
   public void setJumpInterval(int interval) {
      if (interval == -1 && timer.isRunning()) {
         timer.stop();
      } else if (interval > 0 && interval != getJumpInterval()) {
         timer.setDelay(interval);
         timer.setInitialDelay(interval);
         timer.start();
      }
   }

   /**
    * Invoking this method will restore the scroll bar's original model.  In
    * case this <code>JumpScrollBarModel</code> was constructed without a
    * reference to a scroll bar or scroll pane, the method will do nothing.
    * <P>
    * 
    */
   public void dispose() {
      if (scrollBar != null) {
         scrollBar.setModel(oldModel);
      }
   }
}
