import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PreProcessedCorpusReader {
    File file;
    BufferedReader reader;
    String text;


    public PreProcessedCorpusReader(String type) throws IOException {
        // This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
        // You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
        // Close the file when you do not use it any more
        file = new File(Path.ResultHM1 + type);
        reader = new BufferedReader(new FileReader(file),100*1024*1024);
        text = null;
    }


    public Map<String, String> NextDocument() throws IOException {
        // read a line for docNo, put into the map with <"DOCNO", docNo>
        // read another line for the content , put into the map with <"CONTENT", content>
        Map<String, String> m = new HashMap<>();

        if ((text = reader.readLine()) != null) {
            m.put("DOCNO", text);
            text = reader.readLine();
            m.put("CONTENT", text);
            return m;
        }
        return null;
    }

}
	

