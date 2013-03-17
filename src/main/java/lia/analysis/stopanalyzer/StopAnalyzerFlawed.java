package lia.analysis.stopanalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LetterTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;
import java.util.Set;

/**
 * Stop words actually not necessarily removed due to filtering order
 */
public class StopAnalyzerFlawed extends Analyzer {
  private Set stopWords;

  public StopAnalyzerFlawed() {
    stopWords = StopFilter.makeStopSet(StopAnalyzer.ENGLISH_STOP_WORDS);
  }

  public StopAnalyzerFlawed(String[] stopWords) {
    this.stopWords = StopFilter.makeStopSet(stopWords);
  }

  /**
   * Ordering mistake here
   */
  public TokenStream tokenStream(String fieldName, Reader reader) {
    return new LowerCaseFilter(new StopFilter(new LetterTokenizer(reader),
        stopWords));
  }
}
