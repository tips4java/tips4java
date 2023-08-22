import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class AlphaContainerDemo
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
		JComponent west = createPanel("Painting Artifacts");
		JComponent east = new AlphaContainer( createPanel("Using AlphaContainer") );

		JFrame frame = new JFrame("Backgrounds With Transparency");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(west, BorderLayout.WEST);
		frame.add(east, BorderLayout.EAST);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static JPanel createPanel(String text)
	{
		JPanel panel = new JPanel( new GridLayout(0, 1));
		panel.setBorder( new TitledBorder(text) );
		panel.setBackground( new Color(255, 0, 0, 20) );

		for (int i = 0; i < 5; i++)
		{
			JCheckBox checkBox = new JCheckBox("Check Box " + i);
			checkBox.setOpaque(false);
			panel.add(checkBox);
		}

		panel.setPreferredSize( new Dimension(200, 200) );
		return panel;
	}
}
