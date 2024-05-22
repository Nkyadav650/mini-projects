package com.eidiko.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eidiko.exception_handler.ByteArrayIOException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eidiko.entity.ProductEntity;
import com.eidiko.helper.ExcelHelper;
import com.eidiko.service.ExcelDataService;

@RestController
@CrossOrigin("*")
public class ProductController {


	private final ExcelDataService edService;
	public ProductController(ExcelDataService edService){
		this.edService=edService;
	}
	 String message = "Message";
	String status = "Status";

	@PostMapping("/product/upload")
	public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file ) throws Exception{
		List<ProductEntity> product=edService.saveProduct(file);
				Map<String,Object> response=new HashMap<>();
				if(ExcelHelper.checkExcelFormat(file)) {
					response.put("Message", "file is avialable");
				if(product!=null&& !product.isEmpty()) {
				response.put(message, "Data saved Successfully");
				response.put(status, "true");
				return new ResponseEntity< >(response, HttpStatus.CREATED);
			}else {
				response.put(message, "Data saving Failed");
				response.put(status, "false");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			}else {
				response.put(message, "file nOt found");
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

			}
	}
	@GetMapping("/getProduct")
	public ResponseEntity< Map<String,Object>> getAll() throws Exception{
		List<ProductEntity>list= edService.getAll();
		
		Map<String,Object> response=new HashMap<>();
		if(list==null) {
		response.put(message, "Data saving Failed!!");
		response.put(status, HttpStatus.BAD_REQUEST);
		response.put("Result", "failed");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}else {
		response.put(status,HttpStatus.OK);
		response.put("Result", "success");
		response.put("Data", list);
		return new ResponseEntity<>(response, HttpStatus.OK);
		
	}
	}
	
	@RequestMapping("/excel")
	public ResponseEntity<Resource> excelDownload() throws IOException, ByteArrayIOException {
		String fileName="Product.xlsx";
		ByteArrayInputStream actualData=edService.getActualData();
		InputStreamResource file=new InputStreamResource(actualData);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,"attachemet; fileName="+fileName)
					.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
					.body(file);
    }

	@RequestMapping("/pdf")
	public ResponseEntity<Resource> pdfDownload() throws Exception {
		String fileName = "Product.pdf";

		ByteArrayInputStream actualData=edService.createPdf();
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION,"attachment; fileName="+fileName)
			.contentType(MediaType.APPLICATION_PDF)
			.body(new InputStreamResource(actualData));
	}
 }