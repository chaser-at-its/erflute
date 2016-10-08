package org.dbflute.erflute.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.List;

import org.dbflute.erflute.editor.controller.editpart.element.ERDiagramEditPart;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERModelSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class CreateRelationCommand extends AbstractCreateRelationCommand {

    private Relationship relation;

    private List<NormalColumn> foreignKeyColumnList;

    public CreateRelationCommand(Relationship relation) {
        this(relation, null);
    }

    public CreateRelationCommand(Relationship relation, List<NormalColumn> foreignKeyColumnList) {
        super();
        this.relation = relation;
        this.foreignKeyColumnList = foreignKeyColumnList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExecute() {
        ERDiagramEditPart.setUpdateable(false);

        this.relation.setSource((TableView) source.getModel());

        ERDiagramEditPart.setUpdateable(true);

        this.relation.setTargetTableView((TableView) target.getModel(), this.foreignKeyColumnList);

        if (this.relation.getSource() instanceof ERTable || this.relation.getTarget() instanceof ERTable) {
            // �r���[���Ń����[�V�������������ꍇ�A�����ɂ�ERVirtualTable�łȂ�ERTable�ŗ���
            ERModelSet modelSet = this.relation.getSource().getDiagram().getDiagramContents().getModelSet();
            modelSet.createRelation(relation);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doUndo() {
        ERDiagramEditPart.setUpdateable(false);

        this.relation.setSource(null);

        ERDiagramEditPart.setUpdateable(true);

        this.relation.setTargetTableView(null);

        TableView targetTable = (TableView) this.target.getModel();
        targetTable.setDirty();
    }
}