package org.dbflute.erflute.wizard.page;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.db.DBManagerFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class NewDiagramWizardPage2 extends WizardPage {

    private Combo databaseCombo;

    public NewDiagramWizardPage2(IStructuredSelection selection) {
        super(DisplayMessages.getMessage("wizard.new.diagram.title"));
        this.setTitle(DisplayMessages.getMessage("wizard.new.diagram.title"));
    }

    @Override
    public void createControl(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NULL);

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        final Label label = new Label(composite, SWT.NULL);
        label.setText(DisplayMessages.getMessage("label.database"));

        this.databaseCombo = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
        final GridData dbData = new GridData(GridData.FILL_HORIZONTAL);
        dbData.widthHint = 200;
        this.databaseCombo.setLayoutData(dbData);
        this.databaseCombo.setVisibleItemCount(10);
        for (final String db : DBManagerFactory.getAllDBList()) {
            this.databaseCombo.add(db);
        }
        this.databaseCombo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validatePage();
            }
        });
        this.databaseCombo.setFocus();
        this.validatePage();
        this.setControl(composite);
    }

    protected boolean validatePage() {
        boolean valid = true;
        if (this.databaseCombo.getText().length() == 0) {
            setMessage(DisplayMessages.getMessage("select.database.message"));
            valid = false;
            this.setPageComplete(false);
        }
        if (valid) {
            this.setPageComplete(true);
            setMessage(DisplayMessages.getMessage("wizard.new.diagram.message"));
        }
        return valid;
    }

    public String getDatabase() {
        return this.databaseCombo.getText();
    }
}
