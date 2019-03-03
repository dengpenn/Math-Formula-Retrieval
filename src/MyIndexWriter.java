import javax.xml.stream.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MyIndexWriter {
    // I suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
    File indexFile;
    File postingFile;
    File docid_To_docno;

    String type;
    String path;
    String name_index;
    String name_posting;

    Map<String, Map<Integer, Integer>> indexMap;  //<term,doc freq map>

    //    private BufferedWriter writer;
    private BufferedWriter docIdWriter;

    boolean firstTime;

    private int count;
    private int fileCount;
    private final int blocksize;


    public MyIndexWriter(String type) throws IOException {
        // This constructor should initiate the FileWriter to output your index files
        // remember to close files if you finish writing the index
        this.type = type;
        count = 0;//doc count in the block
        fileCount = 1;
        firstTime = true;
        blocksize = 100000;

        initial();


    }

    private void initial() throws IOException {


        if (this.type.contains("text")) {
            path = Path.IndexTextDir;
            name_index = "textdic";
            name_posting = "textpost";
        } else if (this.type.contains("web")) {
            path = Path.IndexWebDir;
            name_index = "webdic";
            name_posting = "webpost";
        } else {
            System.exit(0);
        }

        indexFile = new File(path + name_index + fileCount + ".csv");
        postingFile = new File(path + name_posting + fileCount + ".xml");

        if (firstTime) {
            indexFile.getParentFile().mkdirs();
            String[] entries = indexFile.getParentFile().list();
            for (String s : entries) {
                File currentFile = new File(indexFile.getParentFile().getPath(), s);
                currentFile.delete();
            }
            docid_To_docno = new File(path + "docId_docNo.csv");
            docIdWriter = new BufferedWriter(new FileWriter(docid_To_docno));

            firstTime = false;
        }


        indexMap = new HashMap<>();

    }

    public void IndexADocument(String docno, String content) throws IOException {
        // you are strongly suggested to build the index by installments
        // you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader
        count++;

        int docId = docno.hashCode() & 0xfffffff;
        //write docId to docNo file here
        String line = docId + "," + docno + "\n";
        docIdWriter.write(line);
        docIdWriter.flush();


        //get terms
        String[] split = content.split("\\s+");
        //indexMap:<term,Map<docId,freq>>
        for (String t : split) {
            String term = t.trim().replaceAll("\\W+", "");
            if (term.isEmpty() | term == null) {
            } else {
                if (indexMap.containsKey(term)) {
                    if (indexMap.get(term).containsKey(docId)) {
                        indexMap.get(term).replace(docId, indexMap.get(term).get(docId) + 1);
                    } else {
                        indexMap.get(term).put(docId, 1);
                    }
                } else {

                    indexMap.put(term, new HashMap());
                    indexMap.get(term).put(docId, 1);
                }
            }
        }
        //write to file when reach the blocksize
        if (count == blocksize) {
            WriteFile(indexMap);
            fileCount++;
            count = 0;
            initial();
        }

    }

    public void Close() throws IOException {
        // close the index writer, and you should output all the buffered content (if any).
        // if you write your index into several files, you need to fuse them here.

        if (!indexMap.isEmpty()) {
            WriteFile(indexMap);
        }
        docIdWriter.close();
//        writer.close();
        //merge here
        System.out.println("\nmerging dic.");
        mergeCSV();
        System.out.println("merging post.");
        mergeXML();

    }

    public void WriteFile(Map<String, Map<Integer, Integer>> map) throws IOException {


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile));
//            BufferedWriter postWriter = new BufferedWriter(new FileWriter(postingFile));
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xml = xMLOutputFactory.createXMLStreamWriter(
                    new BufferedWriter(new FileWriter(postingFile)));

            xml.writeStartDocument("UTF-8", "1.0");
            xml.writeStartElement("posting");//start posting
            //posting file looks like:
//            <pointer id="2629676">
//            168051600,1
//            15529751,1
//                    </pointer>

            for (Map.Entry<String, Map<Integer, Integer>> entry : map.entrySet()) {
                String term = entry.getKey();//term
                int termId = term.hashCode() & 0xfffffff;//termId(pointer)
                int collectionFreq = 0;

                xml.writeStartElement("pointer");//start term pointer
                xml.writeAttribute("id", String.valueOf(termId));
                StringBuilder sw =new StringBuilder();
//                xml.writeCharacters("\n");
                sw.append("\n");

                Map<Integer, Integer> docFreqMap = entry.getValue();//map of <docId,freq in the docId>


                for (Map.Entry<Integer, Integer> entry1 : docFreqMap.entrySet()) {
//                    entry1.getKey();                //docId
                    int docFreq = entry1.getValue();//freq in the docId
                    //write posting list to file
                    sw.append(entry1.getKey() + "," + docFreq + "\n");
                    //sum doc frequency to get collection freq
                    collectionFreq += docFreq;

                }
                xml.writeCharacters(sw.toString());
                xml.writeEndElement();//end term pointer

                //write dictionary {term,collectionFreq,termId(pointer)}
//                String line = term + "," + collectionFreq + "," + termId + "\n";
                writer.write(term + "," + collectionFreq + "," + termId + "\n");

            }

            xml.writeEndElement();//end posting
            xml.writeEndDocument();

            xml.flush();
            xml.close();
//            postWriter.close();
            writer.flush();
            writer.close();

        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void mergeXML() {

        try {
            //0.rename 1st to temp File
            new File(path + name_posting + "1.xml").renameTo(new File(path + "temp.xml"));

            for (int i = 1; i < fileCount; ) {//repeat 1.~3.
                File temp2 = new File(path + "temp2.xml");
                File temp = new File(path + "temp.xml");
                File thisFile = new File(path + name_posting + (i + 1) + ".xml");

                XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
                XMLStreamWriter xmlWriter = xMLOutputFactory.createXMLStreamWriter(new FileWriter(temp2));

                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader eventReader = factory.createXMLStreamReader(new BufferedReader(new FileReader(thisFile)));

                HashMap<String, String> map = new HashMap<>();

                //1.read (i+1)th file into map
                String pointer = "";
                String docList = "";
//                boolean thisElement = false;
//                long startTime = System.currentTimeMillis();

                while (eventReader.hasNext()) {
                    int event = eventReader.next();
                    switch (event) {
                        case XMLStreamConstants.START_ELEMENT:
//                            StartElement startElement = eventReader.;
                            String eName = eventReader.getLocalName();
                            if (eName.equals("pointer")) {
                                pointer = eventReader.getAttributeValue(0);
                                docList = eventReader.getElementText();
//                                thisElement=true;
                                if (!pointer.isEmpty() && !docList.isEmpty()) {
                                    map.put(pointer, docList);
//                                    thisElement = false;
                                }
                            }
                            pointer = "";
                            docList = "";
                            break;
                    }

                }

                eventReader.close();
//                long endTime = System.currentTimeMillis();
//                System.out.println("read into map using: " + (endTime - startTime) / 60000.0 + " min");

                //2.read temp File line by line and check if in map
                xmlWriter.writeStartDocument("UTF-8", "1.0");
                xmlWriter.writeStartElement("posting");

                eventReader = factory.createXMLStreamReader(new BufferedReader(new FileReader(temp)));
                pointer = "";
                docList = "";
//                boolean hasThisElement = false;
//                startTime = System.currentTimeMillis();
                while (eventReader.hasNext()) {
                    int event = eventReader.next();
                    switch (event) {
                        case XMLStreamConstants.START_ELEMENT:
                            String eName = eventReader.getLocalName();
                            if (eName.equals("pointer")) {
                                pointer = eventReader.getAttributeValue(0);
                                docList = eventReader.getElementText();
                                if (map.containsKey(pointer)) {
                                    //2.1 true=merge into map
                                    if (!pointer.isEmpty() && !docList.isEmpty()) {
                                        String p = map.get(pointer);
                                        String s = p + docList.trim() + "\n";
                                        map.replace(pointer, s);
                                    }

                                } else {
                                    //2.2 false=write lines into temp2 File
                                    xmlWriter.writeStartElement("pointer");
                                    xmlWriter.writeAttribute("id", pointer);
                                    xmlWriter.writeCharacters(docList);
                                    xmlWriter.writeEndElement();

                                }

                            }
                            pointer = "";
                            docList = "";
                            break;
                    }
                }
                eventReader.close();
//                endTime = System.currentTimeMillis();
//                System.out.println("merge into map using: " + (endTime - startTime) / 60000.0 + " min");
//                startTime = System.currentTimeMillis();
                //3.Write map into temp2 file && rename temp2 File to temp File && delete (i+1)th file
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    xmlWriter.writeStartElement("pointer");
                    xmlWriter.writeAttribute("id", entry.getKey());
                    xmlWriter.writeCharacters(entry.getValue());
                    xmlWriter.writeEndElement();
                }

                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
                xmlWriter.flush();
                xmlWriter.close();
//                endTime = System.currentTimeMillis();
//                System.out.println("Write map into file using: " + (endTime - startTime) / 60000.0 + " min");
                temp.delete();
                temp2.renameTo(new File(path + "temp.xml"));
                thisFile.delete();
                i++;
                System.out.printf("\rmerge posting %.2f %%", ((i / (float) fileCount) * 100));

            }
            new File(path + "temp.xml").renameTo(new File(path + name_posting + ".xml"));

        } catch (
                XMLStreamException | IOException e) {
            e.printStackTrace();
        }
        System.out.print("\n");

    }

    public void mergeCSV() {


        try {
            //0.rename 1st to temp File
            new File(path + name_index + "1.csv").renameTo(new File(path + "temp.csv"));

            for (int i = 1; i < fileCount; ) {//repeat 1-3
                File temp2 = new File(path + "temp2.csv");
                File temp = new File(path + "temp.csv");
                File thisFile = new File(path + name_index + (i + 1) + ".csv");

                BufferedReader reader = new BufferedReader(new FileReader(thisFile));
                BufferedWriter dWriter = new BufferedWriter(new FileWriter(temp2));

                HashMap<String, String> map = new HashMap<>();

                //1.read (i+1)th file into map

                String text;
                while ((text = reader.readLine()) != null) {
                    String[] s = text.split(",", 2);
                    map.put(s[0], s[1]);//<term,{freq,pointer}>
                }
                reader.close();
                //2.read temp File line by line and check if in map
                reader = new BufferedReader(new FileReader(temp));
                while ((text = reader.readLine()) != null) {
                    String[] s = text.split(",", 2);

                    if (map.containsKey(s[0])) {
                        //2.1 true=merge into map
                        String[] ss = s[1].split(",");//{freq,pointer}
                        long a = Long.valueOf(map.get(s[0]).split(",")[0]);
                        long b = Long.valueOf(ss[0]);
                        map.replace(s[0], (a + b) + "," + ss[1]);
                    } else {
                        //2.2 false=write line into temp2 File
                        dWriter.write(text + "\n");
                    }
                }
                reader.close();
                //3.Write map into temp2 file && rename temp2 File to temp File && delete (i+1)th file
                for (Map.Entry entry : map.entrySet()) {
                    dWriter.write(entry.getKey() + "," + entry.getValue() + "\n");
                }
                dWriter.flush();
                dWriter.close();
                temp.delete();
                temp2.renameTo(new File(path + "temp.csv"));
                thisFile.delete();
                i++;
                System.out.printf("\rmerge dictionary %.2f %%", ((i / (float) fileCount) * 100));

            }

            new File(path + "temp.csv").renameTo(new File(path + name_index + ".csv"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("\n");

    }


}
