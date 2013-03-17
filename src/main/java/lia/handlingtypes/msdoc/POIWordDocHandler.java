package lia.handlingtypes.msdoc;

import lia.handlingtypes.framework.DocumentHandler;
import lia.handlingtypes.framework.DocumentHandlerException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.apache.poi.hdf.extractor.WordDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.PrintWriter;

public class POIWordDocHandler implements DocumentHandler {

  public Document getDocument(InputStream is)
    throws DocumentHandlerException {

    String bodyText = null;

    try {
      WordDocument wd = new WordDocument(is);
      StringWriter docTextWriter = new StringWriter();
      wd.writeAllText(new PrintWriter(docTextWriter));
      docTextWriter.close();
      bodyText = docTextWriter.toString();
    }
    catch (Exception e) {
      throw new DocumentHandlerException(
        "Cannot extract text from a Word document", e);
    }

    if ((bodyText != null) && (bodyText.trim().length() > 0)) {
      Document doc = new Document();
      doc.add(Field.UnStored("body", bodyText));
      return doc;
    }
    return null;
  }

  public static void main(String[] args) throws Exception {
    POIWordDocHandler handler = new POIWordDocHandler();
    Document doc = handler.getDocument(
      new FileInputStream(new File(args[0])));
    System.out.println(doc);
  }
}
