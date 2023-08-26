import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class DragLayoutDemo
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    public static void createAndShowGUI()
    {
		ComponentMover cm = new ComponentMover();
		cm.setEdgeInsets( new Insets(-100, -100, -100, -100) );
//		cm.setEdgeInsets( new Insets(10, 10, 10, 10) );
//		cm.setDragInsets( new Insets(5, 5, 5, 5) );
		cm.setAutoLayout(true);

		DragLayout dl = new DragLayout();
		dl.setUsePreferredSize(false);

		JPanel panel = new JPanel( dl );
		panel.setBorder( new MatteBorder(10, 10, 10, 10, Color.YELLOW) );

		createLabel(cm, panel, "North", 150, 0);
		createLabel(cm, panel, "West", 0, 100);
		createLabel(cm, panel, "East", 300, 100);
		createLabel(cm, panel, "South", 150, 200);
		createLabel(cm, panel, "Center", 150, 100);

		JFrame frame = new JFrame( "Drag Layout" );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add( new JScrollPane(panel) );
		frame.pack();
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}

	public static void createLabel(ComponentMover cm, JPanel panel, String text, int x, int y)
	{
		JLabel label = new JLabel( text );
		label.setOpaque(true);
		label.setBackground( Color.ORANGE );
		label.setLocation(x, y);
		panel.add( label );
		cm.registerComponent( label );

//		ComponentResizer cr = new ComponentResizer( label );
//		cr.setSnapSize( new Dimension(10, 10) );
//		cr.setAutoLayout(true);
	}
}
