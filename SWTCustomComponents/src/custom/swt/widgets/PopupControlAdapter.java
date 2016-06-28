package swtcc.custom.swt.widgets;

//import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;

public class PopupControlAdapter {
	
	private static final int TYPE_TEXT = 1;
	private static final int TYPE_TEXT_DROPDOWN = 2;
	private static final int TYPE_DROPDOWN = 3;
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	/*
	 * Flag that indicates whether the adapter is enabled. In some cases,
	 * adapters may be installed but depend upon outside state.
	 */
	private static PopupControlAdapter instance = null;
	private PopupControl popup;
	private static final Color colorBlu = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
	
	private PopupLink selPopupLink;
	private Control controlLink;
	private String textValue;
	private String[] items;
	private int selectedItemIndex;
	private Point position;
	private int popupType;
	
	private ToolTip tooltip;
	
	protected PopupControlAdapter() {
		// Exists only to defeat instantiation.
		tooltip = new ToolTip(Display.getDefault().getActiveShell(), SWT.BALLOON | SWT.ICON_WARNING);
	}

	public static PopupControlAdapter getInstance() {
		if (instance == null) {
			instance = new PopupControlAdapter();
		}
		
		return instance;
	}

	public void display(PopupLink link) {
		selPopupLink = link;
		controlLink = link.getLink();
		textValue = link.getText();
		items = link.getItems();
		selectedItemIndex = link.getSelectionIndex();
		
		//Determine popup type
		if(textValue != null){
			
			if(items !=null && items.length > 0)
				popupType = PopupControlAdapter.TYPE_TEXT_DROPDOWN;
			else
				popupType = PopupControlAdapter.TYPE_TEXT;
		}else
			popupType = PopupControlAdapter.TYPE_DROPDOWN;
		
		openPopupControl();
	}

	private void openPopupControl() {
		
		if(popup != null){
			popup.close();
		}
			
		popup = new PopupControl(controlLink);
		popup.open();
		
		if(popup != null){
			popup.getShell().addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					popup = null;
				}
			});
		}
		
		//After popup is opened, if type is DROPDOWN the list must be
		//automatically expanded for improving user experience.
		if(popupType == PopupControlAdapter.TYPE_DROPDOWN)
			popup.comboCtrl.setListVisible(true);
	}

	public void closePopupControl() {
		if (popup != null) {
			popup.close();
		}
	}

	public class PopupControl extends PopupDialog {
		/*
		 * The listener we install on the popup and related controls to
		 * determine when to close the popup. Some events (move, resize, close,
		 * deactivate) trigger closure as soon as they are received, simply
		 * because one of the registered listeners received them. Other events
		 * depend on additional circumstances.
		 */
		public final class PopupCloserListener implements Listener {

			public void handleEvent(final Event e) {
				//
				// In this method we check when the focus is about to be lost.
				// If the focus switching between controls on the popup we jsu ignore those FocusOut events.
				// Insted when the focus is leaving the Popup shell, we then close the popup/
				
				//System.out.println("event closed");
	
				if (e.type == SWT.Traverse) {
					if (e.detail == SWT.TRAVERSE_ESCAPE)
						updateElemFlag = false;
					else
						return;
				}
				
				/*
				 * Ignore this event if it's only happening because focus
				 * is moving between the popup shells, their controls, or a
				 * scrollbar. Do this in an async since the focus is not
				 * actually switched when this event is received.
				 */
				else if (e.type == SWT.FocusOut && popup.isValid()) {

					// Workaround a problem on X and Mac, whereby at
					// this point, the focused control is not known.
					// This may happen, for example, when resizing
					// the popup shell on the Mac.
					Shell activeShell = e.display.getActiveShell();

					if (activeShell == selPopupLink.getShell()) {
						close();
						
					//Force close when focus is transferred in a window outside the application
					//since popups that contain CCompo, do not close
					}else if(activeShell != getShell()){
						close();
					}
					
					return;
				}

				// For all other events, merely getting them dictates closure.
				close();
			}

			// Install the listeners for events that need to be monitored for
			// popup closure.
			void installListeners() {
				// Listeners on this popup's controls (text, combo and scroll bar)
				for (Control c : popupContent.getChildren()) {
					c.addListener(SWT.FocusOut, this);
				}
				
				// Listeners on this popup's shell
				for (int evt : eventsPopup) {
					getShell().addListener(evt, this);
				}
				
				// Listeners on the target control's shell
				Shell controlShell = controlLink.getShell();
				for (int evt : eventsMainShell) {
					controlShell.addListener(evt, this);
				}
			}

			// Remove installed listeners
			void removeListeners() {
				if (isValid()) {
					// Listeners on this popup's controls (text, combo and scroll bar)
					for (Control c : popupContent.getChildren()) {
						c.removeListener(SWT.FocusOut, this);
					}
					
					// Listeners on this popup's shell
					for (int evt : eventsPopup) {
						getShell().removeListener(evt, this);
					}					
				}

				if (controlLink != null && !controlLink.isDisposed()) {
					// Listeners on the target control's shell
					Shell controlShell = controlLink.getShell();
					for (int evt : eventsMainShell) {
						controlShell.removeListener(evt, this);
					}
				}
			}
		}

		private static final int POPUP_OFFSET = 5;
		private static final int POPUP_SIZE_THRESHOLD = 20;

		private Composite popupContent;
		
		private Text textCtrl;
		private CComboPlus comboCtrl;
		
		int eventsPopup[] = new int[] {SWT.Traverse, SWT.Close}; // SWT.Deactivate };, 
		int eventsMainShell[] = new int[] {SWT.Move, SWT.Resize, SWT.Iconify, SWT.FOCUSED};

		private PopupCloserListener popupCloser;
		private boolean updateElemFlag = true;

		public PopupControl(Control swtControl) {
			super(swtControl.getShell(), SWT.ON_TOP | SWT.NO_TRIM, true, false, false, false, false, null, null);
		}

		public int open() {
			int value = super.open();
			if (popupCloser == null) {
				popupCloser = new PopupCloserListener();
			}
			popupCloser.installListeners();

			return value;
		}
		
		private String textVal;
		private int itemSelIndex = -1;
		
		public void getElementData() {
			if (!textCtrl.isDisposed()) {
				
				textVal = textCtrl.getText();
				
				if(comboCtrl != null) {
					itemSelIndex = comboCtrl.getSelectionIndex();
				}

				if (!updateElemFlag) {
					tooltip.setVisible(false);
				}
			}
		}
		
		public void updateElement() {
			if (updateElemFlag) {
				
				if(popupType == PopupControlAdapter.TYPE_TEXT_DROPDOWN){
					selPopupLink.setText(textVal);
					selPopupLink.setSelection(itemSelIndex);
				}else if(popupType == PopupControlAdapter.TYPE_TEXT){
					selPopupLink.setText(textVal);
				}else if(popupType == PopupControlAdapter.TYPE_DROPDOWN){
					selPopupLink.setSelection(itemSelIndex);
				}
			}
		}

		public boolean close() {
			getElementData();
			popupCloser.removeListeners();
			boolean ret = super.close();
			updateElement();
			updateElemFlag = false;
			return ret;
		}

		/**
		 * A valid PopupControl is not null and has not been disposed.
		 * @return
		 */
		public boolean isValid() {
			return textCtrl != null && !textCtrl.isDisposed();
		}

		public void updateToolTipMessage(String value, String message) {
	        if (message == null) {
	        	tooltip.setVisible(false);
	            if(textCtrl!=null && !textCtrl.isDisposed())
	            	textCtrl.setForeground(null);
	            return ;
	        }
	        
	        if(textCtrl.isDisposed())
	        	return;
	       
	        Point p = textCtrl.toDisplay(0,0);
	      
	        int menuBarHeight = (OS.indexOf("mac") >= 0 ? -10 : 15);
	        p.x -= tooltip.getParent().getBounds().x;
	        p.y -= (menuBarHeight + tooltip.getParent().getBounds().y);

	        textCtrl.setForeground(tooltip.getDisplay().getSystemColor(SWT.COLOR_RED));    
	        
	        String cur_message = tooltip.getMessage();
			
			if (cur_message != null && !cur_message.equals(message)) {
				tooltip.setVisible(false);
			}

			Point location = tooltip.getDisplay().map(tooltip.getParent(), null, p.x, p.y);
			
			tooltip.setLocation(location);
			tooltip.setMessage(message);
	        tooltip.setVisible(true);
	    }
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.PopupDialog#getForeground()
		 */
		protected Color getForeground() {
			return JFaceResources.getColorRegistry().get(JFacePreferences.CONTENT_ASSIST_FOREGROUND_COLOR);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.PopupDialog#getBackground()
		 */
		protected Color getBackground() {
			return selPopupLink.getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.PopupDialog.adjustBounds()
		 */
		protected void adjustBounds() {

			// Get our control's location in display coordinates.
			Point location = controlLink.getDisplay().map(controlLink.getParent(), null, controlLink.getLocation());

			if(controlLink instanceof Shell)
				location = new Point(0,0);
			
			if(position != null){
				location.x += position.x;
				location.y += position.y;
			}
			
			//Point controlSize = controlLink.getSize();
			
			int initialX = location.x + POPUP_OFFSET;
			int initialY = location.y - 2;// - POPUP_OFFSET;

			Point popupSize = getShell().getSize();
			
			popupSize.x = popupContent.getBounds().width + POPUP_SIZE_THRESHOLD;
			
			int diff = POPUP_OFFSET;
			
			getShell().setBounds(initialX - diff, initialY, popupSize.x, popupSize.y);
		}

		/*
		 * Creates the content area for the proposal popup. This creates a table
		 * and places it inside the composite. The table will contain a list of
		 * all the proposals.
		 * 
		 * @param parent The parent composite to contain the dialog area; must
		 * not be <code>null</code>.
		 */
		protected final Control createDialogArea(final Composite parent) {

			int columnsNum = (popupType == PopupControlAdapter.TYPE_TEXT_DROPDOWN ? 2 : 1);
			int style = SWT.NONE;
			
			popupContent = new Composite(parent, style);
			GridLayout gl_content = new GridLayout(columnsNum, false);
			gl_content.marginWidth = 0;
			gl_content.marginHeight = 0;
			gl_content.horizontalSpacing = 0;
			popupContent.setLayout(gl_content);
			
			textCtrl = new Text(popupContent, SWT.BORDER);
			textCtrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			if(popupType == PopupControlAdapter.TYPE_DROPDOWN){
				GridData d = (GridData) textCtrl.getLayoutData();
				d.exclude = true;
				textCtrl.setVisible(false);
				textCtrl.setLayoutData(d);
			}else{
				textCtrl.setText(textValue);
				textCtrl.selectAll();
				textCtrl.setFocus();
			}

			//Add the CComboPlus control
			if (popupType != PopupControlAdapter.TYPE_TEXT) {
				comboCtrl = new CComboPlus(popupContent, SWT.READ_ONLY | SWT.FLAT | SWT.BORDER);
				comboCtrl.setItems(selPopupLink.getItems());
				
				GridData gl_combo;
				
				if(popupType == PopupControlAdapter.TYPE_TEXT_DROPDOWN)
					gl_combo = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
				else
					gl_combo = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
				comboCtrl.setLayoutData(gl_combo);
				comboCtrl.setVisibleItemCount(5);
				comboCtrl.select(selectedItemIndex);
			}

			textCtrl.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					Text text = (Text) e.widget;
					String value = text.getText();

					updateElemFlag = false;

					int columns = value.length();

					if (columns > 0) {
						GC gc = new GC(text);
						FontMetrics fm = gc.getFontMetrics();
						int width = columns * fm.getAverageCharWidth();
						//int height = fm.getHeight();
						gc.dispose();

						int new_width;
						
						if (comboCtrl != null) {
							new_width = width + POPUP_SIZE_THRESHOLD + text.getBorderWidth() * 2 + comboCtrl.getBounds().width;
						} else {
							new_width = width + POPUP_SIZE_THRESHOLD + text.getBorderWidth() * 2;
						}

						getShell().setBounds(getShell().getBounds().x, getShell().getBounds().y, new_width, getShell().getBounds().height);
					}

					String message = null;		
					//boolean valid = true;
					
					//Validate text input using the defined validators if any
					if(selPopupLink.getValidators() != null){
					
						for(IPopupLinkInputValidator v : selPopupLink.getValidators()){
							
							if (popupType == PopupControlAdapter.TYPE_TEXT_DROPDOWN)
								message = v.validateInput(value, selPopupLink.getItems(), comboCtrl.getSelectionIndex());
							else
								message = v.validateInput(value, null, -1);
							
							if(message != null){
								//message = v.getErrorMessage();
								break;
							}
						}
					}
					
					updateToolTipMessage(value, message);
					
					if (message == null) {
						updateElemFlag = true;
						if(isValid())
							textCtrl.setForeground(colorBlu);																	
					}
				}
			});

			textCtrl.addListener(SWT.Traverse, new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (event.detail == SWT.TRAVERSE_RETURN) {
						close();
					}
				}
			});

			if (comboCtrl != null) {
				
				comboCtrl.addListener(SWT.Traverse, new Listener() {
					@Override
					public void handleEvent(Event event) {
						if (event.detail == SWT.TRAVERSE_RETURN) {
							close();
						}
					}
				});

				comboCtrl.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void widgetSelected(SelectionEvent e) {
						CComboPlus c = (CComboPlus) e.widget;

						if(popupType == PopupControlAdapter.TYPE_DROPDOWN){
							updateElemFlag = true;
						}else if(popupType == PopupControlAdapter.TYPE_TEXT_DROPDOWN){
							
							String message = null;		
							//boolean valid = true;
							String value = textCtrl.getText();
							
							//Validate text input using the defined validators if any
							if(selPopupLink.getValidators() != null){
							
								for(IPopupLinkInputValidator v : selPopupLink.getValidators()){
									
									if (popupType == PopupControlAdapter.TYPE_TEXT_DROPDOWN)
										message = v.validateInput(value, selPopupLink.getItems(), comboCtrl.getSelectionIndex());
									else
										message = v.validateInput(value, null, -1);
									
									if(message != null){
										//message = v.getErrorMessage();
										break;
									}
								}
							}
							
							updateToolTipMessage(value, message);
							
							if (message == null) {
								updateElemFlag = true;
								if(isValid())
									textCtrl.setForeground(colorBlu);																	
							}
						}

						if(popupType == PopupControlAdapter.TYPE_DROPDOWN && !c.getListVisible()){
							close();
						}
					}
				});
			}

			popupContent.pack();

			return popupContent;
		}
	}
}
