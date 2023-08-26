import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class TextPromptDemo
{
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public static void createAndShowGUI()
	{
		JPanel west = new JPanel( new GridLayout(0, 1, 0, 5) );
		west.setBorder( new EmptyBorder(10, 10, 10, 5) );
		JPanel east = new JPanel( new GridLayout(0, 1, 0, 5) );
		east.setBorder( new EmptyBorder(10, 5, 10, 10) );

		east.add( new JLabel("Show Always (default)") );
		JTextField tf1 = new JTextField(10);
		west.add(tf1);
		TextPrompt tp1 = new TextPrompt("First Name", tf1);

		east.add( new JLabel("Show on Focus Gained") );
		JTextField tf2 = new JTextField(10);
		west.add(tf2);
		TextPrompt tp2 = new TextPrompt("First Name", tf2, TextPrompt.Show.FOCUS_GAINED);

		east.add( new JLabel("Show on Focus Lost") );
		JTextField tf3 = new JTextField(10);
		west.add(tf3);
		TextPrompt tp3 = new TextPrompt("First Name", tf3, TextPrompt.Show.FOCUS_LOST);

		east.add( new JLabel("Style is Italic") );
		JTextField tf4 = new JTextField(10);
		west.add(tf4);
		TextPrompt tp4 = new TextPrompt("First Name", tf4);
		tp4.changeStyle(Font.ITALIC);

		east.add( new JLabel("Alpha is 128, centered") );
		JTextField tf5 = new JTextField(10);
		west.add(tf5);
		TextPrompt tp5 = new TextPrompt("First Name", tf5);
		tp5.changeAlpha(128);
		tp5.setHorizontalAlignment(JLabel.CENTER);

		east.add( new JLabel("Bold, Italic and 128") );
		JTextField tf6 = new JTextField(10);
		west.add(tf6);
		TextPrompt tp6 = new TextPrompt("First Name", tf6);
		tp6.setForeground( Color.RED );
		tp6.changeAlpha(128);
		tp6.changeStyle(Font.BOLD + Font.ITALIC);

		east.add( new JLabel("Same as above with Icon") );
		JTextField tf7 = new JTextField(10);
		west.add(tf7);
		TextPrompt tp7 = new TextPrompt("First Name", tf7);
		tp7.setForeground( Color.RED );
		tp7.changeAlpha(0.5f);
		tp7.changeStyle(Font.BOLD + Font.ITALIC);
//		tp7.setIcon( new ImageIcon("about16.gif") );
		String path = "about16.gif";
		java.net.URL imgURL = TextPromptDemo.class.getResource(path);
        ImageIcon icon = new ImageIcon(imgURL, path);
		tp7.setIcon( icon );

//		tp7.setShowPromptOnce( true );

		JFrame frame = new JFrame("Text Prompt");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.add(west, BorderLayout.WEST);
		frame.add(east, BorderLayout.EAST);
		frame.pack();
		frame.setLocationRelativeTo( null );
		frame.setVisible(true);
	}
}
