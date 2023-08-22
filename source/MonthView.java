/**
 * @(#)MonthView.java	1.0 2015/02/18
 */
package darrylbu.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.function.Function;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
//import darrylbu.renderer.TemporalTableCellRenderer;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.time.temporal.TemporalAccessor;
import java.util.Map;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * MonthView is a composite of a {@link YearMonthSpinner} and a tabular calendar display of the
 * selected month. The month can also be changed by the mouse scroll wheel. Optionally, a link for
 * setting the value to the current date can be displayed.
 * <P>
 * Clicking on a displayed date sets it as the selected value.
 * <P>
 * <B>Note that compiling or using this class requires Java 8</B>
 *
 * @author Darryl
 * @see LocalDate
 */
public class MonthView extends JPanel {

  private static final DateTimeFormatter LABEL_FORMATTER
      = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
  private volatile boolean programmaticChange;
  private YearMonth yearMonth;
  private LocalDate startDate;
  private LocalDate value;
  private LocalDate min;
  private LocalDate max;
  private final YearMonthSpinner yearMonthSpinner;
  private final JTable table = new JTable(new MonthViewTableModel());

  /**
   * Constructs a MonthView with the value set to the current date, with no upper or lower limits.
   */
  public MonthView() {
    this(LocalDate.now());
  }

  /**
   * Constructs a MonthView with the value set to the supplied date, with no lower or upper limits.
   *
   * @param initialDate The date to set
   */
  public MonthView(LocalDate initialDate) {
    this(initialDate, null, null);
  }

  /**
   * Constructs a MonthView with the value set to the supplied date, with the supplied lower
   * (earliest) and upper (latest) dates.
   * <P>
   * Dates outside the specified range are not displayed.
   * <P>
   * This class does not attempt to verify that minDate <= value <= maxDate. It is the
   * responsibility of client code to supply sane values.
   *
   * @param initialDate The date to set
   * @param minDate The minimum value (earliest date); <CODE>null</CODE> for no limit.
   * @param maxDate The maximum value (latest date); <CODE>null</CODE> for no limit.
   */
  public MonthView(LocalDate initialDate, LocalDate minDate, LocalDate maxDate) {
    this(initialDate, minDate, maxDate, false);
  }

  /**
   * Constructs a MonthView with the value set to the supplied date, with the supplied lower
   * (earliest) and upper (latest) dates, and, optionally, a link to set the value to the current
   * date.
   * <P>
   * Dates outside the specified range are not displayed.
   * <P>
   * This class does not attempt to verify that minDate <= value <= maxDate. It is the
   * responsibility of client code to supply sane values.
   *
   * @param initialDate The date to set
   * @param minDate The minimum value (earliest date); <CODE>null</CODE> for no limit.
   * @param maxDate The maximum value (latest date); <CODE>null</CODE> for no limit.
   * @param showtoday true to show a link to the current date, false otherwise
   */
  public MonthView(LocalDate initialDate, LocalDate minDate, LocalDate maxDate, boolean showtoday) {
    super(new BorderLayout());
    min = minDate;
    max = maxDate;
    yearMonth = YearMonth.from(initialDate);
    startDate = yearMonth.atDay(1).minusDays(yearMonth.atDay(1).getDayOfWeek().getValue());

    YearMonth firstMonth = null;
    if (minDate != null) {
      firstMonth = YearMonth.from(minDate);
    }
    YearMonth lastMonth = null;
    if ((maxDate != null)) {
      lastMonth = YearMonth.from(maxDate);
    }
    yearMonthSpinner = new YearMonthSpinner(yearMonth, firstMonth, lastMonth);
    yearMonthSpinner.addChangeListener((ChangeEvent ce) -> {
      if (!programmaticChange) {
        setYearMonth((YearMonth) yearMonthSpinner.getValue());
      }
    });

    setValue(initialDate);

    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setShowGrid(false);
    table.setBorder(BorderFactory.createLineBorder(table.getForeground()));
    table.setCellSelectionEnabled(true);
    table.setDefaultRenderer(LocalDate.class, new MonthViewTableCellRenderer());
    TableColumnModel columnModel = table.getColumnModel();
    for (int i = 0; i < columnModel.getColumnCount(); i++) {
      columnModel.getColumn(i).setPreferredWidth(30);
    }

    JTableHeader header = table.getTableHeader();
    header.setBorder(table.getBorder());
    header.setReorderingAllowed(false);
    header.setResizingAllowed(false);
    header.setDefaultRenderer(new MonthViewTableHeaderCellRenderer(header.getDefaultRenderer()));

    Timer selectionTimer = new Timer(50, ae -> {
      LocalDate newValue = (LocalDate) table.getValueAt(table.getSelectedRow(),
          table.getSelectedColumn());
      if (newValue.equals(value)) {
        return;
      }
      boolean needSetTableSelection = false;
      if (min != null && newValue.isBefore(min)) {
        newValue = min;
        needSetTableSelection = true;
      }
      if (max != null && newValue.isAfter(max)) {
        newValue = max;
        needSetTableSelection = true;
      }
      firePropertyChange("Value", value, newValue);
      value = newValue;
      if (needSetTableSelection) {
        setTableSelection();
      }
      setYearMonth(YearMonth.from(newValue));
    });
    selectionTimer.setRepeats(false);

    ListSelectionListener selectionListener = lse -> {
      if (!programmaticChange && !lse.getValueIsAdjusting()) {
        if (selectionTimer.isRunning()) {
          selectionTimer.restart();
        } else {
          selectionTimer.start();
        }
      }
    };
    ListSelectionModel rowSelectionModel = table.getSelectionModel();
    rowSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    rowSelectionModel.addListSelectionListener(selectionListener);

    ListSelectionModel columnSelectionModel = columnModel.getSelectionModel();
    columnSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    columnSelectionModel.addListSelectionListener(selectionListener);
    table.addMouseWheelListener(new MouseAdapter() {

      @Override
      public void mouseWheelMoved(MouseWheelEvent mwe) {
        if (mwe.getWheelRotation() < 0) {
          yearMonthSpinner.setValue(yearMonthSpinner.getValue().plusMonths(1));
        } else {
          yearMonthSpinner.setValue(yearMonthSpinner.getValue().minusMonths(1));
        }
      }
    });
    setTableSelection();

    changeTableAction(KeyEvent.VK_LEFT, ae -> {
      if (min != null && value.equals(min)) {
        return false;
      }
      if (table.getSelectedColumn() == 0) {
        LocalDate newValue = value.minusDays(1);
        MonthView.this.firePropertyChange("Value", value, newValue);
        setValue(newValue);
        return false;
      }
      return true;
    });
    changeTableAction(KeyEvent.VK_UP, ae -> {
      if (min != null && value.isBefore(min.plusDays(7))) {
        MonthView.this.firePropertyChange("Value", value, min);
        setValue(min);
        return false;
      }
      if (table.getSelectedRow() == 0) {
        LocalDate newValue = value.minusDays(7);
        MonthView.this.firePropertyChange("Value", value, newValue);
        setValue(newValue);
        return false;
      }
      return true;
    });
    changeTableAction(KeyEvent.VK_RIGHT, ae -> {
      if (max != null && value.equals(max)) {
        return false;
      }
      if (table.getSelectedColumn() == 6) {
        LocalDate newValue = value.plusDays(1);
        MonthView.this.firePropertyChange("Value", value, newValue);
        setValue(newValue);
        return false;
      }
      return true;
    });
    changeTableAction(KeyEvent.VK_DOWN, ae -> {
      if (max != null && value.isAfter(max.minusDays(7))) {
        MonthView.this.firePropertyChange("Value", value, max);
        setValue(max);
        return false;
      }
      if (table.getSelectedRow() == 5) {
        LocalDate newValue = value.plusDays(7);
        MonthView.this.firePropertyChange("Value", value, newValue);
        setValue(newValue);
        return false;
      }
      return true;
    });
    changeTableAction(KeyEvent.VK_ENTER, ae -> {
      MonthView.this.firePropertyChange("Confirm", null, value);
      return false;
    });
    table.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseReleased(MouseEvent me) {
        MonthView.this.firePropertyChange("Confirm", null, value);
      }
    });

    setFocusable(true);

    JPanel north = new JPanel(new BorderLayout());
    north.add(yearMonthSpinner, BorderLayout.CENTER);

    Box tableBox = Box.createVerticalBox();
    tableBox.add(header);
    tableBox.add(table);

    add(north, BorderLayout.NORTH);
    add(tableBox, BorderLayout.CENTER);
    if (showtoday) {
      add(new TodayLinkLabel(), BorderLayout.SOUTH);
    }
  }

  private void changeTableAction(int key, Function<ActionEvent, Boolean> function) {
    InputMap ancestorMap = table.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    InputMap windowMap = table.getInputMap(WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = table.getActionMap();
    KeyStroke keyStroke = KeyStroke.getKeyStroke(key, 0);
    Object actionKey = ancestorMap.get(keyStroke);
    windowMap.put(keyStroke, actionKey);

    Action oldAction = actionMap.get(actionKey);
    Action replacement = new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent ae) {
        if (function.apply(ae)) {
          oldAction.actionPerformed(ae);
        }
      }
    };
    actionMap.put(actionKey, replacement);
  }

  private void setYearMonth(YearMonth yearMonth) {
    if (min != null && yearMonth.atEndOfMonth().isBefore(min)) {
      yearMonth = YearMonth.from(min);
    }
    if (max != null && yearMonth.atDay(1).isAfter(max)) {
      yearMonth = YearMonth.from(max);
    }
    if (yearMonth.equals(this.yearMonth)) {
      //return; // no, we still have to setTableSelection
    }

    if (!yearMonth.equals(yearMonthSpinner.getValue())) {
      programmaticChange = true;
      yearMonthSpinner.setValue(yearMonth);
      programmaticChange = false;
    }
    this.yearMonth = yearMonth;
    startDate = yearMonth.atDay(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    setTableSelection();
    repaint();
  }

  /**
   * Returns the current value
   *
   * @return the current value
   */
  public LocalDate getValue() {
    return value;
  }

  /**
   * Sets the current value, adjusted to be within any specified min/max range.
   *
   * @param value The value to set
   */
  public void setValue(LocalDate value) {
    if (min != null && value.isBefore(min)) {
      value = min;
    }
    if (max != null && value.isAfter(max)) {
      value = max;
    }
    this.value = value;
    setYearMonth(YearMonth.from(value));
  }

  private void setTableSelection() {
    programmaticChange = true;
    if (value.isBefore(startDate)
        || value.isAfter(startDate.plusDays(41))) {
      table.clearSelection();
    } else {
      int days = (int) ChronoUnit.DAYS.between(startDate, value);
      table.setRowSelectionInterval(days / 7, days / 7);
      table.setColumnSelectionInterval(days % 7, days % 7);
    }
    programmaticChange = false;
  }

  /**
   * Returns the minimum value (earliest date), or <CODE>null</CODE> if no limit is set.
   *
   * @return The earliest date that can be selected.
   */
  public LocalDate getMin() {
    return min;
  }

  /**
   * Sets the minimum value (earliest date). Call this method with a <CODE>null</CODE> value for no
   * limit.
   * <P>
   * This method does not attempt to verify that min <= value <= max. It is the responsibility of
   * client code to supply a sane value.
   *
   * @param min The earliest date that can be selected, or <CODE>null</CODE> for no limit.
   */
  public void setMin(LocalDate min) {
    this.min = min;
  }

  /**
   * Returns the maximum value (latest date), or <CODE>null</CODE> if no limit is set.
   *
   * @return The latest date that can be selected.
   */
  public LocalDate getMax() {
    return max;
  }

  /**
   * Sets the maximum value (latest date). Call this method with a <CODE>null</CODE> value for no
   * limit.
   * <P>
   * This method does not attempt to verify that min <= value <= max. It is the responsibility of
   * client code to supply a sane value.
   *
   * @param max The latest date that can be selected, or <CODE>null</CODE> for no limit.
   */
  public void setMax(LocalDate max) {
    this.max = max;
  }

  private class MonthViewTableModel<T extends LocalDate> extends AbstractTableModel {

    @Override
    public Class<?> getColumnClass(int column) {
      return LocalDate.class;
    }

    @Override
    public String getColumnName(int column) {
      return DayOfWeek.of((column + 6) % 7 + 1)
          .getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
          .substring(0, 2);
    }

    @Override
    public int getRowCount() {
      return 6;
    }

    @Override
    public int getColumnCount() {
      return 7;
    }

    @Override
    public Object getValueAt(int row, int column) {
      return startDate.plusDays(row * 7 + column);
    }
  }

  private class MonthViewTableHeaderCellRenderer implements TableCellRenderer {

    private final TableCellRenderer defaultRenderer;

    private MonthViewTableHeaderCellRenderer(TableCellRenderer defaultRenderer) {
      this.defaultRenderer = defaultRenderer;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = defaultRenderer.getTableCellRendererComponent(table, value,
          hasFocus, hasFocus, row, row);
      c.setForeground(column == 0 ? Colors.normalSunday
          : column == 6 ? Colors.normalSaturday
              : Colors.normal);
      return c;
    }

  }

  private class MonthViewTableCellRenderer extends DefaultTableCellRenderer {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d");

    private MonthViewTableCellRenderer() {
      setHorizontalAlignment(RIGHT);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      super.getTableCellRendererComponent(table, value,
          isSelected, hasFocus, row, column);
      setText(formatter.format((TemporalAccessor) value));

      LocalDate dateValue = (LocalDate) value;
      boolean isSunday = dateValue.getDayOfWeek() == DayOfWeek.SUNDAY;
      boolean isSaturday = dateValue.getDayOfWeek() == DayOfWeek.SATURDAY;
      if (dateValue.getMonth() == yearMonth.getMonth()) {
        setForeground(isSunday ? Colors.normalSunday : isSaturday ? Colors.normalSaturday : Colors.normal);
      } else {
        setForeground(isSunday ? Colors.fadedSunday : isSaturday ? Colors.fadedSaturday : Colors.faded);
      }
      hasFocus = true;
      if (min != null && dateValue.isBefore(min)
          || max != null && dateValue.isAfter(max)) {
        setForeground(Colors.blank);
        isSelected = false;
        hasFocus = false;
      }
      super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      setText(getText() + " ");
      return this;
    }
  }

  private static class Colors {

    public static final Color normal;
    public static final Color faded;
    public static final Color normalSunday;
    public static final Color fadedSunday;
    public static final Color normalSaturday;
    public static final Color fadedSaturday;
    public static final Color blank;
    private static final int INCREMENT = 60;

    static {
      JTextField dummy = new JTextField();
      normal = dummy.getForeground();
      faded = dummy.getDisabledTextColor();
      normalSunday = addRed(normal);
      fadedSunday = addRed(faded);
      normalSaturday = addGreen(normal);
      fadedSaturday = addGreen(faded);
      blank = faded.brighter();//dummy.getBackground();
    }

    private static Color addRed(Color color) {
      return new Color(increase(color.getRed()),
          decrease(color.getGreen()),
          decrease(color.getBlue()));
    }

    private static Color addGreen(Color color) {
      return new Color(decrease(color.getRed()),
          increase(color.getGreen()),
          decrease(color.getBlue()));
    }

    private static int increase(int i) {
      i += INCREMENT * 2;
      return i > 255 ? 255 : i;
    }

    private static int decrease(int i) {
      i -= INCREMENT;
      return i < 0 ? 0 : i;
    }
  }

  private class TodayLinkLabel extends JLabel {

    private final Font normalFont;
    private final Font underlinedFont;

    private TodayLinkLabel() {
      super("Today: " + LocalDate.now().format(LABEL_FORMATTER));
      setHorizontalAlignment(CENTER);
      setForeground(Color.BLUE);
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

      normalFont = getFont();
      Map attributes = normalFont.getAttributes();
      attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
      underlinedFont = normalFont.deriveFont(attributes);

      addMouseListener(new MouseAdapter() {

        @Override
        public void mouseEntered(MouseEvent e) {
          setFont(underlinedFont);
        }

        @Override
        public void mouseExited(MouseEvent e) {
          setFont(normalFont);
        }

        @Override
        public void mouseClicked(MouseEvent me) {
          setValue(LocalDate.now());
        }
      });
    }
  }
}
