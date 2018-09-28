package custom.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ButtonWithImage extends CLabel {

    private int[] borderReleasedColor = new int[] { 180, 180, 180 };
    private int[] borderPressedColor = new int[] { 75, 106, 128 };
    private int[] backgroundReleasedColor = new int[] { 222, 226, 222 };
    private int[] backgroundPressedColor = new int[] { 200, 232, 248 };

    private int mode = SWT.DEFAULT;

    private boolean clicked = false;
    private Boolean isMouseHover = false;

    private static String OS = System.getProperty("os.name").toLowerCase();

    public ButtonWithImage(Composite parent, int style, int[] borderReleasedColor, int[] backgroundReleasedColor,
	    int[] borderPressedColor, int[] backgroundPressedColor, boolean clickedOnStart) {
	super(parent, SWT.NONE);
	this.borderReleasedColor = borderReleasedColor;
	this.backgroundReleasedColor = backgroundReleasedColor;
	this.borderPressedColor = borderPressedColor;
	this.backgroundPressedColor = backgroundPressedColor;
	mode = style;

	this.clicked = clickedOnStart;
	// necessary to have right bg on mac
	if (clicked)
	    setBackground(new Color(Display.getDefault(), backgroundPressedColor[0], backgroundPressedColor[1],
		    backgroundPressedColor[2]));
	else
	    setBackground(new Color(Display.getDefault(), backgroundReleasedColor[0], backgroundReleasedColor[1],
		    backgroundReleasedColor[2]));
	init();

    }

    public ButtonWithImage(Composite parent, int style) {
	super(parent, SWT.NONE);
	mode = style;
	init();
    }

    private void init() {

	// draw the border
	if (mode == SWT.BORDER)
	    addPaintListener(new PaintListener() {
		@Override
		public void paintControl(PaintEvent event) {

		    if (!clicked && !isMouseHover) {
			setBackground(new Color(Display.getDefault(), backgroundReleasedColor[0],
				backgroundReleasedColor[1], backgroundReleasedColor[2]));

			event.gc.setForeground(new Color(Display.getDefault(), borderReleasedColor[0],
				borderReleasedColor[1], borderReleasedColor[2]));
		    } else {
			setBackground(new Color(Display.getDefault(), backgroundPressedColor[0],
				backgroundPressedColor[1], backgroundPressedColor[2]));

			event.gc.setForeground(new Color(Display.getDefault(), borderPressedColor[0],
				borderPressedColor[1], borderPressedColor[2]));
		    }

		    event.gc.drawRectangle(event.x + 1, event.y + 1, event.width - 2, event.height - 2);

		}
	    });

	// set the right background color
	addMouseListener(new MouseListener() {
	    @Override
	    public void mouseUp(MouseEvent arg0) {
		if (!clicked) {
		    setBackground(new Color(Display.getDefault(), backgroundReleasedColor[0],
			    backgroundReleasedColor[1], backgroundReleasedColor[2]));
		} else {
		    setBackground(new Color(Display.getDefault(), backgroundPressedColor[0], backgroundPressedColor[1],
			    backgroundPressedColor[2]));
		}
		redraw();
	    }

	    @Override
	    public void mouseDown(MouseEvent arg0) {
		clicked = !clicked;
	    }

	    @Override
	    public void mouseDoubleClick(MouseEvent arg0) {
	    }
	});

	// add mouse-track listener
	if (!(OS.indexOf("mac") >= 0)) {
	    addMouseTrackListener(new MouseTrackListener() {
		@Override
		public void mouseHover(MouseEvent arg0) {
		    isMouseHover = true;
		    redraw();
		}

		@Override
		public void mouseExit(MouseEvent arg0) {
		    isMouseHover = false;
		    redraw();
		}

		@Override
		public void mouseEnter(MouseEvent arg0) {
		    isMouseHover = true;
		    redraw();
		}
	    });
	}
	redraw();
    }

    public void setBorderReleasedColor(int[] borderReleasedColor) {
	this.borderReleasedColor = borderReleasedColor;
    }

    public void setBorderPressedColor(int[] borderPressedColor) {
	this.borderPressedColor = borderPressedColor;
    }

    public void setBackgroundReleasedColor(int[] backgroundReleasedColor) {
	this.backgroundReleasedColor = backgroundReleasedColor;
    }

    public void setBackgroundPressedColor(int[] backgroundPressedColor) {
	this.backgroundPressedColor = backgroundPressedColor;
    }

    public void setIsMouseHover(Boolean isMouseHover) {
	this.isMouseHover = isMouseHover;
    }

    public void setClicked(boolean clicked) {
	this.clicked = clicked;
	redraw();
    }
}
