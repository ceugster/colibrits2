package ch.eugster.colibri.persistence.service;

import java.util.Calendar;

public interface ReceiptMigrationService
{
	void migrateReceipts(Calendar from, Calendar to);

}
