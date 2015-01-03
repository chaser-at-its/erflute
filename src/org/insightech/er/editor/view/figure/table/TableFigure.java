package org.insightech.er.editor.view.figure.table;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToFrameAction;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToSimpleAction;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;
import org.insightech.er.editor.view.figure.table.style.StyleSupport;
import org.insightech.er.editor.view.figure.table.style.frame.FrameStyleSupport;
import org.insightech.er.editor.view.figure.table.style.funny.FunnyStyleSupport;
import org.insightech.er.editor.view.figure.table.style.simple.SimpleStyleSupport;

public class TableFigure extends RoundedRectangle {

    private Figure columns;

    private StyleSupport styleSupport;

    private Color foregroundColor;

    private Font largeFont;

    public TableFigure(Settings settings) {
        this.columns = new Figure();
        this.setLayoutManager(new BorderLayout());
        this.setSettings(settings);
    }

    public void setSettings(Settings settings) {
        //		System.out.println("TableFigure::setTableStyle " + tableStyle);
        String tableStyle = settings.getTableStyle();
        if (ChangeDesignToSimpleAction.TYPE.equals(tableStyle)) {
            this.styleSupport = new SimpleStyleSupport(this, settings);

        } else if (ChangeDesignToFrameAction.TYPE.equals(tableStyle)) {
            this.styleSupport = new FrameStyleSupport(this, settings);

        } else {
            this.styleSupport = new FunnyStyleSupport(this, settings);
        }

        this.styleSupport.init();

        this.create(null);
    }

    public void create(int[] color) {
        this.decideColor(color);

        this.removeAll();

        this.styleSupport.createTitleBar();

        this.columns.removeAll();

        this.styleSupport.createColumnArea(this.columns);

        this.styleSupport.createFooter();
    }

    private void decideColor(int[] color) {
        if (color != null) {
            int sum = color[0] + color[1] + color[2];

            if (sum > 255) {
                this.foregroundColor = ColorConstants.black;
            } else {
                this.foregroundColor = ColorConstants.white;
            }
        }
    }

    public void setName(String name) {
        this.styleSupport.setName(name);
    }

    public void setFont(Font font, Font titleFont) {
        this.setFont(font);
        this.styleSupport.setFont(font, titleFont);
    }

    public void clearColumns() {
        this.columns.removeAll();
    }

    public void addColumn(ERTable table, NormalColumn normalColumn, NormalColumnFigure columnFigure, int viewMode, String physicalName,
            String logicalName, String type, boolean primaryKey, boolean foreignKey, boolean isNotNull, boolean uniqueKey,
            boolean displayKey, boolean displayDetail, boolean displayType, boolean isSelectedReferenced, boolean isSelectedForeignKey,
            boolean isAdded, boolean isUpdated, boolean isRemoved) {

        columnFigure.removeAll();
        columnFigure.setBackgroundColor(null);

        this.styleSupport.addColumn(table, normalColumn, columnFigure, viewMode, physicalName, logicalName, type, primaryKey, foreignKey,
                isNotNull, uniqueKey, displayKey, displayDetail, displayType, isSelectedReferenced, isSelectedForeignKey, isAdded,
                isUpdated, isRemoved);
    }

    public void addColumnGroup(GroupColumnFigure columnFigure, int viewMode, String name, boolean isAdded, boolean isUpdated,
            boolean isRemoved) {

        columnFigure.removeAll();
        columnFigure.setBackgroundColor(null);

        this.styleSupport.addColumnGroup(columnFigure, viewMode, name, isAdded, isUpdated, isRemoved);
    }

    public void addIndex(IndexFigure indexFigure, int viewMode, String physicalName, String logicalName, boolean isFirst) {

        indexFigure.removeAll();
        indexFigure.setBackgroundColor(null);

        this.styleSupport.addIndex(indexFigure, physicalName, isFirst);
    }

    @Override
    public Rectangle getBounds() {
        Rectangle bounds = super.getBounds();

        this.styleSupport.adjustBounds(bounds);

        return bounds;
    }

    public Color getTextColor() {
        return foregroundColor;
    }

    @Override
    protected void fillShape(Graphics graphics) {
        graphics.setAlpha(200);
        super.fillShape(graphics);
    }

    /**
     * columns ���擾���܂�.
     *
     * @return columns
     */
    public Figure getColumns() {
        return columns;
    }

    public String getImageKey() {
        return ImageKey.TABLE;
    }

    public void setLargeFont(Font largeFont) {
        this.largeFont = largeFont;
    }

    /**
     * largeFont���擾���܂��B
     * @return largeFont
     */
    public Font getLargeFont() {
        return largeFont;
    }

}
