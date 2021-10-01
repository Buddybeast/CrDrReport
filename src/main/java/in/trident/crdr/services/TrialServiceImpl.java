package in.trident.crdr.services;

import java.util.Collections;
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
import in.trident.crdr.models.TrialForm;
import in.trident.crdr.models.TrialView;
import in.trident.crdr.repositories.AccHeadRepo;
import in.trident.crdr.repositories.DaybookRepository;

/**
 * 
 * @author Nandhakumar Subramanian
 * 
 * @since 18 Jun 2021
 * 
 * @version 0.0.5b
 *
 */

@Service
public class TrialServiceImpl implements TrialBalService {

	@Autowired
	private AccHeadRepo accHeadRepo;

	@Autowired
	private DaybookRepository daybookRepo;

	private static final Logger LOGGER = LoggerFactory.getLogger(TrialServiceImpl.class);

	private LocalizedNumberFormatter nf = NumberFormatter.withLocale(new Locale("en", "in"))
			.precision(Precision.fixedFraction(2));

	@Override
	public List<TrialView> createTrialBal(TrialForm trialform) {
		Profiler profiler = new Profiler("TrialBalService");
		profiler.setLogger(LOGGER);
		profiler.start("CreateTrialBal");
		LOGGER.debug("Start of CreateTrialBal method");
		List<TrialView> listTrialview = new LinkedList<TrialView>();
		List<AccHead> list = accHeadRepo.findAllAccHead();
		Collections.sort(list);
		if (trialform.isReportOrder()) {
			List<Integer> accCodes = trialform.getAccCode();
			accCodes.forEach((acc) -> {
				TrialView tv = new TrialView();
				tv.setAccName(accHeadRepo.findAccNameByAccCode(acc));
				String[] arr = calculateTrialBalance(acc, trialform.getEndDate());
				if (arr[1].equals("Cr")) {
					tv.setDebit("");
					tv.setCredit(arr[0]);
				} else {
					tv.setDebit(arr[0]);
					tv.setCredit("");
				}
				tv.setLevel(accHeadRepo.findLevelByAccCode(acc));
				if (trialform.isZeroBal() && ((tv.getDebit().equals("0.00") && tv.getCredit().isEmpty())
						|| (tv.getCredit().equals("0.00") && tv.getDebit().isEmpty()))) {
					// Intentionally left empty to remove ZeroBal accounts
				} else {
					listTrialview.add(tv);
				}
			});
		} else {
			list.forEach((acc) -> {
				TrialView tv = new TrialView();
				tv.setAccName(acc.getAccName());
				String[] arr = calculateTrialBalance(acc.getAccCode(), trialform.getEndDate());
				if (arr[1].equals("Cr")) {
					tv.setDebit("");
					tv.setCredit(arr[0]);
				} else {
					tv.setDebit(arr[0]);
					tv.setCredit("");
				}
				tv.setLevel(acc.getLevel1());
				if (trialform.isZeroBal() && ((tv.getDebit().equals("0.00") && tv.getCredit().isEmpty())
						|| (tv.getCredit().equals("0.00") && tv.getDebit().isEmpty()))) {
					// Intentionally left empty to remove ZeroBal accounts
				} else {
					listTrialview.add(tv);
				}
			});
		}
		LOGGER.debug("End of CreateTrialBal method");
		TimeInstrument ti = profiler.stop();
		LOGGER.info("\n" + ti.toString());
		ti.log();
		return listTrialview;
	}

	@Override
	public String[] calculateTrialBalance(Integer code, String endDate) {
		LOGGER.debug("Start of CalculateTrialBalance method");
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
			Double tmp = daybookRepo.openBal(code, "2018-04-01", endDate);
			// Null check daybook repos return value
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
			Double tmp = daybookRepo.openBal(code, "2018-04-01", endDate);
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
		LOGGER.debug("End of CalculateTrialBalance method");
		return arr;
	}

}
