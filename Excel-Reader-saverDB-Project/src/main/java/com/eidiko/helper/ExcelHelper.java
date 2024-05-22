package com.eidiko.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.eidiko.exception_handler.ByteArrayIOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.eidiko.entity.ProductEntity;

public class ExcelHelper {

	private ExcelHelper(){
	}
	 static String[] headers= {"productId","productName","priductDes","productPrice"};
	
	// for check file is excel type of not
	public static boolean checkExcelFormat(MultipartFile file) {
		Optional<String> contentType = Optional.ofNullable(file)
				.map(MultipartFile::getContentType);

        return contentType.isPresent() &&
				contentType.get()
						.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}
	
	//Convert Excel to list of product

	public static List<ProductEntity> convertExcelToListOfProduct(InputStream inputStream) throws ByteArrayIOException {
		List <ProductEntity>list=new ArrayList<>();
		try {
			XSSFWorkbook workBook=new XSSFWorkbook(inputStream);
			XSSFSheet sheet=workBook.getSheet("data");
			int rowNumber=0;

            for (Row row : sheet) {
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cells = row.iterator();
                int cid = 1;
                ProductEntity p = new ProductEntity();
                while (cells.hasNext()) {
                    Cell cell = cells.next();

                    switch (cid) {
                        case 1: {
                            p.setProductId((long) cell.getNumericCellValue());
                            break;
                        }
                        case 2: {
                            p.setProductName(cell.getStringCellValue());
                            break;
                        }
                        case 3: {
                            p.setPriductDes(cell.getStringCellValue());
                            break;
                        }
                        case 4: {
                            p.setProductPrice((double) cell.getNumericCellValue());
                            break;
                        }
                        default:
                            break;
                    }
                    cid++;
                }
                list.add(p);
            }

		}catch(Exception e) {
			throw new ByteArrayIOException(e.getMessage());
		}
		return list;
	}
	
	 static String sheet1="productList";

	public static ByteArrayInputStream dataBaseToExcel(List<ProductEntity> list) throws IOException, ByteArrayIOException {
        try (Workbook workBook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheetName = workBook.createSheet(sheet1);
            Row headerRow = sheetName.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            AtomicInteger rowIndex = new AtomicInteger(1);
            list.forEach(pe -> {
                Row dataRow = sheetName.createRow(rowIndex.getAndIncrement());

                dataRow.createCell(0).setCellValue(pe.getProductId());
                dataRow.createCell(1).setCellValue(pe.getProductName());
                dataRow.createCell(2).setCellValue(pe.getPriductDes());
                dataRow.createCell(3).setCellValue(pe.getProductPrice());
            });
            workBook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
	}

}
