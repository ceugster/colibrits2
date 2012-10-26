/*
 * Created on 11.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info.position;

import javax.swing.DefaultListSelectionModel;

public class PositionListSelectionModel extends DefaultListSelectionModel
{
	public static final long serialVersionUID = 0l;
	
	private PositionListModel model;
	
	public PositionListSelectionModel(PositionListModel model)
	{
		this.model = model;
	}
	
	public PositionListModel getListModel()
	{
		return this.model;
	}
}
