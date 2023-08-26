import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.text.*;

public class CaretPositionListenerDemo
{
	public static void main(String[] args)
		throws Exception
	{
		JPanel left = new JPanel( new GridLayout(0, 1, 5, 5) );
		JPanel right = new JPanel( new GridLayout(0, 1, 5, 5) );
		Box main = Box.createHorizontalBox();
		main.setBorder( new EmptyBorder(10, 10, 10, 10) );
		main.add( left );
		main.add( Box.createHorizontalStrut(10) );
		main.add( right );

		MaskFormatter format1 = new MaskFormatter("###.###.###.###");
		JFormattedTextField ftf1 = new JFormattedTextField( format1 );
		ftf1.setValue( "123.456.789.111" );
		left.add( ftf1 );
		right.add( new JLabel("Default caret behaviour") );

		JFormattedTextField ftf2 = new JFormattedTextField( format1 );
		ftf2.setValue( "123.456.789.111" );
		left.add( ftf2 );
		CaretPositionListener cpl2 = new CaretPositionListener( ftf2 );
//		cpl2.setDynamicFormatting( true );
		right.add( new JLabel("Caret behaviour with listener") );
/*
		DecimalFormat format3 = new DecimalFormat();
		JFormattedTextField ftf3 = new JFormattedTextField( format3 );
		ftf3.setValue( new Integer(1234567890) );
		left.add( ftf3 );

		JFormattedTextField ftf4 = new JFormattedTextField( format3 );
		ftf4.setValue( new Integer(1234567890) );
		left.add( ftf4 );
		CaretPositionListener cpl4 = new CaretPositionListener( ftf4 );
*/
		Integer value = Integer.valueOf( 123456789 );
		JFormattedTextField ftf5 = new JFormattedTextField( value );
		left.add( ftf5 );
		right.add( new JLabel("Default caret behaviour") );

		JFormattedTextField ftf6 = new JFormattedTextField( value );
		left.add( ftf6 );
		CaretPositionListener cpl6 = new CaretPositionListener( ftf6 );
		cpl6.setDynamicFormatting( true );
		right.add( new JLabel("Caret behaviour with listener (dynamic = true)") );

		JFormattedTextField ftf7 = new JFormattedTextField( value );
		left.add( ftf7 );
		CaretPositionListener cpl7 = new CaretPositionListener( ftf7 );
		right.add( new JLabel("Caret behaviour with listener (dynamic = false)") );

        FocusListener fl = new FocusAdapter()
        {
            public void focusGained(final FocusEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        JTextField tf = (JTextField)e.getSource();
                        tf.selectAll();
                    }
                });
            }
        };
//        ftf1.addFocusListener( fl );

		JFrame frame = new JFrame("Caret Position Listener");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.add( main );
		frame.pack();
		frame.setLocationRelativeTo( null );
		frame.setVisible(true);
	}
}
