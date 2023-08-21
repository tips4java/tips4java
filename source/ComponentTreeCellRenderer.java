/*
 * @(#)ComponentTreeCellRenderer.java	1.0 11/16/08
 */
package darrylbu.renderer;

import darrylbu.model.ComponentTreeModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * A tree cell renderer that displays scaled replicas of Swing components
 * at the nodes of the tree.
 * <P>
 * Extends {@link DefaultTreeCellRenderer}.
 * 
 * @author Darryl
 */
public class ComponentTreeCellRenderer extends DefaultTreeCellRenderer {

   private JTree tree;
   private double scale;
   private Map<JComponent, DefaultTreeCellRenderer> renderers;
   private final Color selColor;
   private final Color nonSelColor;

   /**
    * Returns a <code>ComponentTreeCellRenderer</code> suitable for rendering scaled
    * snapshots of Swing components.  This class is intended for use with
    * a <code>JTree</code> that has a <code>ComponentTreeModel</code>.
    * 
    * @param tree the tree of components
    * @param scale the scale at which to render the components
    * @see ComponentTreeModel
    * @see DefaultTreeCellRenderer
    */
   public ComponentTreeCellRenderer(JTree tree, double scale) {
      this.tree = tree;
      this.scale = scale;
      setHorizontalTextPosition(LEFT);
      setOpaque(true);
      setBorder(new EmptyBorder(2, 0, 2, 0));
      setFont(tree.getFont());

      renderers = new HashMap<JComponent, DefaultTreeCellRenderer>();
      selColor = backgroundSelectionColor == null ?
         Color.LIGHT_GRAY :
         new Color(backgroundSelectionColor.getRGB());
      nonSelColor = backgroundNonSelectionColor == null ?
         tree.getBackground() :
         backgroundNonSelectionColor;
   }

   /**
    * Configures the renderer based on the passed in component.
    * The text and icon are set from the <code>JComponent</code> located
    * as the userObject of the node and the scale, or the String returned
    * by invoking <code>String.valueOf</code> on <code>value</code> if
    * the object at the node is not a <code>JComponent</code>.
    * <P>
    * The background color is set based on the selection status and the
    * icon is a scaled replica of the <code>JComponent</code>.
    * <P>
    * For the sake of performance, a renderer for each component is cached.
    * The cache can be synchronized with the current state of the component
    * hierarchy by invoking <code>refresh()</code>
    * 
    * @see #refresh()
    * @see DefaultTreeCellRenderer#getTreeCellRendererComponent(JTree,
    * Object, boolean, boolean, boolean, int, boolean) 
    */
   @Override
   public Component getTreeCellRendererComponent(JTree tree,
         Object value, boolean selected, boolean expanded,
         boolean leaf, int row, boolean hasFocus) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      Object userObject = node.getUserObject();
      if (userObject instanceof JComponent) {
         DefaultTreeCellRenderer renderer = null;
         JComponent component = (JComponent) node.getUserObject();
         if (!renderers.containsKey(component)) {
            renderer = rendererForComponent(component, tree);
            renderers.put(component, renderer);
         } else {
            renderer = renderers.get(component);
         }
         setIcon(renderer.getIcon());
         setText(renderer.getText());
      } else {
         setIcon(null);
         setText(String.valueOf(userObject));
      }
      setBackground(selected ? selColor : nonSelColor);
      return this;
   }

   /**
    * Creates a renderer with a scaled image of the component
    * 
    * @param component the component
    * @param tree the tree
    * @return a renderer for the component
    */
   private DefaultTreeCellRenderer rendererForComponent(JComponent component,
         JTree tree) {
      Rectangle visibleRect = component.getVisibleRect();
      String className = component.getClass().getName();
      DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
      renderer.setHorizontalTextPosition(LEFT);

      if (component == tree) {
         renderer.setText(className + " (this tree)");
         return renderer;
      }

      if (!component.isShowing()) {
         renderer.setText(className + " (not visible)");
         return renderer;
      }

      if (visibleRect.width * scale < 1.0 || visibleRect.height * scale < 1.0) {
         renderer.setText(className + " (too small to show)");
         return renderer;
      }

      BufferedImage bi = new BufferedImage(
            (int) (visibleRect.width * scale) + 2,
            (int) (visibleRect.height * scale) + 2,
            BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = bi.createGraphics();
      g2.setClip(visibleRect);
      g2.scale(scale, scale);
      Method method;
      try {
         method = JComponent.class.getDeclaredMethod("paintComponent",
               Graphics.class);
         method.setAccessible(true);
         method.invoke(component, g2);

         method = JComponent.class.getDeclaredMethod("paintBorder",
               Graphics.class);
         method.setAccessible(true);
         method.invoke(component, g2);

         // attempting to paint this tree on any of its ancestors causes the
         // tree to repaint and leads to infinite recursion
         if (SwingUtilities.isDescendingFrom(tree, component)) {
            g2.setColor(textNonSelectionColor);
            g2.scale(1.0 / scale, 1.0 / scale);
            g2.drawString("(Children not shown)", 2, 15);
         } else {
            method = JComponent.class.getDeclaredMethod("paintChildren",
                  Graphics.class);
            method.setAccessible(true);
            method.invoke(component, g2);
         }
      // ignore checked exceptions
      } catch (IllegalAccessException ignore) {
      } catch (IllegalArgumentException ignore) {
      } catch (InvocationTargetException ignore) {
      } catch (NoSuchMethodException ignore) {
      } catch (SecurityException ignore) {
      }
      g2.dispose();

      renderer.setText(className);
      renderer.setIcon(new ImageIcon(bi));
      return renderer;
   }

   /**
    * Reloads the renderer cache using the current state of the components.
    */
   public void refresh() {
      renderers.clear();
      tree.updateUI();
   }

   /**
    * Sets the scale at which components will be rendered.  The recommended
    * range of vales is 0.1 to 1.0.  Setting a scale much in excess of 1.0
    * may result in slow rendering of the tree.
    * <P>
    * Setting the scale will also refresh the tree using the current state
    * of the components
    * 
    * @param scale the scale at which to render the components
    * @see #refresh
    */
   public void setScale(double scale) {
      if (this.scale != scale) {
         this.scale = scale;
         refresh();
      }
   }
}
