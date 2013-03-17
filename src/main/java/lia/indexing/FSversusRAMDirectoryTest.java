package lia.indexing;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.SimpleAnalyzer;

import junit.framework.TestCase;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 */
public class FSversusRAMDirectoryTest extends TestCase {

  private Directory fsDir;
  private Directory ramDir;
  private Collection docs = loadDocuments(3000, 5);

  protected void setUp() throws Exception {
    String fsIndexDir =
      System.getProperty("java.io.tmpdir", "tmp") +
      System.getProperty("file.separator") + "fs-index";

    ramDir = new RAMDirectory();
    fsDir = FSDirectory.getDirectory(fsIndexDir, true);
  }

  public void testTiming() throws IOException {
    long ramTiming = timeIndexWriter(ramDir);
    long fsTiming = timeIndexWriter(fsDir);

    assertTrue(fsTiming > ramTiming);

    System.out.println("RAMDirectory Time: " + (ramTiming) + " ms");
    System.out.println("FSDirectory Time : " + (fsTiming) + " ms");
  }

  private long timeIndexWriter(Directory dir) throws IOException {
    long start = System.currentTimeMillis();
    addDocuments(dir);
    long stop = System.currentTimeMillis();
    return (stop - start);
  }

  private void addDocuments(Directory dir) throws IOException {
    IndexWriter writer = new IndexWriter(dir, new SimpleAnalyzer(),
      true);

    /**
    // change to adjust performance of indexing with FSDirectory
    writer.mergeFactor = writer.mergeFactor;
    writer.maxMergeDocs = writer.maxMergeDocs;
    writer.minMergeDocs = writer.minMergeDocs;
    */

    for (Iterator iter = docs.iterator(); iter.hasNext();) {
      Document doc = new Document();
      String word = (String) iter.next();
      doc.add(Field.Keyword("keyword", word));
      doc.add(Field.UnIndexed("unindexed", word));
      doc.add(Field.UnStored("unstored", word));
      doc.add(Field.Text("text", word));
      writer.addDocument(doc);
    }
    writer.optimize();
    writer.close();
  }

  private Collection loadDocuments(int numDocs, int wordsPerDoc) {
    Collection docs = new ArrayList(numDocs);
    for (int i = 0; i < numDocs; i++) {
      StringBuffer doc = new StringBuffer(wordsPerDoc);
      for (int j = 0; j < wordsPerDoc; j++) {
        doc.append("Bibamus ");
      }
      docs.add(doc.toString());
    }
    return docs;
  }
}
