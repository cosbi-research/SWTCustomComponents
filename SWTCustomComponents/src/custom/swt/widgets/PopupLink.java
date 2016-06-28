package swtcc.custom.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;

/**
 * The PopupLink class represents an interactive user interface object that has the look of link,
 * but combines a text field and/or a dropdown list (depending on its type) in a popup dialog for
 * changing its content. 
 * <p>
 * This custom widget can be used in user input forms or any other place that an input from the user is
 * needed
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
public class PopupLink extends Composite {

	private Link link;
	private String text;
	private String[] items;
	private int selItemIndex;
	private String connection;
	private IPopupLinkInputValidator[] validators;
	
	public PopupLink(Composite parent, int style){
		super(parent, style);
		
		selItemIndex = 0;
		
		link = new Link(this, SWT.NONE);
		link.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
		
		setLayout(new OurLayout());
		
		installListeners();
	}
	
	private PopupLink getClassInstance(){
		return this;
	}
	
	private void installListeners(){
		
		addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		link.addListener (SWT.Selection, new Listener () {
	        public void handleEvent(Event event) {
	        	//Link l = (Link) event.widget;
	        	PopupControlAdapter.getInstance().display(getClassInstance());
	        }
	    });
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		updateLinkText();
	}

	public int getSelectionIndex() {
		return selItemIndex;
	}

	public void setSelection(int selItemIndex) {
		this.selItemIndex = selItemIndex;
		updateLinkText();
	}

	public String[] getItems() {
		return items;
	}
	
	public void setItems(String [] items){
		this.items = items;
		updateLinkText();
	}
	
	public String getSelectionText(){
		return items[selItemIndex];
	}
	
	public void setConnectionText(String connection){
		this.connection = connection;
		updateLinkText();
	}
	
	public Link getLink(){
		return link;
	}
	
	public void updateLinkText(){
		
		String linkText = "";
		
		if(text != null && !text.isEmpty())
			linkText += text;
		
		if(connection != null && !connection.isEmpty())
			linkText += connection;
		
		if(items != null && items.length > 0){
			linkText += items[selItemIndex];
		}
		
		link.setText("<a>"+linkText+"</a>");
		super.getParent().layout();
		
		//super.setFocus(); // In order to hide the dotted border
	}
	
	public IPopupLinkInputValidator[] getValidators() {
		return validators;
	}

	public void setValidators(IPopupLinkInputValidator[] validators) {
		this.validators = validators;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Forward methods to link
	
	public void setBackground(Color c){
		super.setBackground(c);
		link.setBackground(c);
	}
	
	public void setForeground(Color c){
		super.setForeground(c);
		link.setForeground(c);
	}
	
	public void setEnabled(boolean b){
		super.setEnabled(b);
		link.setEnabled(b);
	}
	
	public void setToolTipText(String text){
		super.setToolTipText(text);
		link.setToolTipText(text);
	}
	
	public void setCursor(Cursor cursor){
		super.setCursor(cursor);
		link.setCursor(cursor);
	}
	
	public void setFont(Font font){
		super.setFont(font);
		link.setFont(font);
	}
	
	private class OurLayout extends Layout {
		Point tExtent; // the cached sizes

		protected Point computeSize(Composite composite, int wHint, int hHint,
				boolean changed) {
			Control[] children = composite.getChildren();
			if (changed || tExtent == null) {
				tExtent = children[0].computeSize(SWT.DEFAULT, SWT.DEFAULT,
						false);
			}
			int width = 5 + tExtent.x;
			int height = tExtent.y;
			return new Point(width + 2, height + 2);
		}

		protected void layout(Composite composite, boolean changed) {
			Control[] children = composite.getChildren();
			if (changed || tExtent == null) {
				tExtent = children[0].computeSize(SWT.DEFAULT, SWT.DEFAULT,
						false);
			}
			
			children[0].setBounds(1, 1, tExtent.x, tExtent.y);
		}
	}
	
}
