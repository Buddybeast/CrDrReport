/**
 * 
 */
package in.trident.crdr.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import in.trident.crdr.entities.Daybook;

/**
 * @author Nandhakumar Subramanian
 * 
 * @version 0.0.6c
 * 
 * @since 14 oct 2021
 *
 */
public class CSVUtil {
	public static String type = "text/csv";
	static String[] headers = {};
	
	public static boolean hasCSVFormat(MultipartFile file) {
		if(type.equals(file.getContentType())) {
			return true;
		}
		
		return false;
	}
	
	public static List<Daybook> csvToDaybook(InputStream in){
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader, 
						CSVFormat.DEFAULT.builder().setTrim(true).setSkipHeaderRecord(true).setIgnoreHeaderCase(true).build());
				){
			List<Daybook> daybooks = new ArrayList<>();
			Iterable<CSVRecord> records = csvParser.getRecords();
			for (CSVRecord record : records) {
				Daybook daybook = new Daybook(
						Integer.parseInt(record.get("Sno")),
						record.get("TDate"),
						record.get("Narration"),
						Integer.parseInt(record.get("acccode")),
						Double.parseDouble(record.get("dramt")),
						Double.parseDouble(record.get("cramt")),
						Integer.parseInt(record.get("stkvalue"))
						);
				daybooks.add(daybook);
			}
			return daybooks;
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to parse CSV file: "+ e.getMessage());
		}
	}
}