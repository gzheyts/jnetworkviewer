package ru.gzheyts.jnetworkviewer.gui.menu;

import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.dockable.action.DefaultDockableStateAction;
import com.javadocking.event.DockingEvent;
import com.javadocking.event.DockingListener;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

/**
 * @author gzheyts
 */
public class MenuBar extends JMenuBar {

    private static Logger logger = Logger.getLogger(MenuBar.class);

    public MenuBar(final JFrame frame, Dockable dockable) {


        JMenu file = add(new JMenu("File"));
        JMenuItem exitMI = new JMenuItem("exit");
        exitMI.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (frame != null) {
                    logger.info("Closing application");
                    frame.dispose();
                }
            }
        });

        exitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        file.add(exitMI);

        JMenu view = add(new JMenu("View"));

        JCheckBoxMenuItem viewItem = new DockableMenuItem(dockable);
        view.add(viewItem);
        viewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));



    }


    /**
     * A check box menu item to add or remove the dockable.
     */
    private class DockableMenuItem extends JCheckBoxMenuItem
    {
        public DockableMenuItem(Dockable dockable)
        {
            super(dockable.getTitle(), dockable.getIcon());

            setSelected(dockable.getDock() != null);

            DockableMediator dockableMediator = new DockableMediator(dockable, this);
            dockable.addDockingListener(dockableMediator);
            addItemListener(dockableMediator);
        }
    }

    /**
     * A listener that listens when menu items with dockables are selected and deselected.
     * It also listens when dockables are closed or docked.
     */
    private class DockableMediator implements ItemListener, DockingListener
    {

        private Dockable dockable;
        private Action closeAction;
        private Action restoreAction;
        private JMenuItem dockableMenuItem;

        public DockableMediator(Dockable dockable, JMenuItem dockableMenuItem)
        {

            this.dockable = dockable;
            this.dockableMenuItem = dockableMenuItem;
            closeAction = new DefaultDockableStateAction(dockable, DockableState.CLOSED);
            restoreAction = new DefaultDockableStateAction(dockable, DockableState.NORMAL);

        }

        public void itemStateChanged(ItemEvent itemEvent)
        {

            dockable.removeDockingListener(this);
            if (itemEvent.getStateChange() == ItemEvent.DESELECTED)
            {
                // Close the dockable.
                closeAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Close"));
            }
            else
            {
                // Restore the dockable.
                restoreAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Restore"));
            }
            dockable.addDockingListener(this);

        }

        public void dockingChanged(DockingEvent dockingEvent) {
            if (dockingEvent.getDestinationDock() != null)
            {
                dockableMenuItem.removeItemListener(this);
                dockableMenuItem.setSelected(true);
                dockableMenuItem.addItemListener(this);
            }
            else
            {
                dockableMenuItem.removeItemListener(this);
                dockableMenuItem.setSelected(false);
                dockableMenuItem.addItemListener(this);
            }
        }

        public void dockingWillChange(DockingEvent dockingEvent) {}

    }


}
