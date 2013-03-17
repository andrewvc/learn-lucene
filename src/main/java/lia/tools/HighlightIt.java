package lia.tools;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

public class HighlightIt {
  private static final String text =
      "Contrary to popular belief, Lorem Ipsum is" +
      " not simply random text. It has roots in a piece of" +
      " classical Latin literature from 45 BC, making it over" +
      " 2000 years old. Richard McClintock, a Latin professor" +
      " at Hampden-Sydney College in Virginia, looked up one" +
      " of the more obscure Latin words, consectetur, from" +
      " a Lorem Ipsum passage, and going through the cites" +
      " of the word in classical literature, discovered the" +
      " undoubtable source. Lorem Ipsum comes from sections" +
      " 1.10.32 and 1.10.33 of \"de Finibus Bonorum et" +
      " Malorum\" (The Extremes of Good and Evil) by Cicero," +
      " written in 45 BC. This book is a treatise on the" +
      " theory of ethics, very popular during the" +
      " Renaissance. The first line of Lorem Ipsum, \"Lorem" +
      " ipsum dolor sit amet..\", comes from a line in" +
      " section 1.10.32.";  // from http://www.lipsum.com/

  public static void main(String[] args) throws IOException {
    String filename = args[0];

    if (filename == null) {
      System.err.println("Usage: HighlightIt <filename>");
      System.exit(-1);
    }

    TermQuery query = new TermQuery(new Term("f", "ipsum"));
    QueryScorer scorer = new QueryScorer(query);
    SimpleHTMLFormatter formatter =
        new SimpleHTMLFormatter("<span class=\"highlight\">",
            "</span>");
    Highlighter highlighter = new Highlighter(formatter, scorer);
    Fragmenter fragmenter = new SimpleFragmenter(50);
    highlighter.setTextFragmenter(fragmenter);

    TokenStream tokenStream = new StandardAnalyzer()
        .tokenStream("f", new StringReader(text));

    String result =
        highlighter.getBestFragments(tokenStream, text, 5, "...");

    FileWriter writer = new FileWriter(filename);
    writer.write("<html>");
    writer.write("<style>\n" +
        ".highlight {\n" +
        " background: yellow;\n" +
        "}\n" +
        "</style>");
    writer.write("<body>");
    writer.write(result);
    writer.write("</body></html>");
    writer.close();
  }
}
