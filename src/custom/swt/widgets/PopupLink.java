package custom.swt.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;


import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

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
 * <dt><b>Styles:</b>
 * <dd>(none)</dd>
 * </dl>
 *
 */
public class PopupLink extends Composite {

	private Link link;
	private String text;
	private String[] items;
	private int selItemIndex;
	private String connection;
	private IPopupLinkInputValidator[] validators;
	private List<ChangeSelectionLister> changeSelectionListeners = new ArrayList<>();
	
	/**
	 * Constructs a new instance of this class given its parent
	 * and a style value describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in
	 * class <code>SWT</code> which is applicable to instances of this
	 * class, or must be built by <em>bitwise OR</em>'ing together 
	 * (that is, using the <code>int</code> "|" operator) two or more
	 * of those <code>SWT</code> style constants. The class description
	 * lists the style constants that are applicable to the class.
	 * Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a widget which will be the parent of the new instance (cannot be null)
	 * @param style the style of widget to construct
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 * </ul>
	 */
	public PopupLink(Composite parent, int style){
		super(parent, style);
		
		selItemIndex = 0;
		
		setLayout(new GridLayout(1, true));
		//setLayout(new OurLayout());

		link = new Link(this, SWT.NONE);
		link.setCursor(new Cursor(getDisplay(), SWT.CURSOR_HAND));
		link.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
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
	
	
	/**
	 * @return the text displayed in the text field of the popup link
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param value - the text to be displayed in the text field of the popup link
	 */
	public void setTextFieldValue(String value) {
		this.text = value;
		updateLinkText();
	}
	
	/**
	 * @return the options for the dropdown list of the popup link
	 */
	public String[] getDropdownOptions() {
		return items;
	}
	
	/**
	 * @param options - the dropdown list options of the popup link
	 */
	public void setDropdownOptions(String [] options){
		this.items = options;
		updateLinkText();
	}
	
	/**
	 * @return the index of the selected option
	 */
	public int getSelectionIndex() {
		return selItemIndex;
	}

	/**
	 * @param selItemIndex - the index of the option to appear as selected
	 */
	public void setSelection(int selItemIndex) {
		this.selItemIndex = selItemIndex;
		updateLinkText();
	}

	/**
	 * @return the selected option's text
	 */
	public String getSelectionText(){
		return items[selItemIndex];
	}
	
	/**
	 * @param connection - a string to used as connection between the textfield value and the selected option
	 * e.g. 10 per sec
	 */
	public void setConnectionText(String connection){
		this.connection = connection;
		updateLinkText();
	}
	
	protected Link getLink(){
		return link;
	}
	
	/**
	 * Updated the text displayed by the link taking which is the combindation of the text field value (if any) and the 
	 * selected option from the dropdown list (if any)
	 */
	private void updateLinkText(){
		
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
		
		changeSelectionListeners.forEach(l->l.onChangeSelection());
		//super.setFocus(); // In order to hide the dotted border
	}
	
	/**
	 * @return the list of {@link IPopupLinkInputValidator} objects that are used for validating
	 * the user input
	 */
	public IPopupLinkInputValidator[] getValidators() {
		return validators;
	}

	/**
	 * @param validators - a list of {@link IPopupLinkInputValidator} objects to be used for validating
	 * user input
	 */
	public void setValidators(IPopupLinkInputValidator[] validators) {
		this.validators = validators;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Forward basic methods to link
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackground(Color c){
		super.setBackground(c);
		link.setBackground(c);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setForeground(org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setForeground(Color c){
		super.setForeground(c);
		link.setForeground(c);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean b){
		super.setEnabled(b);
		link.setEnabled(b);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setToolTipText(java.lang.String)
	 */
	@Override
	public void setToolTipText(String text){
		super.setToolTipText(text);
		link.setToolTipText(text);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setCursor(org.eclipse.swt.graphics.Cursor)
	 */
	@Override
	public void setCursor(Cursor cursor){
		super.setCursor(cursor);
		link.setCursor(cursor);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setFont(org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setFont(Font font){
		super.setFont(font);
		link.setFont(font);
	}
	
	/////////////////////////////////////////////////////////////////////////////
	// Private Layout class for this custom component 
	
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
	
	public void addChangeSelectionListener(ChangeSelectionLister listener){
		this.changeSelectionListeners.add(listener);
	}
	
	public static abstract class ChangeSelectionLister{
		public abstract void onChangeSelection();
	}
}
