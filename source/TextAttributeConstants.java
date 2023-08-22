package darrylbu.util;

/**
 * @(#)TextAttributeConstants.java	1.0 08/31/10
 */
import darrylbu.component.VisualFontDesigner;
import darrylbu.model.FontModel;
import static java.awt.font.TextAttribute.*;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A hardcoded mapping of the TextAttribute class constants to their variable names.
 * <P>
 * This class provides a collection of utility methods useful to a font manipulator and/or
 * code generator.
 *
 * @see TextAttribute
 * @see FontModel
 * @see VisualFontDesigner
 */
public class TextAttributeConstants extends HashMap<TextAttribute, Map<Object, String>> {

  private static final Map<TextAttribute, Map<Object, String>> outer = new HashMap<TextAttribute, Map<Object, String>>();
  private static final List<TextAttribute> list = new ArrayList<TextAttribute>();

  static {
    Map<Object, String> inner;
    // Bold
    inner = new HashMap<Object, String>();
    inner.put(WEIGHT_REGULAR, "WEIGHT_REGULAR"); // 1.0
    inner.put(WEIGHT_BOLD, "WEIGHT_BOLD"); // 2.0
    /*
    ** Omitted as Font seems to support only two weights: bold and not bold
    inner.put(WEIGHT_DEMILIGHT, "WEIGHT_DEMILIGHT"); // 0.875
    inner.put(WEIGHT_LIGHT, "WEIGHT_LIGHT"); // 0.75
    inner.put(WEIGHT_SEMIBOLD, "WEIGHT_SEMIBOLD"); // 1.25
    inner.put(WEIGHT_EXTRA_LIGHT, "WEIGHT_EXTRA_LIGHT"); // 0.5
    inner.put(WEIGHT_DEMIBOLD, "WEIGHT_DEMIBOLD"); //0.75
    inner.put(WEIGHT_HEAVY, "WEIGHT_HEAVY"); // 2.25
    inner.put(WEIGHT_MEDIUM, "WEIGHT_MEDIUM"); // 1.5
    inner.put(WEIGHT_ULTRABOLD, "WEIGHT_ULTRABOLD"); // 2.75
    inner.put(WEIGHT_EXTRABOLD, "WEIGHT_EXTRABOLD"); // 2.5
     */
    outer.put(WEIGHT, inner);
    // Italic
    inner = new HashMap<Object, String>();
    inner.put(POSTURE_REGULAR, "POSTURE_REGULAR");
    inner.put(POSTURE_OBLIQUE, "POSTURE_OBLIQUE");
    outer.put(POSTURE, inner);
    // Underline
    inner = new HashMap<Object, String>();
    inner.put(-1, "No Underline");
    inner.put(UNDERLINE_ON, "UNDERLINE_ON");
    inner.put(UNDERLINE_LOW_DASHED, "UNDERLINE_LOW_DASHED");
    inner.put(UNDERLINE_LOW_DOTTED, "UNDERLINE_LOW_DOTTED");
    inner.put(UNDERLINE_LOW_GRAY, "UNDERLINE_LOW_GRAY");
    inner.put(UNDERLINE_LOW_ONE_PIXEL, "UNDERLINE_LOW_ONE_PIXEL");
    inner.put(UNDERLINE_LOW_TWO_PIXEL, "UNDERLINE_LOW_TWO_PIXEL");
    outer.put(UNDERLINE, inner);
    // Strikethrough
    inner = new HashMap<Object, String>();
    inner.put(STRIKETHROUGH_ON, "STRIKETHROUGH_ON");
    inner.put(false, "No Strikethrough");
    outer.put(STRIKETHROUGH, inner);
    // Kerning
    inner = new HashMap<Object, String>();
    inner.put(KERNING_ON, "KERNING_ON");
    inner.put(0, "No Kerning");
    outer.put(KERNING, inner);
    // Swap colors
    inner = new HashMap<Object, String>();
    inner.put(SWAP_COLORS_ON, "SWAP_COLORS_ON");
    inner.put(false, "No Color Swap");
    outer.put(SWAP_COLORS, inner);
    // Tracking
    inner = new HashMap<Object, String>();
    inner.put(0, "No Tracking");
    inner.put(TRACKING_TIGHT, "TRACKING_TIGHT");
    inner.put(TRACKING_LOOSE, "TRACKING_LOOSE");
    outer.put(TRACKING, inner);
    // Width
    inner = new HashMap<Object, String>();
    inner.put(WIDTH_CONDENSED, "WIDTH_CONDENSED");
    inner.put(WIDTH_SEMI_CONDENSED, "WIDTH_SEMI_CONDENSED");
    inner.put(WIDTH_REGULAR, "WIDTH_REGULAR");
    inner.put(WIDTH_SEMI_EXTENDED, "WIDTH_SEMI_EXTENDED");
    inner.put(WIDTH_EXTENDED, "WIDTH_EXTENDED");
    outer.put(WIDTH, inner);
    // Superscript / subscript
    inner = new HashMap<Object, String>();
    inner.put(0, "Not Super / Subscript");
    inner.put(SUPERSCRIPT_SUPER, "SUPERSCRIPT_SUPER");
    inner.put(SUPERSCRIPT_SUB, "SUPERSCRIPT_SUB");
    outer.put(SUPERSCRIPT, inner);

    // For sequencing the generated code
    list.add(FAMILY);
    list.add(SIZE);
    list.add(WEIGHT);
    list.add(POSTURE);
    list.add(UNDERLINE);
    list.add(STRIKETHROUGH);
    list.add(KERNING);
    list.add(FOREGROUND);
    list.add(BACKGROUND);
    list.add(SWAP_COLORS);
    list.add(WIDTH);
    list.add(TRACKING);
    list.add(SUPERSCRIPT);
  }

  private TextAttributeConstants() {
    throw new Error("TextAttributeConstants is just a container for static methods");
  }

  /**
   * Returns an array of <CODE>TextAttribute</CODE> keys supported by ths class in a specified
   * order.  That order is:
   * <UL>
   * <LI>FAMILY</LI>
   * <LI>SIZE</LI>
   * <LI>WEIGHT</LI>
   * <LI>POSTURE</LI>
   * <LI>UNDERLINE</LI>
   * <LI>STRIKETHROUGH</LI>
   * <LI>KERNING</LI>
   * <LI>FOREGROUND</LI>
   * <LI>BACKGROUND</LI>
   * <LI>SWAP_COLORS</LI>
   * <LI>WIDTH</LI>
   * <LI>TRACKING</LI>
   * <LI>SUPERSCRIPT</LI>
   * </UL>
   * This can be used by a code generator to maintain a consistent and logical order for
   * generating code to populate an attribute map.
   *
   * @return an ordered array of <CODE>TextAttribute</CODE>s that are supported by this class
   */
  public static TextAttribute[] keys() {
    return list.toArray(new TextAttribute[0]);
  }

  /**
   * Returns an array of legal values for a TextAttribute key, in a consistent order.  The
   * actual order in which the values are returned is not defined.
   * <P>
   * This can be used by a font manipulation routine to create a set of options for the attribute.
   *
   * @param key the TextAttribute key whose legal values are required
   * @return an array of legal values, or null if the key is not supported by this class
   * @see FontModel#getCode()
   */
  public static Object[] values(TextAttribute key) {
    if (outer.containsKey(key)) {
      return outer.get(key).keySet().toArray();
    }
    return null;
  }

  public static Object[] booleanValues(TextAttribute key) {
    /*
    ** Omitted as Font seems to support only two weights: bold and not bold
     if (key == WEIGHT) {
    return new Float[]{WEIGHT_REGULAR, WEIGHT_BOLD};
    }
     */
    if (key == UNDERLINE) {
      return new Integer[]{-1, UNDERLINE_ON};
    }
    // POSTURE, STRIKETHROUGH, KERNING, SWAP_COLORS
    // and WEIGHT (Bold)
    return values(key);
  }

  /**
   * Returns the name of the <code>TextAttibute</code> class constant for a legal value
   * for the key.
   * <P>
   * May return null if the key is not supported by this class, or the value is not legal
   * for this key
   *
   * @param key the TextAttribute the name of whose value is to be looked up
   * @param value the value of the TextAttribute
   * @return the name of a static variable of the TextAttribute class
   */
  public static String lookup(TextAttribute key, Object value) {
    if (outer.containsKey(key)) {
      return outer.get(key).get(value);
    }
    return null;
  }

  /**
   * Returns the names of the <code>TextAttibute</code> class constants that are legal values for
   * the passed-in <code>TextAttribute</code>.  The returned name is formatted in Title Case with
   * underscores replaced by spaces.
   * <P>
   * This can be used by a font manipulation routine to display a set of legal options in a user
   * friendly format.
   *
   * @param key the <code>TextAttibute</code> the names of whose legal values are desired
   * @return the names of the legal values
   */
  public static String[] getFriendlyNames(TextAttribute key) {
    Object[] values = values(key);
    String[] friendlyNames = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      friendlyNames[i] = getFriendlyName(key, values[i]);
    }
    return friendlyNames;
  }

  /**
   * Returns the name of the <code>TextAttibute</code> class constant corresponding to the
   * specified value for the <code>TextAttibute</code> passed in.  The returned name is formatted
   * in Title Case with underscores replaced by spaces.
   *
   * @param key the <code>TextAttibute</code> the names of whose legal values are desired
   * @param value the value mapped to the <code>TextAttibute</code>

   * @return the name of the passed in value
   */
  public static String getFriendlyName(TextAttribute key, Object value) {
    return getFriendlyName(lookup(key, value));
  }

  /**
   * Returns the name of the <code>TextAttibute</code> class constant corresponding to this
   * attribute.  The returned name is formatted in Title Case with underscores replaced by spaces.
   *
   * @param attribute the <code>TextAttibute</code>
   * @return the name of the <code>TextAttibute</code> in Title Case with underscores in the
   * variable name replaced by spaces.
   */
  public static String getFriendlyName(TextAttribute attribute) {
    return getFriendlyName(String.valueOf(attribute));
  }

  private static String getFriendlyName(String name) {
    if (name == null) {
      return "null";
    }
    name = name.toLowerCase().replaceAll("^[^(]*\\(([^)]*)\\).*$", "$1");
    StringBuffer buffer = new StringBuffer(name.length());
    for (String word : name.split("_")) {
      buffer.append(" ").append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
    }
    return buffer.substring(1);
  }
}
