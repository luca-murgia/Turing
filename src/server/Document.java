package server;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantLock;

public class Document implements Serializable {

    //  param
    private DocumentData data;
    private ReentrantLock[] locks;
    private String[] cache;

    // constructor
    public Document (DocumentData data){
        this.data=data;
        String nomeDocumento = data.getDocumentName();
        int sectionNumber = data.getSectionNumber();
        locks = new ReentrantLock[sectionNumber];
        cache = new String[sectionNumber];

        //  document path
        String path = "docs/" + nomeDocumento;
        File directory = new File(path);
        System.out.println(directory.mkdirs());

        //  locks initialization
        for(int i=0; i<sectionNumber;i++){
            locks[i] = new ReentrantLock();
        }
    }

    // lock method
    public boolean lock(int sectionNumber){
        if(locks[sectionNumber].isLocked())
            return false;
        else {
            locks[sectionNumber].tryLock();
            return true;
        }
    }

    //  unlock method
    public boolean unlock(int sectionNumber){
        if(!locks[sectionNumber].isLocked())
            return false;
        locks[sectionNumber].unlock();
        return true;
    }

    //  toString method (whole document)
    public String toString(){
        StringBuilder result= new StringBuilder();
        for(int i=0;i<data.getSectionNumber();i++){
            result.append("--- Section #").append(i + 1).append(" ---\n");
            result.append(cache[i]).append("\n\n");
        }
        return result.toString();
    }

    // toString method (one section)
    public String getSection(int sectionNumber){
        return cache[sectionNumber];
    }

    //  data getter
    public DocumentData getData() {
        return data;
    }
    
    //  adds admin to documentData
    public void addAdmin(String admin){data.addAdmin(admin);}

    //  modifies a section, writes on file using NIO
    public void change(int sectionNumber, String text){
        if(locks[sectionNumber].isLocked()) {
            cache[sectionNumber] = text;
            Charset charset = StandardCharsets.US_ASCII;
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("docs/" + data.getDocumentName() + "/" + sectionNumber + ".txt"), charset)) {
                writer.write(text, 0, text.length());
            } catch (IOException x) {
                System.err.format("IOException: %s%n", x);
            }
            unlock(sectionNumber);
        }
    }
}
