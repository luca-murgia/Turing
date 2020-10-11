package server;

import server.RMI.RegistrationServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControlServer implements Runnable{

    //  param
    private static ConcurrentHashMap<String, UserData> userRegistry;
    private static HashMap<String, Document> documentRegistry;
    private ServerGUI serverGUI;
    private ServerSocket serverSocket;
    //  private Thread timer;
    RegistrationServer registrationServer;

    //  constructor
    public ControlServer(ServerGUI serverGUI){
        userRegistry = SaveLibrary.retrieveUsers();
        documentRegistry = SaveLibrary.retrieveDocs();
        this.serverGUI=serverGUI;
    }

    // run method
    public void run(){
        //startAutoSave();
        activateRegistration();
        activateLogin();
    }

    /*
    //  save timer activation method
    public void startAutoSave(){
        SaveTimer saveTimer = new SaveTimer();
        timer = new Thread(saveTimer);
        timer.start();
        showMessage("Autosave: ON \n");
    }
    */

    //  create reg server, activate registry
    public void activateRegistration(){
        registrationServer = new RegistrationServer(userRegistry,serverGUI);
        registrationServer.activateRegistration();
    }

    //  opening serverSocket, bind on port 1500, threadpool init
    public void activateLogin(){
        try (ServerSocket server = new ServerSocket()) {
            this.serverSocket=server;
            server.setReceiveBufferSize(100);
            server.bind(
                    new InetSocketAddress(
                            InetAddress.getLocalHost(), 1500));

            //  threadpool creation
            ExecutorService es =
                    Executors.newFixedThreadPool(500);
            serverGUI.showMessage("Login service active at port 1500 \n");

            //  loop: handler execution
            while (!Thread.currentThread().isInterrupted()) {
                try {

                    Socket client = server.accept();
                    Handler handler = new Handler(client,this);
                    es.execute(handler);

                } catch (IOException e) {
                    es.shutdown();
                    registrationServer.deactivateRegistration();
                    //timer.interrupt();
                }
            }
            es.shutdown();
            registrationServer.deactivateRegistration();
            //timer.interrupt();
            showMessage("Threadpool is shutting down... \n");
        } catch (IOException | NotBoundException e) {e.printStackTrace();}
    }


    public void showMessage(String toShow){
        serverGUI.showMessage(toShow);
    }

    //  locks a specified section
    public boolean lock(String documentName, int sectionNumber){
        return documentRegistry.get(documentName).lock(sectionNumber);
    }
    
    //  unlocks a specified section
    public boolean unlock(String documentName,int sectionNumber){
        return documentRegistry.get(documentName).unlock(sectionNumber);
    }

    //  checks if user is present inside the user registry
    public boolean containsUserKey(String userName){
        return userRegistry.containsKey(userName);
    }

    //  checks if document is present inside documentRegistry
    public boolean containsDocumentKey(String documentName){
        return documentRegistry.containsKey(documentName);
    }

    //  adds an admin to a document inside documentRegistry
    public void addAdmin(String docName, String admin){
        synchronized (this) {
            documentRegistry.get(docName).addAdmin(admin);
        }
    }
    
    //  adds a document to the document registry
    public void addDocument(String documentName,Document documento){
        documentRegistry.put(documentName,documento);
    }

    //  adds an invite to the reciever's userData
    public void addInvite(String receiver,String docName){
        userRegistry.get(receiver).addInvite(docName);
        userRegistry.get(receiver).addDocument(docName);
    }

    //  removes an invite from the reciever's userData
    public void removeInvite(String userName, String documentName){
        userRegistry.get(userName).removeInvite(documentName);
    }

    //  replaces userData inside the userRegistry
    public void replaceUser(String userName,UserData userData){
        userRegistry.replace(userName,userData);
    }

    //  replaces a document inside the documentRegistry
    public void editDoc(String documentName, int sectionNumber, String text){
        documentRegistry.get(documentName).change(sectionNumber,text);
    }

    //  returns the whole document in string form
    public String getDocumentString(String documentName){
        return documentRegistry.get(documentName).toString();
    }

    //  returns the requested section in string form
    public String getSection(String documentName, int sectionNumber) {
        return documentRegistry.get(documentName).getSection(sectionNumber);
    }

    //  returns the requested userData
    public UserData getUserData(String userName){
        return userRegistry.get(userName);
    }

    //  returns the number of sections in a document
    public int getPortions(String docName){
        return documentRegistry.get(docName).getData().getSectionNumber();
    }

    //  returns the adminList of a requested document
    public Vector<String> getAdmins(String docName){
        return documentRegistry.get(docName).getData().getAdmins();
    }

    //  returns a list of all the registered users
    public Vector<String> getUserNames () {
        return new Vector<>(userRegistry.keySet());
    }

    //  returns the whole userRegistry
    public static ConcurrentHashMap<String, UserData> getUserRegistry() {
        return userRegistry;
    }

    //  returns the whole documentRegistry
    public static HashMap<String, Document> getDocumentRegistry() {
        return documentRegistry;
    }

    //  close sockets
    public void close() throws IOException, NotBoundException {
        serverSocket.close();
        registrationServer.deactivateRegistration();
    }
}