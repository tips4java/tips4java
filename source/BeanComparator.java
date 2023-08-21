import java.lang.reflect.*;
import java.util.*;

/**
 *  A comparator to sort on the specified field of a given class.
 *
 *  Reflection is used to retrieve the data to be sorted, therefore
 *  you must provide the Class and the method name within the class
 *  that will be used to retrieve the data.
 *
 *  Several sort properties can be set:
 *
 *  a) ascending (default true)
 *  b) ignore case (default true)
 *  c) nulls last (default true)
 */
public class BeanComparator implements Comparator
{
	private static final Class[] EMPTY_CLASS_ARRAY = new Class[] {};
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};

	private Method method;
	private boolean isAscending;
	private boolean isIgnoreCase;
	private boolean isNullsLast = true;


	/*
	 *	Sort using default sort properties
	 */
	BeanComparator(Class<?> beanClass, String methodName)
	{
		this(beanClass, methodName, true);
	}

	/*
	 *	Sort in the specified order with the remaining default properties
	 */
	BeanComparator(Class<?> beanClass, String methodName, boolean isAscending)
	{
		this(beanClass, methodName, isAscending, true);
	}

	/*
	 *	Sort in the specified order and case sensitivity with the
	 *  remaining default properties
	 */
	BeanComparator(Class<?> beanClass, String methodName, boolean isAscending, boolean isIgnoreCase)
	{
		setAscending( isAscending );
		setIgnoreCase( isIgnoreCase );

		//  Make sure the method exists in the given bean class

		try
		{
			method = beanClass.getMethod(methodName, EMPTY_CLASS_ARRAY);
		}
		catch(NoSuchMethodException nsme)
		{
			String message = methodName + "() method does not exist";
			throw new IllegalArgumentException( message );
		}

		//  Make sure the method returns a value

		Class returnClass =	method.getReturnType();

		if (returnClass.getName().equals("void"))
		{
			String message = methodName + " has a void return type";
			throw new IllegalArgumentException( message );
		}
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
	@SuppressWarnings("unchecked")
	public int compare(Object object1, Object object2)
	{
		Object field1 = null;
		Object field2 = null;

		try
		{
			field1 = method.invoke(object1, EMPTY_OBJECT_ARRAY);
			field2 = method.invoke(object2, EMPTY_OBJECT_ARRAY);
		}
		catch (Exception e)
		{
			throw new RuntimeException( e );
		}

		// Treat empty strings like nulls

		if (field1 instanceof String && ((String)field1).length() == 0)
		{
			field1 = null;
		}

		if (field2 instanceof String && ((String)field2).length() == 0)
		{
			field2 = null;
		}

		// Handle sorting of null values

		if (field1 == null && field2 == null) return 0;

		if (field1 == null) return isNullsLast ? 1 : -1;

		if (field2 == null) return isNullsLast ? -1 : 1;

		//  Compare objects

		Object c1;
		Object c2;

		if (isAscending)
		{
			c1 = field1;
			c2 = field2;
		}
		else
		{
			c1 = field2;
			c2 = field1;
		}

		if (c1 instanceof Comparable)
		{
			if (c1 instanceof String
			&&  isIgnoreCase)
				return ((String)c1).compareToIgnoreCase((String)c2);
			else
				return ((Comparable)c1).compareTo(c2);
		}
		else  // Compare as a String
		{
			if (isIgnoreCase)
				return c1.toString().compareToIgnoreCase(c2.toString());
			else
				return c1.toString().compareTo(c2.toString());
		}
	}
}
