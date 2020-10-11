package client.gui;

import client.ControlClient;
import client.ReaderUdp;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class EditorGUI implements Runnable{

    //  param
    String documentName,text;
    int sectionNumber;
    boolean editable;
    ControlClient controlClient;
    InetAddress chatAdress;

    //  constructor
    public EditorGUI(String nomeDocumento, int sectionNumber, String text, ControlClient controlClient, boolean editable) throws UnknownHostException {
        this.text=text;
        this.controlClient=controlClient;
        this.documentName =nomeDocumento;
        this.sectionNumber=sectionNumber;
        this.editable=editable;
        this.chatAdress = InetAddress.getByName("226.226." + nomeDocumento.codePointAt(0) + "." + nomeDocumento.codePointAt(nomeDocumento.length() -1));
    }

    //  constructor
    public EditorGUI(String nomeDocumento, String text, ControlClient controlClient, boolean editable) throws UnknownHostException {
        this.text=text;
        this.controlClient=controlClient;
        this.documentName =nomeDocumento;
        this.editable=editable;
        this.chatAdress = InetAddress.getByName("226.226." + nomeDocumento.codePointAt(0) + "." + nomeDocumento.codePointAt(nomeDocumento.length() -1));

    }

    //  run method
    public void run(){

        //  Frame creation
        JFrame fe = new JFrame("Turing Editor");


        //  label document name - section
        JLabel labDocName = new JLabel();
        labDocName.setBounds(30, 20, 150, 20);
        if(editable)
            labDocName.setText("Editing: "+ documentName +" #" + (sectionNumber +1));
        else
            labDocName.setText("Showing: "+ documentName);

        //  Label chat
        JLabel labChat = new JLabel("Document Chat");
        labChat.setBounds(520, 20, 150, 20);

        //  editor
        JEditorPane editor = new JEditorPane("plain/text",text);
        editor.setEditable(editable);
        //  scrollpane editor
        JScrollPane paneEditor = new JScrollPane(editor);
        paneEditor.setBounds(30, 50, 450, 500);
        paneEditor.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //  Chat writer
        JEditorPane chatWriter = new JEditorPane();
        chatWriter.setEditable(true);
        chatWriter.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        chatWriter.setBounds(520,500,140,50);

        //  chat reader
        JTextArea chatReader = new JTextArea();
        chatReader.setEditable(false);
        //  scrollpane chat
        JScrollPane paneChatReader = new JScrollPane(chatReader);
        paneChatReader.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        paneChatReader.setBounds(520,50,200,420);

        //	thread udp chat
        ReaderUdp readerUdp = new ReaderUdp(null, chatReader, documentName);
        Thread t = new Thread(readerUdp);
        t.start();

        //  button send
        ImageIcon send = new ImageIcon("send.png");
        JButton butSend = new JButton(send);
        butSend.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        butSend.setBounds(670, 500, 50, 50);//  Bottone Send
        //  Send ActionListener
        butSend.addActionListener(e -> {
            try (DatagramSocket serverSocket = new DatagramSocket())
            {
                String msg = controlClient.getUserName() +": "+chatWriter.getText();
                chatWriter.setText("");
                DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),msg.getBytes().length, chatAdress, 8888);
                serverSocket.send(msgPacket);

            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        });

        //  windowClosing actionlistener
        fe.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (editable) {
                    if (JOptionPane.showConfirmDialog(fe, "Save changes to your file?", "Close Window?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                        try {
                            controlClient.endEdit(documentName, editor.getText(), sectionNumber);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        fe.dispose();
                    }else{
                        try {
                            if(controlClient.unlock(documentName, sectionNumber)) {
                                controlClient.startMainMenuGUI();
                                t.interrupt();
                                fe.dispose();
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                        t.interrupt();
                        fe.dispose();
                }
            }
        });


        //  frame details
        fe.setLayout(null);
        fe.setSize(770, 630);
        fe.setVisible(true);
        fe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //  frame components
        fe.add(labDocName);
        fe.add(labChat);
        fe.add(butSend);
        fe.add(paneEditor);
        fe.add(chatWriter);
        fe.add(paneChatReader);

    }

}
