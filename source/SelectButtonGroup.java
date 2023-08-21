/*
 * @(#)SelectButtonGroup.java 08/11/09
 */
package darrylbu.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * This class is used to create a multiple-exclusion scope for a set of buttons.
 * Creating a set of buttons with the same <code>SelectButtonGroup</code> object
 * means that selecting one of those buttons deselects all other buttons in the
 * group.
 * <p>
 * This class provides useful methods not available in
 * <code>javax.swing.ButtonGroup</code> to simultaneously enable or disable all
 * the buttons in the group, to listen for a change of selected button by adding
 * a <code>PropertyChangeListener</code>, and to obtain the selected index as an
 * int, facilitating switch-case processing.
 * <p>
 * A <code>SelectButtonGroup</code> can be used with any set of objects that
 * inherit from <code>AbstractButton</code>.  Typically a button group contains
 * instances of  <code>JRadioButton</code>, <code>JRadioButtonMenuItem</code>,
 * or <code>JToggleButton</code>.  It wouldn't make sense to put an instance of 
 * <code>JButton</code> or <code>JMenuItem</code> in a button group because
 * <code>JButton</code> and <code>JMenuItem</code> don't implement the selected
 * state.
 * <p>
 * When a SelectButtonGroup is no longer required, #dispose() should be  invoked
 * to release static references to the buttons it contains.
 * <p>
 * Extends {@link javax.swing.ButtonGroup}.
 *
 * @see #dispose
 * @author Darryl
 */
public class SelectButtonGroup extends ButtonGroup implements Serializable {

   /**
    * The collection of all buttons present in SelectedButtonGroups
    */
   static Vector<AbstractButton> allButtons = new Vector<AbstractButton>();
   /**
    * The currently selected button.
    */
   AbstractButton selectedButton = null;
   /**
    * The enabled state of the buttons in the group
    */
   protected boolean enabled = true;
   /**
    * all propertyChangeSupport goes through this.
    */
   private SwingPropertyChangeSupport propertyChangeSupport =
         new SwingPropertyChangeSupport(this, true);

   /**
    * Creates a new <code>SelectButtonGroup</code>.
    */
   public SelectButtonGroup() {
   }

   /**
    * Adds the button to the group.
    * @param button the button to be added
    * 
    * @exception IllegalArgumentException  if the button is already added to
    * another group.  A button whose model does not provide a method getGroup()
    * may not be detected as belonging to another group.
    */
   @Override
   public void add(AbstractButton button) {
      if (button == null) {
         // for backward compatibility with ButtonGroup
         return;
      }

      if (buttons.contains(button)) {
         throw new IllegalArgumentException("Button already added to group");
      }

      if (allButtons.contains(button)) {
         throw new IllegalArgumentException(
               "Button cannot be added to two groups");
      }

      ButtonModel model = button.getModel();
      if (model instanceof DefaultButtonModel) {
         DefaultButtonModel dbModel = (DefaultButtonModel) button.getModel();
         ButtonGroup oldGroup = dbModel.getGroup();
         if (oldGroup != null) {
            throw new IllegalArgumentException(
                  "Button cannot be added to two groups");
         }
      } else {
         try {
            Class<?> clazz = model.getClass();
            Method method = clazz.getMethod("getGroup");
            method.setAccessible(true);
            if (method.getReturnType().isAssignableFrom(ButtonGroup.class)) {
               ButtonGroup oldGroup = (ButtonGroup) method.invoke(model);
               if (oldGroup != null) {
                  throw new IllegalArgumentException(
                        "Button cannot be added to two groups");
               }
            }
         } catch (IllegalAccessException ignore) {
         } catch (IllegalArgumentException ignore) {
         } catch (InvocationTargetException ignore) {
         } catch (NoSuchMethodException ignore) {
         } catch (SecurityException ignore) {
         }
      }
      button.setEnabled(enabled);
      allButtons.add(button);
      super.add(button);
   }

   /**
    * Removes the button from the group.
    * 
    * @param button the button to be removed
    */
   @Override
   public void remove(AbstractButton button) {
      if (button == null) {
         // for backward compatibility with ButtonGroup
         return;
      }
      allButtons.remove(button);
      super.remove(button);

      if (selectedButton == button) {
         selectedButton = null;
      }
   }

   /**
    * Clears the selection such that none of the buttons in the
    * <code>SelectButtonGroup</code> are selected.
    * 
    * @since 1.6       
    */
   @Override
   public void clearSelection() {
      super.clearSelection();
      AbstractButton oldSelection = selectedButton;
      selectedButton = null;
      firePropertyChanged("selectedButton", oldSelection, null);
   }

   /**
    * Sets the selected value for the <code>ButtonModel</code>.  Only one button
    * in the group may be selected at a time.
    * <P>
    * Notifies any listeners if the model changes
    * 
    * @param model the <code>ButtonModel</code>
    * @param selected <code>true</code> if this button is to be
    *   selected, otherwise <code>false</code>
    * @see   #getSelection
    * @see   #addPropertyChangeListener
    */
   @Override
   public void setSelected(ButtonModel model, boolean selected) {
      if (selected && model != null && model != getSelection()) {
         super.setSelected(model, selected);
         for (AbstractButton button : buttons) {
            if (button.getModel() == model) {
               AbstractButton oldSelection = selectedButton;
               selectedButton = button;
               firePropertyChanged("selectedButton", oldSelection, button);
               return;
            }
         }
      }
   }

   /**
    * Sets the selected value for the <code>AbstractButton</code>. Only one
    * button in the group may be selected at a time.
    * <P>
    * Notifies any listeners if the model changes
    * 
    * @param button the <code>AbstractButton</code>
    * @see   #getSelectedButton
    * @see   #addPropertyChangeListener
    */
   public void setSelectedButton(AbstractButton button) {
      setSelected(button.getModel(), true);
   }

   /**
    * Gets the current selected button from this group.  The current selected
    * button is the <code>AbstractButton</code> in this group that is currently
    * in the selected state, or <code>null</code> if there is no currently
    * selected button.
    * 
    * @return   the <code>AbstractButton</code> that is currently in the
    *                 selected state, or <code>null</code>.
    * @see      javax.swing.AbstractButton
    * @see      #setSelectedButton
    */
   public AbstractButton getSelectedButton() {
      return selectedButton;
   }

   /**
    * Returns whether a <code>Button</code> is selected.
    * 
    * @param button the button
    * @return <code>true</code> if the button is selected,
    *   otherwise returns <code>false</code>
    */
   public boolean isSelected(AbstractButton button) {
      return button == selectedButton;
   }

   /**
    * Sets the group's selected index to <I>index</I>.   Only one button in the
    * group may be selected at a time.
    * <P>
    * Notifies any listeners if the model changes
    *
    * @param index an int specifying the model selection
    * @see   #getSelectedIndex
    * @see   #addPropertyChangeListener
    */
   public void setSelectedIndex(int index) {
      if (index < buttons.size()) {
         setSelectedButton(buttons.get(index));
      }
   }

   /**
    * Returns the index of the selected button.
    *
    * @return  the the index of the selected button, or -1 if there is no
    * selection.
    * 
    * @see     #setSelectedIndex
    */
   public int getSelectedIndex() {
      for (int i = 0; i < buttons.size(); i++) {
         if (buttons.get(i) == selectedButton) {
            return i;
         }
      }
      return -1;
   }

   /**
    * Adds a PropertyChangeListener to the listener list. The listener
    * is registered for all bound properties of this class, including
    * the following:
    * <ul>
    *    <li>this SelectButtonGroup's selection ("selection")</li>
    * </ul>
    * <p>
    * If <code>listener</code> is <code>null</code>,
    * no exception is thrown and no action is performed.
    *
    * @param listener  the property change listener to be added
    *
    * @see #removePropertyChangeListener
    * @see #getPropertyChangeListeners
    */
   public synchronized void addPropertyChangeListener(
         PropertyChangeListener listener) {
      propertyChangeSupport.addPropertyChangeListener(listener);
   }

   /**
    * Removes a PropertyChangeListener from the listener list.  This method
    * should be used to remove PropertyChangeListeners that were registered for
    * all bound properties of this class.
    * <P>
    * If listener is null, no exception is thrown and no action is performed.
    *
    * @param listener the PropertyChangeListener to be removed
    *
    * @see #addPropertyChangeListener
    * @see #getPropertyChangeListeners
    */
   public synchronized void removePropertyChangeListener(
         PropertyChangeListener listener) {
      propertyChangeSupport.removePropertyChangeListener(listener);
   }

   /**
    * Returns an array of all the property change listeners registered on this
    * group.
    *
    * @return all of this group's <code>PropertyChangeListener</code>s
    *         or an empty array if no property change
    *         listeners are currently registered
    *
    * @see      #addPropertyChangeListener
    * @see      #removePropertyChangeListener
    */
   public PropertyChangeListener[] getPropertyChangeListeners() {
      return propertyChangeSupport.getPropertyChangeListeners();
   }

   /**
    * Support for reporting bound property changes for Object properties. This
    * method can be called when a bound property has changed and it will send
    * the appropriate <code>PropertyChangeEvent</code> to any registered
    * PropertyChangeListeners.
    *
    * @param propertyName the property whose value has changed
    * @param oldValue the property's previous value
    * @param newValue the property's new value
    */
   protected void firePropertyChanged(String propertyName,
         Object oldValue, Object newValue) {
      propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
            propertyName, oldValue, newValue));
   }

   /**
    * Enables or disables all the buttons in this group, depending on the
    * value of the parameter enable. An enabled button can respond to user
    * input and generate events. 
    * 
    * @param enable
    */
   public void setEnabled(boolean enable) {
      this.enabled = enable;
      for (AbstractButton button : buttons) {
         button.setEnabled(enable);
      }
   }

   /**
    * Removes all the buttons from the group
    * <p>
    * It is recommended to invoke this method when the group is no longer
    * required.  This releases static references to the buttons of the group.
    */
   public void dispose() {
      for (AbstractButton button : buttons) {
         remove(button);
      }
   }

   /**
    * Ensures that the <code>dispose</code> method of this group is called when
    * there are no more refrences to it. 
    * 
    * @exception  Throwable if an error occurs.
    * @see SelectButtonGroup#dispose() 
    */
   @Override
   public void finalize() throws Throwable {
      dispose();
   }
}
