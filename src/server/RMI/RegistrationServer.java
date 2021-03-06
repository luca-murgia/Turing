package server.RMI;

import server.ServerGUI;
import server.UserData;
import java.rmi .*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server .*;
import java.util.concurrent.ConcurrentHashMap;

public class RegistrationServer extends RemoteServer
        implements ServerInterface {

    //  param
    private static final int REGPORT = 5252;
    private ConcurrentHashMap<String, UserData> registroUtenti;
    private ServerGUI serverGUI;
    Registry registry;
    ServerInterface stub;

    //  constructor
    public RegistrationServer(ConcurrentHashMap<String,UserData> registroUtenti,ServerGUI serverGUI) {
        this.registroUtenti = registroUtenti;
        this.serverGUI=serverGUI;
    }

    //  remote method register
    public String register(String userName,String passWord) {
        if (!registroUtenti.containsKey(userName)) {

            //  setup user data
            UserData userData = new UserData(userName,passWord);
            userData.setPassWord(passWord);
            userData.setUserName(userName);

            //  input data
            registroUtenti.put(userName, userData);
            serverGUI.showMessage(
                    "user " + userName + " is now registered to the service \n"
            );
            return "Registration successful \n";
        }
        serverGUI.showMessage("User " + userName + " attempted " +
                "to register a second time \n");
        return ("Error: User already registered \n");
    }

    //  create registry, export, bind to port
    public void activateRegistration() {
        try {
            registry = LocateRegistry.createRegistry(REGPORT);
            stub = (ServerInterface)UnicastRemoteObject.exportObject(this, REGPORT);
            registry.rebind("REGISTER", stub);

        } catch (RemoteException rem) {serverGUI.showMessage("Remote exception in ActivateRegistration \n");}
        serverGUI.showMessage(
                "Registration service running at port: " + REGPORT + "\n"
        );
    }

    public void deactivateRegistration() throws RemoteException, NotBoundException {
        UnicastRemoteObject.unexportObject(stub,true);
        UnicastRemoteObject.unexportObject(registry,true);
        registry.unbind("REGISTER");
        serverGUI.showMessage("Remote Objects have been unexported");
    }
}
