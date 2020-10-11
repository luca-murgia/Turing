package server;

import client.Message;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class Handler implements Runnable
{
    //  param
    private ControlServer controlServer;
    ObjectInputStream in;
    ObjectOutputStream out;

    //	constructor
    public Handler(Socket client,ControlServer controlServer) throws IOException {
        this.controlServer=controlServer;

        //  initialization writer-reader
        in= new ObjectInputStream(
                client.getInputStream());
        out= new ObjectOutputStream(
                client.getOutputStream());
    }

    //  sends a message to the handled client
    public void answer(Message response) throws IOException {
        out.writeObject(response);
        out.flush();
    }

    //  handles a login request
    public Message handleLogin(Message loginRequest){
        String userNameRequested = loginRequest.getUserName();
        String passWordRequested = loginRequest.getPassWord();

        Message badResult = new Message(Message.LOGIN_ERROR);

        //  case: non registered user
        if(!controlServer.containsUserKey(userNameRequested)){
            return badResult;
        }

        //  case: registered user
        UserData userData =  controlServer.getUserData(userNameRequested);

        //  compare userName & passWord with the ones in the user register
        if(userData.getPassWord().equals(passWordRequested)){
            Message goodResult = new Message(Message.LOGIN_OK);
            goodResult.setUserData(userData);
            controlServer.showMessage("user '" + userData.getUserName() + "' just logged in \n");
            return goodResult;
        }
        return badResult;
    }

    //  handles a userData request
    public Message handleGetUserData(Message getUserData){

        //  update userData on register
        UserData toMod = controlServer.getUserData(getUserData.getUserName());

        //  sends answer to client
        getUserData.setUserData(new UserData(toMod));
        getUserData.setId(Message.UPDATE_UD);
        return getUserData;
    }

    //  handles 'create new document' request
    public Message handleCreate(Message createRequest) {

        Message response = new Message(Message.GENERIC_ERROR);

        int SectionNumbers = createRequest.getSectionNumbers();
        String DocumentName = createRequest.getDocumentName();
        String owner = createRequest.getUserName();

        //  case: clash in register
        if (controlServer.containsDocumentKey(DocumentName)) {
            response.setId(Message.INVALID_DOCNAME);
        }

        //  case: no clash in register
        else{
                DocumentData data = new DocumentData(DocumentName,owner,SectionNumbers);
                Document nuovoDocumento = new Document(data);

                //  insert document into register
                controlServer.addDocument(DocumentName,nuovoDocumento);

                //  update relative userData
                UserData toMod = controlServer.getUserData(owner);
                toMod.addDocument(DocumentName);
                controlServer.replaceUser(owner,toMod);

                //  sends response to client
                response.setUserData(new UserData(toMod));
                response.setDocumentName(DocumentName);
                response.setSectionNumbers(SectionNumbers);
                response.setId(Message.UPDATE_UD);
        }
        return response;
    }

    //  generic request handle method
    public Message handleRequest(Message request){

        Message error = new Message(Message.GENERIC_ERROR);

        switch (request.getId()){

            case Message.LOGIN:
                return handleLogin(request);
            case Message.CREATE_DOCUMENT:
                return handleCreate(request);
            case Message.REMOVE_INVITE:
                controlServer.removeInvite(request.getUserName(),request.getDocumentName());
                return request;
            case Message.PORTIONS:
                System.out.println("HANDLER pre-handle: " +request.getDocumentName());
                request.setSectionNumbers(controlServer.getPortions(request.getDocumentName()));
                return request;
            case Message.ADMINS:
                Message response = new Message(Message.ADMINS);
                response.setStringVector(new Vector<>(controlServer.getAdmins(request.getDocumentName())));
                return response;
            case Message.USERS:
                request.setStringVector(controlServer.getUserNames());
                return request;
            case Message.INVITATION:
                controlServer.addAdmin(request.getDocumentName(),request.getReceiver());
                controlServer.addInvite(request.getReceiver(),request.getDocumentName());
                request.setId(Message.INVITE_SENT);
                return request;
            case Message.GET_INVITES:
                String owner = request.getUserName();
                request.setStringVector(controlServer.getUserData(owner).getInvites());
                return request;
            case Message.GET_USERDATA:
                return handleGetUserData(request);
            case Message.LOCK:
                if(!controlServer.lock(request.getDocumentName(),request.getSectionNumbers()))
                    request.setId(Message.LOCK_NOK);
                else
                    request.setId(Message.LOCK_OK);
                return request;
            case Message.UNLOCK:
                if(controlServer.unlock(request.getDocumentName(),request.getSectionNumbers()))
                    request.setId(Message.UNLOCK_OK);
                return request;
            case Message.EDIT:
                String DocumentName = request.getDocumentName();
                int SectionNumbers = request.getSectionNumbers();
                request.setText(controlServer.getSection(DocumentName,SectionNumbers));
                return request;
            case Message.END_EDIT:
                controlServer.editDoc(request.getDocumentName(),request.getSectionNumbers(),request.getText());
                return request;
            case Message.PRINT:
                request.setText(controlServer.getDocumentString(request.getDocumentName()));
                return request;
            case Message.SHOW:
                String text = controlServer.getSection(request.getDocumentName(),request.getSectionNumbers());
                request.setText(text);
                return request;
            default:
                controlServer.showMessage("unknown request");
                break;
        }return error;
    }


    //	handle loop
    @Override
    public void run()
    {
        try {
                while(!Thread.currentThread().isInterrupted()) {
                    Message request = (Message) in.readObject();
                    Message response = handleRequest(request);
                    answer(response);
                }
                controlServer.showMessage("thread " + Thread.currentThread() + " is done handling \n");
        }

        catch (IOException e){
            controlServer.showMessage
                    ("IOException in handler \n");
            e.printStackTrace();
        } catch (ClassNotFoundException cnf){
            cnf.printStackTrace();}
        catch (NullPointerException nul){
            controlServer.showMessage("Handler " + Thread.currentThread().getId() + " : NPE \n");
            nul.printStackTrace();
        }
    }
}
