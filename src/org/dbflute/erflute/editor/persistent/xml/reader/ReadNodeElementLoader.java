package org.dbflute.erflute.editor.persistent.xml.reader;

import org.dbflute.erflute.core.util.Srl;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Bendpoint;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.CommentConnection;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.connection.Relationship;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.NodeElement;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.TableView;
import org.dbflute.erflute.editor.persistent.xml.PersistentXml;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author modified by jflute (originated in ermaster)
 */
public class ReadNodeElementLoader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final PersistentXml persistentXml;
    protected final ReadAssistLogic assistLogic;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReadNodeElementLoader(PersistentXml persistentXml, ReadAssistLogic assistLogic) {
        this.persistentXml = persistentXml;
        this.assistLogic = assistLogic;
    }

    // ===================================================================================
    //                                                                        Node Element
    //                                                                        ============
    public void loadNodeElement(NodeElement nodeElement, Element element, LoadContext context) {
        String id = getStringValue(element, "id");
        if (Srl.is_Null_or_TrimmedEmpty(id)) {
            if (nodeElement instanceof TableView) {
                id = ((TableView) nodeElement).buildTableViewId(); // #for_erflute
            } else {
                id = "#error:unknownId_for_" + nodeElement.getName();
            }
        }
        assistLogic.loadLocation(nodeElement, element);
        assistLogic.loadColor(nodeElement, element);
        assistLogic.loadFont(nodeElement, element);
        context.nodeElementMap.put(id, nodeElement);
        loadConnections(element, context);
    }

    private void loadConnections(Element parent, LoadContext context) {
        final Element element = getElement(parent, "connections");
        if (element != null) {
            final NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                final Element connectionElement = (Element) nodeList.item(i);
                if ("relation".equals(connectionElement.getTagName())) {
                    loadRelation(connectionElement, context);
                } else if ("comment_connection".equals(connectionElement.getTagName())) {
                    loadCommentConnection(connectionElement, context);
                }
            }
        }
    }

    private void loadRelation(Element element, LoadContext context) {
        final boolean referenceForPK = getBooleanValue(element, "reference_for_pk");
        final Relationship connection = new Relationship(referenceForPK, null, null);
        connection.setName(getStringValue(element, "name"));
        connection.setChildCardinality(getStringValue(element, "child_cardinality"));
        connection.setParentCardinality(getStringValue(element, "parent_cardinality"));
        connection.setOnDeleteAction(getStringValue(element, "on_delete_action", "NO ACTION"));
        connection.setOnUpdateAction(getStringValue(element, "on_update_action", "NO ACTION"));
        connection.setSourceLocationp(getIntValue(element, "source_xp", -1), getIntValue(element, "source_yp", -1));
        connection.setTargetLocationp(getIntValue(element, "target_xp", -1), getIntValue(element, "target_yp", -1));
        final String referencedColumnId = getStringValue(element, "referenced_column"); // needed? (in relation) by jflute
        if (referencedColumnId != null) {
            context.referencedColumnMap.put(connection, referencedColumnId);
        }
        final String referencedComplexUniqueKeyId = getStringValue(element, "referenced_complex_unique_key");
        if (referencedComplexUniqueKeyId != null) {
            context.referencedComplexUniqueKeyMap.put(connection, referencedComplexUniqueKeyId);
        }
        loadConnectionElement(connection, element, context);
    }

    private void loadCommentConnection(Element element, LoadContext context) {
        final CommentConnection connection = new CommentConnection();
        loadConnectionElement(connection, element, context);
    }

    private void loadConnectionElement(ConnectionElement connection, Element element, LoadContext context) {
        final String source = getStringValue(element, "source");
        final String target = getStringValue(element, "target");
        String id = getStringValue(element, "id");
        if (Srl.is_Null_or_TrimmedEmpty(id)) {
            if (connection instanceof Relationship) {
                id = ((Relationship) connection).buildRelationshipId(); // #for_erflute
            } else {
                id = "#error:unknownId_for_" + target + "_to_" + source;
            }
        }
        context.connectionMap.put(id, connection);
        context.connectionSourceMap.put(connection, source);
        context.connectionTargetMap.put(connection, target);
        final NodeList nodeList = element.getElementsByTagName("bendpoint");
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Element bendPointElement = (Element) nodeList.item(i);
            final Bendpoint bendpoint = new Bendpoint(getIntValue(bendPointElement, "x"), this.getIntValue(bendPointElement, "y"));
            bendpoint.setRelative(getBooleanValue(bendPointElement, "relative"));
            connection.addBendpoint(i, bendpoint);
        }
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String getStringValue(Element element, String tagname) {
        return assistLogic.getStringValue(element, tagname);
    }

    private String getStringValue(Element element, String tagname, String defaultValue) {
        return assistLogic.getStringValue(element, tagname, defaultValue);
    }

    private boolean getBooleanValue(Element element, String tagname) {
        return assistLogic.getBooleanValue(element, tagname);
    }

    private int getIntValue(Element element, String tagname) {
        return assistLogic.getIntValue(element, tagname);
    }

    private int getIntValue(Element element, String tagname, int defaultValue) {
        return assistLogic.getIntValue(element, tagname, defaultValue);
    }

    private Element getElement(Element element, String tagname) {
        return assistLogic.getElement(element, tagname);
    }
}