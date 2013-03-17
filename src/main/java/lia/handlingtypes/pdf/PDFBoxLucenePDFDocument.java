package lia.handlingtypes.pdf;

import org.pdfbox.searchengine.lucene.LucenePDFDocument;
import org.apache.lucene.document.Document;
import java.io.File;

public class PDFBoxLucenePDFDocument {
    public static void main(String[] args) throws Exception {
        Document doc = LucenePDFDocument.getDocument(new File(args[0]));
        System.out.println(doc);
    }
}
