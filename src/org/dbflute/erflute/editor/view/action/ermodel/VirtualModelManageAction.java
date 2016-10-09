package org.dbflute.erflute.editor.view.action.ermodel;

import org.dbflute.erflute.editor.RealModelEditor;
import org.dbflute.erflute.editor.controller.command.common.ChangeSettingsCommand;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.settings.Settings;
import org.dbflute.erflute.editor.view.action.AbstractBaseAction;
import org.dbflute.erflute.editor.view.dialog.category.CategoryManageDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class VirtualModelManageAction extends AbstractBaseAction {

    public static final String ID = VirtualModelManageAction.class.getName();

    public VirtualModelManageAction(RealModelEditor editor) {
        super(ID, "Manage Virtual Models", editor);
    }

    @Override
    public void execute(Event event) throws Exception {
        final ERDiagram diagram = this.getDiagram();
        final Settings settings = (Settings) diagram.getDiagramContents().getSettings().clone();
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final CategoryManageDialog dialog = new CategoryManageDialog(shell, settings, diagram);
        if (dialog.open() == IDialogConstants.OK_ID) {
            final ChangeSettingsCommand command = new ChangeSettingsCommand(diagram, settings);
            this.execute(command);
        }
    }
}
