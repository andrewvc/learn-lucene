package lia.indexing;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import junit.framework.TestCase;
import java.io.IOException;

/**
 *
 */
public class LockTest extends TestCase {

  private Directory dir;

  protected void setUp() throws IOException {
    String indexDir =
      System.getProperty("java.io.tmpdir", "tmp") +
      System.getProperty("file.separator") + "index";
    dir = FSDirectory.getDirectory(indexDir, true);
  }

  public void testWriteLock() throws IOException {
    IndexWriter writer1 = null;
    IndexWriter writer2 = null;
    boolean gotException = false;

    try {
      writer1 = new IndexWriter(dir, new SimpleAnalyzer(), true);
      writer2 = new IndexWriter(dir, new SimpleAnalyzer(), true);

      fail("We should never reach this point");
    }
    catch (IOException e) {
      gotException = true;
      // we are expecting IOException
      // uncomment to see the full exception stack trace
      // e.printStackTrace();
    }
    finally {
      writer1.close();
      assertNull(writer2);
      assertTrue(gotException);
    }
  }

  public void testCommitLock() throws IOException {
    IndexReader reader1 = null;
    IndexReader reader2 = null;

    try {
      IndexWriter writer = new IndexWriter(dir,
        new SimpleAnalyzer(), true);
      writer.close();
      reader1 = IndexReader.open(dir);
      reader2 = IndexReader.open(dir);
    }
    finally {
      reader1.close();
      reader2.close();
    }
  }
}
