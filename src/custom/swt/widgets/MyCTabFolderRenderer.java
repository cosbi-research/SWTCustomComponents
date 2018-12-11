package custom.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderRenderer;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class MyCTabFolderRenderer extends CTabFolderRenderer {

	private CTabFolder parent;
	final int[] SIMPLE_UNSELECTED_INNER_CORNER = new int[] { 0, 0 };
	final int[] TOP_RIGHT_CORNER = new int[] { -6, 0, -5, 1, -4, 1, -1, 4, -1, 5, 0, 6 };
	final int[] TOP_LEFT_CORNER = new int[] { 0, 6, 1, 5, 1, 4, 4, 1, 5, 1, 6, 0 };
	final int[] SIMPLE_TOP_LEFT_CORNER = new int[] { 0, 2, 1, 1, 2, 0 };
	final int[] SIMPLE_TOP_RIGHT_CORNER = new int[] { -2, 0, -1, 1, 0, 2 };
	int d;
	int[] curve;
	int curveWidth;
	int curveIndent;

	Color borderColor; 
	Color aliasColor; 

	protected MyCTabFolderRenderer(CTabFolder parent) {
	    super(parent);
	    this.parent = parent;
	    Color borderColor = new Color(parent.getDisplay().getDefault(), 120, 120, 120);
		Color aliasColor = new Color(parent.getDisplay().getDefault(), 242, 242, 242);
	    int d = parent.getTabHeight() - 12;
	    curve = new int[] { 0, 0, 0, 1, 2, 1, 3, 2, 5, 2, 6, 3, 7, 3, 9, 5, 10, 5, 11, 6, 11 + d, 6 + d, 12 + d,
		    7 + d, 13 + d, 7 + d, 15 + d, 9 + d, 16 + d, 9 + d, 17 + d, 10 + d, 19 + d, 10 + d, 20 + d, 11 + d,
		    22 + d, 11 + d, 23 + d, 12 + d };
	    curveWidth = 26 + d;
	    curveIndent = curveWidth / 3;
	    // TODO Auto-generated constructor stub
	}

	@Override
	protected void draw(int part, int state, Rectangle bounds, GC gc) {

	    super.draw(part, state, bounds, gc);
	    updateCurves();
	    if (0 <= part && part < parent.getItemCount()) {
		if (bounds.width == 0 || bounds.height == 0)
		    return;
		if ((state & SWT.SELECTED) != 0) {
		    drawSelected(part, gc, bounds, state);
		} else {
		    drawUnselected(part, gc, bounds, state);
		}
	    }

	}

	private void drawSelected(int itemIndex, GC gc, Rectangle bounds, int state) {

	    CTabItem item = parent.getItems()[itemIndex];
	    int x = bounds.x;
	    int y = bounds.y;
	    int height = bounds.height;
	    int width = bounds.width;
	    if (!parent.getSimple() && !parent.getSingle())
		width -= (curveWidth - curveIndent);
	    int borderLeft = parent.getBorderVisible() ? 1 : 0;
	    int borderRight = borderLeft;
	    int borderTop = 0;
	    int borderBottom = borderLeft;

	    int rightEdge = Math.min(x + width, 1000);
	    //	    int rightEdge = Math.min(x + width, 1000);

	    Point size = parent.getSize();
	    int highlight_header = (parent.getStyle() & SWT.FLAT) != 0 ? 1 : 3;
	    int xx = borderLeft;
	    int yy = borderTop + parent.getTabHeight() + 1;
	    int ww = size.x - borderLeft - borderRight;
	    int hh = highlight_header - 1;

	    int[] shape = new int[] { xx, yy, xx + ww, yy, xx + ww, yy + hh, xx, yy + hh };

	    int[] left = parent.getSimple() ? SIMPLE_TOP_LEFT_CORNER : TOP_LEFT_CORNER;
	    int[] right = parent.getSimple() ? SIMPLE_TOP_RIGHT_CORNER : curve;
	    if (borderLeft == 0 && itemIndex == 0) {
		left = new int[] { x, y };
	    }
	    shape = new int[left.length + right.length + 8];
	    int index = 0;
	    shape[index++] = x; // first point repeated here because below we reuse shape to draw outline
	    shape[index++] = y + height + 1;
	    shape[index++] = x;
	    shape[index++] = y + height + 1;
	    for (int i = 0; i < left.length / 2; i++) {
		shape[index++] = x + left[2 * i];
		shape[index++] = y + left[2 * i + 1];
	    }
	    for (int i = 0; i < right.length / 2; i++) {
		shape[index++] = parent.getSimple() ? rightEdge - 1 + right[2 * i]
			: rightEdge - 1 - curveIndent + right[2 * i];
		shape[index++] = y + right[2 * i + 1];
	    }
	    shape[index++] = parent.getSimple() ? rightEdge - 1 : rightEdge + curveWidth - curveIndent - 1;
	    shape[index++] = y + height + 1;
	    shape[index++] = parent.getSimple() ? rightEdge - 1 : rightEdge + curveWidth - curveIndent - 1;
	    shape[index++] = y + height + 1;

	    Rectangle clipping = gc.getClipping();
	    Rectangle clipBounds = item.getBounds();
	    clipBounds.height += 1;

	    // draw outline

	    shape[0] = Math.max(0, borderLeft - 1);
	    if (borderLeft == 0 && itemIndex == 0) {
		shape[1] = y;
		shape[5] = shape[3] = shape[1];
	    }
	    shape[shape.length - 2] = size.x - borderRight + 1 - 1;
	    for (int i = 0; i < shape.length / 2; i++) {
		if (shape[2 * i + 1] == y + height + 1)
		    shape[2 * i + 1] -= 1;
	    }

	    gc.setForeground(borderColor);
	    gc.drawPolyline(shape);
	    antialias (shape,  aliasColor, aliasColor, gc);

	}

	void drawUnselected(int index, GC gc, Rectangle bounds, int state) {
	    CTabItem item = parent.getItems()[index];
	    int x = bounds.x;
	    int y = bounds.y;
	    int height = bounds.height;
	    int width = bounds.width;

	    // Do not draw partial items
	    if (!item.getShowClose())
		return;

	    Rectangle clipping = gc.getClipping();
	    if (!clipping.intersects(bounds))
		return;

	    if ((state & SWT.BACKGROUND) != 0) {
		if (index > 0 && index < parent.getSelectionIndex())
		    drawLeftUnselectedBorder(gc, bounds, state);
		// If it is the last one then draw a line
		if (index > parent.getSelectionIndex())
		    drawRightUnselectedBorder(gc, bounds, state);
	    }
	}

	void drawLeftUnselectedBorder(GC gc, Rectangle bounds, int state) {
	    int x = bounds.x;
	    int y = bounds.y;
	    int height = bounds.height;

	    int[] shape = null;

	    int[] left = parent.getSimple() ? SIMPLE_UNSELECTED_INNER_CORNER : TOP_LEFT_CORNER;

	    shape = new int[left.length + 2];
	    int index = 0;
	    shape[index++] = x;
	    shape[index++] = y + height;
	    for (int i = 0; i < left.length / 2; i++) {
		shape[index++] = x + left[2 * i];
		shape[index++] = y + left[2 * i + 1];
	    }

	    drawBorder(gc, shape);
	}

	void drawRightUnselectedBorder(GC gc, Rectangle bounds, int state) {
	    int x = bounds.x;
	    int y = bounds.y;
	    int width = bounds.width;
	    int height = bounds.height;

	    int[] shape = null;
	    int startX = x + width - 1;

	    int[] right = parent.getSimple() ? SIMPLE_UNSELECTED_INNER_CORNER : TOP_RIGHT_CORNER;

	    shape = new int[right.length + 2];
	    int index = 0;

	    for (int i = 0; i < right.length / 2; i++) {
		shape[index++] = startX + right[2 * i];
		shape[index++] = y + right[2 * i + 1];
	    }

	    shape[index++] = startX;
	    shape[index++] = y + height;

	    drawBorder(gc, shape);

	}

	void drawBorder(GC gc, int[] shape) {

	    gc.setForeground(borderColor);
	    gc.drawPolyline(shape);
	    antialias (shape,  aliasColor, aliasColor, gc);
	}

	void updateCurves() {
	    //Temp fix for Bug 384743
	    int tabHeight = parent.getTabHeight();

	    int d = tabHeight - 12;
	    curve = new int[] { 0, 0, 0, 1, 2, 1, 3, 2, 5, 2, 6, 3, 7, 3, 9, 5, 10, 5, 11, 6, 11 + d, 6 + d, 12 + d,
		    7 + d, 13 + d, 7 + d, 15 + d, 9 + d, 16 + d, 9 + d, 17 + d, 10 + d, 19 + d, 10 + d, 20 + d, 11 + d,
		    22 + d, 11 + d, 23 + d, 12 + d };
	    
	    curve = new int[] { 0, 0, 0, 1, 2, 1, 3, 2, 5, 2, 6, 3, 7, 3, 9, 5, 10, 5, 11, 6, 11 + d, 6 + d, 12 + d,
			    7 + d, 13 + d, 7 + d, 15 + d, 9 + d, 16 + d, 9 + d, 17 + d, 10 + d, 19 + d, 10 + d, 19 + d, 11 + d,
			    21 + d, 11 + d, 22 + d, 12 + d };
		    
	    

	    
	    curveWidth = 26 + d;
	    curveIndent = curveWidth / 3;


	}
	
	void antialias (int[] shape, Color innerColor, Color outerColor, GC gc){
		
		// Don't perform anti-aliasing on low resolution displays
		if (parent.getDisplay().getDepth() < 15) return;
		if (outerColor != null) {
			int index = 0;
			boolean left = true;
			int oldY = parent.getSize().y;
			int[] outer = new int[shape.length];
			for (int i = 0; i < shape.length/2; i++) {
				if (left && (index + 3 < shape.length)) {
					left =  oldY >= shape[index+3];
					oldY = shape[index+1];
				}
				outer[index] = shape[index++] + (left ? -1 : +1);
				outer[index] = shape[index++];
			}
			gc.setForeground(outerColor);
			gc.drawPolyline(outer);
		}
		if (innerColor != null) {
			int[] inner = new int[shape.length];
			int index = 0;
			boolean left = true;
			int oldY =  parent.getSize().y;
			for (int i = 0; i < shape.length/2; i++) {
				if (left && (index + 3 < shape.length)) {
					left =  oldY >= shape[index+3];
					oldY = shape[index+1];
				}
				inner[index] = shape[index++] + (left ? +1 : -1);
				inner[index] = shape[index++];
			}
			gc.setForeground(innerColor);
			gc.drawPolyline(inner);
		}
	}

   }
