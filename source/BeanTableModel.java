import java.lang.reflect.*;
import java.util.*;

/**
 *  The BeanTableModel will use reflection to determine the columns of
 *  data to be displayed in the table model. Reflection is used to find all
 *  the methods declared in the specified bean. The criteria used for
 *  adding columns to the model are:
 *
 *  a) the method name must start with either "get" or "is"
 *  b) the parameter list for the method must contain 0 parameters
 *
 *  You can also specify an ancestor class in which case the declared methods
 *  of the ancestor and all its descendents will be included in the model.
 *
 *  A column name will be assigned to each column based on the method name.
 *
 *  The cell will be considered editable when a corresponding "set" method
 *  name is found.
 *
 *  Reflection will also be used to implement the getValueAt() and
 *  setValueAt() methods.
 */
public class BeanTableModel<T> extends RowTableModel<T>
{
	//  Map "type" to "class". Class is needed for the getColumnClass() method.

	private static Map<Class, Class> primitives = new HashMap<Class, Class>(10);

	static
	{
		primitives.put(Boolean.TYPE, Boolean.class);
		primitives.put(Byte.TYPE, Byte.class);
		primitives.put(Character.TYPE, Character.class);
		primitives.put(Double.TYPE, Double.class);
		primitives.put(Float.TYPE, Float.class);
		primitives.put(Integer.TYPE, Integer.class);
		primitives.put(Long.TYPE, Long.class);
		primitives.put(Short.TYPE, Short.class);
	}

	private Class beanClass;
	private Class ancestorClass;

	private List<ColumnInformation> columns = new ArrayList<ColumnInformation>();

	/**
	 *  Constructs an empty <code>BeanTableModel</code> for the specified bean.
	 *
	 *  @param beanClass  class of the beans that will be added to the model.
	 *                    The class is also used to determine the columns that
	 *                    will be displayed in the model
	 */
	public BeanTableModel(Class beanClass)
	{
		this(beanClass, beanClass, new ArrayList<T>());
	}

	/**
	 *  Constructs an empty <code>BeanTableModel</code> for the specified bean.
	 *
	 *  @param beanClass      class of the beans that will be added to the model.
	 *  @param ancestorClass  the methods of this class and its descendents down
	 *						 to the bean class can be included in the model.
	 */
	public BeanTableModel(Class beanClass, Class ancestorClass)
	{
		this(beanClass, ancestorClass, new ArrayList<T>());
	}

	/**
	 *  Constructs an empty <code>BeanTableModel</code> for the specified bean.
	 *
	 *  @param beanClass      class of the beans that will be added to the model.
	 *  @param modelData      the data of the table
	 */
	public BeanTableModel(Class beanClass, List<T> modelData)
	{
		this(beanClass, beanClass, modelData);
	}

	/**
	 *  Constructs an empty <code>BeanTableModel</code> for the specified bean.
	 *
	 *  @param beanClass      class of the beans that will be added to the model.
	 *  @param ancestorClass  the methods of this class and its descendents down
	 *						 to the bean class can be included in the model.
	 *  @param modelData      the data of the table
	 */
	public BeanTableModel(Class beanClass, Class ancestorClass, List<T> modelData)
	{
		super( beanClass );
		this.beanClass = beanClass;
		this.ancestorClass = ancestorClass;

		//  Use reflection on the beanClass and ancestorClass to find properties
		//  to add to the TableModel

		createColumnInformation();

		//  Initialize the column name List to the proper size. The actual
		//  column names will be reset in the resetModelDefaults() method.

		List<String> columnNames = new ArrayList<String>();

		for (ColumnInformation info: columns)
		{
			columnNames.add( info.getName() );
		}

		//  Reset all the values in the RowTableModel

		super.setDataAndColumnNames(modelData, columnNames);
		resetModelDefaults();
	}

	/*
	 *  Use reflection to find all the methods that should be included in the
	 *  model.
	 */
	@SuppressWarnings("unchecked")
	private void createColumnInformation()
	{
		Method[] theMethods = beanClass.getMethods();

		//  Check each method to make sure it should be used in the model

		for (int i = 0; i < theMethods.length; i++)
		{
			Method theMethod = theMethods[i];

			if (theMethod.getParameterTypes().length == 0
			&&  ancestorClass.isAssignableFrom(theMethod.getDeclaringClass()) )
			{
				String methodName = theMethod.getName();

				if (theMethod.getName().startsWith("get"))
					buildColumnInformation(theMethod, methodName.substring(3));

				if (theMethod.getName().startsWith("is"))
					buildColumnInformation(theMethod, methodName.substring(2));
			}
		}
	}

	/*
	 *	We found a method candidate so gather the information needed to fully
	 *  implemennt the table model.
	 */
	@SuppressWarnings("unchecked")
	private void buildColumnInformation(Method theMethod, String theMethodName)
	{
		//  Make sure the method returns an appropriate type

		Class returnType = getReturnType( theMethod );

		if (returnType == null) return;

		//  Convert the method name to a display name for each column and
		//  then check for a related "set" method.

		String headerName = formatColumnName( theMethodName );

		Method setMethod = null;

		try
		{
			String setMethodName = "set" + theMethodName;
			setMethod = beanClass.getMethod(setMethodName, theMethod.getReturnType());
		}
		catch(NoSuchMethodException e) {}

		//  We have all the information we need, so save it for later use
		//  by the table model methods.

		ColumnInformation ci = new ColumnInformation(headerName, returnType, theMethod, setMethod);
    	columns.add( ci );
	}

	/*
	 *  Make sure the return type of the method is something we can use
	 */
	private Class getReturnType(Method theMethod)
	{
		Class returnType = theMethod.getReturnType();

		if (returnType.isInterface()
		||  returnType.isArray())
			return null;

		//  The primitive class type is different then the wrapper class of the
		//  primitive. We need the wrapper class.

		if (returnType.isPrimitive())
			returnType = primitives.get(returnType);

		return returnType;
	}

	/*
	 *  Use information collected from the bean to set model default values.
	 */
	private void resetModelDefaults()
	{
		columnNames.clear();

		for (int i = 0; i < columns.size(); i++)
		{
			ColumnInformation info = columns.get(i);
			columnNames.add( info.getName() );
			super.setColumnClass(i, info.getReturnType());
			super.setColumnEditable(i, info.getSetter() == null ? false : true);
		}
	}

	/**
	 *  Returns an attribute value for the cell at <code>row</code>
	 *  and <code>column</code>.
	 *
	 *  @param   row        the row whose value is to be queried
	 *  @param   column     the column whose value is to be queried
	 *  @return             the value Object at the specified cell
	 *  @exception  IndexOutOfBoundsException
	 *                     if an invalid row or column was given
	 */
	@Override
	public Object getValueAt(int row, int column)
	{
		ColumnInformation ci = (ColumnInformation)columns.get( column );

		Object value = null;

		try
		{
			value = ci.getGetter().invoke(getRow(row));
		}
		catch(IllegalAccessException e) {}
		catch(InvocationTargetException e) {}

		return value;
	}

	/**
	 *  Sets the object value for the cell at <code>column</code> and
	 *  <code>row</code>.  <code>value</code> is the new value.  This method
	 *  will generate a <code>tableChanged</code> notification.
	 *
	 * @param   value           the new value; this can be null
	 * @param   row             the row whose value is to be changed
	 * @param   column          the column whose value is to be changed
	 * @exception  IndexOutOfBoundsException  if an invalid row or
	 *			   column was given
	 */
	@Override
	public void setValueAt(Object value, int row, int column)
	{
		ColumnInformation ci = (ColumnInformation)columns.get( column );

		try
		{
			Method setMethod = ci.getSetter();

			if (setMethod != null)
			{
				setMethod.invoke(getRow(row), value);
				fireTableCellUpdated(row, column);
			}
		}
		catch(IllegalAccessException e) {}
		catch(InvocationTargetException e) {}
	}

	/**
	 *  You are not allowed to change the class of any column.
	 */
	@Override
	public void setColumnClass(int column, Class columnClass)
	{
	}

	/**
	 *  Sets the editability for the specified column.
	 *
	 *	Override to make sure you can't set a column editable that doesn't
	 *  have a defined setter method.
	 *
	 *  @param  column       the column whose Class is being changed
	 *  @param  isEditable   indicates if the column is editable or not
	 *  @exception  ArrayIndexOutOfBoundsException
	 *	 		            if an invalid column was given
	 */
	@Override
	public void setColumnEditable(int column, boolean isEditable)
	{
		ColumnInformation ci = (ColumnInformation)columns.get( column );

		if (isEditable && ci.getSetter() == null) return;

		super.setColumnEditable(column, isEditable);
	}

	/**
	 *  Convenience method to change the generated column header name.
	 *
	 *  This method must be invoked before the model is added to the table.
	 *
	 *  @param   column     the column whose value is to be queried
	 *  @exception  IndexOutOfBoundsException  if an invalid column
	 *	            was given
	 */
	public void setColumnName(int column, String name)
	{
		ColumnInformation ci = (ColumnInformation)columns.get( column );
		ci.setName( name );
		resetModelDefaults();
	}

	/*
	 *  Columns are created in the order in which they are defined in the
	 *  bean class. This method will sort the columns by colum header name.
	 *
	 *  This method must be invoked before the model is added to the table.
	 */
	public void sortColumnNames()
	{
		Collections.sort(columns);
		resetModelDefaults();
	}

	/*
	 *  Class to hold data required to implement the TableModel interface
	 */
	private class ColumnInformation implements Comparable<ColumnInformation>
	{
		private String name;
		private Class returnType;
		private Method getter;
		private Method setter;

		public ColumnInformation(String name, Class returnType, Method getter, Method setter)
		{
			this.name = name;
			this.returnType = returnType;
			this.getter = getter;
			this.setter = setter;
		}

		/*
		 *  The column class of the model
		 */
		public Class getReturnType()
		{
			return returnType;
		}

		/*
		 *  Used by the getValueAt() method to get the data for the cell
		 */
		public Method getGetter()
		{
			return getter;
		}

		/*
		 *  The value used as the column header name
		 */
		public String getName()
		{
			return name;
		}

		/*
		 *  Used by the setValueAt() method to update the bean
		 */
		public Method getSetter()
		{
			return setter;
		}

		/*
		 *  Use to change the column header name
		 */
		public void setName(String name)
		{
			this.name = name;
		}

    	/*
    	 *  Implement the natural sort order for this class
    	 */
    	public int compareTo(ColumnInformation o)
    	{
    		return getName().compareTo(o.getName());
    	}
	}
}
