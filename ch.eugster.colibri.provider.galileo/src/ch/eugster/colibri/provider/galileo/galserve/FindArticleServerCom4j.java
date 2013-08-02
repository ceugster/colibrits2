/*
 * Created on 18.07.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.galserve;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.provider.galileo.Activator;

public class FindArticleServerCom4j extends AbstractServer implements IFindArticleServer
{
	@Override
	public IStatus findAndRead(final Barcode barcode, final Position position)
	{
		IStatus status = Status.OK_STATUS;
		String msg = null;

		if (isConnect())
		{
			if (this.open())
			{
				if (barcode.getType().equals(Barcode.Type.CUSTOMER))
				{
					final int customerId = this.getCustomerId(barcode);
					if (this.getGalserve().do_getkunde(customerId))
					{
						this.updatePosition(barcode, position);
					}
					else
					{
						msg = "Kundennummer \"" + customerId + "\" nicht vorhanden.";
						log(LogService.LOG_INFO, msg);
					}
				}
				else
				{
					barcode.updatePosition(position);
					if (position.isEbook())
					{
						this.updatePosition(barcode, position);
					}
					else
					{
						this.getGalserve().do_NSearch(barcode.getProductCode());
						if (((Boolean) this.getGalserve().gefunden()).booleanValue())
						{
							this.updatePosition(barcode, position);
							barcode.updatePosition(position);
						}
						else
						{
							msg = barcode.getType().getArticle() + " " + barcode.getType() + " mit dem Code "
									+ barcode.getProductCode() + " konnte nicht gefunden werden.\nBitte erfassen Sie die zus�tzlich ben�tigten Daten manuell.";
							status = new Status(IStatus.CANCEL, Activator.PLUGIN_ID, msg);
							log(LogService.LOG_INFO, (msg));
						}
					}
				}

				this.close();
			}
		}
		return status;
	}

//	@Override
//	public Customer selectCustomer(final Barcode barcode) throws Exception
//	{
//		Customer customer = null;
//		if (isConnect())
//		{
//			if (this.open())
//			{
//				final int customerId = this.getCustomerId(barcode);
//				log(LogService.LOG_INFO, "Suche Kunden " + customerId + ".");
//				if (this.getGalserve().do_getkunde(customerId))
//				{
//					customer = this.getCustomer(customerId);
//				}
//				log(LogService.LOG_INFO, (customer == null ? "Kunden " + customerId + " nicht gefunden." : "Kunden gefunden."));
//		
//				this.close();
//			}
//		}
//		return customer;
//	}

	@Override
	public IStatus checkConnection(String path)
	{
		IStatus status = null;
		if (isConnect())
		{
			Igdserve galserve = ClassFactory.creategdserve();
			if (galserve.do_NOpen(path))
			{
				galserve.do_NClose();
				status = new Status(IStatus.OK, Activator.PLUGIN_ID,
						"Die Verbindung zur Warenbewirtschaftung Galileo konnte erfolgreich hergestellt werden.");
			}
			else
			{
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Das Herstellen der Verbindung zur Warenbewirtschaftung Galileo ist fehlgeschlagen.");
			}
			galserve.dispose();
		}
		else
		{
			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"Die Verbindung zur Warenbewirtschaftung ist deaktiviert.");
		}
		return status;
	}
}
