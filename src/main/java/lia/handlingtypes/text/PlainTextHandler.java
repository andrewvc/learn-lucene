package lia.handlingtypes.text;

import lia.handlingtypes.framework.DocumentHandler;
import lia.handlingtypes.framework.DocumentHandlerException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;

public class PlainTextHandler implements DocumentHandler {

  public Document getDocument(InputStream is)
    throws DocumentHandlerException {

    String bodyText = "";

    try {
      BufferedReader br =
        new BufferedReader(new InputStreamReader(is));
      String line = null;
      while ((line = br.readLine()) != null) {
        bodyText += line;
      }
      br.close();
    }
    catch(IOException e) {
      throw new DocumentHandlerException(
        "Cannot read the text document", e);
    }

    if (!bodyText.equals("")) {
      Document doc = new Document();
      doc.add(Field.UnStored("body", bodyText));
      return doc;
    }

    return null;
  }

  public static void main(String[] args) throws Exception {
    PlainTextHandler handler = new PlainTextHandler();
    Document doc = handler.getDocument(
      new FileInputStream(new File(args[0])));
    System.out.println(doc);
  }
}
