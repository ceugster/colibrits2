package ch.eugster.colibri.persistence.model;

public enum PrintMode
{
	NORMAL, DOUBLE_WIDTH, DOUBLE_HEIGHT, DOUBLE_WIDTH_AND_HEIGHT;
	
	public String label()
	{
		switch (this)
		{
		case NORMAL:
		{
			return "Normal";
		}
		case DOUBLE_WIDTH:
		{
			return "Doppelte Breite";
		}
		case DOUBLE_HEIGHT:
		{
			return "Doppelte Höhe";
		}
		case DOUBLE_WIDTH_AND_HEIGHT:
		{
			return "Doppelte Breite und Höhe";
		}
		default:
		{
			return NORMAL.label();
		}
		}
	}

	public int mode()
	{
		switch (this)
		{
		case NORMAL:
		{
			return 0;
		}
		case DOUBLE_WIDTH:
		{
			return 1;
		}
		case DOUBLE_HEIGHT:
		{
			return 2;
		}
		case DOUBLE_WIDTH_AND_HEIGHT:
		{
			return 3;
		}
		default:
		{
			return NORMAL.mode();
		}
		}
	}
}
