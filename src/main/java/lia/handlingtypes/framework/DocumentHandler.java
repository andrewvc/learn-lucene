package lia.handlingtypes.framework;

import org.apache.lucene.document.Document;
import java.io.InputStream;

public interface DocumentHandler {

  /**
   * Creates a Lucene Document from an InputStream.
   * This method can return <code>null</code>.
   *
   * @param is the InputStream to convert to a Document
   * @return a ready-to-index instance of Document
   */
  Document getDocument(InputStream is)
    throws DocumentHandlerException;
}
