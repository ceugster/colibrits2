package ch.eugster.colibri.provider.galileo.wgserve;

public class GalileoProductGroup
{
	public static final String CODE = "code";

	public static final String PROPERTY_TEXT = "WGTEXT";

	public static final String PROPERTY_ACCOUNT = "KONTO";

	public static final String PROPERTY_BOX_1 = "BOX1";

	public static final String PROPERTY_BOX_2 = "BOX2";

	public static final String PROPERTY_DESC_BOX_1 = "DESCBOX1";

	public static final String PROPERTY_DESC_BOX_2 = "DESCBOX2";

	private String code;

	private String text;

	private String account;

	private String box1;

	private String box2;

	private String descBox1;

	private String descBox2;

	public String getAccount()
	{
		return this.account;
	}

	public String getBox1()
	{
		return this.box1;
	}

	public String getBox2()
	{
		return this.box2;
	}

	public String getCode()
	{
		return this.code;
	}

	public String getDescBox1()
	{
		return this.descBox1;
	}

	public String getDescBox2()
	{
		return this.descBox2;
	}

	public String getText()
	{
		return this.text;
	}

	public void setAccount(final String account)
	{
		this.account = account;
	}

	public void setBox1(final String box1)
	{
		this.box1 = box1;
	}

	public void setBox2(final String box2)
	{
		this.box2 = box2;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	public void setDescBox1(final String descBox1)
	{
		this.descBox1 = descBox1;
	}

	public void setDescBox2(final String descBox2)
	{
		this.descBox2 = descBox2;
	}

	public void setText(final String text)
	{
		this.text = text;
	}
}
