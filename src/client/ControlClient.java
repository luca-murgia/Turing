package client;

import client.gui.EditorGUI;
import client.gui.LoginGUI;
import client.gui.MainMenuGUI;
import server.UserData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public class ControlClient implements Runnable {

    //  param
    private UserData userData;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private LoginGUI loginGUI;
    private MainMenuGUI mainMenuGUI;

    //  run method
    public void run(){
            startLoginGUI();
    }

    //  login starter
    public void startLoginGUI(){

        try {
            LoginGUI loginGUI = new LoginGUI(this);
            this.loginGUI = loginGUI;
            Thread loginThread = new Thread(loginGUI);
            loginThread.start();

            //  initialize socket, binding to port
            Socket socket = new Socket(
                    InetAddress.getLocalHost(), 1500);

            //  writer creation - reader creation
            out = new ObjectOutputStream(
                    socket.getOutputStream());
            in = new ObjectInputStream(
                    socket.getInputStream());
        }catch (IOException ex){
            loginGUI.show("Unable to reach server");
            loginGUI.close();
        }
    }
    
    //  main menu starter
    public void startMainMenuGUI() throws IOException, ClassNotFoundException {
        MainMenuGUI mainMenuGUI = new MainMenuGUI(this);
        this.mainMenuGUI=mainMenuGUI;
        Thread mainMenuThread = new Thread(mainMenuGUI);
        mainMenuThread.start();
    }
    
    //  editor starter  
    public void startEditorGUI(String documentName, int numeroSezioni, String text) throws UnknownHostException {
        EditorGUI editorGUI = new EditorGUI(documentName,numeroSezioni,text,this,true);

        Thread editorThread = new Thread(editorGUI);
        editorThread.start();
    }
    
    //  editor (non editable) starter
    public void startDisplayGUI(String documentName, int numeroSezioni, String text) throws UnknownHostException {
        EditorGUI editorGUI = new EditorGUI(documentName,numeroSezioni,text,this,false);
        Thread editorThread = new Thread(editorGUI);
        editorThread.start();
    }
    
    //  whole document editor (non editable) starter 
    public void startDisplayGUI(String documentName, String text) throws UnknownHostException {
        EditorGUI editorGUI = new EditorGUI(documentName,text,this,false);
        Thread editorThread = new Thread(editorGUI);
        editorThread.start();
    }

    //  register method
    public String register(String userName, String passWord){
        RegistrationClient regUtility =
                new RegistrationClient(userName, passWord);
        return regUtility.register();
    }

    //  login method
    public void login(String userName,String passWord) throws IOException, ClassNotFoundException {
        //  request login
        Message loginMessage = new Message(Message.LOGIN);
        loginMessage.setUserName(userName);
        loginMessage.setPassWord(passWord);
        handle(request(loginMessage));
    }
    
    //  request method: sends request to server, returns answer
    public Message request (Message request) throws IOException, ClassNotFoundException {
        out.writeObject(request);
        out.flush();
        return ((Message) in.readObject());
    }
    
    //  handle different kinds of answers (according to ID)
    public void handle(Message response) throws IOException, ClassNotFoundException {
        switch(response.getId()){

            case Message.LOGIN_OK:
                loginGUI.close();
                userData = response.getUserData();
                startMainMenuGUI();
                break;

            case Message.LOGIN_ERROR:
                loginGUI.show("Login Error");
                break;

            case Message.INVALID_DOCNAME:
                mainMenuGUI.showMessage("A document with the same name already exists ");
                break;

            case Message.UPDATE_UD:
                userData = response.getUserData();
                break;

            case Message.INVITE_SENT:
                mainMenuGUI.showMessage("Invite sent");
                break;

            default:
                System.out.println("something went wrong");
                System.out.println(response.getId());
                break;
        }
    }

    //  lock section method
    public boolean lock(String documentName, int sectionNumber) throws IOException, ClassNotFoundException {
        Message message = new Message(Message.LOCK);
        message.setDocumentName(documentName);
        message.setSectionNumbers(sectionNumber);

        byte responseID = request(message).getId();
        return responseID == Message.LOCK_OK;
    }
    
    // unlock section method
    public boolean unlock(String documentName, int sectionNumber) throws IOException, ClassNotFoundException {
        Message message = new Message(Message.UNLOCK);
        message.setDocumentName(documentName);
        message.setSectionNumbers(sectionNumber);

        byte responseID = request(message).getId();
        return responseID == Message.UNLOCK_OK;
    }

    //  edit section method
    public void edit(String documentName, int sectionNumber) throws IOException, ClassNotFoundException {
        Message message = new Message(Message.EDIT);
        message.setDocumentName(documentName);
        message.setSectionNumbers(sectionNumber);

        startEditorGUI(documentName,sectionNumber,request(message).getText());
    }

    //  print document method
    public void print(String documentName) throws IOException, ClassNotFoundException {
        Message message = new Message(Message.PRINT);
        message.setDocumentName(documentName);

        startDisplayGUI(documentName,request(message).getText());
    }

    //  show section method
    public void show(String documentName, int sectionNumber) throws IOException, ClassNotFoundException {
        Message message = new Message(Message.SHOW);
        message.setDocumentName(documentName);
        message.setSectionNumbers(sectionNumber);
        Message response = request(message);

        startDisplayGUI(documentName,sectionNumber,response.getText());
    }

    //  end edit method
    public void endEdit(String documentName, String text, int sectionNumber) throws IOException, ClassNotFoundException {
        Message message = new Message(Message.END_EDIT);
        message.setText(text);
        message.setDocumentName(documentName);
        message.setSectionNumbers(sectionNumber);

        request(message);
        startMainMenuGUI();
    }

    //  updates userData method (invoked from mainMenu's update)
    public void updateUserData() throws IOException, ClassNotFoundException {
        Message request = new Message(Message.GET_USERDATA);
        request.setUserName(userData.getUserName());
        handle(request(request));
        mainMenuGUI.update();
    }

    //  create document method
    public void createDocument(String documentName, int numeroSezioni) throws IOException, ClassNotFoundException {
        Message create = new Message(Message.CREATE_DOCUMENT);
        create.setDocumentName(documentName);
        create.setSectionNumbers(numeroSezioni);
        create.setUserName(userData.getUserName());
        handle(request(create));
    }

    //  invite user method
    public void invite(String owner, String user, String docName) throws IOException, ClassNotFoundException {
        Message message = new Message(Message.INVITATION);
        message.setDocumentName(docName);
        message.setUserName(owner);
        message.setReceiver(user);
        handle(request(message));
    }

    //  removes invite from userData
    public void removeInvite(String docName) throws IOException, ClassNotFoundException {
        Message remove = new Message(Message.REMOVE_INVITE);
        remove.setUserName(getUserName());
        remove.setDocumentName(docName);
        request(remove);
        updateUserData();
    }

    //  getters
    public String getUserName(){ return userData.getUserName();}
    public Vector<String> getDocNames(){ return userData.getDocNames();}
    public Vector<String> getPendingInvites(){
        return userData.getInvites();
    }
    public Vector<String> getAdmins(String documentName) throws IOException, ClassNotFoundException {
        Message message = new Message(Message.ADMINS);
        message.setDocumentName(documentName);
        Message response = request(message);
        return response.getStringVector();
    }
    public Vector<String> getUserList() throws IOException, ClassNotFoundException {
        Message response = request(new Message(Message.USERS));
        return response.getStringVector();
    }
    public int getPortions(String documentName) throws IOException, ClassNotFoundException {

        Message message = new Message(Message.PORTIONS);
        message.setDocumentName(documentName);

        Message response = request(message);
        return response.getSectionNumbers();
    }

    /*public boolean isAdmin(String documentName,String userName) throws IOException, ClassNotFoundException {
        Message message = new Message(Message.IS_ADMIN);
        message.setDocumentName(documentName);
        message.setUserName(userName);

        return request(message).getAnswer();
    }*/
}
