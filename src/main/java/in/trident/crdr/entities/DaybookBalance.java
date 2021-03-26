package in.trident.crdr.entities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DaybookBalance {

	private String date;
	private Double crTot;
	private Double drTot;
	private Double closeBl;
	private String dayOfWeek;
	
public DaybookBalance(String date, Double crTot, Double drTot, Double closeBal, String dayOfWeek ) {
	this.date = date;
	this.crTot = crTot;
	this.drTot = drTot;
	this.closeBl = closeBal;
	this.dayOfWeek = dayOfWeek;
}
	
public DaybookBalance() {
}

	public DaybookBalance findBalance(ArrayList<Daybook> daybookList) {
		for (Daybook d : daybookList) {
			crTot += d.getCrAmt();
			drTot += d.getDrAmt();
			date = d.getDate();
		}
		closeBl = crTot - drTot;
		DaybookBalance dBal = new DaybookBalance();
		dBal.setCloseBl(closeBl);
		dBal.setCrTot(crTot);
		dBal.setDrTot(drTot);
		dBal.setDate(date);
		Date date1 = new Date();
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
		}
		DateFormat df = new SimpleDateFormat("EEEE");
		dayOfWeek = df.format(date1);
		dBal.setDayOfWeek(dayOfWeek);
		return dBal;
	}
	
	@Override
	public String toString() {
		return "DaybookBalance [date=" + date + ", crTot=" + crTot + ", drTot=" + drTot + ", closeBl=" + closeBl + ", Day of week: "+ dayOfWeek +"]";
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Double getCrTot() {
		return crTot;
	}
	public void setCrTot(Double crTot) {
		this.crTot = crTot;
	}
	public Double getDrTot() {
		return drTot;
	}
	public void setDrTot(Double drTot) {
		this.drTot = drTot;
	}
	public Double getCloseBl() {
		return closeBl;
	}
	public void setCloseBl(Double closeBl) {
		this.closeBl = closeBl;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	
}
