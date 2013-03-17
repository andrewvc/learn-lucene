package lia.meetlucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Hits;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import java.io.File;
import java.util.Date;

/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
public class Searcher {

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      throw new Exception("Usage: java " + Searcher.class.getName()
        + " <index dir> <query>");
    }

    File indexDir = new File(args[0]);
    String q = args[1];

    if (!indexDir.exists() || !indexDir.isDirectory()) {
      throw new Exception(indexDir +
        " does not exist or is not a directory.");
    }

    search(indexDir, q);
  }

  public static void search(File indexDir, String q)
    throws Exception {
    Directory fsDir = FSDirectory.getDirectory(indexDir, false);
    IndexSearcher is = new IndexSearcher(fsDir);

    Query query = QueryParser.parse(q, "contents",
      new StandardAnalyzer());
    long start = new Date().getTime();
    Hits hits = is.search(query);
    long end = new Date().getTime();

    System.err.println("Found " + hits.length() +
      " document(s) (in " + (end - start) +
      " milliseconds) that matched query '" +
        q + "':");

    for (int i = 0; i < hits.length(); i++) {
      Document doc = hits.doc(i);
      System.out.println(doc.get("filename"));
    }
  }
}
