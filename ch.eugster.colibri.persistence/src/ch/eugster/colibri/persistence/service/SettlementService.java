package ch.eugster.colibri.persistence.service;

import java.util.Collection;

import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementReceipt;

public interface SettlementService
{
	long countReceipts(Settlement settlement);
	
	Collection<SettlementReceipt> getReversedReceipts(Settlement settlement);

	Settlement settle(Settlement settlement, State state);

	Salespoint updateSettlement(Salespoint salespoint);

	public enum State
	{
		PROVISIONAL, DEFINITIVE;
	}
}
