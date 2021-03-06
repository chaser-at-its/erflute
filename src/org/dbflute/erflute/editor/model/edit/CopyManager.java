package org.dbflute.erflute.editor.model.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbflute.erflute.editor.model.diagram_contents.DiagramContents;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.WalkerConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalker;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.DiagramWalkerSet;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.category.Category;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.ERColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.unique_key.CompoundUniqueKey;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.view.ERView;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.dictionary.Word;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.dbflute.erflute.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.dbflute.erflute.editor.model.settings.DiagramSettings;

public class CopyManager {

    private static DiagramWalkerSet copyList = new DiagramWalkerSet();

    private static int numberOfCopy;

    private Map<DiagramWalker, DiagramWalker> walkerMap;

    public static void copy(DiagramWalkerSet nodeElementList) {
        final CopyManager copyManager = new CopyManager();
        copyList = copyManager.copyNodeElementList(nodeElementList);
    }

    public static DiagramWalkerSet paste() {
        numberOfCopy++;
        final CopyManager copyManager = new CopyManager();
        return copyManager.copyNodeElementList(copyList);
    }

    public static void clear() {
        copyList.clear();
        numberOfCopy = 0;
    }

    public static boolean canCopy() {
        if (copyList != null && !copyList.isEmpty()) {
            return true;
        }

        return false;
    }

    public static int getNumberOfCopy() {
        return numberOfCopy;
    }

    public Map<DiagramWalker, DiagramWalker> getNodeElementMap() {
        return walkerMap;
    }

    public DiagramWalkerSet copyNodeElementList(DiagramWalkerSet nodeElementList) {
        final DiagramWalkerSet copyList = new DiagramWalkerSet();
        this.walkerMap = new HashMap<DiagramWalker, DiagramWalker>();
        final Map<ERColumn, ERColumn> columnMap = new HashMap<ERColumn, ERColumn>();
        final Map<CompoundUniqueKey, CompoundUniqueKey> complexUniqueKeyMap = new HashMap<CompoundUniqueKey, CompoundUniqueKey>();
        for (final DiagramWalker walker : nodeElementList) {
            if (walker instanceof ModelProperties) {
                continue;
            }
            final DiagramWalker cloneNodeElement = walker.clone();
            copyList.addDiagramWalker(cloneNodeElement);
            walkerMap.put(walker, cloneNodeElement);
            if (walker instanceof ERTable) {
                copyColumnAndIndex((ERTable) walker, (ERTable) cloneNodeElement, columnMap, complexUniqueKeyMap);
            } else if (walker instanceof ERView) {
                copyColumn((ERView) walker, (ERView) cloneNodeElement, columnMap);
            }
        }
        final Map<WalkerConnection, WalkerConnection> connectionElementMap = new HashMap<WalkerConnection, WalkerConnection>();
        for (final DiagramWalker walker : walkerMap.keySet()) {
            final DiagramWalker cloneWalker = walkerMap.get(walker);
            replaceIncoming(walker, cloneWalker, connectionElementMap, walkerMap);
        }

        for (final DiagramWalker walker : walkerMap.keySet()) {
            if (walker instanceof ERTable) {
                final ERTable table = (ERTable) walker;
                for (final ERColumn column : table.getColumns()) {
                    if (column instanceof NormalColumn) {
                        final NormalColumn oldColumn = (NormalColumn) column;
                        if (oldColumn.isForeignKey()) {
                            final NormalColumn newColumn = (NormalColumn) columnMap.get(oldColumn);
                            newColumn.renewRelationList();
                            for (final Relationship oldRelation : oldColumn.getRelationshipList()) {
                                final Relationship newRelation = (Relationship) connectionElementMap.get(oldRelation);
                                if (newRelation != null) {
                                    final NormalColumn oldReferencedColumn = newRelation.getReferredSimpleUniqueColumn();
                                    if (oldReferencedColumn != null) {
                                        final NormalColumn newReferencedColumn = (NormalColumn) columnMap.get(oldReferencedColumn);
                                        newRelation.setReferredSimpleUniqueColumn(newReferencedColumn);
                                    }
                                    final CompoundUniqueKey oldReferencedComplexUniqueKey = newRelation.getReferredCompoundUniqueKey();
                                    if (oldReferencedComplexUniqueKey != null) {
                                        final CompoundUniqueKey newReferencedComplexUniqueKey =
                                                complexUniqueKeyMap.get(oldReferencedComplexUniqueKey);
                                        if (newReferencedComplexUniqueKey != null) {
                                            newRelation.setReferredCompoundUniqueKey(newReferencedComplexUniqueKey);
                                        }
                                    }

                                    NormalColumn targetReferencedColumn = null;

                                    for (final NormalColumn referencedColumn : oldColumn.getReferencedColumnList()) {
                                        if (referencedColumn.getColumnHolder() == oldRelation.getSourceTableView()) {
                                            targetReferencedColumn = referencedColumn;
                                            break;
                                        }
                                    }
                                    final NormalColumn newReferencedColumn = (NormalColumn) columnMap.get(targetReferencedColumn);

                                    newColumn.removeReference(oldRelation);
                                    newColumn.addReference(newReferencedColumn, newRelation);

                                } else {
                                    // ������̗���O���L�[�ł͂Ȃ��A�ʏ�̗�ɍ�蒼���܂�
                                    newColumn.removeReference(oldRelation);
                                }
                            }
                        }
                    }
                }

            }
        }

        return copyList;
    }

    private static void replaceIncoming(DiagramWalker from, DiagramWalker to, Map<WalkerConnection, WalkerConnection> connectionElementMap,
            Map<DiagramWalker, DiagramWalker> nodeElementMap) {
        final List<WalkerConnection> cloneIncomings = new ArrayList<WalkerConnection>();
        for (final WalkerConnection incoming : from.getIncomings()) {
            final DiagramWalker oldSource = incoming.getWalkerSource();
            final DiagramWalker newSource = nodeElementMap.get(oldSource);
            if (newSource != null) {
                final WalkerConnection cloneIncoming = incoming.clone();
                cloneIncoming.setSourceWalker(newSource);
                cloneIncoming.setTargetWalker(to);
                connectionElementMap.put(incoming, cloneIncoming);
                cloneIncomings.add(cloneIncoming);
                newSource.addOutgoing(cloneIncoming);
            }
        }
        to.setIncoming(cloneIncomings);
    }

    private static void copyColumnAndIndex(ERTable from, ERTable to, Map<ERColumn, ERColumn> columnMap,
            Map<CompoundUniqueKey, CompoundUniqueKey> complexUniqueKeyMap) {
        copyColumn(from, to, columnMap);
        copyIndex(from, to, columnMap);
        copyComplexUniqueKey(from, to, columnMap, complexUniqueKeyMap);
    }

    private static void copyColumn(TableView from, TableView to, Map<ERColumn, ERColumn> columnMap) {
        final List<ERColumn> cloneColumns = new ArrayList<ERColumn>();
        for (final ERColumn column : from.getColumns()) {
            ERColumn cloneColumn = null;
            if (column instanceof ColumnGroup) {
                cloneColumn = column;
            } else {
                cloneColumn = (NormalColumn) column.clone();
            }

            cloneColumns.add(cloneColumn);

            columnMap.put(column, cloneColumn);
        }

        // ������̃e�[�u���ɁA������̗�ꗗ��ݒ肵�܂��B
        to.setColumns(cloneColumns);
    }

    private static void copyComplexUniqueKey(ERTable from, ERTable to, Map<ERColumn, ERColumn> columnMap,
            Map<CompoundUniqueKey, CompoundUniqueKey> complexUniqueKeyMap) {
        final List<CompoundUniqueKey> cloneComplexUniqueKeyList = new ArrayList<CompoundUniqueKey>();

        // ���̃e�[�u���̕�����ӃL�[�ɑ΂��āA�������J��Ԃ��܂��B
        for (final CompoundUniqueKey complexUniqueKey : from.getCompoundUniqueKeyList()) {

            // ������ӃL�[�𕡐����܂��B
            final CompoundUniqueKey cloneComplexUniqueKey = (CompoundUniqueKey) complexUniqueKey.clone();
            complexUniqueKeyMap.put(complexUniqueKey, cloneComplexUniqueKey);

            final List<NormalColumn> cloneColumns = new ArrayList<NormalColumn>();

            // ������̕�����ӃL�[�̗�ɑ΂��āA�������J��Ԃ��܂��B
            for (final NormalColumn column : cloneComplexUniqueKey.getColumnList()) {
                // ������̗���擾���āA������̕�����ӃL�[�̗�ꗗ�ɒǉ����܂��B
                cloneColumns.add((NormalColumn) columnMap.get(column));
            }

            // ������̕�����ӃL�[�ɁA������̕�����ӃL�[�̗�ꗗ��ݒ肵�܂��B
            cloneComplexUniqueKey.setColumnList(cloneColumns);

            cloneComplexUniqueKeyList.add(cloneComplexUniqueKey);
        }

        // ������̃e�[�u���ɁA������̃C���f�b�N�X�ꗗ��ݒ肵�܂��B
        to.setComplexUniqueKeyList(cloneComplexUniqueKeyList);
    }

    private static void copyIndex(ERTable from, ERTable to, Map<ERColumn, ERColumn> columnMap) {
        final List<ERIndex> cloneIndexes = new ArrayList<ERIndex>();

        // ���̃e�[�u���̃C���f�b�N�X�ɑ΂��āA�������J��Ԃ��܂��B
        for (final ERIndex index : from.getIndexes()) {

            // �C���f�b�N�X�𕡐����܂��B
            final ERIndex cloneIndex = index.clone();

            final List<NormalColumn> cloneIndexColumns = new ArrayList<NormalColumn>();

            // ������̃C���f�b�N�X�̗�ɑ΂��āA�������J��Ԃ��܂��B
            for (final NormalColumn indexColumn : cloneIndex.getColumns()) {
                // ������̗���擾���āA������̃C���f�b�N�X��ꗗ�ɒǉ����܂��B
                cloneIndexColumns.add((NormalColumn) columnMap.get(indexColumn));
            }

            // ������̃C���f�b�N�X�ɁA������̃C���f�b�N�X��ꗗ��ݒ肵�܂��B
            cloneIndex.setColumns(cloneIndexColumns);

            cloneIndexes.add(cloneIndex);
        }

        // ������̃e�[�u���ɁA������̃C���f�b�N�X�ꗗ��ݒ肵�܂��B
        to.setIndexes(cloneIndexes);
    }

    public DiagramContents copy(DiagramContents originalDiagramContents) {
        final DiagramContents copyDiagramContents = new DiagramContents();

        copyDiagramContents.setDiagramWalkers(this.copyNodeElementList(originalDiagramContents.getDiagramWalkers()));
        final Map<DiagramWalker, DiagramWalker> nodeElementMap = this.getNodeElementMap();

        final DiagramSettings settings = (DiagramSettings) originalDiagramContents.getSettings().clone();
        this.setSettings(nodeElementMap, settings);
        copyDiagramContents.setSettings(settings);

        this.setColumnGroup(copyDiagramContents, originalDiagramContents);

        copyDiagramContents.setSequenceSet(originalDiagramContents.getSequenceSet().clone());
        copyDiagramContents.setTriggerSet(originalDiagramContents.getTriggerSet().clone());

        this.setWord(copyDiagramContents, originalDiagramContents);
        this.setTablespace(copyDiagramContents, originalDiagramContents);

        return copyDiagramContents;
    }

    private void setSettings(Map<DiagramWalker, DiagramWalker> nodeElementMap, DiagramSettings settings) {
        for (final Category category : settings.getCategorySetting().getAllCategories()) {
            final List<DiagramWalker> newContents = new ArrayList<DiagramWalker>();
            for (final DiagramWalker nodeElement : category.getContents()) {
                newContents.add(nodeElementMap.get(nodeElement));
            }

            category.setContents(newContents);
        }
    }

    private void setColumnGroup(DiagramContents copyDiagramContents, DiagramContents originalDiagramContents) {

        final Map<ColumnGroup, ColumnGroup> columnGroupMap = new HashMap<ColumnGroup, ColumnGroup>();

        for (final ColumnGroup columnGroup : originalDiagramContents.getColumnGroupSet()) {
            final ColumnGroup newColumnGroup = columnGroup.clone();
            copyDiagramContents.getColumnGroupSet().add(newColumnGroup);

            columnGroupMap.put(columnGroup, newColumnGroup);
        }

        for (final TableView tableView : copyDiagramContents.getDiagramWalkers().getTableViewList()) {
            final List<ERColumn> newColumns = new ArrayList<ERColumn>();

            for (final ERColumn column : tableView.getColumns()) {
                if (column instanceof ColumnGroup) {
                    newColumns.add(columnGroupMap.get(column));

                } else {
                    newColumns.add(column);
                }
            }

            tableView.setColumns(newColumns);
        }
    }

    private void setWord(DiagramContents copyDiagramContents, DiagramContents originalDiagramContents) {

        final Map<Word, Word> wordMap = new HashMap<Word, Word>();
        final Dictionary copyDictionary = copyDiagramContents.getDictionary();

        for (final Word word : originalDiagramContents.getDictionary().getWordList()) {
            final Word newWord = (Word) word.clone();
            wordMap.put(word, newWord);
        }

        for (final TableView tableView : copyDiagramContents.getDiagramWalkers().getTableViewList()) {
            for (final NormalColumn normalColumn : tableView.getNormalColumns()) {
                final Word oldWord = normalColumn.getWord();
                if (oldWord != null) {
                    final Word newWord = wordMap.get(oldWord);
                    normalColumn.setWord(newWord);

                    copyDictionary.add(normalColumn);
                }
            }
        }

        for (final ColumnGroup columnGroup : copyDiagramContents.getColumnGroupSet()) {
            for (final NormalColumn normalColumn : columnGroup.getColumns()) {
                final Word oldWord = normalColumn.getWord();
                if (oldWord != null) {
                    final Word newWord = wordMap.get(oldWord);
                    normalColumn.setWord(newWord);

                    copyDictionary.add(normalColumn);
                }
            }
        }

    }

    private void setTablespace(DiagramContents copyDiagramContents, DiagramContents originalDiagramContents) {

        final Map<Tablespace, Tablespace> tablespaceMap = new HashMap<Tablespace, Tablespace>();
        final TablespaceSet copyTablespaceSet = copyDiagramContents.getTablespaceSet();

        for (final Tablespace tablespace : originalDiagramContents.getTablespaceSet()) {
            final Tablespace newTablespace = tablespace.clone();
            tablespaceMap.put(tablespace, newTablespace);

            copyTablespaceSet.addTablespace(newTablespace);
        }

        for (final TableView tableView : copyDiagramContents.getDiagramWalkers().getTableViewList()) {
            final TableViewProperties tableProperties = tableView.getTableViewProperties();
            final Tablespace oldTablespace = tableProperties.getTableSpace();

            final Tablespace newTablespace = tablespaceMap.get(oldTablespace);
            tableProperties.setTableSpace(newTablespace);
        }

        final TableViewProperties defaultTableProperties = copyDiagramContents.getSettings().getTableViewProperties();
        final Tablespace oldDefaultTablespace = defaultTableProperties.getTableSpace();

        final Tablespace newDefaultTablespace = tablespaceMap.get(oldDefaultTablespace);
        defaultTableProperties.setTableSpace(newDefaultTablespace);
    }
}
