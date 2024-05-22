package com.eidiko.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.eidiko.exception_handler.ByteArrayIOException;
import com.lowagie.text.*;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eidiko.entity.ProductEntity;
import com.eidiko.excel_entity_repository.ProductRepository;
import com.eidiko.helper.ExcelHelper;

@Service

public class ExcelDataService {
   

	 ProductRepository productRepo;
@Autowired
    public ExcelDataService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public List<ProductEntity> saveProduct(MultipartFile file) {
		List<ProductEntity> product;
		try {
			product = ExcelHelper.convertExcelToListOfProduct(file.getInputStream());
			
			return this.productRepo.saveAll(product);
		
		} catch (IOException | ByteArrayIOException e) {
			return Collections.emptyList();
		}
    }
	
	public List<ProductEntity> getAll() {
			 return  productRepo.findAll();

	}
	public ByteArrayInputStream getActualData() throws IOException, ByteArrayIOException {
		List<ProductEntity>list=productRepo.findAll();
		return ExcelHelper.dataBaseToExcel(list);
	}

	public ByteArrayInputStream createPdf() {
		String title = "Welcome to Pdf file";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document, out);

		HeaderFooter footer= new HeaderFooter(new Phrase("nk"),true);
		footer.setAlignment(Element.ALIGN_CENTER);
		footer.setBorderWidthBottom(0);
		document.setFooter(footer);

		document.open();
		Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
		Paragraph titlePara = new Paragraph(title, titleFont);
		titlePara.setAlignment(Element.ALIGN_CENTER);
		document.add(titlePara);

		List<ProductEntity> dataList = productRepo.findAll();
		Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 18);
		// Assuming dataList is a list of objects where each object represents a row
		// and has appropriate getters for the columns
		if (!dataList.isEmpty()) {
			// Define the number of columns based on the FileEntity fields
			int numColumns = 4;
			Table table = new Table(numColumns);
			// Set table column widths
			table.setWidths(new float[] { 2, 2, 2, 2 }); // Adjust as necessary

			// Add header row
			table.addCell(new Cell(new Phrase("Product ID", tableFont)));
			table.addCell(new Cell(new Phrase("Product Name", tableFont)));
			table.addCell(new Cell(new Phrase("Description", tableFont)));
			table.addCell(new Cell(new Phrase("Price", tableFont)));

			// Add data rows
			for (ProductEntity entity : dataList) {
				table.addCell(new Cell(new Phrase(entity.getProductId())));
				table.addCell(new Cell(new Phrase(entity.getProductName())));
				table.addCell(new Cell(new Phrase(entity.getPriductDes())));
				table.addCell(new Cell(new Phrase(String.valueOf(entity.getProductPrice()))));


			}

			document.add(table);
		}
		Paragraph paragraph = new Paragraph("This end of line ! ", tableFont);
		paragraph.add(new Chunk("Thank You"));
		paragraph.setAlignment(Element.ALIGN_CENTER);
		document.add(paragraph);
		document.close();
		return new ByteArrayInputStream(out.toByteArray());
	}


}
