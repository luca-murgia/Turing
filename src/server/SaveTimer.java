package server;

import java.io.IOException;

import static java.lang.Thread.interrupted;

public class SaveTimer implements Runnable {

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("timer interrupted while sleeping \n");
            }
            SaveLibrary.saveUserData(ControlServer.getUserRegistry());
            SaveLibrary.saveDocumentData(ControlServer.getDocumentRegistry());
        }
        SaveLibrary.saveUserData(ControlServer.getUserRegistry());
        SaveLibrary.saveDocumentData(ControlServer.getDocumentRegistry());
    }
}
