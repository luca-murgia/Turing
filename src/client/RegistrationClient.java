package client;

import server.RMI.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class RegistrationClient {

    //  param
    private String userName;
    private String passWord;
    ServerInterface server;

    //  constructor
    public RegistrationClient(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
        this.server = null;
    }

    //  registration for a new user
    public String register() {
        try {

            if (server == null) {
                //  registry access
                Registry registry =
                        LocateRegistry.getRegistry(5252);

                //  remote object server lookup
                server = (ServerInterface) registry.lookup(
                        "REGISTER");
            }

            //  registration via username/password
            return server.register(userName, passWord);

        } catch (RemoteException e) {
            System.out.println
                    ("Remote exception " +
                            "nella funzione register nel client");
            return "Remote exception: Registry not found \n";

        } catch (NotBoundException nb) {
            System.out.println("not bound exception in" +
                    " register (client)");
            return "NotBoundException: not corresponding name \n";
        }
    }
}
