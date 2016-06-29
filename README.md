# SWTCustomComponents
A collaborative effort to design and deliver modern and appealing SWT-based components.

This is an Eclipse project. Pull it and add local build path references to 

- swt.jar
- org.eclipse.core.commands.jar
- org.eclipse.equinox.common.jar
- org.eclipse.jface.jar

The project consists of the following components:

- **custom.jface.widgets.ContentPrososalAdapterPlus**

	A modified version of the [ContentProposalAdapter](http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjface%2Ffieldassist%2FContentProposalAdapter.html)
	class that provides a way of having a user-defined Table in terms of appearance and behavior for hosting the proposals. This is achieved by setting as table provider an object that implements
the [custom.jface.widgets.IProposalTableProvider](https://github.com/SWTCustomComponents/SWTCustomComponents/blob/master/src/custom/jface/widgets/IProposalTableProvider.java) interface.

- **custom.swt.widgets.CComboPlus**
	
	A modified version of the [CCombo](http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fcustom%2FCCombo.html) 
class with new methods for enabling the item selection on mouse hover _CComboPlus:setMouseHoverSelection_ and also, for changing the foreground color of 
	the editable text _CComboPlus:setTextForeground_
	
- **custom.swt.widgets.LateralTabFolder**
	
	Instances of this class provide an interactive and customizable user interface object of a lateral tab folder with custom painted buttons for navigating through the tabs.
	
	The content that can be hosted in a tab should be of type SWT [Composite](http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fwidgets%2FComposite.html)
	and a new tab can be added at any time by calling one of the variations of method _LateralTabFolder:addTab_
	
- **custom.swt.widgets.PopupLink**
	
	Instances of this class are selectable user interface objects that have the look of a link and allow the user to click and modify their content in a popup dialog. 
	
	The popup dialog may consist by an SWT [Text](http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fwidgets%2FText.html) 
	field and/or a [CComboPlus](https://github.com/SWTCustomComponents/SWTCustomComponents/blob/master/src/custom/swt/widgets/CComboPlus.java) depending on whether a value was set 
	for these internal controls using the methods _PopupLink:setTextFieldValue_ and _PopupLink:setDropdownOptions_.
	Moreover, it allows to set a list of validators that implemenent the [custom.swt.widgets.IPopupLinkInputValidator](https://github.com/SWTCustomComponents/SWTCustomComponents/blob/master/src/custom/swt/widgets/IPopupLinkInputValidator.java)
	interface for validating the user input upon edit.

	This custom component can replace the traditional SWT text and/or combo dropdown controls in an input form, providing also the possibility to have one single widget for a pair of these controls.

	
	

