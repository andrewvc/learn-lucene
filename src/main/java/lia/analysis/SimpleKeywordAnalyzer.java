package lia.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.CharTokenizer;

import java.io.Reader;

/**
 * CharTokenizer limits token width to 255 characters, though.
 * This implementation assumes keywords are 255 in length or less.
 */
public class SimpleKeywordAnalyzer extends Analyzer {

    public TokenStream tokenStream(String fieldName,
                                   Reader reader) {
        return new CharTokenizer(reader) {
            protected boolean isTokenChar(char c) {
                return true;
            }
        };
    }

}
