package ch.eugster.colibri.persistence.model;

public enum ChargeType 
{
	NONE, AMOUNT, PERCENTUAL;
	
	public String label()
	{
		switch (this)
		{
		case NONE:
		{
			return "Ohne Belastung";
		}
		case AMOUNT:
		{
			return "Fixbetrag";
		}
		case PERCENTUAL:
		{
			return "Prozentualer Anteil";
		}
		default:
		{
			throw new RuntimeException("Invalid charge type");
		}
		}
	}
	
	public double calculateCharge(PaymentType paymentType, double amount)
	{
		switch (this)
		{
		case NONE:
		{
			return 0D;
		}
		case AMOUNT:
		{
			return paymentType.getFixCharge();
		}
		case PERCENTUAL:
		{
			double roundFactor = paymentType.getCurrency().getRoundFactor();
			double charge = Math.round(amount * paymentType.getPercentualCharge() / roundFactor) * roundFactor;
			return Math.abs(charge) < Math.abs(paymentType.getFixCharge()) ? paymentType.getFixCharge() : charge;
		}
		default:
		{
			throw new RuntimeException("Invalid charge type");
		}
		}
	}

}
