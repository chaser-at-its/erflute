package org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.all;

import java.util.HashSet;
import java.util.Set;

import org.dbflute.erflute.core.DisplayMessages;
import org.dbflute.erflute.db.impl.mysql.MySQLDBManager;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.ValidateResult;
import org.dbflute.erflute.editor.model.dbexport.ddl.validator.rule.BaseRule;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.eclipse.core.resources.IMarker;

public class DuplicatedPhysicalNameRule extends BaseRule {

    public boolean validate(ERDiagram diagram) {
        Set<String> nameSet = new HashSet<String>();

        for (ERTable table : diagram.getDiagramContents().getDiagramWalkers().getTableSet()) {

            String name = table.getNameWithSchema(diagram.getDatabase()).toLowerCase();

            if (nameSet.contains(name)) {
                ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(DisplayMessages.getMessage("error.validate.duplicated.name") + " [TABLE] " + name);
                validateResult.setLocation(table.getLogicalName());
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(table);

                this.addError(validateResult);

            }
            nameSet.add(name);

            if (!MySQLDBManager.ID.equals(diagram.getDatabase())) {
                for (ERIndex index : table.getIndexes()) {
                    String indexName = index.getName().toLowerCase();

                    if (nameSet.contains(indexName)) {
                        ValidateResult validateResult = new ValidateResult();
                        validateResult.setMessage(DisplayMessages.getMessage("error.validate.duplicated.name") + " [INDEX] "
                                + indexName + " on table [" + table.getLogicalName() + "]");
                        validateResult.setLocation(indexName);
                        validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                        validateResult.setObject(index.getTable());

                        this.addError(validateResult);
                    }
                    nameSet.add(indexName);
                }
            }
        }

        for (Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
            String name = sequence.getNameWithSchema(diagram.getDatabase()).toLowerCase();

            if (nameSet.contains(name)) {
                ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(DisplayMessages.getMessage("error.validate.duplicated.name") + " [SEQUENCE] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(sequence);

                this.addError(validateResult);
            }
            nameSet.add(name);
        }

        for (ERView view : diagram.getDiagramContents().getDiagramWalkers().getViewSet()) {
            String name = view.getNameWithSchema(diagram.getDatabase()).toLowerCase();

            if (nameSet.contains(name)) {
                ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(DisplayMessages.getMessage("error.validate.duplicated.name") + " [VIEW] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(view);

                this.addError(validateResult);
            }
            nameSet.add(name);
        }

        for (Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
            String name = trigger.getNameWithSchema(diagram.getDatabase()).toLowerCase();

            if (nameSet.contains(name)) {
                ValidateResult validateResult = new ValidateResult();
                validateResult.setMessage(DisplayMessages.getMessage("error.validate.duplicated.name") + " [TRIGGER] " + name);
                validateResult.setLocation(name);
                validateResult.setSeverity(IMarker.SEVERITY_WARNING);
                validateResult.setObject(trigger);

                this.addError(validateResult);
            }
            nameSet.add(name);
        }

        return true;
    }
}
