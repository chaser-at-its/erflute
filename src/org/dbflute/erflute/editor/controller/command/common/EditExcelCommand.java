package org.dbflute.erflute.editor.controller.command.common;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dbflute.erflute.Activator;
import org.dbflute.erflute.core.util.POIUtils;
import org.dbflute.erflute.editor.controller.command.AbstractCommand;
import org.dbflute.erflute.editor.model.ERModelUtil;
import org.dbflute.erflute.editor.model.ViewableModel;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class EditExcelCommand extends AbstractCommand {

    private ViewableModel model;

    public EditExcelCommand(ViewableModel model) {
        this.model = model;
    }

    @Override
    protected void doExecute() {
        if (model instanceof ERVirtualTable) {
            ERVirtualTable vtable = (ERVirtualTable) model;
            String tableName = vtable.getRawTable().getPhysicalName();
            DiagramSettings settings = vtable.getDiagram().getDiagramContents().getSettings();
            String path = settings.getMasterDataBasePath();
            if (path != null) {
                IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(path));

                try {
                    IResource[] members = folder.members();
                    boolean hit = false;
                    for (IResource excelFile : members) {
                        String ext = excelFile.getFileExtension();
                        if (ext == null) {
                            continue;
                        }
                        if (!ext.equals("xls") && !ext.equals("xlsx")) {
                            continue;
                        }
                        //						Activator.log(new Exception(excelFile.getLocation().toFile().toString()));
                        try {
                            HSSFWorkbook book = POIUtils.readExcelBook(excelFile.getLocation().toFile());
                            for (int i = 0; i < book.getNumberOfSheets(); i++) {
                                String name = book.getSheetName(i);
                                if (name.equalsIgnoreCase(tableName)) {
                                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                                    IDE.openEditor(page, (IFile) excelFile);
                                    hit = true;
                                    break;
                                }
                            }
                        } catch (Throwable e) {
                            // ���܂�Excel���J���Ȃ����Ƃ����邪����
                        }
                    }
                    if (!hit) {
                        if (Activator.showConfirmDialog(
                                tableName + " �e�[�u���̃f�[�^���L�ڂ���Excel��������܂���B�f�B���N�g�����J���܂����H", SWT.OK, SWT.CANCEL)) {
                            ERModelUtil.openDirectory(members[0]);
                        }
                    }

                } catch (CoreException e) {
                    Activator.error(e);
                }

            }

        }
    }

    @Override
    protected void doUndo() {
        // do nothing
    }

}
