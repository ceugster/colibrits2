package ch.eugster.colibri.barcode.code;

import ch.eugster.colibri.persistence.model.Position;

public abstract class AbstractBarcode implements Barcode
{
	protected final String CODE = "code";

	private String code;

	protected AbstractBarcode(final String code)
	{
		this.code = code;
	}
	
	public boolean isEbook()
	{
		return false;
	}

	@Override
	public boolean equals(final Object other)
	{
		if (other.getClass().equals(this.getClass()))
		{
			final Barcode barcode = (Barcode) other;
			return barcode.getCode().equals(this.getCode());
		}
		return false;
	}

	public String getCode()
	{
		return this.code;
	}

	@Override
	public String getDetail()
	{
		return this.code;
	}

	public Barcode getProductBarcode()
	{
		return this;
	}

	public String getProductCode()
	{
		return this.getCode();
	}

	@Override
	public Type getType()
	{
		return Type.ARTICLE;
	}

	@Override
	public int length()
	{
		return this.code == null ? 0 : this.code.length();
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	@Override
	public void updatePosition(final Position position)
	{
		position.setSearchValue(this.getCode());
		position.setEbook(this.isEbook());
	}
}
