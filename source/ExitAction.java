import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import javax.swing.Action;
import javax.swing.AbstractAction;

/**
 *  This class will create and dispatch a WINDOW_CLOSING event to the active
 *  window.  As a result any WindowListener that handles the windowClosing
 *  event will be executed. Since clicking on the "Close" button of the
 *  frame/dialog or selecting the "Close" option from the system menu also
 *  invoke the WindowListener, this will provide a commen exit point for
 *  the application.
 */
public class ExitAction extends AbstractAction
{
	public ExitAction()
	{
		super("Exit");
		putValue( Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_X) );
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		//  Find the active window before creating and dispatching the event

		Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();

		if (window != null)
		{
			WindowEvent windowClosing = new WindowEvent(window, WindowEvent.WINDOW_CLOSING);
			window.dispatchEvent(windowClosing);
		}
	}
}
