import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
/**
 *  This class will use a combo box as the editor for a JTable cell. It is
 *  different from the default combo box editor in that it supports multiple
 *  combo box models. The appropriate model will be choosen based on the cell
 *  about to be edited.
 *
 *  The model to be used by the combo box is determined by the value contained
 *  in the cell of a related column. This value is used to lookup the model.
 */
public class ComboBoxTableEditor extends DefaultCellEditor
{
	private int relatedColumn;

	protected HashMap<Object, ComboBoxModel> models = new HashMap<Object, ComboBoxModel>();

	/**
	 *  Create a combo box editor that supports different combo box models.
	 *
	 *  Convenience constructor. The related column is assumed to be the column
	 *  to the left of the editing column.
	 */
	public ComboBoxTableEditor()
	{
		this(-1);
	}

	/**
	 *  Create a combo box editor that supports different combo box models.
	 *
	 *  @param relatedColumn column whose value will be used to lookup the
	 *                       model to be used by the combo box
	 */
	public ComboBoxTableEditor(int relatedColumn)
	{
		this(new JComboBox(), relatedColumn);
	}

	/**
	 *  Create a combo box editor that supports different combo box models.
	 *
	 *  @param comboBox      a JComboBox to be used as the editor
	 *
	 *  @param relatedColumn column whose value will be used to lookup the
	 *                       model to be used by the combo box
	 */
	public ComboBoxTableEditor(JComboBox comboBox, int relatedColumn)
	{
		super( comboBox );
		this.relatedColumn = relatedColumn;
	}

	@Override
	/**
	 *  Each time the editor is invoked the combo box model will be set
	 *  based on the value contained in the related cell.
	 */
	public Component getTableCellEditorComponent(
		JTable table, Object value, boolean isSelected, int row, int column)
	{
		ComboBoxModel model = getModelForRow(table, value, row, column);

		//  When a model can't be found a null editor will be returned,
		//  meaning the cell can't be edited.

		if (model == null)
		{
			return null;
		}
		else
		{
			JComboBox comboBox = (JComboBox)getComponent();
			comboBox.setModel( model );
			comboBox.setSelectedItem( value );
        	return comboBox;
        }
	}

	/*
	 *  Get the model for the specified row
	 */
	protected ComboBoxModel getModelForRow(JTable table, Object value, int row, int column)
	{
		//  The key column defaults to the previous column in the model

		if (relatedColumn == -1)
			relatedColumn = table.convertColumnIndexToModel( column ) - 1;

		//  Use the value from the related column to do the lookup for the model

		Object key = table.getModel().getValueAt(row, relatedColumn);
		ComboBoxModel model = models.get( key );

		return model;
	}

//
//  Added methods
//
	/**
	 *  Convenience method to create a ComboBoxModel to be used by the editor
	 *  when the related column contains the specified key.
	 *
	 *  @param key used to lookup the related model
	 *  @param items List containing items to create a DefaultComboBoxModel
	 */
	public void addModel(Object key, List items)
	{
		Vector<Object> vector = new Vector<Object>(items.size());

		for (Object o : items)
			vector.add( o );

		addModel(key, vector);
	}

	/**
	 *  Convenience method to create a ComboBoxModel to be used by the editor
	 *  when the related column contains the specified key.
	 *
	 *  @param key used to lookup the related model
	 *  @param items Vector containing items to create a DefaultComboBoxModel
	 */
	public void addModel(Object key, Vector items)
	{
		addModel(key, new DefaultComboBoxModel(items));
	}

	/**
	 *  Convenience method to create a ComboBoxModel to be used by the editor
	 *  when the related column contains the specified key.
	 *
	 *  @param key used to lookup the related model
	 *  @param items Array containing items to create a DefaultComboBoxModel
	 */
	public void addModel(Object key, Object[] items)
	{
		addModel(key, new DefaultComboBoxModel(items));
	}

	/**
	 *  Convenience method to create a ComboBoxModel to be used by the editor
	 *  when the related column contains the specified key.
	 *
	 *  @param key used to lookup the related model
	 *  @param model model to be used by the editor for the given key
	 */
	public void addModel(Object key, ComboBoxModel model)
	{
		models.put(key, model);
	}
}
