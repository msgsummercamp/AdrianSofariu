package com.example.java_pdf_example.services;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

@Service
public class PdfGenerationService {

    @Value("${pdf.output.new-file-path}")
    private String newFilePath;

    @Value("${pdf.new-doc.title}")
    private String newDocTitle;

    @Value("${pdf.new-doc.content}")
    private String newDocContent;

    @Value("${pdf.new-doc.image-url}")
    private String newDocImageUrl;

    @Value("${pdf.output.modified-file-path}")
    private String modifiedFilePath;

    @Value("${pdf.modify-doc.text-to-add}")
    private String textToAdd;

    @Value("${pdf.modify-doc.page-to-add-text}")
    private int pageToAddText;

    @Value("${pdf.modify-doc.text-x}")
    private int textX;

    @Value("${pdf.modify-doc.text-y}")
    private int textY;

    // Generate a new PDF with text and image
    public void generatePdf() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(newFilePath));
            document.open();
            document.add(new Paragraph(newDocTitle));
            document.add(new Paragraph(newDocContent));
            Image image = Image.getInstance(new URI(newDocImageUrl).toURL());
            document.add(image);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
            }
    }

    // Add text to an existing PDF
    public void modifyExistingPdf(String existingFilePath) {
        try {
            File file = new File(existingFilePath);
            if (!file.exists()) {
                throw new RuntimeException("Existing PDF file not found: " + existingFilePath);
            }
            PdfReader pdfReader = new PdfReader(existingFilePath);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(modifiedFilePath));
            PdfContentByte content = pdfStamper.getOverContent(pageToAddText);
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            content.beginText();
            content.setFontAndSize(baseFont, 12);
            content.setTextMatrix(textX, textY);
            content.showText(textToAdd);
            content.endText();
            pdfStamper.close();
            pdfReader.close();
        } catch (Exception e) {
            throw new RuntimeException("Error modifying PDF: " + e.getMessage(), e);
        }
    }
}