package lia.indexing;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;

import junit.framework.TestCase;
import java.io.IOException;

/**
 *
 */
public abstract class BaseIndexingTestCase extends TestCase {
  protected String[] keywords = {"1", "2"};
  protected String[] unindexed = {"Netherlands", "Italy"};
  protected String[] unstored = {"Amsterdam has lots of bridges",
                                 "Venice has lots of canals"};
  protected String[] text = {"Amsterdam", "Venice"};
  protected Directory dir;

  protected void setUp() throws IOException {
    String indexDir =
      System.getProperty("java.io.tmpdir", "tmp") +
      System.getProperty("file.separator") + "index-dir";
    dir = FSDirectory.getDirectory(indexDir, true);
    addDocuments(dir);
  }

  protected void addDocuments(Directory dir)
    throws IOException {
    IndexWriter writer = new IndexWriter(dir, getAnalyzer(),
      true);
    writer.setUseCompoundFile(isCompound());
    for (int i = 0; i < keywords.length; i++) {
      Document doc = new Document();
      doc.add(Field.Keyword("id", keywords[i]));
      doc.add(Field.UnIndexed("country", unindexed[i]));
      doc.add(Field.UnStored("contents", unstored[i]));
      doc.add(Field.Text("city", text[i]));
      writer.addDocument(doc);
    }
    writer.optimize();
    writer.close();
  }

  protected Analyzer getAnalyzer() {
    return new SimpleAnalyzer();
  }

  protected boolean isCompound() {
    return true;
  }

  public void testIndexWriter() throws IOException {
    IndexWriter writer = new IndexWriter(dir, getAnalyzer(),
      false);
    assertEquals(keywords.length, writer.docCount());
    writer.close();
  }

  public void testIndexReader() throws IOException {
    IndexReader reader = IndexReader.open(dir);
    assertEquals(keywords.length, reader.maxDoc());
    assertEquals(keywords.length, reader.numDocs());
    reader.close();
  }
}
