package com.finops.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PDFUtil {

    public static void main(String[] args) {
        // Path to the PDF file
        String filePath = "C:\\Users\\Bhaumik Patel\\Downloads\\SHIK.PDF";

        try {
            // Load PDF document
            PDDocument document = PDDocument.load(new File(filePath));

            // Create PDFTextStripper object
            PDFTextStripper pdfStripper = new PDFTextStripper();

            // Extract text from PDF
            String text = pdfStripper.getText(document);

            // Print the extracted text
            System.out.println(text);

            // Close the document
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
