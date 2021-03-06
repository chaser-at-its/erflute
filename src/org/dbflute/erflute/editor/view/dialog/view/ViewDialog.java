package org.dbflute.erflute.editor.view.dialog.view;

import java.util.ArrayList;
import java.util.List;

import org.dbflute.erflute.core.dialog.AbstractDialog;
import org.dbflute.erflute.core.exception.InputException;
import org.dbflute.erflute.core.widgets.ValidatableTabWrapper;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.view.dialog.view.tab.AdvancedTabWrapper;
import org.dbflute.erflute.editor.view.dialog.view.tab.ViewAttributeTabWrapper;
import org.dbflute.erflute.editor.view.dialog.view.tab.DescriptionTabWrapper;
import org.dbflute.erflute.editor.view.dialog.view.tab.SqlTabWrapper;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

public class ViewDialog extends AbstractDialog {

    private ERView copyData;

    private TabFolder tabFolder;

    private EditPartViewer viewer;

    private List<ValidatableTabWrapper> tabWrapperList;

    public ViewDialog(Shell parentShell, EditPartViewer viewer, ERView copyData, ColumnGroupSet columnGroups) {
        super(parentShell);

        this.viewer = viewer;
        this.copyData = copyData;

        this.tabWrapperList = new ArrayList<ValidatableTabWrapper>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initComponent(Composite composite) {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalAlignment = GridData.FILL;

        this.tabFolder = new TabFolder(composite, SWT.NONE);
        this.tabFolder.setLayoutData(gridData);

        final ViewAttributeTabWrapper attributeTabWrapper = new ViewAttributeTabWrapper(this, tabFolder, SWT.NONE, this.copyData);
        this.tabWrapperList.add(attributeTabWrapper);

        this.tabWrapperList.add(new SqlTabWrapper(this, tabFolder, SWT.NONE, this.copyData));
        this.tabWrapperList.add(new DescriptionTabWrapper(this, tabFolder, SWT.NONE, this.copyData));
        this.tabWrapperList.add(new AdvancedTabWrapper(this, tabFolder, SWT.NONE, this.copyData));

        this.tabFolder.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                int index = tabFolder.getSelectionIndex();

                ValidatableTabWrapper selectedTabWrapper = tabWrapperList.get(index);
                selectedTabWrapper.setInitFocus();
            }

        });

        attributeTabWrapper.setInitFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doValidate() {
        try {
            for (ValidatableTabWrapper tabWrapper : this.tabWrapperList) {
                tabWrapper.validatePage();
            }

        } catch (InputException e) {
            return e.getMessage();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTitle() {
        return "dialog.title.view";
    }

    @Override
    protected void performOK() throws InputException {
    }

    @Override
    protected void setupData() {
    }

    public EditPartViewer getViewer() {
        return viewer;
    }

    public ERDiagram getDiagram() {
        return (ERDiagram) this.viewer.getContents().getModel();
    }
}
