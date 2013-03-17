package lia.analysis.queryparser;

import lia.common.LiaTestCase;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.Analyzer;

public class AnalysisParalysisTest extends LiaTestCase {
  public void testAnalyzer() throws Exception {
    Analyzer analyzer = new StandardAnalyzer();
    String queryString = "category:/philosophy/eastern";

    Query query = QueryParser.parse(queryString,
                                    "contents",
                                    analyzer);
    assertEquals("path got split, yikes!",
                 "category:\"philosophy eastern\"",
                 query.toString("contents"));

    PerFieldAnalyzerWrapper perFieldAnalyzer =
                            new PerFieldAnalyzerWrapper(analyzer);
    perFieldAnalyzer.addAnalyzer("category",
                                       new WhitespaceAnalyzer());
    query = QueryParser.parse(queryString, "contents",
                             perFieldAnalyzer);
    assertEquals("leave category field alone",
                 "category:/philosophy/eastern",
                 query.toString("contents"));
  }
}
