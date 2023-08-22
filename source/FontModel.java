package darrylbu.model;

import darrylbu.component.VisualFontDesigner;
import darrylbu.util.TextAttributeConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import static java.awt.font.TextAttribute.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * A class that encapsulates a Font and provides convenient methods to query or change
 * various attributes.
 * <P>
 * The default model for {@link VisualFontDesigner}.
 *
 * @author Darryl
 */
public class FontModel {

  public static final String FONT_PROPERTY = "font";
  private Font font;
  private Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
  private SwingPropertyChangeSupport propertyChangeSupport = new SwingPropertyChangeSupport(this);

  /**
   * Constructs a new <code>FontModel</code> with the specified font.
   *
   * @param font the font
   */
  public FontModel(Font font) {
    setFont(font);
  }

  /**
   * Constructs a new <code>FontModel</code> with a font derived from the specified attriburte map.
   * This is equivalent to
   * <PRE>
   Font font = Font.getFont(attributes).deriveFont(attributes);
   new FontModel(font);
   * </PRE>
   *
   * @param attributes a Map of <code>TextAttribute</code> keys and their values.
   *
   * @see Font#getFont(java.util.Map)
   * @see Font#deriveFont(java.util.Map)
   */
  public FontModel(Map<TextAttribute, Object> attributes) {
    setAttributes(attributes);
  }

  /**
   * Returns the font currently held by this model.
   *
   * @return the font
   */
  public Font getFont() {
    return font;
  }

  /**
   * Sets the font of this model to the specified font.
   *
   * @param newFont the new font to be set
   */
  @SuppressWarnings(value = "unchecked")
  public void setFont(Font newFont) {
    if (!newFont.equals(font)) {
      Font oldFont = font;
      font = newFont;
      attributes = (Map<TextAttribute, Object>) font.getAttributes();
      firePropertyChange(FONT_PROPERTY, oldFont, newFont);
    }
  }

  /**
   * Sets the font held by this model to a font derived from the specified map.  This is
   * equivalent to
   * <PRE>
   Font font = Font.getFont(attributes).deriveFont(attributes);
   setFont(font);
   * </PRE>
   *
   * @param attributes a Map of <code>TextAttribute</code> keys and their values.
   * @see Font#getFont(java.util.Map)
   * @see Font#deriveFont(java.util.Map)
   */
  @SuppressWarnings(value = "unchecked")
  protected void setAttributes(Map<TextAttribute, Object> attributes) {
    Font newFont = Font.getFont(attributes).deriveFont(attributes);
    setFont(newFont);
  }

  /**
   * Generates and returns code that will programatically recreate this model's Font.  Meant for
   * use by code generrators.
   *
   * @return the code needed to programatically recreate the designed Font.
   */
  public String getCode() {
    final StringBuilder sb = new StringBuilder("Map<TextAttribute, Object>"
            + " attributes\n      = new HashMap<TextAttribute, Object>();\n\n");
    for (TextAttribute key : TextAttributeConstants.keys()) {
      Object value = attributes.get(key);
      if (value != null) {
        String keyName = String.valueOf(key);
        keyName = keyName.replaceAll("^[^(]*\\(([^)]*)\\).*$", "$1").
                toUpperCase();
        sb.append("attributes.put(TextAttribute." + keyName + ", ");
        if (value instanceof String) {
          sb.append("\"");
        }
        String valueName = TextAttributeConstants.lookup(key, value);
        if (valueName == null) {
          if (value instanceof Color) {
            Color color = (Color) value;
            sb.append("new Color(" + color.getRed() + ", "
                    + color.getGreen() + ", "
                    + color.getBlue() + ")");
          } else {
            sb.append(String.valueOf(value));
          }
        } else {
          sb.append("TextAttribute." + valueName);
        }
        if (value instanceof String) {
          sb.append("\"");
        }
        sb.append(");\n");
      }
    }
    sb.append("\nFont font = Font.getFont(attributes);\n");
    sb.append("font = font.deriveFont(attributes);\n");
    return sb.toString();
  }

  /**
   * Returns the value associated with a <code>TextAttribute</code> key for this model's font.
   * Returns null if the attribute is not supported by this class, or if no value is presently
   * associated with the attribute.
   *
   * @param attribute the attribute
   * @return the value
   */
  public Object getAttributeValue(TextAttribute attribute) {
    return attributes.get(attribute);
  }

  /**
   * Sets the value to be associated with a <code>TextAttribute</code> key and updates the font.
   * The effect of setting a value that is not legal for the attribute is undefined.
   *
   * @param attribute the attribute
   * @param value the value
   */
  @SuppressWarnings(value = "unchecked")
  public void setAttributeValue(TextAttribute attribute, Object value) {
    Font oldFont = font;
    attributes.put(attribute, value);
    font = font.deriveFont(attributes);
     // remove default values form mapping
    attributes = (Map<TextAttribute, Object>) font.getAttributes();
    firePropertyChange(FONT_PROPERTY, oldFont, font);
  }

  /**
   * Returns the family name of this model's font.
   *
   * @return the familt name
   * @see #setFamily(java.lang.String)
   * @see Font#getFamily()
   */
  public String getFamily() {
    return (String) attributes.get(FAMILY);
  }

  /**
   * Sets the family of this model's font.
   *
   * @param family the family name
   * @see #getFamily()
   */
  public void setFamily(String family) {
    setAttributeValue(FAMILY, family);
  }

  /**
   * Returns the size of this model's font in float precision
   *
   * @return the size
   * @see #setSize(float)
   * @see Font#getSize()
   */
  public float getSize() {
    Float size = (Float) attributes.get(SIZE);
    return size != null ? size : font.getSize();
  }

  /**
   * Sets the size of this model's font
   *
   * @param size the size
   * @see #getSize()
   * @see Font#deriveFont(float)
   */
  public void setSize(float size) {
    setAttributeValue(SIZE, size);
  }

  /**
   * Returns true if this model's font is bold, false otherwise
   *
   * @return true if the font is bold, false otherwise
   * @see #setBold(boolean)
   * @see Font#isBold()
   */
  public boolean isBold() {
    return getAttributeValue(WEIGHT) == WEIGHT_BOLD || font.isBold();
  }

  /**
   * Sets this model's font bold if the parameter is <code>true</code> or not bold otherwise.
   *
   * @param bold true to set a bold font, false otherwise.
   * @see #isBold()
   * @see Font#deriveFont(int)
   */
  public void setBold(boolean bold) {
    setAttributeValue(WEIGHT, bold ? WEIGHT_BOLD : WEIGHT_REGULAR);
  }

  /**
   * Returns true if this model's font is italic, false otherwise
   *
   * @return true if the font is italic, false otherwise
   * @see #setItalic(boolean)
   * @see Font#isItalic()
   */
  public boolean isItalic() {
    return getAttributeValue(POSTURE) == POSTURE_OBLIQUE || font.isItalic();
  }

  /**
   * Sets this model's font italic if the parameter is <code>true</code> or not italic otherwise.
   *
   * @param italic true to set an italic font, false otherwise.
   * @see #isItalic()
   * @see Font#deriveFont(int)
   */
  public void setItalic(boolean italic) {
    setAttributeValue(POSTURE, italic ? POSTURE_OBLIQUE : POSTURE_REGULAR);
  }

  /**
   * Returns true if this model's font is underlined, false otherwise
   * <P>
   * Note that this method returns true if any kind of underline is applied.
   *
   * @return true if the font is underlined, false otherwise
   * @see #setUnderline(boolean)
   * @see #getUnderline()
   * @see #setUnderline(int)
   */
  public boolean isUnderline() {
    Integer underline = (Integer) getAttributeValue(UNDERLINE);
    return underline != null && underline != -1;
  }

  /**
   * Sets this model's font to a single underline if the parameter is <code>true</code> or
   * not underlined otherwise.
   *
   * @param underline true to set a single underline, false otherwise.
   * @see #isUnderline()
   * @see #getUnderline()
   * @see #setUnderline(int)
   */
  public void setUnderline(boolean underline) {
    setAttributeValue(UNDERLINE, underline ? UNDERLINE_ON : -1);
  }

  /**
   * Returns an int corresponding to the type of underline of this model's font.  The value will
   * be one of those listed as valid values for {@link #setUnderline(int)}, or -1 for no underline.
   * <P>
   * The descriptuve name for the value can be obtained from
   * TextAttributeConstants#getFriendlyName(TextAttribute.UNDERLINE, value) where value is the
   * int value returned by this method.
   *
   * @return the value of the underline attribute.
   * @see #isUnderline()
   * @see #setUnderline(boolean)
   * @see #setUnderline(int)
   */
  public int getUnderline() {
    Integer underline = (Integer) getAttributeValue(UNDERLINE);
    if (underline == null) {
      return -1;
    } else {
      return underline;
    }
  }

  /**
   * Sets the type of underline for this model's font.  Must be one of
   * <ul>
   * <li>TextAttribute.UNDERLINE_ON</li>
   * <li>TextAttribute.UNDERLINE_LOW_DASHED</li>
   * <li>TextAttribute.UNDERLINE_LOW_DOTTED</li>
   * <li>TextAttribute.UNDERLINE_LOW_GRAY</li>
   * <li>TextAttribute.UNDERLINE_LOW_ONE_PIXEL</li>
   * <li>TextAttribute.UNDERLINE_LOW_TWO_PIXEL</li>
   * <li>-1 (no underline)</li>
   * </ul>
   *
   * @param underline a legal value for <code>TextAttribure.UNDERLINE</code>.
   * @see #isUnderline()
   * @see #setUnderline(boolean)
   * @see #getUnderline()
   */
  public void setUnderline(int underline) {
    setAttributeValue(UNDERLINE, underline);
  }

  /**
   * Returns true if this model's font is strikethrough, false otherwise
   *
   * @return true if the font is strikethrough, false otherwise
   * @see #setStrikethrough(boolean)
   */
  public boolean isStrikethrough() {
    return getAttributeValue(STRIKETHROUGH) == STRIKETHROUGH_ON;
  }

  /**
   * Sets this model's font strikethrough if the parameter is <code>true</code> or
   * not strikethrough otherwise.
   *
   * @param strikethrough true to set the font as strikethrough, false otherwise.
   * @see #isStrikethrough()
   */
  public void setStrikethrough(boolean strikethrough) {
    setAttributeValue(STRIKETHROUGH, strikethrough);
  }

  /**
   * Returns true if this model's font attempts kerning, false otherwise
   * <P>
   * Note that not all fonts support kerning.
   *
   * @return true if the font attempts kerning, false otherwise
   * @see #setKerning(boolean)
   */
  public boolean isKerning() {
    return getAttributeValue(KERNING) == KERNING_ON;
  }

  /**
   * Sets this model's font to attempt kerning if the parameter is <code>true</code> or not
   * attempt kerning otherwise.
   * <P>
   * Note that not all fonts support kerning.
   *
   * @param kerning true to set the font to attempt kerning, false otherwise.
   * @see #isKerning()
   */
  public void setKerning(boolean kerning) {
    setAttributeValue(KERNING, kerning ? KERNING_ON : 0);
  }

  /**
   * Returns the foreground of this model's font.
   *
   * @return the foreground value
   * @see #setForeground(java.awt.Paint)
   */
  public Paint getForeground() {
    return (Paint) getAttributeValue(FOREGROUND);
  }

  /**
   * Sets the foreground of this model's font.  This will most commonly be an instance of Color,
   * but some interesting effects can be obtained by setting a gradient or texture paint.
   *
   * @param paint the foreground to set
   * @see #getForeground()
   */
  public void setForeground(Paint paint) {
    setAttributeValue(FOREGROUND, paint);
  }

  /**
   * Returns the background of this model's font.
   *
   * @return the background value
   * @see #setBackground(java.awt.Paint)
   */
  public Paint getBackground() {
    return (Paint) getAttributeValue(BACKGROUND);
  }

  /**
   * Sets the background of this model's font.  This will most commonly be an instance of Color,
   * but some interesting effects can be obtained by setting a gradient or texture paint.
   *
   * @param paint the background to set
   * @see #getBackground()
   */
  public void setBackground(Paint paint) {
    setAttributeValue(BACKGROUND, paint);
  }

  /**
   * Returns true if this model's font swaps the foreground and background, false otherwise.
   *
   * @return true if the foreground and background are swapped, false otherwise
   * @see #setSwapColors(boolean)
   */
  public boolean isSwapColors() {
    return getAttributeValue(SWAP_COLORS) == SWAP_COLORS_ON;
  }

  /**
   * Sets whether this model's font's foreground and background should be swapped.
   *
   * @param swapColors true to swap the foreground and background, false otherwise.
   * @see #isSwapColors()
   */
  public void setSwapColors(boolean swapColors) {
    setAttributeValue(SWAP_COLORS, swapColors);
  }

  /**
   * Adds a PropertyChangeListener to the listener list. The listener
   * is registered for all bound properties of this class, including
   * the following:
   * <ul>
   *    <li>this FontModel's font ("font")</li>
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
  public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
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
   * Returns an array of all the property change listeners registered on this model.
   *
   * @return all of this model's <code>PropertyChangeListener</code>s or an empty array if
   * no property change listeners are currently registered
   *
   * @see #addPropertyChangeListener
   * @see #removePropertyChangeListener
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
  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, propertyName,
            oldValue, newValue));
  }
}
