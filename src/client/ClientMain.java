package client;

public class ClientMain {
public static void main(String[] args){
        ControlClient controlClient = new ControlClient();
        Thread t1 = new Thread(controlClient);
        t1.start();
    }
}
