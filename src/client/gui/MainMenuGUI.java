package client.gui;

import client.ControlClient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

public class MainMenuGUI implements Runnable {

    //  param
    private ControlClient controlClient;
    private JFrame fmm;
    private Vector<String> shared;
    private JList<String> userList, docList, sezList, sharedList;
    private String docname;

    //  constructor
    public MainMenuGUI(ControlClient controlClient) throws IOException, ClassNotFoundException {
        this.controlClient = controlClient;
        this.docList = new JList<>();
        docList.setSelectedIndex(0);
        this.userList = new JList<>();
        this.sezList = new JList<>();
        this.sharedList = new JList<>();
        update();
    }

    //  show message method
    public void showMessage(String toShow) {
        JOptionPane.showMessageDialog(fmm, toShow);
    }

    //  update method
    public void update() throws IOException, ClassNotFoundException {

        int indexUser = userList.getSelectedIndex();
        int indexDoc = docList.getSelectedIndex();

        userList.setListData(controlClient.getUserList());
        docList.setListData(controlClient.getDocNames());

        docList.setSelectedIndex(indexDoc);
        userList.setSelectedIndex(indexUser);
    }

    public void notifyInvite() throws IOException, ClassNotFoundException {
        //  notify pending invites
        if(!controlClient.getPendingInvites().isEmpty()){
            for (String document:controlClient.getPendingInvites()) {
                showMessage("You've been invited to edit: " + document);
                controlClient.removeInvite(document);
            }
        }
    }

    public void run() {
        //  Label owner
        JLabel labOwner = new JLabel("Logged in as: " + controlClient.getUserName());
        labOwner.setBounds(30, 5, 150, 20);

        //  Label documents
        JLabel labDocs = new JLabel("Documents:");
        labDocs.setBounds(30, 25, 150, 20);

        //  Label sections
        JLabel labSez = new JLabel("Sections:");
        labSez.setBounds(200, 25, 150, 20);

        // Label friends
        JLabel labUsers = new JLabel("Users:");
        labUsers.setBounds(370, 25, 150, 20);

        //  Label share
        JLabel labShare = new JLabel("Shared with:");
        labShare.setBounds(370, 150, 150, 20);

        //  pane documents
        JScrollPane panDocs = new JScrollPane(docList);
        panDocs.setBounds(30, 50, 150, 200);
        panDocs.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //  pane secctions
        JScrollPane panSez = new JScrollPane(sezList);
        panSez.setBounds(200, 50, 150, 200);
        panSez.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // pane users
        JScrollPane panUsers = new JScrollPane(userList);
        panUsers.setBounds(370, 50, 150, 75);
        panUsers.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //  pane admins
        JScrollPane panShare = new JScrollPane(sharedList);
        panShare.setBounds(370, 175, 150, 75);
        panShare.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //  button create document
        JButton butCreate = new JButton("New");
        butCreate.setBounds(30, 280, 70, 30);
        butCreate.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //ActionListener create
        butCreate.addActionListener(e -> {
            //  input document name - number of sections
            String nomeDocumento = JOptionPane.showInputDialog("Document Name: ");
            if(nomeDocumento.isEmpty())
                showMessage("Input a valid name");
            else{
                try {
                    int numeroSezioni = Integer.parseInt(JOptionPane.showInputDialog("Number of sections: "));
                    if(numeroSezioni>0)
                        controlClient.createDocument(nomeDocumento, numeroSezioni);
                    else
                        showMessage("Number of sections cannot be equal to 0");
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(fmm, "Insert a valid number");
                }
            }
        });

        //  button print document
        JButton butPrint = new JButton("Print");
        butPrint.setBounds(110, 280, 70, 30);
        butPrint.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //  ActionListener button print
        butPrint.addActionListener(e -> {
            if(docList.isSelectionEmpty())
                showMessage("Select a document");
            else{
                try {
                    controlClient.print(docList.getSelectedValue());
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

            }
        });

        //  button edit section
        JButton butEdit = new JButton("Edit");
        butEdit.setBounds(200, 280, 70, 30);
        butEdit.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //  ActionListener button edit
        butEdit.addActionListener(e -> {

            //  if any element is selected
            if(sezList.isSelectionEmpty()||docList.isSelectionEmpty()) {
                showMessage("select a document and a section to edit");
            }
            else {

                //  retrieve documentName - retrieve section number
                String nomeDocumento = docList.getSelectedValue();
                int numeroSezione = sezList.getSelectedIndex();

                //  lock on section
                try {
                    if (!controlClient.lock(nomeDocumento, numeroSezione))
                        showMessage("Somebody else is editing this section");
                    else{
                        controlClient.edit(nomeDocumento,numeroSezione);
                        fmm.dispose();

                    }
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });


        //  button show
        JButton butShow = new JButton("Show");
        butShow.setBounds(280, 280, 70, 30);
        butShow.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //  ActionListener button show
        butShow.addActionListener(e -> {
            if(docList.isSelectionEmpty()||sezList.isSelectionEmpty())
                showMessage("Select a valid document and section to open");
            else{
                try {
                    controlClient.show(docList.getSelectedValue(),sezList.getSelectedIndex());
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

            }
        });

        // button invite
        JButton butInvite = new JButton("invite");
        butInvite.setBounds(370, 280, 150, 30);
        butInvite.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //  ActionListener button invite
        butInvite.addActionListener(e -> {
            if(docList.isSelectionEmpty() || userList.isSelectionEmpty())
                showMessage("Select a document and a User");
            else {
                String document = docList.getSelectedValue();
                String receiver = userList.getSelectedValue();
                String sender = controlClient.getUserName();

                if (!sender.equals(shared.elementAt(0)))
                    showMessage("Only "+ shared.elementAt(0) + " is able to invite another user");
                else {
                    try {
                        if (controlClient.getAdmins(document).contains(receiver))
                            showMessage("User " + receiver + " is already able to edit");
                        else
                            controlClient.invite(sender, receiver, document);
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        //  onclick documentlist
        docList.addListSelectionListener(e -> {
            if(e.getValueIsAdjusting()) {


                if (!docList.isSelectionEmpty()){
                    docname = docList.getSelectedValue();
                }

                int numeroSezioni;
                Vector<String> sections = new Vector<>();

                try {
                    //  richiedo numero sezioni
                    numeroSezioni = controlClient.getPortions(docname);

                    //  richiedo admins documento
                    shared = controlClient.getAdmins(docname);

                    //riempio lista sezioni
                    for (int i = 1; i <= numeroSezioni; i++) {
                        sections.add(docname + " " + i);
                    }

                    //  update section list
                    sezList.setListData(sections);

                    //  update admin list
                    sharedList.setListData(shared);

                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

            }
        });

        //  timer task
        ActionListener update_notify = actionEvent -> {
            try {
                controlClient.updateUserData();
                notifyInvite();
                update();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        };

        //  timer update creation
        Timer timer = new Timer(1000, update_notify);
        timer.start();

        //  frame creation
        JFrame fmm = new JFrame("Main Menu");
        this.fmm = fmm;

        //  frame details
        fmm.setLayout(null);
        fmm.setSize(565, 370);
        fmm.setVisible(true);
        fmm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //  frame elements
        fmm.add(panDocs);
        fmm.add(panSez);
        fmm.add(panShare);
        fmm.add(panUsers);

        fmm.add(labOwner);
        fmm.add(labDocs);
        fmm.add(labSez);
        fmm.add(labShare);
        fmm.add(labUsers);

        fmm.add(butCreate);
        fmm.add(butEdit);
        fmm.add(butPrint);
        fmm.add(butShow);
        fmm.add(butInvite);
    }
}
