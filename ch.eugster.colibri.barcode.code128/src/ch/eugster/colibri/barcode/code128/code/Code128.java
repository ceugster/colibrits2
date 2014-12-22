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
import java.util.Locale;

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
import ch.eugster.colibri.provider.service.ProviderQuery;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Code128 extends AbstractBarcode
{
	public static final String PROVIDER = "code128";

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ProviderQuery, ProviderQuery> providerQueryTracker;

	private Code128Type code128Type = null;
	/**
	 * 
	 */
	protected Code128(final String code128)
	{
		super(code128);
		this.code128Type = Code128Type.getType(code128.length());
	}

	public long getDate()
	{
		final int day = Integer.parseInt(this.getCode().substring(0, 2));
		final int month = Integer.parseInt(this.getCode().substring(2, 4));
		int year = Integer.parseInt(this.getCode().substring(4, 6));

		final Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
		final int centuryYear = calendar.get(Calendar.YEAR);
		final GregorianCalendar now = new GregorianCalendar();
		year = centuryYear - now.get(Calendar.YEAR) + year;

		return new GregorianCalendar(year, month, day).getTimeInMillis();
	}

	public String getDescription()
	{
		Code128Type type = Code128.Code128Type.getType(this.getCode().length());
		String strType =  type == null ? "unbekannt" : type.type();
		return "Code128 Barcode Typ " + strType + " (" + this.getCode().length()
				+ " Stellen)";
	}

	public String getName()
	{
		Code128Type type = Code128.Code128Type.getType(this.getCode().length());
		String strType =  type == null ? "?" : type.type();
		return "Code128" + strType;
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
		String productCode = null;
		try
		{
			productCode = Long.valueOf(this.getProductCode()).toString();
		}
		catch (final NumberFormatException e)
		{
			// do nothing
		}

		Barcode barcode = null;
		
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
				barcode = Ean13.verify(productCode);
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
		String taxCode = "0";
		String code = this.getCode().substring(8, 9);
		if (code.equals("0"))
		{
			taxCode = "1";
		}
		else if (code.equals("5"))
		{
			taxCode = "2";
		}
		else if (code.equals("7"))
		{
			taxCode = "3";
		}
		else if (code.equals("8"))
		{
			taxCode = "0";
		}
		return taxCode;
	}

	@Override
	public Type getType()
	{
		if (this.getProductCode().startsWith(Barcode.PREFIX_ORDER))
		{
			return Barcode.Type.ORDER;
		}
		return this.getProductBarcode().getType();
	}

	@Override
	public void updatePosition(final Position position)
	{
		if (!this.code128Type.equals(Code128Type.CODE128_C))
		{
			this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().context, PersistenceService.class, null);
			this.persistenceServiceTracker.open();

			this.providerQueryTracker = new ServiceTracker<ProviderQuery, ProviderQuery>(Activator.getDefault().context, ProviderQuery.class, null);
			this.providerQueryTracker.open();

			position.setSearchValue(this.getCode());
			Product product = position.getProduct();
			if (product == null)
			{
				product = Product.newInstance(position);
				product.setCode(this.getProductCode());
				position.setProduct(product);
			}
			position.setPrice(this.getOrdinalPrice());

			final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
			if ((persistenceService != null))
			{
				final ProviderQuery providerQuery = (ProviderQuery) this.providerQueryTracker.getService();
				if (providerQuery!= null)
				{
					final ExternalProductGroupQuery epgQuery = (ExternalProductGroupQuery) persistenceService.getCacheService().getQuery(
							ExternalProductGroup.class);
					final ExternalProductGroup externalProductGroup = epgQuery.selectByProviderAndCode(providerQuery.getProviderId(),
							this.getProductGroupCode());
					if (externalProductGroup != null)
					{
						product.setExternalProductGroup(externalProductGroup);
					}

					final TaxCodeMappingQuery tcmQuery = (TaxCodeMappingQuery) persistenceService.getCacheService().getQuery(TaxCodeMapping.class);
					final TaxCodeMapping taxCodeMapping = tcmQuery.selectTaxCodeMappingByProviderAndCode(providerQuery.getProviderId(), this.getTaxCode());
					if (taxCodeMapping != null)
					{
						position.setCurrentTax(taxCodeMapping.getTax().getCurrentTax());
					}
				}
			}

			this.persistenceServiceTracker.close();
		}
	}

	public static Code128 verify(final String code)
	{
		Code128 code128 = null;
		if (code != null)
		{
			if (Code128.Code128Type.getType(code.length()) instanceof Code128Type)
			{
				try
				{
					new BigInteger(code);
					code128 = new Code128(code);
					if (code128.getType().equals(Barcode.Type.ORDER))
					{
						code128 = null;
					}
				}
				catch (final NumberFormatException e)
				{
				}
			}
		}
		return code128;
	}

	public enum Code128Type
	{
		CODE128_A, CODE128_B, CODE128_C;
		
		public static Code128Type getType(int length)
		{
			for (Code128Type type : Code128Type.values())
			{
				if (type.length() == length)
				{
					return type;
				}
			}
			return null;
		}
		
		public int length()
		{
			switch (this)
			{
			case CODE128_A:
			{
				return 34;
			}
			case CODE128_B:
			{
				return 38;
			}
			case CODE128_C:
			{
				return 16;
			}
			default:
			{
				throw new RuntimeException("Invalid code128 selected");
			}
			}
		}
		
		public String type()
		{
			switch (this)
			{
			case CODE128_A:
			{
				return "A";
			}
			case CODE128_B:
			{
				return "B";
			}
			case CODE128_C:
			{
				return "C";
			}
			default:
			{
				throw new RuntimeException("Invalid code128 selected");
			}
			}
		}
		
	}
}
