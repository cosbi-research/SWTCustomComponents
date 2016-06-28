package swtcc.custom.swt.widgets;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * The LateralTabFolder class represents an interactive and customizable 
 * user interface object of a lateral tab folder.
 * <p>
 * This class combines custom painted buttons for navigating through the tabs
 * and a composite with a stack layout for hosting the tabs content.
 * </p>
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add children to it, or set a layout on it.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be sub-classed.
 * </p>
 *
 */
public class LateralTabFolder extends Composite {

	private Composite compTabs;
	private Composite container;
	private HashMap<Integer,Composite> compContents;
	private StackLayout containerLayout;
	private static int widthCompAlg = 100;
	private static int heightTabs = 32;
	private PaintListener paintRect, paintLinesUp, paintLinesRight, paintLinesDown, paintLinesCompTabs, paintTabModified;
	private Rectangle rect;
	private Color colorLines = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
	private int counter = 0;
	private int selectedTabId;
	private int xMargin = 20;
	private Listener tabListener;
	private HashMap<Integer, Image> tabImagesMap = new HashMap<Integer,Image>();
	private HashMap<Integer, Image> tabHotImagesMap = new HashMap<Integer,Image>();
	private Listener listener;
	private Color activeTabBtnColor;
	
	/**
	 * Constructs a new instance of this class given its parent
	 * and a style value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in
	 * class <code>SWT</code> which is applicable to instances of this
	 * class, or must be built by <em>bitwise OR</em>'ing together 
	 * (that is, using the <code>int</code> "|" operator) two or more
	 * of those <code>SWT</code> style constants.
	 * </p>
	 * 
	 * @param parent - a widget which will be the parent of the new instance (cannot be null)
	 * @param style - the style of widget to construct
	 * 
	 * @exception IllegalArgumentException <ul>
	 * 		<li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *		<li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 * </ul>
	 * 
	 */
	public LateralTabFolder(Composite parent, int style) {
		this(parent, style, widthCompAlg, heightTabs);
	}
	
	/**
	 * Constructs a new instance of this class given its parent
	 * and a style value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in
	 * class <code>SWT</code> which is applicable to instances of this
	 * class, or must be built by <em>bitwise OR</em>'ing together 
	 * (that is, using the <code>int</code> "|" operator) two or more
	 * of those <code>SWT</code> style constants.
	 * </p>
	 * 
	 * @param parent - a widget which will be the parent of the new instance (cannot be null)
	 * @param style - the style of widget to construct
	 * @param tabBtnWidth - the width of the tab buttons (>= 100)
	 * @param tabBtnWidth - the height of the tab buttons (>= 32)
	 * 
	 * @exception IllegalArgumentException <ul>
	 * 		<li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *		<li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 * </ul>
	 * 
	 */
	public LateralTabFolder(Composite parent, int style, int tabBtnWidth, int tabBtnHeight) {
		super(parent, style);

		compContents = new HashMap<Integer,Composite>();
		
		if(tabBtnWidth >= widthCompAlg)
			widthCompAlg = tabBtnWidth;
		
		if(tabBtnHeight >= heightTabs)
			heightTabs = tabBtnHeight;
		
		//Set 2-column GridLayout
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		this.setLayout(gl_composite);
		
		compTabs = new Composite(this, SWT.NONE);
		GridLayout gl_compTabs = new GridLayout(1, false);
		gl_compTabs.marginWidth = 0;
		gl_compTabs.marginHeight = 0;
		gl_compTabs.horizontalSpacing = 0;
		gl_compTabs.verticalSpacing = 1;
		compTabs.setLayout(gl_compTabs);
		GridData gd_compTabs = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_compTabs.widthHint = widthCompAlg;
		compTabs.setLayoutData(gd_compTabs);
		
		container = new Composite(this, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		containerLayout = new StackLayout();
		container.setLayout(containerLayout);
		
		initListeners();
	}
	
	/**
	 * Defines and initializes the listeners of the widget
	 */
	private void initListeners(){
		
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				colorLines.dispose();
				activeTabBtnColor.dispose();
			}
		});
		
		rect = new Rectangle(6,0,widthCompAlg,0);
		activeTabBtnColor = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		
		tabListener = new Listener() {

			@Override
			public void handleEvent(Event e) {
				Widget w = e.widget;
				tabSelected((Integer) w.getData());
				
				((StackLayout) container.getLayout()).topControl = compContents.get((Integer) w.getData());
				container.layout();
			}
		};
		
		paintTabModified = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
					GC gc=e.gc;
				 	gc.setLineWidth(0);
				 	gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				 	gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				 	int xRect = 2;
				 	int yRect = 1;
				 	int endRect = 4;
				 	gc.setLineCap(SWT.CAP_ROUND);
			 		gc.fillRoundRectangle(xRect, yRect, endRect, heightTabs-1-yRect, 6, 6);
			 		gc.drawRoundRectangle(xRect, yRect, endRect, heightTabs-1-yRect, 6, 6);
				 	gc.setLineCap(SWT.CAP_SQUARE);
			 		gc.fillRectangle(endRect, yRect, 3, heightTabs-1);
			}
		};
		
		paintRect = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
					GC gc=e.gc;
				 	gc.setLineWidth(0);
				 	gc.setBackground(activeTabBtnColor);
				 	gc.setForeground(colorLines);
			 		gc.fillRoundRectangle(2, 0, widthCompAlg, heightTabs-1, 6, 6);
			 		gc.drawRoundRectangle(2, 0, widthCompAlg, heightTabs-1, 6, 6);
			}
		};
		
		paintLinesUp = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {				
				 	GC gc=e.gc;
				 	gc.setLineWidth(0);
				 	gc.setForeground(colorLines);
				 	gc.drawLine(rect.x, rect.y, rect.width, rect.height);			 		
			}
		};
		
		paintLinesRight = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				 	GC gc=e.gc;
				 	gc.setLineWidth(0);
				 	gc.setForeground(colorLines);
				 	gc.drawLine(widthCompAlg-1,0,widthCompAlg-1, heightTabs-1);			 		
			}
		};
		
		paintLinesDown = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
					GC gc=e.gc;
				 	gc.setLineWidth(0);
				 	gc.setForeground(colorLines);
				 	gc.drawLine(6, heightTabs-1, widthCompAlg-2, heightTabs-1);
				 	gc.drawLine(widthCompAlg-1, heightTabs-1, widthCompAlg-1, heightTabs);
			}
		};
		
		paintLinesCompTabs= new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
					GC gc=e.gc;
				 	gc.setLineWidth(0);
				 	gc.setForeground(colorLines);
				 	gc.drawLine(widthCompAlg-1, -2, widthCompAlg-1, compTabs.getSize().y);
			}
		};
		
		compTabs.addPaintListener(paintLinesCompTabs);
	}
	
	/**
	 * Adds the paint listeners for the new tab's button
	 * 
	 * @param tabBtn - the new tab button <code>Composite</code> instance
	 * @param index - the index of the new tab button
	 */
	private void addLines(Composite tabBtn, int index){
		
		//if first element draw all lines except right one
		if(index == 0){
			tabBtn.addPaintListener(paintLinesUp);
			tabBtn.addPaintListener(paintLinesDown);
			tabBtn.addPaintListener(paintRect);
		}else{
			if(index > 1){
				tabBtn.addPaintListener(paintLinesUp);
			}
			
			tabBtn.addPaintListener(paintLinesRight);
			tabBtn.addPaintListener(paintLinesDown);
			compTabs.getChildren()[index-1].removePaintListener(paintLinesDown);
		}		
		
		//Repaint all
		for (Control c: compTabs.getChildren()) {
			c.redraw();
		}
	}
	
	/**
	 * Performs the necessary actions upon the selection of a new tab.
	 * <p>
	 * One of the actions is to also inform the registered listener (if any) 
	 * that the active tab has changed
	 * </p>
	 * 
	 * @param tab_id - the index of the tab that is selected
	 */
	private void tabSelected(int tab_id){
		
		if(selectedTabId == tab_id)
			return;
		
		//Reset previous tab
		Composite prevTabBtn = (Composite) compTabs.getChildren()[selectedTabId];
		prevTabBtn.setBackground(null);		
		prevTabBtn.removePaintListener(paintRect);
		prevTabBtn.addPaintListener(paintLinesUp);
		prevTabBtn.addPaintListener(paintLinesRight);
		prevTabBtn.redraw();		
		if(selectedTabId<compTabs.getChildren().length-1){
			compTabs.getChildren()[selectedTabId+1].addPaintListener(paintLinesUp);
			compTabs.getChildren()[selectedTabId+1].redraw();
		}
		CLabel tabLabel = (CLabel) prevTabBtn.getChildren()[0];
		tabLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		FontData fontDatas[] = tabLabel.getFont().getFontData();
		FontData data = fontDatas[0];
		Font font = new Font(Display.getCurrent(), data.getName(), data.getHeight(), SWT.NORMAL);
		
		//Set tab inactive image
		if(tabImagesMap.containsKey(selectedTabId))
			tabLabel.setImage(tabImagesMap.get(selectedTabId));
		
		tabLabel.setFont(font);
		tabLabel.pack();
		
		selectedTabId = tab_id;
		
		Composite tabBtn = (Composite) compTabs.getChildren()[selectedTabId];		
		tabBtn.addPaintListener(paintRect);
		tabBtn.removePaintListener(paintLinesUp);
		tabBtn.removePaintListener(paintLinesRight);
		tabBtn.redraw();

		tabLabel = (CLabel) tabBtn.getChildren()[0];
		tabLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		//Set tab hot image
		if(tabHotImagesMap.containsKey(selectedTabId)){
			tabLabel.setImage(tabHotImagesMap.get(selectedTabId));
		}
		
		data = tabLabel.getFont().getFontData()[0];
		font = new Font(Display.getCurrent(), data.getName(), data.getHeight(), SWT.BOLD);
		tabLabel.setFont(font);		
		
		tabLabel.pack();
		
		if(selectedTabId<compTabs.getChildren().length-1){
			compTabs.getChildren()[selectedTabId+1].removePaintListener(paintLinesUp);
			compTabs.getChildren()[selectedTabId+1].redraw();
		}
		
		if(listener != null)
			listener.handleEvent(new Event());	
	}
	
	/**
	 * Visually marks the active tab as modified if second argument is <code>true</code>
	 * otherwise as un-modified
	 * 
	 * @param modified - the new state of the tab
	 */
	public void setActiveTabModified(boolean modified){
		setTabModified(selectedTabId, modified);
	}
	
	/**
	 * Visually marks the given tab as modified if second argument is <code>true</code>
	 * otherwise as un-modified
	 * 
	 * @param tab_id - the index of the tab being modified or un-modified
	 * @param modified - the new state of the tab
	 */
	public void setTabModified(int tab_id, boolean modified){
		Composite tabBtn = (Composite) compTabs.getChildren()[tab_id];		
		tabBtn.removePaintListener(paintTabModified);
		if(modified)
			tabBtn.addPaintListener(paintTabModified);
		else
			tabBtn.removePaintListener(paintTabModified);
		tabBtn.redraw();		
	}
	
	/**
	 * Sets the background color of the active tab button to the one
	 * specified by the argument
	 * 
	 * @param color - the new background color of the active tab button
	 */
	public void setActiveTabBtnColor(Color color){
		activeTabBtnColor = color;
		
		//Update active tab btn
		Composite tabBtn = (Composite) compTabs.getChildren()[selectedTabId];	
		tabBtn.redraw();
	}
	
	/**
	 * Registers a new tab in the lateral tab folder
	 * 
	 * @param tabContent - the tab user interface
	 * @param btnText - the text for the tab button
	 * @param tabHeader - the text for the tab header
	 * 
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the tabContent is null</li>
	 * </ul>
	 */
	public void addTab(Composite tabContent, String btnText, String tabHeader){
		addTab(tabContent, btnText, tabHeader, null);
	}
	
	/**
	 * Registers a new tab in the lateral tab folder
	 * 
	 * @param tabContent - the tab user interface
	 * @param btnText - the text for the tab button
	 * @param tabHeader - the text for the tab header
	 * @param headerControls - a <code>Composite</code> instance to be displayed in the tab header
	 * 
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the tabContent is null</li>
	 * </ul>
	 */
	public void addTab(Composite tabContent, String btnText, String tabHeader, Composite headerControls){
		addTab(tabContent, btnText, tabHeader, null, null, headerControls);
	}
	
	/**
	 * Registers a new tab in the lateral tab folder
	 * 
	 * @param tabContent - the tab user interface
	 * @param btnText - the text for the tab button
	 * @param tabHeader - the text for the tab header
	 * @param tabBtnImg - the image of the tab button
	 * @param tabHotBtnImg - the image of the tab button when it is active
	 * 
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the tabContent is null</li>
	 * </ul>
	 */
	public void addTab(Composite tabContent, String btnText, String tabHeader, Image tabBtnImg, Image tabHotBtnImg){
		addTab(tabContent, btnText, tabHeader, tabBtnImg, tabHotBtnImg, null);
	}
	
	/**
	 * Registers a new tab in the lateral tab folder
	 * 
	 * @param tabContent - the tab user interface
	 * @param btnText - the text for the tab button
	 * @param tabHeader - the text for the tab header
	 * @param tabBtnImg - the image of the tab button
	 * @param tabHotBtnImg - the image of the tab button when it is active
	 * @param headerControls - a <code>Composite</code> instance to be displayed in the tab header
	 * 
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the tabContent is null</li>
	 * </ul>
	 */
	public void addTab(Composite tabContent, String btnText, String tabHeader, Image tabBtnImg, Image tabHotBtnImg, Composite headerControls){
		
		if(tabContent == null)
			SWT.error (SWT.ERROR_NULL_ARGUMENT);
		
		tabContent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		//Create a new composite for the tab button
		Composite tabBtn =  new Composite(compTabs, SWT.NONE);
		GridData gd_tabBtn = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_tabBtn.heightHint = heightTabs;
		tabBtn.setLayoutData(gd_tabBtn);
		tabBtn.setLayout(new GridLayout(1, false));
		tabBtn.setBounds(0, 0, widthCompAlg, heightTabs);
		tabBtn.setData(counter);
		
		//Create label for tab name
		CLabel lblTabName = new CLabel(tabBtn, SWT.NONE);
		lblTabName.setAlignment(SWT.CENTER);
		lblTabName.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, true));
		
		lblTabName.setText(btnText);
		lblTabName.setData(counter);
		
		if(tabBtnImg != null){
			lblTabName.setImage(tabBtnImg);
			tabImagesMap.put(counter, tabBtnImg);
		}
		
		if(tabHotBtnImg != null){
			tabHotImagesMap.put(counter, tabHotBtnImg);
		}
		
		compTabs.layout();
		
		//Add listeners
		tabBtn.addListener(SWT.MouseDown, tabListener);
		lblTabName.addListener(SWT.MouseDown, tabListener);
		
		//Create contents host composite
		Composite hostComp = new Composite(container, SWT.NONE);
		GridLayout gl_hostComp = new GridLayout(1, false);
		gl_hostComp.verticalSpacing = 0;
		gl_hostComp.marginWidth = 0;
		gl_hostComp.horizontalSpacing = 0;
		gl_hostComp.marginHeight = 0;
		hostComp.setLayout(gl_hostComp);
		
		Color headerColor = new Color(Display.getCurrent(), new RGB(198,225,253));
		
		Composite header = new Composite(hostComp, SWT.NONE);
		GridData gd_header = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_header.heightHint = heightTabs;
		header.setLayoutData(gd_header);
		header.setLayout(new FillLayout(SWT.HORIZONTAL));
		header.setBackground(headerColor);
		
		CLabel lblTitle = new CLabel(header, SWT.LEFT);
		lblTitle.setBackground(new Color[] {
				headerColor,
				Display.getCurrent().getSystemColor(SWT.COLOR_WHITE),},  new int[] {100});

		// this sets the bold font for the title label
		lblTitle.setLeftMargin(xMargin);
		FontData data = lblTitle.getFont().getFontData()[0];
		Font font = new Font(Display.getCurrent(), data.getName(), data.getHeight(), SWT.BOLD);
		lblTitle.setFont(font);		
		lblTitle.setText(tabHeader);
		
		if(headerControls != null){
			headerControls.setParent(header);
			lblTitle.setBackground(headerColor);
			headerControls.setBackground(headerColor);
			
			for(Control ctrl : headerControls.getChildren()){
				if(ctrl instanceof Label){
					ctrl.setBackground(headerColor);
				}
			}
		}
		
		Composite contents = new Composite(hostComp, SWT.NONE);
		GridLayout gl_contents = new GridLayout(1, false);
		gl_contents.marginHeight = 0;
		gl_contents.marginWidth = 0;
		contents.setLayout(gl_contents);
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		contents.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
		
		//Set parent
		tabContent.setParent(contents);

		hostComp.layout();
		
		//Register new tab
		compContents.put(counter, hostComp);
		
		if(compContents.keySet().size() == 1){
			selectedTabId = counter;
			lblTabName.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			
			data = lblTabName.getFont().getFontData()[0];
			font = new Font(Display.getCurrent(), data.getName(), data.getHeight(), SWT.BOLD);
			
			//Set hot image
			if(tabHotImagesMap.containsKey(selectedTabId))
				lblTabName.setImage(tabHotImagesMap.get(selectedTabId));
			else
				lblTabName.setFont(font);
			
			lblTabName.setFont(font);
			lblTabName.pack();
			containerLayout.topControl = hostComp;
		}		
		
		addLines(tabBtn, counter);
		
		counter++;
		container.layout();
	}
	
	/**
	 * @return the index of the selected (active) tab
	 */
	public int getSelectedTabId(){
		return selectedTabId;
	}
	
	/**
	 * Sets a <code>Listener</code> instance that will be notified by the widget
	 * when a the active tab is changed
	 * 
	 * @param l - the <code>Listener</code> instance
	 */
	public void setTabSelectionListener(Listener l){
		listener = l;
	}
}
