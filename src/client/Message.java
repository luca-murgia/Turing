package client;
import server.UserData;
import java.io.Serializable;
import java.util.Vector;

public class Message implements Serializable {

    //  parameters
    private byte id;
    private String userName;
    private String receiver;
    private String passWord;
    private String documentName;
    private String text;
    private int sectionNumbers;
    private Vector<String> stringVector;
    private UserData userData;

    //  message IDs
    public static final byte LOGIN = 0;
    public static final byte LOGIN_OK = 1;
    public static final byte LOGIN_ERROR =2;
    public static final byte GENERIC_ERROR = 4;
    public static final byte CREATE_DOCUMENT = 5;
    public static final byte EDIT=6;
    public static final byte INVALID_DOCNAME = 7;
    public static final byte GET_USERDATA = 8;
    public static final byte USERS = 9;
    public static final byte GET_INVITES = 10;
    public static final byte UPDATE_UD = 11;
    public static final byte INVITE_SENT=12;
    public static final byte INVITATION=13;
    public static final byte REMOVE_INVITE=14;
    public static final byte PORTIONS=15;
    public static final byte ADMINS=16;
    public static final byte LOCK = 17;
    public static final byte UNLOCK = 18;
    public static final byte LOCK_OK = 19;
    public static final byte LOCK_NOK = 20;
    public static final byte UNLOCK_OK = 21;
    public static final byte END_EDIT = 22;
    public static final byte PRINT = 23;
    public static final byte SHOW = 24;

    //  constructor
    public Message(byte id){
        this.id = id;
    }

    //  getters
    public String getUserName() {
        return userName;
    }
    public String getPassWord() {
        return passWord;
    }
    public String getDocumentName(){
        return documentName;
    }
    public int getSectionNumbers() {
        return sectionNumbers;
    }
    public byte getId(){
        return id;
    }
    public UserData getUserData() {
        return userData;
    }
    public String getReceiver(){
        return receiver;
    }
    public Vector<String> getStringVector(){return stringVector;}
    public String getText() {
        return text;
    }

    //  setters
    public void setId(byte id) {
        this.id = id;
    }
    public void setUserName(String userName){
        this.userName=userName;
    }
    public void setPassWord(String passWord){this.passWord=passWord;}
    public void setUserData(UserData userData) {
        this.userData = userData;
    }
    public void setDocumentName(String documentName) {this.documentName=documentName;}
    public void setSectionNumbers(int sectionNumbers) {this.sectionNumbers=sectionNumbers;}
    public void setReceiver(String receiver){
        this.receiver=receiver;
    }
    public void setStringVector(Vector<String> stringVector) {
        this.stringVector = stringVector;
    }
    public void setText(String text) {
        this.text = text;
    }
}
