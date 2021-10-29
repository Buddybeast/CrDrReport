package in.trident.crdr.services;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;

import in.trident.crdr.entities.AccHead;
import in.trident.crdr.models.TradingPLForm;
import in.trident.crdr.models.TradingPLView;
/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 21 Jun 2021
 * 
 * @version 0.0.5b
 *
 */
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.DaybookRepository;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.5b
 * @since 21 Jun 2021
 *
 */
@Service
public class TradingPLServiceImpl implements TradingPLService {

	@Autowired
	private AccHeadRepo accHeadRepo;

	@Autowired
	private DaybookRepository daybookRepo;

	private static final Logger LOGGER = LoggerFactory.getLogger(TradingPLServiceImpl.class);

	private LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en", "in"))
			.precision(Precision.fixedFraction(2));

	private static int counter = 0;

	@Override
	public List<TradingPLView> createTradingPL(TradingPLForm tradingPLForm, Long userid) {
		Profiler profiler = new Profiler("TradingPLService");
		profiler.setLogger(LOGGER);
		profiler.start("Start TradingPL Service");
		LinkedHashSet<TradingPLView> tradingPLViewSet = new LinkedHashSet<>();
		counter = 0;
		if (tradingPLForm.isReportOrder()) { // Group == true
			
		} else {
			// All - false
			List<AccHead> tradingAccs = accHeadRepo.findTradingPLAccs(userid);
			Collections.sort(tradingAccs);
			tradingAccs.forEach((accs) -> {
				if (accs.getOrderCode() == 3 || accs.getOrderCode() == 4) {
					TradingPLView tplv = new TradingPLView();
					tplv.setParticulars(accs.getAccName());
					String[] arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate(), userid);
					if (arr[1].equals("Cr")) {
						tplv.setDebit("");
						tplv.setCredit(arr[0]);
					} else {
						tplv.setDebit(arr[0]);
						tplv.setCredit("");
					}
					tplv.setLevel(accs.getLevel1());
					if (tradingPLForm.isZeroBal() && ((tplv.getDebit().equals("0.00") && tplv.getCredit().isEmpty())
							|| (tplv.getCredit().equals("0.00") && tplv.getDebit().isEmpty()))) {
						// Intentionally left empty to remove ZeroBal accounts
					} else {
						tradingPLViewSet.add(tplv);
					}

				} else {
					String[] arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate(), userid);
					if (counter < 1) {
						TradingPLView grossProfit = new TradingPLView();
						grossProfit.setParticulars("Gross Profit");
						if (arr[1].equals("Cr")) {
							grossProfit.setDebit("");
							grossProfit.setCredit(arr[0]);
						} else {
							grossProfit.setDebit(arr[0]);
							grossProfit.setCredit("");
						}
						grossProfit.setLevel(0);
						tradingPLViewSet.add(grossProfit);
						TradingPLView total = new TradingPLView();
						total.setParticulars("Total");
						arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate(), userid);
						if (arr[1].equals("Cr")) {
							total.setDebit("");
							total.setCredit(arr[0]);
						} else {
							total.setDebit(arr[0]);
							total.setCredit("");
						}

						total.setLevel(0);
						tradingPLViewSet.add(total);
						TradingPLView grossProfitB = new TradingPLView();
						grossProfitB.setParticulars("Gross Profit Before");
						arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate(), userid);
						if (arr[1].equals("Cr")) {
							grossProfitB.setDebit("");
							grossProfitB.setCredit(arr[0]);
						} else {
							grossProfitB.setDebit(arr[0]);
							grossProfitB.setCredit("");
						}
						grossProfitB.setLevel(0);
						tradingPLViewSet.add(grossProfitB);
						counter++;
					}
					TradingPLView tplv = new TradingPLView();
					tplv.setParticulars(accs.getAccName());
					arr = calculateTradingBalance(accs.getAccCode(), tradingPLForm.getEndDate(), userid);
					if (arr[1].equals("Cr")) {
						tplv.setDebit("");
						tplv.setCredit(arr[0]);
					} else {
						tplv.setDebit(arr[0]);
						tplv.setCredit("");
					}
					tplv.setLevel(accs.getLevel1());
					if (tradingPLForm.isZeroBal() && ((tplv.getDebit().equals("0.00") && tplv.getCredit().isEmpty())
							|| (tplv.getCredit().equals("0.00") && tplv.getDebit().isEmpty()))) {
						// Intentionally left empty to remove ZeroBal accounts
					} else {
						tradingPLViewSet.add(tplv);
					}
				}
				
			});
		}

		TradingPLView netProfit = new TradingPLView();
		netProfit.setParticulars("Net Profit");
		String[] arr = { "", "Dr" };
		if (arr[1].equals("Cr")) {
			netProfit.setDebit("");
			netProfit.setCredit(arr[0]);
		} else {
			netProfit.setDebit(arr[0]);
			netProfit.setCredit("");
		}
		netProfit.setLevel(0);
		tradingPLViewSet.add(netProfit);
		TradingPLView total2 = new TradingPLView();
		total2.setParticulars("Total");
		arr[0] = "";
		arr[1] = "Cr";
		if (arr[1].equals("Cr")) {
			total2.setDebit("");
			total2.setCredit(arr[0]);
		} else {
			total2.setDebit(arr[0]);
			total2.setCredit("");
		}
		total2.setLevel(0);
		tradingPLViewSet.add(total2);
		List<TradingPLView> listTradingPLView = new LinkedList<TradingPLView>(tradingPLViewSet);
		TimeInstrument ti = profiler.stop();
		LOGGER.info("\n" + ti.toString());
		ti.log();
		return listTradingPLView;
	}

	@Override
	public String[] calculateTradingBalance(Integer code, String endDate, Long userid) {
		LOGGER.debug("Start of CalculateTradingBalance method");
		String[] arr = { "", "" }; // 0 => amount, 1=> Cr/Dr
		if (code == 0) {
			String[] array = { "", "Cr" };
			return array;
		}
		// ----------------------------
		Double d1 = accHeadRepo.findCrAmt(code);
		Double d2 = accHeadRepo.findDrAmt(code);

		if (d1 == 0d) {
			// Prev year Bal is Dr
			LOGGER.debug("AccCode" + code + "Opening Debit: " + d2);
			Double tmp = daybookRepo.openBal(code, "2018-04-01", endDate, userid);
			if (tmp == null) {
				// d2 is also zero, so there is no txn & no prev year bal
				// whether d2 is 0 or Somevalue Balance is Dr
				arr[0] = nf.format(Math.abs(d2)).toString();
				arr[1] = "Dr";
				return arr;
			} else if (tmp > 0d || tmp == 0d) {
				// tmp is +ve so Cr
				tmp = d2 - tmp;
				if (tmp > 0d) {
					arr[0] = nf.format(Math.abs(tmp)).toString();
					arr[1] = "Dr";
				} else {
					arr[0] = nf.format(Math.abs(tmp)).toString();
					arr[1] = "Cr";
				}
			} else {
				d2 = d2 - tmp;
				arr[0] = nf.format(Math.abs(d2)).toString();
				arr[1] = "Dr";
			}
		} else { // then Prev year Bal is Cr
			Double tmp = daybookRepo.openBal(code, "2018-04-01", endDate,userid);
			if (tmp == null) {
				arr[0] = nf.format(Math.abs(d1)).toString();
				arr[1] = "Cr";
				return arr;
			} else if (tmp > 0d || tmp == 0d) {
				// tmp is +ve so Cr
				tmp = d1 + tmp;
				arr[0] = nf.format(Math.abs(tmp)).toString();
				arr[1] = "Cr";
			} else {
				// tmp is -ve so Dr
				d1 = d1 + tmp;
				if (d1 > 0d) {
					arr[0] = nf.format(Math.abs(d1)).toString();
					arr[1] = "Cr";
				} else {
					arr[0] = nf.format(Math.abs(d1)).toString();
					arr[1] = "Dr";
				}
			}

		}
		// ----------------------------
		LOGGER.debug("End of CalculateTradingPL method");
		return arr;

	}

}
