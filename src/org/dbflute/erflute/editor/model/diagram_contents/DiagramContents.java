package org.dbflute.erflute.editor.model.diagram_contents;

import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalkerSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.ermodel.ERVirtualDiagramSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.IndexSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroupSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.dbflute.erflute.editor.model.settings.Settings;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class DiagramContents {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Settings settings;
    private Dictionary dictionary;
    private TablespaceSet tablespaceSet;
    private DiagramWalkerSet walkerSet;
    private IndexSet indexSet;
    private ERVirtualDiagramSet vdiagramSet;
    private ColumnGroupSet columnGroupSet;
    private SequenceSet sequenceSet;
    private TriggerSet triggerSet;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DiagramContents() {
        this.settings = new Settings();
        this.dictionary = new Dictionary();
        this.tablespaceSet = new TablespaceSet();
        this.walkerSet = new DiagramWalkerSet();
        this.indexSet = new IndexSet();
        this.vdiagramSet = new ERVirtualDiagramSet();
        this.columnGroupSet = new ColumnGroupSet();
        this.sequenceSet = new SequenceSet();
        this.triggerSet = new TriggerSet();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{" + settings + ", " + walkerSet + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Dictionary getDictionary() {
        return this.dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public TablespaceSet getTablespaceSet() {
        return tablespaceSet;
    }

    public void setTablespaceSet(TablespaceSet tablespaceSet) {
        this.tablespaceSet = tablespaceSet;
    }

    public DiagramWalkerSet getDiagramWalkers() {
        return this.walkerSet;
    }

    public void setDiagramWalkers(DiagramWalkerSet walkers) {
        this.walkerSet = walkers;
    }

    public IndexSet getIndexSet() {
        return indexSet;
    }

    public void setIndexSet(IndexSet indexSet) {
        this.indexSet = indexSet;
    }

    public ERVirtualDiagramSet getVirtualDiagramSet() {
        return vdiagramSet;
    }

    public void setVirtualDiagramSet(ERVirtualDiagramSet modelSet) {
        this.vdiagramSet = modelSet;
    }

    public ColumnGroupSet getColumnGroupSet() {
        return this.columnGroupSet;
    }

    public void setColumnGroupSet(ColumnGroupSet columnGroupSet) {
        this.columnGroupSet = columnGroupSet;
    }

    public SequenceSet getSequenceSet() {
        return sequenceSet;
    }

    public void setSequenceSet(SequenceSet sequenceSet) {
        this.sequenceSet = sequenceSet;
    }

    public TriggerSet getTriggerSet() {
        return triggerSet;
    }

    public void setTriggerSet(TriggerSet triggerSet) {
        this.triggerSet = triggerSet;
    }
}
