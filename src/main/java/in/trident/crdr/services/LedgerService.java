package in.trident.crdr.services;

import java.util.List;

import in.trident.crdr.models.Dailybooks;
import in.trident.crdr.models.LedgerForm;
import in.trident.crdr.models.LedgerView;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 0.0.5
 *
 */
public interface LedgerService {

	List<LedgerView> createLedgerViewList(LedgerForm ledgerForm);
	Dailybooks createDailybooks(String date);
	LedgerView createLedgerView(Integer code, LedgerForm ledgerForm);
	
}
