package lia.indexing;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 */
public class IndexTuningDemo {

  public static void main(String[] args) throws Exception {
    int docsInIndex  = Integer.parseInt(args[0]);

    // create an index called 'index-dir' in a temp directory
    Directory dir = FSDirectory.getDirectory(
      System.getProperty("java.io.tmpdir", "tmp") +
      System.getProperty("file.separator") + "index-dir", true);
    Analyzer analyzer = new SimpleAnalyzer();
    IndexWriter writer = new IndexWriter(dir, analyzer, true);

    // set variables that affect speed of indexing
    writer.mergeFactor   = Integer.parseInt(args[1]);
    writer.maxMergeDocs  = Integer.parseInt(args[2]);
    writer.minMergeDocs  = Integer.parseInt(args[3]);
    writer.infoStream    = System.out;

    System.out.println("Merge factor:   " + writer.mergeFactor);
    System.out.println("Max merge docs: " + writer.maxMergeDocs);
    System.out.println("Min merge docs: " + writer.minMergeDocs);

    long start = System.currentTimeMillis();
    for (int i = 0; i < docsInIndex; i++) {
      Document doc = new Document();
      doc.add(Field.Text("fieldname", "Bibamus"));
      writer.addDocument(doc);
    }
    writer.close();
    long stop = System.currentTimeMillis();
    System.out.println("Time: " + (stop - start) + " ms");
  }
}
