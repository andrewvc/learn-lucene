package lia.extsearch.hitcollector;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BookLinkCollector extends HitCollector {
  private IndexSearcher searcher;
  private HashMap documents = new HashMap();

  public BookLinkCollector(IndexSearcher searcher) {
    this.searcher = searcher;
  }

  public void collect(int id, float score) {
    try {
      Document doc = searcher.doc(id);
      documents.put(doc.get("url"), doc.get("title"));
      System.out.println(doc.get("title") + ":" + score);
    } catch (IOException e) {
      // ignore
    }
  }

  public Map getLinks() {
    return Collections.unmodifiableMap(documents);
  }
}
