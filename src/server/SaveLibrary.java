package server;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SaveLibrary{

    //  save object function (java IO)
    public static void save(Object serObj, String fileName){
        try{
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(serObj);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //  load saved object
    public static Object retrieve(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return ois.readObject();
    }

    //  save documentData inside documentMemory
    public static void saveDocumentData(Object serObj){
        String fileName = "DocumentMemory";
        save(serObj,fileName);
        System.out.println("documents saved");
    }

    //  saveUserData inside userMemory
    public static void saveUserData(Object serObj){
        String fileName = "UserMemory";
        save(serObj,fileName);
        System.out.println("userdata saved");
    }

    //  load documentData from documentMemory
    public static HashMap<String, Document> retrieveDocs() {
        try {
            return (HashMap<String,Document>)retrieve("DocumentMemory");
            }catch ( IOException io){
            return new HashMap<>();
        }catch (ClassNotFoundException cnf){
            System.out.println("class not found in retrievedocs");
            return new HashMap<>();
        }
    }

    //  load userData from userMemory
    public static ConcurrentHashMap<String, UserData> retrieveUsers() {
        try {
            return (ConcurrentHashMap)retrieve("UserMemory");
        }catch ( IOException io){
            return new ConcurrentHashMap<>();
        }catch (ClassNotFoundException cnf){
            System.out.println("class not found in retrieveUsers");
            return new ConcurrentHashMap<>();
        }
    }
}
