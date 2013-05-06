package ch.eugster.colibri.report.export.model;

public enum Section
{
	HEADER, POSITION, PAYMENT, SUMMARY, TAX, INTERNAL, RESTITUTION, PAYED_INVOICES, REVERSED_RECEIPT, SETTLEMENT, CASH_CHECK, FOOTER;

	@Override
	public String toString()
	{
		switch (this)
		{
			case HEADER:
			{
				return "Kopf";
			}
			case POSITION:
			{
				return "Warengruppen";
			}
			case PAYMENT:
			{
				return "Zahlungsarten";
			}
			case SUMMARY:
			{
				return "Zusammenfassung";
			}
			case TAX:
			{
				return "Mehrwertsteuer";
			}
			case INTERNAL:
			{
				return "Einlagen/Entnahmen";
			}
			case RESTITUTION:
			{
				return "Rücknahmen";
			}
			case PAYED_INVOICES:
			{
				return "Bezahlte Rechnungen";
			}
			case REVERSED_RECEIPT:
			{
				return "Stornierte Belege";
			}
			case SETTLEMENT:
			{
				return "Abschluss";
			}
			case CASH_CHECK:
			{
				return "Kassensturz";
			}
			case FOOTER:
			{
				return "Fuss";
			}
			default:
			{
				return "";
			}
		}
	}
}
