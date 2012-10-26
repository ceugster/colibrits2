/*
 * Created on 24.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels;

import ch.eugster.colibri.persistence.model.Configurable;

public interface IConfigurable
{
	public Configurable getConfigurable();

	public String getId();

	public String getName();
}
