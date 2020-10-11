package server;

import java.io.Serializable;
import java.util.Vector;

public class UserData implements Serializable {

    //  param
    private String userName;
    private String passWord;
    private Vector<String> docNames;
    private Vector<String> invites;

    //  constructor
    public UserData(String userName,String passWord){
        this.userName=userName;
        this.passWord=passWord;
        this.docNames = new Vector<>();
        this.invites = new Vector<>();
    }

    //  constructor 2
    public UserData(UserData userData){
        this.userName=userData.getUserName();
        this.passWord=userData.getPassWord();
        this.docNames = new Vector<>(userData.getDocNames());
        this.invites=new Vector<>(userData.getInvites());
    }

    //  getters
    public String getUserName() {
        return userName;
    }
    public String getPassWord() {
        return passWord;
    }
    public Vector<String> getDocNames() {
        return this.docNames;
    }
    public Vector<String> getInvites(){return this.invites;}

    //  Setters
    public void setUserName(String userName){
        this.userName = userName;
    }
    public void setPassWord(String passWord){
        this.passWord=passWord;
    }

    //  modifiers
    public void addDocument(String nomeDocumento){
        docNames.add(nomeDocumento);
    }
    public void addInvite(String nomeDocumento){
        invites.add(nomeDocumento);
    }
    public void removeInvite(String nomeDocumento){
        invites.remove(nomeDocumento);
    }






}
