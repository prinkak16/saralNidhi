package selenium.saral_nidhi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ActionReletedDataDriven {
	public static void main(String[] args) throws IOException {

		ArrayList<String> a = new ArrayList<String>();

		ActionReletedDataDriven dd = new ActionReletedDataDriven();
		//ArrayList<String> al = dd.getData("Login", a);
		ArrayList<String> al = dd.getData("chequeMode", a);
		System.out.println("After calling method :" + al.get(1));

	}

	ArrayList<String> getData(String testCaseName, ArrayList<String> a) throws IOException {
		//File file = new File("C://Users//PC//Desktop//actionData.xlsx");  
		File file = new File(System.getProperty("user.dir")+"\\src\\main\\java\\resources\\actionData.xlsx");
		//‪
		FileInputStream fis = new FileInputStream(file);
		
		@SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook(fis);

		int sheets = workbook.getNumberOfSheets();
		for (int i = 0; i < sheets; i++) {
			if (workbook.getSheetName(i).equalsIgnoreCase("Sheet1")) {

				XSSFSheet sheet = workbook.getSheetAt(i);

				Iterator<Row> rows = sheet.iterator(); // sheet is collection of rows
				Row firstrow = rows.next();

				Iterator<Cell> ce = firstrow.cellIterator(); // row is collection of cells
				int column = 0;
				int k = 0;
				while (ce.hasNext()) {
					Cell value = ce.next();
					// System.out.println("cell :"+value.getStringCellValue());

					if (value.getStringCellValue().equalsIgnoreCase("TestCases")) {
						column = k;
					}
					k++;
				}

				System.out.println(column);

				while (rows.hasNext()) {
					Row row_value = rows.next();

					if (row_value.getCell(column).getStringCellValue().equalsIgnoreCase(testCaseName)) {
						System.out.println("row_value :" + row_value.getCell(column).getStringCellValue());
						Iterator<Cell> cv = row_value.cellIterator();
						//int j = 0;
						while (cv.hasNext()) {

							Cell cell_content_type = cv.next();
							if (cell_content_type.getCellType() == CellType.STRING) {
								System.out.println(cell_content_type.getStringCellValue());
								a.add(cell_content_type.getStringCellValue());
							} else {
								System.out.println(cell_content_type.getNumericCellValue());

								// convert incoming double type cell value to String type using poi methods
								String str = NumberToTextConverter.toText(cell_content_type.getNumericCellValue());

								System.out.println(str);

								a.add(str);
							}
							// System.out.println(cv.next());

						}
					}

				}
			}

		}
		return a;
	}

}
