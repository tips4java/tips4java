package darrylbu.util;

import darrylbu.model.FontModel;
import java.awt.Font;
import java.beans.PropertyChangeListener;

/**
 * Implemented by classes that use a <code>FontModel</code> and design a font.
 *
 * @author Darryl
 * @see FontModel
 */
public interface FontDesigner extends PropertyChangeListener {

  String FONT_PROPERTY = "font";
  String FONT_MODEL_PROPERTY = "fontModel";

  /**
   * Returns the font represented by the selections in this designer.  Equivalent to
   * <PRE>
   * getFontModel().getFont()
   * </PRE>
   *
   * @return the designed font
   */
  Font getDesignedFont();

  /**
   * Sets the designed font of this designer. Equivalent to
   * <PRE>getFontModel().setFont()</PRE>
   *
   * @param newFont the new font
   */
  void setDesignedFont(Font newFont);

  /**
   * Gets the <code>FontModel</code> that provides the designed Font for this
   * designer.
   *
   * @return the <code>FontModel</code> that provides the designed Font
   */
  FontModel getFontModel();

  /**
   * Sets a <code>FontModel</code> and registers with it for listener notifications from
   * the new model.
   *
   * @param newModel the new Font source for this designer
   */
  void setFontModel(FontModel newModel);
}
