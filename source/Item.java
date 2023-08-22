/**
 *  The Item class is designed to be used by Swing components that use a
 *  renderer to display an Object. Swing renderers rely on the toString()
 *  implementation to display a description of an Object. Separating
 *  the value and description properties gives increased flexibilty.
 *
 *  The "description" property is used by the renderer. The "value"
 *  property is used for processing by the application.
 */
public class Item<V> implements Comparable<Item>
{
	private V value;
	private String description;

	/**
	 *  Create an Item object
	 *
	 *  @param value an Object containing data used by the application
	 *  @param description the text to be displayed by a renderer
	 */
	public Item(V value, String description)
	{
		this.value = value;
		this.description = description;
	}

	/**
	 *  Get the Object containing application data
	 *
	 *  @returns the application data
	 */
	public V getValue()
	{
		return value;
	}

	/**
	 *  Get the description of the value data
	 *
	 *  @returns the description to be displayed by a renderer
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 *  Implement the natural order for this class using the
	 *  Description property
	 *
	 *  @param item the other Item object to be used in the comparison
	 */
	public int compareTo(Item item)
	{
		return getDescription().compareTo(item.getDescription());
	}

	/**
	 *  The Value property will be used to check for equality
	 */
	@Override
	public boolean equals(Object object)
	{
		Item item = (Item)object;

		return (item == null) ? false : value.equals(item.getValue());
	}

	/**
	 *  The Value property will be used to determine the hashCode
	 */
	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	/**
	 *	The Description property will double as the toString representation.
	 *
	 *  @return the description
	 */
	@Override
	public String toString()
	{
		return description;
	}
}
