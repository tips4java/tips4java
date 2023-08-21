/*
 * @(#)ComponentTreeModel.java	1.0 11/15/08
 */
//package darrylbu.model;

//import darrylbu.util.SwingUtils;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * A tree data model that reproduces the structure of a GUI to reveal the 
 * hierarchical placement of components in containers.  Also provides a
 * static method to facilitate adding such a hierarchy at a specified node
 * in an existing <code>DefaultTreeModel</code>.
 * <P>
 * Extends {@link DefaultTreeModel}.
 * 
 * @version 1.0 11/15/08
 * @author Darryl
 */
public class ComponentTreeModel extends DefaultTreeModel
      implements Serializable {

   /**
    * Creates a new tree model with a specified <code>JComponent</code>
    * as the root, including nested components at all levels.
    * 
    * @param container the <code>JComponent</code> to be placed at the root
    * of the tree
    * @see DefaultTreeModel
    */
   public ComponentTreeModel(JComponent container) {
      this(container, true);
   }

   /**
    * Creates a new tree model with a specified <code>JComponent</code>
    * as the root, optionally including nested components.
    * 
    * @param container the <code>JComponent</code> to be placed at the root
    * of the tree
    * @param nested <code>true</code> to include nested components,
    * <code>false</code> otherwise
    */
   public ComponentTreeModel(JComponent container, boolean nested) {
      this(container, new DefaultMutableTreeNode(container), nested);
   }

   /**
    * private constructor for aligning the public constructors with the
    * static method getComponentTreeModel
    */
   private ComponentTreeModel(JComponent container,
         DefaultMutableTreeNode root, boolean nested) {
      super(root);
      addNodes(container, this, root, nested);
   }

   /**
    * Invoked to create a new model or to add a <code>JComponent</code>'s
    * hierarchy to an existing <code>DefaultTreeModel</code>.
    * 
    * @param container the <code>JComponent</code> to be placed at the root
    * of the tree or added at a node of an existing tree
    * @param model a <code>DefaultTreeModel</code> to which the
    * <code>container</code>'s GUI hierarchy will be added, or
    * <code>null</code> to create a new model
    * @param root the node on the model to which the containerand its
    * hierarchy will be added, or null for a model that has the container as
    * its root
    * @param nested <code>true</code> to include nested components,
    * <code>false</code> otherwise
    * @return the <code>model</code>, or a new <code>DefaultTreeModel</code>
    * if the <code>model</code> parameter is null
    */
   public static DefaultTreeModel getComponentTreeModel(JComponent container,
         DefaultTreeModel model, DefaultMutableTreeNode root, boolean nested) {
      DefaultMutableTreeNode node = new DefaultMutableTreeNode(container);
      if (model == null) {
         model = new DefaultTreeModel(node);
      }
      if (root == null) {
         root = node;
      }
      if (model.getRoot() == null) {
         model.setRoot(root);
      } else {
         root.add(node);
      }
      addNodes(container, model, node, nested);
      return model;
   }

   /**
    * private method to obtain the hierarchy devolving from the root
    * container and add nodes to the tree accordingly.
    */ 
   private static void addNodes(JComponent container, DefaultTreeModel model,
         DefaultMutableTreeNode root, boolean nested) {
      Map<JComponent, List<JComponent>> componentMap = SwingUtils.getComponentMap(
            container, true);
      List<JComponent> components = componentMap.get(container);
      if (components != null) {
         for (JComponent component : componentMap.get(container)) {
            DefaultMutableTreeNode branch = new DefaultMutableTreeNode(component);
            branch.setUserObject(component);
            root.add(branch);
            if (nested) {
               addNodes(component, model, branch, true);
            }
         }
      }
   }
}
