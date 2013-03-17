package lia.handlingtypes.msdoc;

import lia.handlingtypes.framework.DocumentHandler;
import lia.handlingtypes.framework.DocumentHandlerException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.textmining.text.extraction.WordExtractor;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

public class TextMiningWordDocHandler implements DocumentHandler {

  public Document getDocument(InputStream is)
    throws DocumentHandlerException {

    String bodyText = null;
    try {
      bodyText = new WordExtractor().extractText(is);
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
    TextMiningWordDocHandler handler =
      new TextMiningWordDocHandler();
    Document doc = handler.getDocument(
      new FileInputStream(new File(args[0])));
    System.out.println(doc);
  }
}
