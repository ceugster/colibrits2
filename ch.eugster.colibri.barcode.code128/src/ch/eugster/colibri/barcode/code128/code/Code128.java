package ch.eugster.colibri.barcode.code128.code;

/*
 * Created on 08.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.barcode.bookcenter.code.BookCenterCode;
import ch.eugster.colibri.barcode.code.AbstractBarcode;
import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.code128.Activator;
import ch.eugster.colibri.barcode.ean13.code.Ean13;
import ch.eugster.colibri.barcode.isbn.code.Isbn;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Product;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.queries.TaxCodeMappingQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderInterface;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Code128 extends AbstractBarcode
{
	public static final String PROVIDER = "code128";

	public static final int CODE128_A_LENGTH = 34;

	public static final int CODE128_B_LENGTH = 38;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ProviderInterface, ProviderInterface> providerInterfaceTracker;

	/**
	 * 
	 */
	protected Code128(final String code128)
	{
		super(code128);
	}

	public long getDate()
	{
		final int day = Integer.parseInt(this.getCode().substring(0, 2));
		final int month = Integer.parseInt(this.getCode().substring(2, 4));
		int year = Integer.parseInt(this.getCode().substring(4, 6));

		final Calendar calendar = Calendar.getInstance();
		final int centuryYear = calendar.get(Calendar.YEAR);
		final GregorianCalendar now = new GregorianCalendar();
		year = centuryYear - now.get(Calendar.YEAR) + year;

		return new GregorianCalendar(year, month, day).getTimeInMillis();
	}

	public String getDescription()
	{
		return "Code128 Barcode Typ " + (this.getCode().length() == Code128.CODE128_A_LENGTH ? "A" : "B") + " (" + this.getCode().length()
				+ " Stellen)";
	}

	public String getName()
	{
		return "Code128" + (this.getCode().length() == Code128.CODE128_A_LENGTH ? "A" : "B");
	}

	public double getNetPrice()
	{
		final int value = Integer.parseInt(this.getCode().substring(16, 22));
		return value / 100d;
	}

	public double getOrdinalPrice()
	{
		final int value = Integer.parseInt(this.getCode().substring(10, 16));
		return value / 100d;
	}

	@Override
	public Barcode getProductBarcode()
	{
		Barcode barcode = null;

		String productCode = null;
		try
		{
			productCode = Long.valueOf(this.getProductCode()).toString();
		}
		catch (final NumberFormatException e)
		{
			// do nothing
		}

		switch (this.getQualifier())
		{
			case 0:
				// Artikelnummer ist eine ISBN ohne Prüfziffer
				barcode = Isbn.verify(productCode);

				if (barcode == null)
				{
					barcode = Ean13.verify(productCode);
				}
				break;
			case 1:
				// Artikelnummer ist eine 7-stellige BZ-Nummer
				barcode = BookCenterCode.verify(productCode);
				break;
			case 2:
				// Artikelnummer ist eine lieferanteneigene Artikelnummer
				// barcode = new Other(articleCode);
				break;
			default:
				/*
				 * Artikelnummerdefinition unbekannt, daher so wie sie
				 * extrahiert wurde zurückgeben.
				 */
				// barcode = new Other(articleCode);
				break;
		}
		return barcode;
	}

	/**
	 * The product code as is (as string). To get the product code as barcode
	 * use getProductBarcode
	 * 
	 * The qualifier (place 9) defines the type of product code.
	 * 
	 * @return
	 */
	@Override
	public String getProductCode()
	{
		String productCode = this.getCode().substring(25);
		try
		{
			productCode = Long.valueOf(productCode).toString();
		}
		catch (final NumberFormatException e)
		{
			// do nothing
		}
		return productCode;
	}

	public String getProductGroupCode()
	{
		return this.getCode().substring(22, 25);
	}

	/**
	 * The qualifier represents the type of product code that is in use
	 * 
	 * 0 isbn (product code is '978' + isbn + checksum (1 char) 1 code of swiss
	 * book center (Schweizer Buchzentrum) (7 chars filled with preceedings '0')
	 * 2 ean (country code (3 chars) + code + checksum (1 char)
	 * 
	 * @return
	 */
	public int getQualifier()
	{
		return Integer.parseInt(this.getCode().substring(9, 10));
	}

	/**
	 * The supplier code represents the supplier. used codes:
	 * 
	 * 00 swiss book center (Schweizer Buchzentrum)
	 * 
	 * @return
	 */
	public int getSupplier()
	{
		return Integer.parseInt(this.getCode().substring(6, 8));
	}

	/**
	 * The qualifier represents the type of product code that is in use
	 * 
	 * 0 reduced tax (2.4%) 5 full tax (7.6%) 7 value added tax on input 8 no
	 * tax at all
	 * 
	 * @return
	 */
	public String getTaxCode()
	{
		return this.getCode().substring(8, 9);
	}

	@Override
	public void updatePosition(final Position position)
	{
		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().context, PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		this.providerInterfaceTracker = new ServiceTracker<ProviderInterface, ProviderInterface>(Activator.getDefault().context, ProviderInterface.class, null);
		this.providerInterfaceTracker.open();

		position.setSearchValue(this.getCode());
		Product product = position.getProduct();
		if (product == null)
		{
			product = Product.newInstance(position);
			position.setProduct(product);
		}
		product.setCode(this.getProductCode());

		position.setPrice(this.getOrdinalPrice());

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if ((persistenceService != null))
		{
			final ProviderInterface providerInterface = (ProviderInterface) this.providerInterfaceTracker.getService();
			if (providerInterface != null)
			{
				final ExternalProductGroupQuery epgQuery = (ExternalProductGroupQuery) persistenceService.getCacheService().getQuery(
						ExternalProductGroup.class);
				final ExternalProductGroup externalProductGroup = epgQuery.selectByProviderAndCode(providerInterface.getProviderId(),
						this.getProductGroupCode());
				product.setExternalProductGroup(externalProductGroup);

				final TaxCodeMappingQuery tcmQuery = (TaxCodeMappingQuery) persistenceService.getCacheService().getQuery(TaxCodeMapping.class);
				final TaxCodeMapping taxCodeMapping = tcmQuery.selectTaxCodeMappingByProviderAndCode(providerInterface.getProviderId(), this.getTaxCode());
				if (taxCodeMapping != null)
				{
					position.setCurrentTax(taxCodeMapping.getTax().getCurrentTax());
				}
			}
		}

		this.persistenceServiceTracker.close();
	}

	public static Code128 verify(final String code)
	{
		if (code == null)
		{
			return null;
		}

		if ((code.length() != Code128.CODE128_A_LENGTH) && (code.length() != Code128.CODE128_B_LENGTH))
		{
			return null;
		}

		try
		{
			new BigInteger(code);
		}
		catch (final NumberFormatException e)
		{
			return null;
		}

		return new Code128(code);
	}

}
