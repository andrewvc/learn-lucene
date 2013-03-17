package lia.indexing;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.SimpleAnalyzer;

import junit.framework.TestCase;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;

/**
 *
 */
public class CompoundVersusMultiFileIndexTest extends TestCase {

  private Directory cDir;
  private Directory mDir;
  private Collection docs = loadDocuments(5000, 10);

  protected void setUp() throws IOException {
    String indexDir =
      System.getProperty("java.io.tmpdir", "tmp") +
      System.getProperty("file.separator") + "index-dir";

    String cIndexDir = indexDir + "-compound";
    String mIndexDir = indexDir + "-multi";
    (new File(cIndexDir)).delete();
    (new File(mIndexDir)).delete();

    cDir = FSDirectory.getDirectory(cIndexDir, true);
    mDir = FSDirectory.getDirectory(mIndexDir, true);
  }

  public void testTiming() throws IOException {
    long cTiming = timeIndexWriter(cDir, true);
    long mTiming = timeIndexWriter(mDir, false);

    assertTrue(cTiming > mTiming);

    System.out.println("Compound Time : " + (cTiming) + " ms");
    System.out.println("Multi-file Time: " + (mTiming) + " ms");
  }

  private long timeIndexWriter(Directory dir, boolean isCompound)
    throws IOException {
    long start = System.currentTimeMillis();
    addDocuments(dir, isCompound);
    long stop = System.currentTimeMillis();
    return (stop - start);
  }

  private void addDocuments(Directory dir, boolean isCompound)
    throws IOException {
    IndexWriter writer = new IndexWriter(dir, new SimpleAnalyzer(),
      true);
    writer.setUseCompoundFile(isCompound);

    // change to adjust performance of indexing with FSDirectory
    writer.mergeFactor = writer.mergeFactor;
    writer.maxMergeDocs = writer.maxMergeDocs;
    writer.minMergeDocs = writer.minMergeDocs;

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
