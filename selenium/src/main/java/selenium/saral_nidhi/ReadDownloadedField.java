package selenium.saral_nidhi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadDownloadedField {

	public static void main(String[] args) throws IOException {
		ArrayList<String> a = new ArrayList<String>();

		ReadDownloadedField dd = new ReadDownloadedField();
		dd.getDownloadFields(a);

	}

	public void getDownloadFields(ArrayList<String> a) throws IOException {  
		File file = new File(System.getProperty("user.dir")+"\\downloadTotalForm_Cheque\\NidhiCollection-Wed Aug 10 2022 11_05_57.xlsx");
		//‪
		FileInputStream fis = new FileInputStream(file);
		
		@SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook(fis);

		int sheets = workbook.getNumberOfSheets();
		for (int i = 0; i < sheets; i++) {
			if (workbook.getSheetName(i).equalsIgnoreCase("work_book")) {

				XSSFSheet sheet = workbook.getSheetAt(i);

				Iterator<Row> rows = sheet.iterator(); // sheet is collection of rows
				Row firstrow = rows.next();

				Iterator<Cell> ce = firstrow.cellIterator(); // row is collection of cells
				while (ce.hasNext()) {
					Cell value = ce.next();
				     a.add(value.getStringCellValue());
				}
			}
            
		}
		System.out.println("print fields..");
		for(int i=0;i<a.size();i++) {
			System.out.println(a.get(i));
		}
	}

}
