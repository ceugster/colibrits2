package ch.eugster.colibri.persistence.service;

import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;

public interface SettlementService
{
	long countReceipts(Settlement settlement);

	Settlement settle(Settlement settlement, State state);

	Salespoint updateSettlement(Salespoint salespoint);

	public enum State
	{
		PROVISIONAL, DEFINITIVE;
	}
}
