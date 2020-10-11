package server;

import java.io.Serializable;
import java.util.Vector;

public class DocumentData implements Serializable {
    private String owner;
    private int sectionNumber;
    String documentName;
    private Vector<String> admins;

    // constructor
    public DocumentData(String documentName, String owner, int sectionNumber){
        this.documentName=documentName;
        this.owner=owner;
        this.admins = new Vector<>();
        addAdmin(owner);
        this.sectionNumber=sectionNumber;
    }

    //  adds an admin to the admin vector
    public void addAdmin(String admin){
        admins.add(admin);
    }

    //  returns number of sections
    public int getSectionNumber() {
        return sectionNumber;
    }

    //  returns document name
    public String getDocumentName() {
        return documentName;
    }

    //  returns owner of the document
    public String getOwner() {
        return owner;
    }

    //  returns the admin vector
    public Vector<String> getAdmins(){return admins;}
}
