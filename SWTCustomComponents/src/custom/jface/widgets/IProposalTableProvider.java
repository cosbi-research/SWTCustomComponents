package swtcc.custom.jface.widgets;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * Classes that implement the IProposalTableProvider interface can be used in combination
 * with the {@link ContentProposalAdapterPlus} class for having in a user-defined table 
 * in matters of both appearance and behavior for the display of the proposals
 * <p>
 * The methods defined in this interface are called automatically by the {@link ContentProposalAdapterPlus} class
 * in which the IProposalTableProvider instance was set.
 * </p>
 * 
 */
public interface IProposalTableProvider {
	
	/**
	 * @return the background color to be used in the proposals popup. If <code>null</code> the default
	 * 			background color of {@link ContentProposalAdapterPlus} is used 
	 */
	public Color getBackground();
	
	/**
	 * @return the text color to be used for the proposals. If <code>null</code> the default
	 * 			foreground color of {@link ContentProposalAdapterPlus} is used
	 */
	public Color getForeground();
	
	/**
	 * This method is called by the {@link ContentProposalAdapterPlus} instance when creating
	 * the dialog area of the proposals popup for retrieving the table inside which the proposals
	 * will be displayed
	 * 
	 * <p>
	 * <b>IMPORTANT:</b> This method should create and return a completely new {@link Table} instance using as parent
	 * and style the given arguments. Given style is not allowed to be modified but the user can optionally append the 
	 * <b>SWT.FULL_SELECTION</b> flag on table creation.
	 * </p>
	 * 
	 * @param parent - the widget that must be used as parent of the table
	 * @param style - the style of the table to create i.e SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.VIRTUAL (if applicable)
	 * @return a new {@link Table} instance that will host the proposals (cannot be <code>null</code>)
	 */
	public Table getProposalsTable(Composite parent, final int style);
}
