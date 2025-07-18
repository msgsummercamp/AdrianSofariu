package com.example.java_pdf_example;

import com.example.java_pdf_example.services.PdfGenerationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class JavaPdfExampleApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(JavaPdfExampleApplication.class, args);

		PdfGenerationService pdfGenerationService = context.getBean(PdfGenerationService.class);

		pdfGenerationService.generatePdf();

		pdfGenerationService.modifyExistingPdf("./generated-pdfs/new_document.pdf");
	}

}
