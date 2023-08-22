/**
 * @(#)SpinnerTemporalModel.java	1.0 2014/12/05
 */
package darrylbu.model;

import darrylbu.editor.SpinnerTemporalEditor;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.UnsupportedTemporalTypeException;
import javax.swing.AbstractSpinnerModel;

/**
 * SpinnerTemporalModel in conjunction with {@link SpinnerTemporalEditor} allows using a JSpinner
 * with temporal classes of the java.time package. Generics and additional methods provide
 * compile-time type safety for accessing and mutating the temporal value.
 * <P>
 *  <B>Note that compiling or using this class requires Java 8</B>
 * 
 * @author Darryl
 * @see Temporal
 * @param <T> The type of value, usually one of LocalDate, LocalDateTime, LocalTime, MonthDay, Year
 * and YearMonth.
 */
public class SpinnerTemporalModel<T extends Temporal & TemporalAdjuster & Comparable>
    extends AbstractSpinnerModel {

  private T value;
  private T min;
  private T max;
  private ChronoUnit step;

  /**
   * Constructs a SpinnerTemporalModel with the specified value, minimum (earliest) / maximum
   * (latest) bounds, and step.
   *
   * @param value the current value of the model
   * @param min the earliest element in the sequence, or null for no limit.
   * @param max the latest element in the sequence, or null for no limit.
   * @param step the temporal period between elements of the sequence
   *
   * @throws IllegalArgumentException if value is null or either or both of min/max are non-null and
   * (minimum <= value <= maximum) is false. @throws UnsupportedTemporalTypeException if the unit of
   * the step is not supported by the generic type T.
   */
  public SpinnerTemporalModel(T value, T min, T max, ChronoUnit step) {
    if (value == null) {
      throw new IllegalArgumentException("value is null");
    }
    if (!step.isSupportedBy(value)) {
      throw new UnsupportedTemporalTypeException("Unit " + step.name()
          + " is not supported for type " + value.getClass().getSimpleName());
    }
    // min <= value <= max
    if ((min != null && min.compareTo(value) > 0)
        || max != null && max.compareTo(value) < 0) {
      throw new IllegalArgumentException("(start <= value <= end) is false");
    }

    this.value = value;
    this.min = min;
    this.max = max;
    this.step = step;
  }

  /**
   * This method provides compile-time type safety. Client code should call this method rather than
   * getValue(), which is retained for compatibility with core Swing code.
   *
   * @return
   */
  public T getTemporalValue() {
    return value;
  }

  /**
   * This method provides compile-time type safety. Client code should call this method rather than
   * setValue(Object), which is retained for compatibility with core Swing code.
   *
   * @param value
   */
  public void setTemporalValue(T value) {
    setValue(value);
  }

  /**
   * {@inheritDoc }
   *
   * @deprecated Client code should use getTemporalValue which provides compile-time type safety.
   */
  @Override
  @Deprecated
  public Object getValue() {
    return value;
  }

  /**
   * {@inheritDoc }
   *
   * @param value The value to set
   * @throws IllegalArgumentException if value is null or cannot be cast to the requisite type.
   *
   * @deprecated Client code should use setTemporalValue which provides compile-time type safety.
   */
  @Override
  @Deprecated
  public void setValue(Object value) {
    if (value == null) {
      throw new IllegalArgumentException("value is null");
    }
    T t;
    try {
      t = (T) value;
    } catch (ClassCastException cce) {
      throw new IllegalArgumentException("illegal value", cce);
    }
    if (!t.equals(this.value)) {
      this.value = t;
      fireStateChanged();
    }
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public Object getNextValue() {
    if (max != null && max.equals(value)) {
      return null;
    }
    
    T newValue = step.addTo(value, 1);
    return max == null || max.compareTo(newValue) > 0 ? newValue : max;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public Object getPreviousValue() {
    if (min != null && min.equals(value)) {
      return null;
    }
    
    T newValue = step.addTo(value, -1);
    return min == null || min.compareTo(newValue) < 0 ? newValue : min;
  }

  /**
   * Returns the minimum (earliest) value.
   *
   * @return The minimum value
   */
  public T getMin() {
    return min;
  }
  
  /**
   * Sets the minimum (earliest) value.
   * 
   * @param min the earliest value
   */
  public void setTemporalMin(T min) {
    setMin(min);
  }

  /**
   * Sets the minimum (earliest) value.
   *
   * @param min the minimum value
   * 
   * @deprecated  Client code should use setTemporalMin which provides compile-time type safety.
   */
  @Deprecated
  public void setMin(Comparable min) {
    if ((min == null) ? (this.min != null) : !min.equals(this.min)) {
      this.min = (T) min;
      fireStateChanged();
    }
  }

  /**
   * Returns the maximum (latest) value.
   *
   * @return The maximum value
   */
  public T getMax() {
    return max;
  }
  
  /**
   * Sets the maximum (latest) value.
   * 
   * @param max the latest value
   */
  public void setTemporalMax(T max) {
    setMax(max);
  }

  /**
   * Sets the maximum (latest) value.
   *
   * @param max
   * 
   * @deprecated  Client code should use setTemporalMax which provides compile-time type safety.
   */
  @Deprecated
  public void setMax(Comparable max) {
    if ((max == null) ? (this.max != null) : !max.equals(this.max)) {
      this.max = (T) max;
      fireStateChanged();
    }
  }

  /**
   * Returns the size of the value change computed by the getNextValue and getPreviousValue methods.
   *
   * @return The value of the step property.
   */
  public ChronoUnit getStep() {
    return step;
  }

  /**
   * Changes the size of the value change computed by the getNextValue and getPreviousValue methods.
   *
   * @param step the step to set
   * @throws UnsupportedTemporalTypeException if the step is not supported by the temporal type e.g.
   * attempting to set ChronoUnit.DAYS when the type T is YearMonth.
   */
  public void setStep(ChronoUnit step) {
    if (!step.isSupportedBy(value)) {
      throw new UnsupportedTemporalTypeException("Unsupported unit: " + step.name());
    }
    this.step = step;
  }
}
