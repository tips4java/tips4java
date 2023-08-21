//package darrylbu.sample;

//import darrylbu.model.ComponentTreeModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

public class ComponentTree {

   private JFrame frame;
   Action showTree = new AbstractAction("Show tree") {

      public void actionPerformed(ActionEvent e) {
         JComponent component = (JComponent) e.getSource();
         JComponent root = SwingUtilities.getRootPane(component);
         if (root == null) {
            JComponent parent = component;
            while (parent instanceof JComponent && parent != null) {
               root = parent;
               parent = (JComponent) parent.getParent();
            }
         }
         JTree tree = new JTree(new ComponentTreeModel(root));
         for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
         }
         JScrollPane scrollPane = new JScrollPane(tree);
//         scrollPane.setPreferredSize(new Dimension(570, 150));
         scrollPane.setPreferredSize(new Dimension(950, 500));
         JOptionPane.showMessageDialog(root, scrollPane, "Component Tree",
               JOptionPane.PLAIN_MESSAGE);
      }
   };

   public static void main(String[] args) {
      JFrame.setDefaultLookAndFeelDecorated(true);
      JDialog.setDefaultLookAndFeelDecorated(true);
      SwingUtilities.invokeLater(new Runnable() {

         @Override
         public void run() {
            new ComponentTree().makeUI();
         }
      });
   }

   public void makeUI() {
      JMenuItem showTreeMenuItem = new JMenuItem(showTree);
      JMenuItem menuItem = new JMenuItem("Menu Item");
      JMenu menu = new JMenu("Menu");
      menu.add(showTreeMenuItem);
      menu.add(menuItem);
      JMenuBar menuBar = new JMenuBar();
      menuBar.add(menu);

      Object[] data = {"One", "Two", "Three", "Four", "Five"};
      JComboBox comboBox = new JComboBox(data);

      JTable table = new JTable(30, 5);
      JScrollPane scrollPane = new JScrollPane(table);

      JLabel label = new JLabel("Label");
      JCheckBox checkBox = new JCheckBox("Check Box");
      JRadioButton radioButton = new JRadioButton("Radio Button");
      JButton button = new JButton(showTree);

      JPanel panel = new JPanel();
      BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.X_AXIS);
      panel.setLayout(boxLayout);
      panel.add(label);
      panel.add(checkBox);
      panel.add(radioButton);
      panel.add(Box.createHorizontalGlue());
      panel.add(button);

      frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(600, 200);
      frame.setJMenuBar(menuBar);
      frame.add(comboBox, BorderLayout.NORTH);
      frame.add(scrollPane, BorderLayout.CENTER);
      frame.add(panel, BorderLayout.SOUTH);

      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }
}
