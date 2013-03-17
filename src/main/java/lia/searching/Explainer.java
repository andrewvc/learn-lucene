package lia.searching;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;

public class Explainer {
  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("Usage: Explainer <index dir> <query>");
      System.exit(1);
    }

    String indexDir = args[0];
    String queryExpression = args[1];

    FSDirectory directory =
        FSDirectory.getDirectory(indexDir, false);

    Query query = QueryParser.parse(queryExpression,
        "contents", new SimpleAnalyzer());

    System.out.println("Query: " + queryExpression);

    IndexSearcher searcher = new IndexSearcher(directory);
    Hits hits = searcher.search(query);

    for (int i = 0; i < hits.length(); i++) {
      Explanation explanation =
                              searcher.explain(query, hits.id(i));

      System.out.println("----------");
      Document doc = hits.doc(i);
      System.out.println(doc.get("title"));
      System.out.println(explanation.toString());
    }
  }
}
