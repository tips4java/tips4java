import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ItemDemo extends JPanel
{
	public ItemDemo()
	{
		setBorder( new EmptyBorder(10, 10, 10, 10) );
		setLayout( new GridLayout(0, 2, 10, 5) );
		add( new JLabel( "Item Basic Use" ) );
		add( new JLabel( "Item As Wrapper" ) );

		//  Create combo box showing basic use of the Item class

		JComboBox<Item<String>> comboBox = new JComboBox<Item<String>>();
		add( comboBox );

		comboBox.addItem( new Item<String>("CA", "Canada" ) );
		comboBox.addItem( new Item<String>("GB", "United Kingdom" ) );
		comboBox.addItem( new Item<String>("US", "United States" ) );

		comboBox.addActionListener( new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JComboBox comboBox = (JComboBox)e.getSource();
				Item item = (Item)comboBox.getSelectedItem();
				String code = (String)item.getValue();
				System.out.println( code );
			}
		});

		// Create combo box using Item class as a wrapper

		JComboBox<Item<Car>> comboBox2 = new JComboBox<Item<Car>>();
		add( comboBox2 );

		Car c1 = new Car("Ford", "Crown Victoria");
		Car c2 = new Car("Ford", "Focus");
		Car c3 = new Car("Ford", "Fusion");
		Car c4 = new Car("Honda", "Civic");
		Car c5 = new Car("Honda", "Fit");
		Car c6 = new Car("Nissan", "Altima");
		Car c7 = new Car("Toyota", "Camry");
		Car c8 = new Car("Toyota", "Corolla");

		comboBox2.setModel( new SortedComboBoxModel<Item<Car>>() );
		comboBox2.addItem( new Item<Car>(c1, c1.getModel()) );
		comboBox2.addItem( new Item<Car>(c2, c2.getModel()) );
		comboBox2.addItem( new Item<Car>(c3, c3.getModel()) );
		comboBox2.addItem( new Item<Car>(c4, c4.getModel()) );
		comboBox2.addItem( new Item<Car>(c5, c5.getModel()) );
		comboBox2.addItem( new Item<Car>(c6, c6.getModel()) );
		comboBox2.addItem( new Item<Car>(c7, c7.getModel()) );
		comboBox2.addItem( new Item<Car>(c8, c8.getModel()) );

		comboBox2.addActionListener( new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JComboBox comboBox = (JComboBox)e.getSource();
				Item item = (Item)comboBox.getSelectedItem();
				Car car = (Car)item.getValue();
				System.out.println( car );
			}
		});
	}

	private static void createAndShowUI()
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("ItemDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new ItemDemo() );
		frame.pack();
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}

	static class Car
	{
		private String make;
		private String model;

    	public Car(String make, String model)
    	{
    	   	this.make = make;
    	   	this.model = model;
    	}

    	public String getMake()
    	{
    		return make;
    	}

    	public String getModel()
    	{
    		return model;
    	}

    	public String toString()
    	{
    		return make + " : " + model;
    	}
    }

	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				createAndShowUI();
			}
		});
	}
}
