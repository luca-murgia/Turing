package server;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.rmi.NotBoundException;

public class ServerGUI implements Runnable {

    private ControlServer controlServer;
    private JTextArea log;
    private Thread server;

    //  show message method
    public void showMessage(String toShow) {
        log.append(toShow);
    }

    public void run(){

        //  server log
        JTextArea log = new JTextArea();
        log.setBounds(20,20,500,300);
        log.setEditable(false);
        log.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.log=log;

        //  button run
        JButton butRun = new JButton("RUN");
        butRun.setBounds(150,340,100,50);
        butRun.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //  run actionListener
        butRun.addActionListener(actionEvent -> {
            ControlServer controlServer = new ControlServer(this);
            this.controlServer=controlServer;
            Thread server = new Thread(controlServer);
            this.server=server;
            server.start();
        });

        // button stop
        JButton butStop = new JButton("STOP");
        butStop.setBounds(300,340,100,50);
        butStop.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //  stop actionListener
        butStop.addActionListener(actionEvent -> {
            showMessage("Interrupting execution... \n");
            showMessage("Saving data... \n");
            SaveLibrary.saveDocumentData(ControlServer.getDocumentRegistry());
            SaveLibrary.saveUserData(ControlServer.getUserRegistry());
            showMessage("Data saved \n");
            server.interrupt();
            try {
                controlServer.close();
            } catch (IOException e) {
                showMessage("Accept interrupted \n");
            }
            catch (NullPointerException nul){showMessage("Server not found");} catch (NotBoundException e) {
                e.printStackTrace();
            }
        });


        //  frame details
        JFrame fs = new JFrame("Turing Server");
        fs.setSize(560,450);
        fs.setLayout(null);
        fs.setVisible(true);
        fs.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //  frame components
        fs.add(log);
        fs.add(butRun);
        fs.add(butStop);
    }
}
