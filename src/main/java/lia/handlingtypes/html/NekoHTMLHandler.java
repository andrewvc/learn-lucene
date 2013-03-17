package lia.handlingtypes.html;

import lia.handlingtypes.framework.DocumentHandler;
import lia.handlingtypes.framework.DocumentHandlerException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.cyberneko.html.parsers.DOMFragmentParser;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.DocumentFragment;

import org.apache.html.dom.HTMLDocumentImpl;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

public class NekoHTMLHandler implements DocumentHandler {
  private DOMFragmentParser parser = new DOMFragmentParser();

  public Document getDocument(InputStream is)
    throws DocumentHandlerException {

    DocumentFragment node =
      new HTMLDocumentImpl().createDocumentFragment();
    try {
      parser.parse(new InputSource(is), node);
    }
    catch (IOException e) {
      throw new DocumentHandlerException(
        "Cannot parse HTML document: ", e);
    }
    catch (SAXException e) {
      throw new DocumentHandlerException(
        "Cannot parse HTML document: ", e);
    }

    org.apache.lucene.document.Document doc =
      new org.apache.lucene.document.Document();

    StringBuffer sb = new StringBuffer();
    getText(sb, node, "title");
    String title = sb.toString();

    sb.setLength(0);
    getText(sb, node);
    String text = sb.toString();

    if ((title != null) && (!title.equals(""))) {
      doc.add(Field.Text("title", title));
    }
    if ((text != null) && (!text.equals(""))) {
      doc.add(Field.Text("body", text));
    }

    return doc;
  }

  private void getText(StringBuffer sb, Node node) {
    if (node.getNodeType() == Node.TEXT_NODE) {
      sb.append(node.getNodeValue());
    }
    NodeList children = node.getChildNodes();
    if (children != null) {
      int len = children.getLength();
      for (int i = 0; i < len; i++) {
        getText(sb, children.item(i));
      }
    }
  }

  private boolean getText(StringBuffer sb, Node node,
    String element) {
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      if (element.equalsIgnoreCase(node.getNodeName())) {
        getText(sb, node);
        return true;
      }
    }
    NodeList children = node.getChildNodes();
    if (children != null) {
      int len = children.getLength();
      for (int i = 0; i < len; i++) {
        if (getText(sb, children.item(i), element)) {
          return true;
        }
      }
    }
    return false;
  }

  public static void main(String args[]) throws Exception {
    NekoHTMLHandler handler = new NekoHTMLHandler();
    org.apache.lucene.document.Document doc = handler.getDocument(
      new FileInputStream(new File(args[0])));
    System.out.println(doc);
  }
}
