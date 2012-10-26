package ch.eugster.colibri.export.service;

import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Settlement;

public interface ExportService 
{
	void add(Receipt receipt);
	
	void update(Receipt receipt);
	
	void settle(Settlement settlement);
}
