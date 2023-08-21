import java.util.Comparator;
import java.util.List;

/**
 *  Sort an object based on a specified column within the object.
 *  The object must be either a List or an Array. Several sort
 *  properties can be set:
 *
 *  a) ascending (default true)
 *  b) ignore case (default true)
 *  c) nulls last (default true)
 *
 *  An array of primitives can also be sorted. Only the ascending
 *  property is considered for the sort.
 *
 *  Use the Arrays.sort() method with the ColumnComparator to sort Arrays.
 *  All rows in the Array must be of the same class, but they can be either:
 *  - an Array
 *  - a List
 *
 *  Use the Collections.sort(.) method with the ColumnComparator to sort Lists.
 *  All rows in the List must be of the same class, but they can be either:
 *  - a List
 *  - an Array
 */
public class ColumnComparator implements Comparator
{
	private int column;
	private boolean isAscending;
	private boolean isIgnoreCase;
	private boolean isNullsLast = true;

	/*
	 *  The specified column will be sorted using default sort properties.
	 */
	ColumnComparator(int column)
	{
		this(column, true);
	}

	/*
	 *  The specified column wil be sorted in the specified order
	 *  with the remaining default properties
	 */
	ColumnComparator(int column, boolean isAscending)
	{
		this(column, isAscending, true);
	}

	/*
	 *  The specified column wil be sorted in the specified order and
	 *  case sensitivity with the remaining default properties
	 */
	ColumnComparator(int column, boolean isAscending, boolean isIgnoreCase)
	{
		setColumn( column );
		setAscending( isAscending );
		setIgnoreCase( isIgnoreCase );
	}

	/*
	 *  Set the column to be sorted
	 */
	public void setColumn(int column)
	{
		this.column = column;
	}

	/*
	 *  Set the sort order
	 */
	public void setAscending(boolean isAscending)
	{
		this.isAscending = isAscending;
	}

	/*
	 *  Set whether case should be ignored when sorting Strings
	 */
	public void setIgnoreCase(boolean isIgnoreCase)
	{
		this.isIgnoreCase = isIgnoreCase;
	}

	/*
	 *  Set nulls position in the sort order
	 */
	public void setNullsLast(boolean isNullsLast)
	{
		this.isNullsLast = isNullsLast;
	}

	/*
	 *  Implement the Comparable interface
	 */
	public int compare(Object a, Object b)
	{
		//  Sorting of primitives/objects is done separately

		String className = a.getClass().getName();

		if (className.length() == 2)
		{
			return primitiveSort(a, b, className);
		}
		else
			return objectSort(a, b);
	}

	private int primitiveSort(Object a, Object b, String className)
	{
		int result = 0;

		if (className.equals("[Z"))
			result = Boolean.compare(((boolean[])a)[column], ((boolean[])b)[column]);
		else if (className.equals("[B"))
			result = Byte.compare(((byte[])a)[column], ((byte[])b)[column]);
		else if (className.equals("[C"))
			result = Character.compare(((char[])a)[column], ((char[])b)[column]);
		else if (className.equals("[D"))
			result = Double.compare(((double[])a)[column], ((double[])b)[column]);
		else if (className.equals("[F"))
			result = Float.compare(((float[])a)[column], ((float[])b)[column]);
		else if (className.equals("[I"))
			result = Integer.compare(((int[])a)[column], ((int[])b)[column]);
		else if (className.equals("[I"))
			result = Integer.compare(((int[])a)[column], ((int[])b)[column]);
		else if (className.equals("[J"))
			result = Long.compare(((long[])a)[column], ((long[])b)[column]);
		else if (className.equals("[S"))
			result = Short.compare(((short[])a)[column], ((short[])b)[column]);

		if (! isAscending)
			result *= -1;

		return result;
	}

	@SuppressWarnings("unchecked")
	private int objectSort(Object a, Object b)
	{
		Object o1 = null;
		Object o2 = null;

		//  The object to be sorted must be a List or an Array

		if (a instanceof List)
		{
			List list1 = (List)a;
			List list2 = (List)b;

			o1 = list1.get(column);
			o2 = list2.get(column);
		}

		if (a.getClass().isArray())
		{
			Object[] a1 = (Object[])a;
			Object[] a2 = (Object[])b;

			o1 = a1[column];
			o2 = a2[column];
		}

		// Treat empty strings like nulls

		if (o1 instanceof String && ((String)o1).length() == 0)
		{
			o1 = null;
		}

		if (o2 instanceof String && ((String)o2).length() == 0)
		{
			o2 = null;
		}

		// Handle sorting of null values

		if (o1 == null && o2 == null) return 0;

		if (o1 == null) return isNullsLast ? 1 : -1;

		if (o2 == null) return isNullsLast ? -1 : 1;

		//  Compare objects

		int result = 0;

		if (o1 instanceof Comparable)
		{
			if (o1 instanceof String
			&&  isIgnoreCase)
				result = ((String)o1).compareToIgnoreCase((String)o2);
			else
				result = ((Comparable)o1).compareTo(o2);
		}
		else  // Compare as a String
		{
			if (isIgnoreCase)
				result = o1.toString().compareToIgnoreCase(o2.toString());
			else
				result = o1.toString().compareTo(o2.toString());
		}

		if (! isAscending)
			result *= -1;

		return result;
	}
}
