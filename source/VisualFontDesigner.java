
/**
 * @(#)VisualFontDesigner.java	2.0 08/27/10
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import static java.awt.font.TextAttribute.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.jnlp.ClipboardService;
import javax.jnlp.ServiceManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

/**
 * <CODE>VisualFontDesigner</CODE> provides a pane of controls designed to allow a user to
 * manipulate and design a afont by applying various <CODE>TextAttributes</CODE>.  The effects
 * of these attributes are visible in a previoew pane.
 * <P>
 * This class provides three levels of API which are similar to and patterned after
 * <CODE>JColorChooser</CODE>, plus one more.
 * <OL>
 * <LI>A static convenience method which shows a modal font designer dialog and returns
 * the Font designed by the user.
 * <LI>A static convenience method for creating a font designer dialog where
 * <code>ActionListeners</code> can be specified to be invoked when the user presses one of
 * the dialog buttons.
 * <LI>The ability to display instances of <CODE>VisualFontDesigner</CODE> panes directly
 * within any container. <CODE>PropertyChangeListener</CODE>s can be added to detect when
 * the current "font" property changes.
 * <LI>Run as a standalone application, <CODE>VisualFontDesigner</CODE> generates and displays
 * the code needed to reproduce the designed Font.  A button is provided to copy the generated
 * code to the clipboard.
 * </OL>
 *
 * @version 1.0 11/25/08
 * @author Darryl
 */
public class VisualFontDesigner extends JComponent {

  /**
   * Class constant for the "font" property change
   */
  public static final String FONT_PROPERTY = "font";
  private static final String TITLE = " Visual Font Designer";
  private static final Insets buttonInsets = new Insets(0, 5, 0, 5);
  private static final Font DEFAULT_FONT = new Font(Font.DIALOG, Font.PLAIN, 12);
  private Locale locale = Locale.getDefault();
  private boolean showCode = false;
  private Font designedFont;
  private Font comboFont;
  private Font buttonFont;
  private Map<TextAttribute, Object> attributes;
  private Font[] fonts;
  private Integer fontSize;
  private Window window;
  // first row
  private JComboBox fontCombo;
  private JComboBox sizeCombo;
  private JToggleButton boldButton;
  private JToggleButton italicButton;
  private JToggleButton underlineButton;
  private JToggleButton strikethroughButton;
  private JToggleButton kerningButton;
  private JButton foregroundButton;
  private JButton backgroundButton;
  private JToggleButton swapColorsButton;
  private JButton copyButton;
  // second row
  private JComboBox widthCombo;
  private JComboBox trackingCombo;
  private JComboBox superscriptCombo;
  private JTextArea previewTextArea;
  private JTextArea codeTextArea;

  /**
   * Entry point to run the program as a standalone application.  Constructs and displays a
   * <CODE>VisualFontDesigner</CODE> with default font and sample text and code generator.
   *
   * @param args not used
   */
  public static void main(String... args) {
    JFrame.setDefaultLookAndFeelDecorated(true);
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        new VisualFontDesigner(true).showFrame(JFrame.EXIT_ON_CLOSE);
      }
    });
  }

  /**
   * Constructs a <CODE>VisualFontDesigner</CODE> with an initial font obtained from the
   * component and no code generator.
   * <P>
   * If the component is a JTextComponent and contains text, the sample text will be set to
   * this.
   * 
   * @param component the Component used to set the initial properties
   */
  public VisualFontDesigner(Component component) {
    this(component.getFont());
    if (component instanceof JTextComponent) {
      String text = ((JTextComponent) component).getText();
      if (!text.isEmpty()) {
        previewTextArea.setText(text);
      }
    }
  }

  /**
   * Constructs a <CODE>VisualFontDesigner</CODE> with an initial font as specified,
   * default sample text and no code generator.
   *
   * @param font the initial font
   */
  public VisualFontDesigner(Font font) {
    this();
    setFont(font);
  }

  /**
   * Constructs a <CODE>VisualFontDesigner</CODE> with default font and sample text and
   * no code generator.
   */
  public VisualFontDesigner() {
    this(false);
  }

  /**
   * Constructs a <CODE>VisualFontDesigner</CODE> with default font and sample text.
   * If the showCode parameter is true, the code required to regenerate the designed font
   * will be displayed below the sample area and a button will be available for copying
   * the generated code to the clipboard.
   * <P>
   * This constructor is private as it is only meant to be invoked when
   * <CODE>VisualFontDesigner</CODE> is run as a standalone application.
   *
   * @param showCode true to display the code generator, false otherwise.
   */
  private VisualFontDesigner(boolean showCode) {
    this.showCode = showCode;
    comboFont = UIManager.getFont("ComboBox.font");
    buttonFont = UIManager.getFont("Button.font");
    setLayout(new BorderLayout());
    createComponents();
    layoutComponents();
  }

  /**
   * Overridden to return the current font of the designer.
   *
   * @return the visually designed Font
   */
  @Override
  public Font getFont() {
    return previewTextArea.getFont();
  }

  /**
   * Overridden to set the current font of the designer to the specified Font.  If the passed in
   * Font is different from the current Font, the <code>VisualFontDesigner</code> will fire a
   * <code>PropertyChangeEvent</code>
   *
   * @param font the Font to display
   */
  @Override
  public void setFont(Font font) {
    if (!font.equals(getFont())) {
      previewTextArea.setFont(font);
      setDefaultState(font);
    }
  }

  /**
   * This method can be invoked on an instance of <CODE>VisualFontDesigner</CODE> to launch the
   * designer in a JFrame.  The frame will be centered in the screen.
   * 
   * @param closeOperation The operation that will happen by default when the user initiates a
   * "close" on this frame.  The values that may be passed are as detailed in
   * {@link JFrame#setDefaultCloseOperation(int)}
   */
  public void showFrame(int closeOperation) {
    if (window != null) {
      throw new IllegalStateException(
              "This instance of VisualFontDesigner is already running.");
    }

    JFrame frame = new JFrame(TITLE);
    frame.setDefaultCloseOperation(closeOperation);
    setWindow(frame);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  /**
   * Shows a modal font designer dialog and blocks until the dialog is hidden. If the user
   * presses the "OK" button, this method disposes the dialog and returns the designed
   * Font. If the user presses the "Cancel" button or closes the dialog without pressing
   * "OK", then this method disposes the dialog and returns null.
   *
   * @param parent the parent Component for the dialog
   * @return the designed Font or null if the user opted out
   */
  public Font showDialog(Component parent) {
    return showDialog(parent, TITLE,
            parent != null ? parent.getFont() : DEFAULT_FONT);
  }

  /**
   * Shows a modal font designer dialog with the specified title and initial font and blocks
   * until the dialog is hidden. If the user presses the "OK" button, this method disposes the
   * dialog and returns the designed Font. If the user presses the "Cancel" button or closes
   * the dialog without pressing "OK", then this method disposes the dialog and returns null.
   *
   * @param parent  the parent Component for the dialog
   * @param title  the String containing the dialog's title
   * @param initialFont the initial Font set when the designer is shown
   * 
   * @return the designed Font or null if the user opted out
   */
  public Font showDialog(Component parent, String title, Font initialFont) {
    JDialog dialog = createDialog(parent, title, true, this, null, null);

    initialFont = initialFont != null ? initialFont
            : parent != null ? parent.getFont() : DEFAULT_FONT;
    setFont(initialFont);

    designedFont = null;
    dialog.setVisible(true);
    return designedFont;
  }

  /**
   * Creates and returns a new dialog containing the specified <code>VisualFontDesigner</code>
   * along with "OK" and "Cancel" buttons. If the "OK" or "Cancel" buttons are pressed, the
   * dialog is automatically disposed.
   * 
   * @param parent the parent component for the dialog
   * @param title the title for the dialog
   * @param modal a boolean. When true, the remainder of the program is inactive until the
   * dialog is closed.
   * @param designer the visual font designer to be placed inside the dialog
   * @param okListener the ActionListener invoked when "OK" is pressed
   * @param cancelListener the ActionListener invoked when "Cancel" is pressed
   * 
   * @return a new dialog containing the visual font designer
   */
  public static JDialog createDialog(Component parent, String title, boolean modal,
          VisualFontDesigner designer, ActionListener okListener,
          ActionListener cancelListener) {
    if (title == null) {
      title = TITLE;
    }
    Window parentWindow = null;
    JDialog dialog;
    if (parent == null) {
      dialog = new JDialog((Frame) null, title, modal);
    } else {
      parentWindow = parent instanceof Window
              ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
      dialog = parentWindow instanceof Frame
              ? new JDialog((Frame) parentWindow, title, modal)
              : new JDialog((Dialog) parentWindow, title, modal);
      if (parent instanceof JTextComponent) {
        designer.previewTextArea.setText(((JTextComponent) parent).getText());
      }
    }
    designer.addButtonPanel(dialog, okListener, cancelListener);
    designer.setWindow(dialog);
    dialog.setLocationRelativeTo(parent);
    return dialog;
  }

  private void addButtonPanel(JDialog dialog,
          ActionListener okListener, ActionListener cancelListener) {
    JButton okButton = new JButton(UIManager.getString("OptionPane.okButtonText", locale));
    okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        designedFont = getFont();
        window.dispose();
      }
    });
    if (okListener != null) {
      okButton.addActionListener(okListener);
    }
    dialog.getRootPane().setDefaultButton(okButton);

    JButton cancelButton = new JButton(UIManager.getString("OptionPane.cancelButtonText", locale));
    cancelButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        window.dispose();
      }
    });
    if (cancelListener != null) {
      cancelButton.addActionListener(cancelListener);
    }
    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    JPanel southPanel = new JPanel();
    southPanel.add(buttonPanel);
    dialog.add(southPanel, BorderLayout.SOUTH);
  }

  private void setWindow(Window window) {
    this.window = window;
    window.add(this, BorderLayout.CENTER);
    window.setIconImage(createIconImage());
    window.pack();
    changeFont();
  }

  /**
   * Generates and returns the code needed to programatically create the designed Font.
   *
   * @return the code needed to programatically create the designed Font.
   */
  public String getCode() {
    Map<TextAttribute, ?> attribs = getFont().getAttributes();
    final StringBuilder sb = new StringBuilder("Map<TextAttribute, Object>"
            + " attributes\n      = new HashMap<TextAttribute, Object>();\n\n");
    for (TextAttribute key : TextAttributeConstants.list) {
      Object value = attribs.get(key);
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

  private void createComponents() {
    Font font = UIManager.getFont("TextArea.font");
    fontSize = font.getSize();

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] families = ge.getAvailableFontFamilyNames();
    fonts = new Font[families.length];
    for (int i = 0; i < fonts.length; i++) {
      fonts[i] = new Font(families[i], Font.PLAIN, fontSize);
    }
    fontCombo = new AttributeCombo(fonts);
    fontCombo.setToolTipText("Font family");
    fontCombo.setRenderer(new FontListCellRenderer());
    fontCombo.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Font font = (Font) fontCombo.getSelectedItem();
        fontCombo.setFont(font.canDisplayUpTo(font.getName()) == -1
                ? font.deriveFont((float) comboFont.getSize())
                : comboFont);

        // put FONT resets many other attributes
        attributes.putAll(font.getAttributes());

        attributes.put(WEIGHT, boldButton.isSelected()
                ? WEIGHT_BOLD : WEIGHT_REGULAR);
        attributes.put(POSTURE, italicButton.isSelected()
                ? POSTURE_OBLIQUE : POSTURE_REGULAR);
        attributes.put(UNDERLINE, underlineButton.isSelected()
                ? UNDERLINE_ON : -1);
        attributes.put(STRIKETHROUGH, strikethroughButton.isSelected()
                ? STRIKETHROUGH_ON : false);
        attributes.put(KERNING, kerningButton.isSelected()
                ? KERNING_ON : false);
        attributes.put(SWAP_COLORS, swapColorsButton.isSelected()
                ? SWAP_COLORS_ON : false);

        attributes.put(SIZE, sizeCombo.getSelectedItem());
        attributes.put(TextAttribute.WIDTH, widthCombo.getSelectedItem());
        attributes.put(TRACKING, trackingCombo.getSelectedItem());
        attributes.put(SUPERSCRIPT, superscriptCombo.getSelectedItem());
        changeFont();
      }
    });

    Object[] sizes = {
      8, 9, 10, 11, 12, 14, 16, 18, 20, 24, 26, 28, 36, 48, 72
    };
    sizeCombo = new JComboBox(sizes);
    sizeCombo.setToolTipText("Font size");
    sizeCombo.setPrototypeDisplayValue(888);
    sizeCombo.setPreferredSize(sizeCombo.getPreferredSize());
    sizeCombo.setEditable(true);
    sizeCombo.getEditor().getEditorComponent().
            addFocusListener(new FocusAdapter() {

      @Override
      public void focusLost(FocusEvent e) {
        validateFontSize();
      }
    });
    sizeCombo.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        validateFontSize();
      }
    });

    widthCombo = getComboBox(TextAttribute.WIDTH, "Width");
    trackingCombo = getComboBox(TRACKING, "Tracking");
    superscriptCombo = getComboBox(SUPERSCRIPT, "Superscript");

    boldButton = getToggleButton(WEIGHT, "Bold");
    italicButton = getToggleButton(POSTURE, "Italic");
    underlineButton = getToggleButton(UNDERLINE, "Underline", true);

    strikethroughButton = getToggleButton(STRIKETHROUGH, "Strikethrough");
    kerningButton = getToggleButton(KERNING, "Kerning");

    foregroundButton = getColorButton(FOREGROUND, "Foreground color");
    foregroundButton.setText("A");
    backgroundButton = getColorButton(BACKGROUND, "Background color");
    backgroundButton.setText("ab");
    swapColorsButton = getToggleButton(SWAP_COLORS, "Swap colors");

    previewTextArea = new JTextArea();
    previewTextArea.setLineWrap(true);
    previewTextArea.setWrapStyleWord(true);
    previewTextArea.setText("Sample text here.");

    if (showCode) {
      codeTextArea = new JTextArea();
      if (isRestricted()) {
        Action safeCopy = new AbstractAction() {

          @Override
          public void actionPerformed(ActionEvent e) {
            try {
              ClipboardService cs =
                      (ClipboardService) ServiceManager.lookup("javax.jnlp.ClipboardService");
              StringSelection transferable = new StringSelection(codeTextArea.getSelectedText());
              cs.setContents(transferable);
            } catch (Exception ex) {
              JOptionPane.showMessageDialog(null, "Error copying code to clipboard:\n" + ex);
              ex.printStackTrace();
            }
          }
        };
        codeTextArea.getActionMap().put(DefaultEditorKit.copyAction, safeCopy);
      }
      codeTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, fontSize));
      codeTextArea.setEditable(false);

      copyButton = new JButton("Copy Code");
      copyButton.setToolTipText("Copy code to clipboard");
      copyButton.setMargin(buttonInsets);
      copyButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          codeTextArea.selectAll();
          Action copyAction = codeTextArea.getActionMap().get(DefaultEditorKit.copyAction);
          copyAction.actionPerformed(new ActionEvent(codeTextArea,
                  ActionEvent.ACTION_PERFORMED, (String) copyAction.getValue(Action.NAME)));
        }
      });
    }
    setDefaultState(font);
    changeFont();
  }

  private boolean isRestricted() {
    SecurityManager manager = System.getSecurityManager();
    if (manager == null) {
      return false;
    }
    try {
      manager.checkSystemClipboardAccess();
      return false;
    } catch (SecurityException e) {
      // nothing to do - not allowed to access
    }
    return true;
  }

  private void layoutComponents() {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = GridBagConstraints.RELATIVE;
    gbc.gridy = 0;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    gbc.insets = new Insets(2, 1, 1, 1);
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.fill = GridBagConstraints.VERTICAL;

    JPanel toolBarPanelA = new JPanel(new GridBagLayout());

    toolBarPanelA.add(fontCombo, gbc);
    toolBarPanelA.add(sizeCombo, gbc);
    toolBarPanelA.add(new JSeparator(JSeparator.VERTICAL), gbc);

    JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
    buttonPanel.add(boldButton);
    buttonPanel.add(italicButton);
    buttonPanel.add(underlineButton);
    toolBarPanelA.add(buttonPanel, gbc);
    toolBarPanelA.add(new JSeparator(JSeparator.VERTICAL), gbc);

    buttonPanel = new JPanel(new GridLayout(1, 0));
    buttonPanel.add(strikethroughButton);
    buttonPanel.add(kerningButton);
    toolBarPanelA.add(buttonPanel, gbc);
    toolBarPanelA.add(new JSeparator(JSeparator.VERTICAL), gbc);

    buttonPanel = new JPanel(new GridLayout(1, 0));
    buttonPanel.add(foregroundButton);
    buttonPanel.add(backgroundButton);
    buttonPanel.add(swapColorsButton);
    toolBarPanelA.add(buttonPanel, gbc);

    gbc.anchor = GridBagConstraints.EAST;
    gbc.weightx = 1.0;
    if (showCode) {
      toolBarPanelA.add(copyButton, gbc);
    } else {
      toolBarPanelA.add(Box.createHorizontalGlue(), gbc);
    }

    JPanel toolBarPanelB = new JPanel(new GridBagLayout());
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 0.0;
    toolBarPanelB.add(widthCombo, gbc);
    toolBarPanelB.add(trackingCombo, gbc);
    gbc.weightx = 1.0;
    toolBarPanelB.add(superscriptCombo, gbc);

    JPanel toolBarPanel = new JPanel(new GridLayout(0, 1));
    toolBarPanel.add(toolBarPanelA);
    toolBarPanel.add(toolBarPanelB);
    add(toolBarPanel, BorderLayout.NORTH);

    JScrollPane sampleScroll = new JScrollPane(previewTextArea);
    sampleScroll.setPreferredSize(new Dimension(0, 150));

    if (showCode) {
      add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, sampleScroll,
              new JScrollPane(codeTextArea)), BorderLayout.CENTER);
    } else {
      add(sampleScroll, BorderLayout.CENTER);
    }
  }

  @SuppressWarnings(value = "unchecked")
  private void setDefaultState(Font font) {
    attributes = (Map<TextAttribute, Object>) font.getAttributes();
    boldButton.setSelected(font.isBold());
    italicButton.setSelected(font.isItalic());
    underlineButton.setSelected(attributes.get(UNDERLINE) == UNDERLINE_ON);
    strikethroughButton.setSelected(attributes.get(STRIKETHROUGH) == STRIKETHROUGH_ON);
    kerningButton.setSelected(attributes.get(KERNING) == KERNING_ON);
    swapColorsButton.setSelected(attributes.get(SWAP_COLORS) == SWAP_COLORS_ON);
    sizeCombo.setSelectedItem(font.getSize());
    Object selectedItem;
    if ((selectedItem = attributes.get(TextAttribute.WIDTH)) != null) {
      widthCombo.setSelectedItem(selectedItem);
    }
    if ((selectedItem = attributes.get(TRACKING)) != null) {
      trackingCombo.setSelectedItem(selectedItem);
    }
    if ((selectedItem = attributes.get(SUPERSCRIPT)) != null) {
      superscriptCombo.setSelectedItem(selectedItem);
    }
    // must be done last as it updates the attribute map
    for (Font font1 : fonts) {
      if (font1.getName().equals(font.getName())) {
        fontCombo.setSelectedItem(font1);
      }
    }
  }

  private JToggleButton getToggleButton(final TextAttribute textAttribute,
          String toolTipText) {
    return getToggleButton(textAttribute, toolTipText, false);
  }

  private JToggleButton getToggleButton(final TextAttribute textAttribute,
          String toolTipText, final boolean flip) {
    final JToggleButton button = new JToggleButton(toolTipText.substring(0, 1));
    button.setMargin(buttonInsets);
    button.setToolTipText(toolTipText);

    final Object[] keys = TextAttributeConstants.values(textAttribute);
    Map<TextAttribute, Object> attribute = new HashMap<TextAttribute, Object>();
    attribute.put(textAttribute, keys[flip ? 0 : 1]);
    button.setFont(buttonFont.deriveFont(attribute));
    button.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Map<TextAttribute, Object> attribute = new HashMap<TextAttribute, Object>();
        attribute.put(textAttribute, keys[flip ^ button.isSelected() ? 0 : 1]);
        button.setFont(buttonFont.deriveFont(attribute));

        attributes.put(textAttribute, keys[flip ^ button.isSelected() ? 1 : 0]);
        changeFont();
      }
    });
    return button;
  }

  private JButton getColorButton(final TextAttribute textAttribute, String toolTipText) {
    final boolean isForeground = textAttribute == FOREGROUND;
    final JButton button = new JButton() {

      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color color = (Color) attributes.get(textAttribute);
        if (color != null) {
          g.setColor(color);
          g.fillRect(5, getHeight() - 6, getWidth() - 10, 3);
        }
      }
    };
    button.setMargin(buttonInsets);
    button.setToolTipText(toolTipText);
    button.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Color color = (Color) attributes.get(textAttribute);
        if (color == null) {
          color = isForeground
                  ? previewTextArea.getForeground()
                  : previewTextArea.getBackground();
        }
        color = JColorChooser.showDialog(window,
                isForeground
                ? "SelectForeground"
                : "Select Background",
                color);
        attributes.put(textAttribute, color);
        changeFont();
      }
    });
    return button;
  }

  private JComboBox getComboBox(final TextAttribute textAttribute, String toolTipText) {
    final JComboBox comboBox =
            new AttributeCombo(TextAttributeConstants.values(textAttribute));
    comboBox.setToolTipText(toolTipText);
    comboBox.setSelectedIndex(0);
    comboBox.setRenderer(new DefaultListCellRenderer() {

      @Override
      public Component getListCellRendererComponent(JList list, Object value,
              int index, boolean isSelected, boolean cellHasFocus) {
        Map<TextAttribute, Object> attribute =
                new HashMap<TextAttribute, Object>();
        attribute.put(textAttribute, value);
        Font font = getFont().deriveFont(attribute);
        setFont(font);
        setText(TextAttributeConstants.lookup(textAttribute, value));
        if (isSelected) {
          setForeground(list.getSelectionForeground());
          setBackground(list.getSelectionBackground());
        } else {
          setForeground(list.getForeground());
          setBackground(list.getBackground());
        }
        return this;
      }
    });

    comboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Map<TextAttribute, Object> attribute =
                new HashMap<TextAttribute, Object>();
        attribute.put(textAttribute, comboBox.getSelectedItem());
        comboBox.setFont(
                comboBox.getFont().deriveFont(attribute));
        attributes.put(textAttribute, comboBox.getSelectedItem());
        changeFont();
      }
    });
    return comboBox;
  }

  private void validateFontSize() {
    try {
      fontSize = (Integer) sizeCombo.getSelectedItem();
      if (fontSize != getFont().getSize()) {
        attributes.put(SIZE, fontSize);
        changeFont();
      }
    } catch (ClassCastException cce) {
      // illegal value entered, revert
      sizeCombo.setSelectedItem(fontSize);
    }
  }

  private void changeFont() {
    Font oldFont = previewTextArea.getFont();
    Font newFont = Font.getFont(attributes);
    newFont = newFont.deriveFont(attributes);
    if (!oldFont.equals(newFont)) {
      setFont(newFont);
      firePropertyChange(FONT_PROPERTY, oldFont, newFont);
      if (window != null) {
        window.setIconImage(createIconImage());
      }
      if (showCode) {
        codeTextArea.setText(getCode());
      }
    }
  }

  private Image createIconImage() {
    BufferedImage image = new BufferedImage(16, 16,
            BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = image.createGraphics();
    Font font = previewTextArea.getFont().deriveFont(12.0f);
    if (font.canDisplayUpTo(font.getName()) != -1) {
      font = DEFAULT_FONT;
    }
    Boolean isInverse = (Boolean) attributes.get(SWAP_COLORS);
    Color background = previewTextArea.getBackground();
    if (isInverse != null) {
      background = isInverse
              ? (Color) attributes.get(FOREGROUND)
              : (Color) attributes.get(BACKGROUND);
      if (background == null) {
        background = isInverse
                ? previewTextArea.getForeground()
                : previewTextArea.getBackground();
      }
    }
    g2.setColor(background);
    g2.fillRect(0, 0, 16, 16);

    g2.setColor(Color.BLACK);
    g2.setFont(font);
    g2.drawString("F", 1, 10);
    g2.drawString("D", 6, 15);
    return image;
  }

  class AttributeCombo extends JComboBox {

    AttributeCombo(Object[] items) {
      super(items);
    }

    @Override
    public Dimension getPreferredSize() {
      return new Dimension(208, 25);
    }

    @Override
    public Dimension getMinimumSize() {
      return getPreferredSize();
    }
  }

  /**
   * A ListCellRenderer that displays font names in their own font if possible.  The list's
   * model must contain objects of type Font, or a ClassCastException will be thrown.
   * <P>
   * A font that lacks the glyphs to display its own name is rendered with the default font,
   * in red or a specified color.
   */
  public static class FontListCellRenderer extends DefaultListCellRenderer {

    private final Font defaultFont = super.getFont();
    private final Color symbolColor;

    /**
     * Constructs a <CODE>FontListCellRenderer</CODE>.  Any font that lacks the glyphs to display
     * its own name is rendered in red with the default Font of a {@link DefaultListCellRenderer}.
     */
    public FontListCellRenderer() {
      this(Color.RED);
    }

    /**
     * Constructs a <CODE>FontListCellRenderer</CODE> with a custom color for symbolic fonts.
     * Any font that lacks the glyphs to display its own name is rendered in the passed in color
     * with the default Font of a {@link DefaultListCellRenderer}.
     *
     * @param symbolColor the color is which to render a font which is unable to display its name
     */
    public FontListCellRenderer(Color symbolColor) {
      if (symbolColor == null) {
        this.symbolColor = Color.RED;
      } else {
        this.symbolColor = symbolColor;
      }
    }

    /**
     * Return a component that has been configured to display a font name.  Any font that
     * lacks the glyphs to display its own name is rendered with the default Font of a
     * DefaultListCellRenderer in red or a custom color passed to the constructor.
     * 
     * @param list the JList to which to render
     * @param value the value returned by list.getModel().getElementAt(index)
     * @param index the cell's index
     * @param isSelected true if the specified cell was selected
     * @param cellHasFocus true if the specified cell has the focus.
     * 
     * @return a component whose paint() method will render the specified value.
     *
     * @see ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
     * java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(JList list,
            Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
      Font font = (Font) value;
      setText(font.getName());
      if (isSelected) {
        setForeground(list.getSelectionForeground());
        setBackground(list.getSelectionBackground());
      } else {
        setForeground(list.getForeground());
        setBackground(list.getBackground());
      }
      if (font.canDisplayUpTo(font.getName()) == -1) {
        setFont(font);
      } else {
        setForeground(symbolColor);
        setFont(defaultFont);
      }
      return this;
    }
  }

  /**
   * A hardcoded mapping of the TextAttribute class constants to their
   * variable names, for use by the code generator.
   * <P>
   * Also holds a list to provide a suitable sequence of atributes.
   */
  static class TextAttributeConstants
          extends HashMap<TextAttribute, Map<Object, String>> {

    static Map<TextAttribute, Map<Object, String>> outer;
    static List<TextAttribute> list;

    static {
      outer = new HashMap<TextAttribute, Map<Object, String>>();
      Map<Object, String> inner;

      // Bold
      inner = new HashMap<Object, String>();
      inner.put(WEIGHT_REGULAR, "WEIGHT_REGULAR");
      inner.put(WEIGHT_BOLD, "WEIGHT_BOLD");
      outer.put(WEIGHT, inner);

      // Italic
      inner = new HashMap<Object, String>();
      inner.put(POSTURE_REGULAR, "POSTURE_REGULAR");
      inner.put(POSTURE_OBLIQUE, "POSTURE_OBLIQUE");
      outer.put(POSTURE, inner);

      // Underline
      inner = new HashMap<Object, String>();
      inner.put(UNDERLINE_ON, "UNDERLINE_ON");
      inner.put(-1, "No Underline");
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
      outer.put(TextAttribute.WIDTH, inner);

      // Superscript / subscript
      inner = new HashMap<Object, String>();
      inner.put(SUPERSCRIPT_SUPER, "SUPERSCRIPT_SUPER");
      inner.put(0, "Not Super / Subscript");
      inner.put(SUPERSCRIPT_SUB, "SUPERSCRIPT_SUB");
      outer.put(SUPERSCRIPT, inner);

      // For sequencing the generated code
      list = new ArrayList<TextAttribute>();
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
      list.add(TextAttribute.WIDTH);
      list.add(TRACKING);
      list.add(SUPERSCRIPT);
    }

    /**
     * returns the variable name of the constant of the TextAttribute
     * value corresponding to the key.
     * Used for the combo boxes' rendering and for code generation.
     *
     * @param key the TextAttribute whose value is to be looked up
     * @param value the value of the TextAttribute
     * @return the name of the static variable of the TextAttribute class,
     * or null if the key is not found
     */
    static String lookup(TextAttribute key, Object value) {
      if (outer.containsKey(key)) {
        return outer.get(key).get(value);
      }
      return null;
    }

    /**
     * Returns an array of legitimate values for a TextAttribute.
     * Used for populating the combo boxes
     *
     * @param key the TextAttribute whose legal values are required
     * @return an array of legal values, or null if the key is not found
     */
    static Object[] values(TextAttribute key) {
      if (outer.containsKey(key)) {
        return outer.get(key).keySet().toArray();
      }
      return null;
    }
  }
}
