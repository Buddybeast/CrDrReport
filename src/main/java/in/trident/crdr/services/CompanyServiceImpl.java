package in.trident.crdr.services;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.trident.crdr.entities.Company;
import in.trident.crdr.entities.CompanySelection;
import in.trident.crdr.repositories.CompSelectionRepo;
import in.trident.crdr.repositories.CompanyRepo;

@Service
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	private CompanyRepo companyRepo;
	
	@Autowired
	private CompSelectionRepo csr;
	
	public void storeSelection(Long cid){
		CompanySelection cs0 = csr.findCompanyByCompanyid(cid);
		if (cs0 == null) {
			Company c = companyRepo.findCompanyById(cid);
			 cs0 = new CompanySelection(c.getCompanyid(), c.getUserid(), c.getCompName(), "20"+c.getCompYear(), c.getFromDate(), c.getToDate(), c.getAddress1()+c.getAddress2()+c.getCity(), c.getcNoofAc(), c.getcNoofEntries(), Calendar.getInstance().getTime(), c.getCloseStk(), c.getOpenCash(), c.getCompType());
			csr.save(cs0); 
		}
		Long sid = cs0.getSid();
		Company c = companyRepo.findCompanyById(cid);
		 cs0 = new CompanySelection(sid, c.getCompanyid(), c.getUserid(), c.getCompName(), "20"+c.getCompYear(), c.getFromDate(), c.getToDate(), c.getAddress1()+c.getAddress2()+c.getCity(), c.getcNoofAc(), c.getcNoofEntries(), Calendar.getInstance().getTime(), c.getCloseStk(), c.getOpenCash(), c.getCompType());
		csr.save(cs0);
	}

	@Override
	public List<String> listCompanies(Long uid) {
		return companyRepo.findUniqueCompanyByUser(uid);
	}

	@Override
	public Map<Long, String> listYears(String cname) {
		Map<Long, String> map = new TreeMap<>();
		List<Company> companies = companyRepo.findCompanyYearsByName(cname);
		companies.forEach(c->{
			map.put(c.getCompanyid(), "20"+c.getCompYear());
		});
		return map;
	}
	
	
	
}
