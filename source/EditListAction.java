import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/*
 *	A simple popup editor for a JList that allows you to change
 *  the value in the selected row.
 *
 *  The default implementation has a few limitations:
 *
 *  a) the JList must be using the DefaultListModel
 *  b) the data in the model is replaced with a String object
 *
 *  If you which to use a different model or different data then you must
 *  extend this class and:
 *
 *  a) invoke the setModelClass(...) method to specify the ListModel you need
 *  b) override the applyValueToModel(...) method to update the model
 */
public class EditListAction extends AbstractAction
{
	private JList list;

	private JPopupMenu editPopup;
	private JTextField editTextField;
	private Class<?> modelClass;

	public EditListAction()
	{
		setModelClass(DefaultListModel.class);
	}

	protected void setModelClass(Class modelClass)
	{
		this.modelClass = modelClass;
	}

	protected void applyValueToModel(String value, ListModel model, int row)
	{
		DefaultListModel dlm = (DefaultListModel)model;
		dlm.set(row, value);
	}

	/*
	 *	Display the popup editor when requested
	 */
	public void actionPerformed(ActionEvent e)
	{
		list = (JList)e.getSource();
		ListModel model = list.getModel();

		if (! modelClass.isAssignableFrom(model.getClass())) return;

		//  Do a lazy creation of the popup editor

		if (editPopup == null)
    		createEditPopup();

		//  Position the popup editor over top of the selected row

		int row = list.getSelectedIndex();
		Rectangle r = list.getCellBounds(row, row);

		editPopup.setPreferredSize(new Dimension(r.width, r.height));
		editPopup.show(list, r.x, r.y);

		//  Prepare the text field for editing

		editTextField.setText( list.getSelectedValue().toString() );
		editTextField.selectAll();
		editTextField.requestFocusInWindow();
	}

	/*
	 *  Create the popup editor
	 */
	private void createEditPopup()
	{
		//  Use a text field as the editor

		editTextField = new JTextField();
		Border border = UIManager.getBorder("List.focusCellHighlightBorder");
		editTextField.setBorder( border );

		//  Add an Action to the text field to save the new value to the model

		editTextField.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String value = editTextField.getText();
				ListModel model = list.getModel();
				int row = list.getSelectedIndex();
				applyValueToModel(value, model, row);
				editPopup.setVisible(false);
			}
		});

		//  Add the editor to the popup

	    editPopup = new JPopupMenu();
		editPopup.setBorder( new EmptyBorder(0, 0, 0, 0) );
    	editPopup.add(editTextField);
	}
}
