package lia.handlingtypes.pdf;

import org.pdfbox.searchengine.lucene.IndexFiles;
import java.io.File;

public class PDFBoxIndexFiles {
    public static void main(String[] args) throws Exception {
        IndexFiles indexFiles = new IndexFiles();
        indexFiles.index(new File(args[0]), true, args[1]);
    }
}
