package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import javax.swing.JTextArea;

public class ReaderUdp implements Runnable
{
    int PORT = 8888;
    private String adress;
    private JTextArea textArea;
    String utente;

    //  constructor
    public ReaderUdp(String user, JTextArea textArea, String adress)
    {
        this.utente=user;
        this.textArea=textArea;
        this.adress= adress;
    }

    //  run method
    public void run()
    {
        InetAddress inetAddress = null;
        try {
            String adr = "226.226." + adress.codePointAt(0) + "." + adress.codePointAt(adress.length() -1);
            inetAddress = InetAddress.getByName(adr);
        } catch (UnknownHostException e)
        {
            e.printStackTrace();
        }

        //  bytebuffer creation
        byte[] buf = new byte[256];

        //  connection to service, reading messages and printing inside textArea
        try (MulticastSocket clientSocket = new MulticastSocket(PORT))
        {
            assert inetAddress != null;
                clientSocket.joinGroup(inetAddress);
            while (!Thread.interrupted())
            {
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                clientSocket.receive(msgPacket);
                String msg = new String(buf, 0, msgPacket.getLength());
                textArea.append(msg+"\n");
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }


    }

}
