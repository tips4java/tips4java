/**
 * @(#)SpinnerTemporalEditor.java	1.0 2014/12/15
 */
package darrylbu.editor;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.InternationalFormatter;
import darrylbu.model.SpinnerTemporalModel;

/**
 * This class extends {@link JSpinner.DefaultEditor} to support using a JSpinner with the temporal
 * classes of the java.time package. These include LocalDate, LocalDateTime, LocalTime, MonthDay,
 * Year and YearMonth.
 * <P>
 * It is designed to be used in conjunction with a {@link SpinnerTemporalModel}.
 * <P>
 * <B>Note that compiling or using this class requires Java 8</B>
 *
 * @author Darryl
 * @see Temporal
 */
public class SpinnerTemporalEditor extends JSpinner.DefaultEditor {

  private final SpinnerTemporalModel model;
  private final DateTimeFormatter formatter;

  /**
   * Constructs a SpinnerTemporalEditor for the spinner, with entered values parsed according to the
   * formatter.
   *
   * @param spinner The JSpinner to which this editor is set
   * @param formatter A formatter suited to the temporal type
   */
  public SpinnerTemporalEditor(JSpinner spinner, DateTimeFormatter formatter) {
    super(spinner);
    this.model = (SpinnerTemporalModel) spinner.getModel();
    this.formatter = formatter;

    JFormattedTextField ftf = getTextField();
    ftf.setEditable(true);
    ftf.setFormatterFactory(new DefaultFormatterFactory(new TemporalEditorFormatter()));

    // wildly approximate, see comments in JSpinner.DateEditor source
    int startLength = model.getMin() == null ? 0
        : formatter.format(model.getMin()).length();
    int endLength = model.getMax() == null ? 0
        : formatter.format(model.getMax()).length();
    int valueLength = formatter.format(model.getTemporalValue()).length();
    int maxLength = startLength > valueLength && startLength > endLength ? startLength
        : valueLength > endLength ? valueLength : endLength;
    ftf.setColumns(maxLength);
  }

  /**
   * Return our spinner ancestor's SpinnerTemporalModel.
   * 
   * @return getSpinner().getModel()
   */
  public SpinnerTemporalModel getModel() {
    return (SpinnerTemporalModel) getSpinner().getModel();
  }

  private class TemporalEditorFormatter extends InternationalFormatter {

    private TemporalEditorFormatter() {
    }

    @Override
    public Object stringToValue(String text) throws ParseException {
      TemporalAccessor ta;
      try {
        ta = formatter.parse(text);
      } catch (DateTimeParseException dtpe) {
        return model.getValue();
      }
      Temporal value = (Temporal) model.getValue();
      for (ChronoField field : ChronoField.values()) {
        if (field.isSupportedBy(value)
            && ta.isSupported(field)) {
          value = field.adjustInto(value, ta.getLong(field));
        }
      }
      if ((getMinimum() != null && getMinimum().compareTo(value) > 0)
          || (getMaximum() != null && getMaximum().compareTo(value) < 0)) {
        throw new ParseException("Value out of range", 0);
      }
      return value;
    }

    @Override
    public String valueToString(Object value) {
      if (formatter == null) {
        return value.toString();
      }
      return formatter.format((TemporalAccessor) value);
    }

    @Override
    public void setMinimum(Comparable min) {
      model.setMin(min);
    }

    @Override
    public Comparable getMinimum() {
      return (Comparable) model.getMin();
    }

    @Override
    public void setMaximum(Comparable max) {
      model.setMax(max);
    }

    @Override
    public Comparable getMaximum() {
      return (Comparable) model.getMax();
    }

    protected Object getValue() {
      return model.getValue();
    }
  }
}
