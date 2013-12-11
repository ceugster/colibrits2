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
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.provider.galileo.Activator;

public class FindArticleServerCom4j extends AbstractServer implements IFindArticleServer
{
	@Override
	public IStatus findAndRead(final Barcode barcode, final Position position)
	{
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.PROVIDER_QUERY.topic());
		String msg = null;

		log(LogService.LOG_INFO, "Verbindung checken in Server.");
		if (isConnect())
		{
			log(LogService.LOG_INFO, "Verbindung öffnen.");
			if (this.open())
			{
				if (barcode.getType().equals(Barcode.Type.CUSTOMER))
				{
					log(LogService.LOG_INFO, "Kundennummer setzen.");
					final int customerId = this.getCustomerId(barcode);
					log(LogService.LOG_INFO, "Kunden suchen.");
					try
					{
						if (this.getGalserve().do_getkunde(customerId))
						{
							log(LogService.LOG_INFO, "Kunden gefunden; aktualisieren.");
							this.updatePosition(barcode, position);
						}
						else
						{
							msg = "Kundennummer \"" + customerId + "\" nicht vorhanden.";
							log(LogService.LOG_INFO, msg);
						}
					}
					catch(Exception e)
					{
						log(LogService.LOG_ERROR, e.getLocalizedMessage());
					}
					position.setSearchValue(null);
				}
				else
				{
					log(LogService.LOG_INFO, "Position mit Barcodedaten aktualisieren.");
					barcode.updatePosition(position);
					if (position.isEbook())
					{
						log(LogService.LOG_INFO, "Position mit EBookdaten aktualisieren.");
						this.updatePosition(barcode, position);
					}
					else
					{
						log(LogService.LOG_INFO, "Artikel suchen.");
						try
						{
							this.getGalserve().do_NSearch(barcode.getProductCode());
							if (((Boolean) this.getGalserve().gefunden()).booleanValue())
							{
								log(LogService.LOG_INFO, "Artikel gefunden; Position aktualisieren.");
								this.updatePosition(barcode, position);
								log(LogService.LOG_INFO, "Defaultwerte des Barcodes übernehmen, falls notwendig.");
								barcode.updatePosition(position);
							}
							else
							{
								msg = barcode.getType().getArticle() + " " + barcode.getType() + " mit dem Code "
										+ barcode.getProductCode() + " konnte nicht gefunden werden.\nBitte erfassen Sie die zusätzlich benötigten Daten manuell.";
								status = new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(), msg);
								log(LogService.LOG_INFO, (msg));
							}
						}
						catch(Exception e)
						{
							msg = barcode.getType().getArticle() + " " + barcode.getType() + " mit dem Code "
									+ barcode.getProductCode() + " konnte nicht gefunden werden.\nBitte erfassen Sie die zusätzlich benötigten Daten manuell. " + e.getLocalizedMessage();
							status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), msg);
							log(LogService.LOG_ERROR, (msg));
						}
					}
				}

				this.close();
			}
			else
			{
				if (barcode.getType().equals(Barcode.Type.CUSTOMER))
				{
					log(LogService.LOG_INFO, "Suchwert auf null setzen.");
					position.setSearchValue(null);
				}
				status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.PROVIDER_QUERY.topic(), new Exception("Die Verbindung zu " + this.getConfiguration().getName() + " kann nicht hergestellt werden."));
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

//	@Override
//	public IStatus checkConnection(String path)
//	{
//		IStatus status = null;
//		if (isConnect())
//		{
//			Igdserve galserve = ClassFactory.creategdserve();
//			if (galserve.do_NOpen(path))
//			{
//				galserve.do_NClose();
//				status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(),
//						"Die Verbindung zur Warenbewirtschaftung Galileo konnte erfolgreich hergestellt werden.");
//			}
//			else
//			{
//				status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
//						Topic.PROVIDER_QUERY.topic(), new Exception("Die Verbindung zu " + this.getConfiguration().getName() + " kann nicht hergestellt werden."));
//			}
//			galserve.dispose();
//		}
//		else
//		{
//			status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(),
//					"Die Verbindung zur Warenbewirtschaftung ist deaktiviert.");
//		}
//		return status;
//	}
}
