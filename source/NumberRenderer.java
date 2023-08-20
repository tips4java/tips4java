import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.SwingConstants;

public class NumberRenderer extends FormatRenderer
{
	/*
	 *  Use the specified number formatter and right align the text
	 */
	public NumberRenderer(NumberFormat formatter)
	{
		super(formatter);
		setHorizontalAlignment( SwingConstants.RIGHT );
	}

	/*
	 *  Use the default currency formatter for the default locale
	 */
	public static NumberRenderer getCurrencyRenderer()
	{
		return new NumberRenderer( NumberFormat.getCurrencyInstance() );
	}

	/*
	 *  Use the default integer formatter for the default locale
	 */
	public static NumberRenderer getIntegerRenderer()
	{
		return new NumberRenderer( NumberFormat.getIntegerInstance() );
	}

	/*
	 *  Use the default percent formatter for the default locale
	 */
	public static NumberRenderer getPercentRenderer()
	{
		return new NumberRenderer( NumberFormat.getPercentInstance() );
	}
}
