import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

public class TextLineNumberTest
{
	public static void main(String[] args)
	{
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
		panel.setLayout(new BorderLayout());

		String text =
			"Typically the same font would be used for the entire text component.\n\n" +
			"However, this component now supports multiple fonts and font sizes.\n\n" +
			"Test it out using the buttons below.\n";

//		String text =
//			"<html>line1<br>line2<br>line3<br></html>";

		final JTextPane textPane = new JTextPane();
//		textPane.setContentType("text/html");
//		final JTextArea textPane = new JTextArea();
//		textPane.setLineWrap(true);
//		textPane.setFont( new Font("monospaced", Font.PLAIN, 36) );
		textPane.setText(text);

		JScrollPane scrollPane = new JScrollPane(textPane);
		panel.add(scrollPane);
		scrollPane.setPreferredSize(new Dimension(300, 250));


		TextLineNumber lineNumber = new TextLineNumber(textPane, 3);
//		lineNumber.setUpdateFont(true);
		lineNumber.setUpdateFont(false);
		float fontSize = textPane.getFont().getSize() - 6;
		Font font = textPane.getFont().deriveFont( fontSize );
//		lineNumber.setFont(font);
//		lineNumber.setBorderGap(20);
//		lineNumber.setForeground(Color.ORANGE);
//		lineNumber.setDigitAlignment(TextLineNumber.LEFT);

//		float fontSize = component.getFont().getSize() - 0.0f;
//		componentFont = component.getFont().deriveFont( fontSize );
		scrollPane.setRowHeaderView( lineNumber );

		JPanel buttons = new JPanel( new FlowLayout(FlowLayout.CENTER, 5, 5) );

		buttons.add( new JButton(new StyledEditorKit.FontSizeAction("Font 12", 12)));
		buttons.add( new JButton(new StyledEditorKit.FontSizeAction("Font 16", 16)));
		buttons.add( new JButton(new StyledEditorKit.FontSizeAction("Font 20", 20)));
		buttons.add( new JButton(new StyledEditorKit.FontSizeAction("Font 24", 24)));

		panel.add(buttons, BorderLayout.SOUTH);

		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Text Component Line Number");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setContentPane( panel );
		frame.setSize(500, 240);
		frame.setLocationRelativeTo( null );
		frame.setVisible(true);
	}
}
