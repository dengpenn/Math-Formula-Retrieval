import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class MyIndexReader {
    //you are suggested to write very efficient code here, otherwise, your memory cannot hold our corpus...
    String type;
    String parentPath;
    String Token;

    File dicFile;
    File postFile;
    File docIdFile;

    int[][] postingList;

    Map<Integer, String> docIdMap;

    public MyIndexReader(String type) throws IOException {
        //read the index files you generated in task 1
        //remember to close them when you finish using them
        //use appropriate structure to store your index
        this.type = type;
        initial();

    }

    public void initial() {


        if (this.type.contains("text")) {
             parentPath= Path.IndexTextDir;

        } else if (this.type.contains("web")) {
             parentPath= Path.IndexWebDir;

        } else {
            System.exit(0);
        }
        File parent = new File(parentPath);
        String[] list = parent.list();
        for (String s : list) {
            if (s.contains("dic")) {
                dicFile = new File( parentPath + s);
            } else if (s.contains("post")) {
                postFile = new File( parentPath + s);
            } else if (s.contains("docId")) {
                docIdFile = new File( parentPath + s);
            }
        }

        docIdMap = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(docIdFile));
            String text;
            while ((text = reader.readLine()) != null) {
                String[] s = text.split(",");
                docIdMap.put(Integer.valueOf(s[0]), s[1]);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //get the non-negative integer dociId for the requested docNo
    //If the requested docno does not exist in the index, return -1
    public int GetDocid(String docno) {
        if (docno != null && !docno.isEmpty()) {
            return docno.hashCode() & 0xfffffff;
        }
        return -1;
    }

    // Retrieve the docno for the integer docid
    public String GetDocno(int docid) {
        //read from docIdFile
        return docIdMap.get(docid);
    }

    /**
     * Get the posting list for the requested token.
     * <p>
     * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
     * <p>
     * [docid]		[freq]
     * 1			3
     * 5			7
     * 9			1
     * 13			9
     * <p>
     * ...
     * <p>
     * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
     * <p>
     * For example:
     * array[0][0] records the docid of the first document the token appears.
     * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
     * ...
     * <p>
     * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest.
     *
     * @param token
     * @return
     */
    public int[][] GetPostingList(String token) {
        if (postingList != null && token == Token) {
            return postingList;
        }
        Token = token;
//        boolean thisToken = false;

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader eventReader = factory.createXMLStreamReader(new FileReader(postFile));

            while (eventReader.hasNext()) {
                int event = eventReader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String eName = eventReader.getLocalName();
                    if (eName.equals("pointer")) {
                        String pointer = eventReader.getAttributeValue(0);

                        if (Integer.valueOf(pointer) == (token.hashCode() & 0xfffffff)) {
                            String list = eventReader.getElementText();
                            String[] l = list.trim().split("\\n");
                            LinkedList<int[]> pList = new LinkedList<>();
                            for (int i = 0; i < l.length; i++) {
                                if (l[i].isEmpty()) {
                                    continue;
                                }
                                String[] ss = l[i].split(",");
                                int[] post = {Integer.valueOf(ss[0]), Integer.valueOf(ss[1])};
                                pList.add(post);
                            }
                            postingList = new int[pList.size()][2];
                            for (int i = 0; i < postingList.length; i++) {
                                postingList[i] = pList.get(i);
                            }
                            break;

                        }

                    }

                }

            }
            eventReader.close();
            return postingList;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Return the number of documents that contains the token.
    public int GetDocFreq(String token)  {
        if (postingList != null && token == Token) {
            return postingList.length;
        } else {
            GetPostingList(token);
            return postingList.length;
        }

    }

    // Return the total number of times the token appears in the collection.
    public long GetCollectionFreq(String token) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dicFile));
            String text;
            long freq = 0;
            while ((text = reader.readLine()) != null) {
                String[] ss = text.trim().split(",");
                if (ss[0].equals(token)) {
                    freq = Long.valueOf(ss[1]);
                    break;
                }
            }
            reader.close();
            return freq;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void Close() {
        //don't need to do anything

    }


}