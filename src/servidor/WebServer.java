package servidor;

import java.io.*;
import java.net.*;
import java.util.*;

public class WebServer
{
        
    public static boolean comprobaExtension(File sel, String extension) {
        boolean valido = false;
        int punto = sel.getPath().lastIndexOf(".");
        String ext = (sel.getPath().substring(punto + 1)).toLowerCase();
        String[] joe = extension.split(",");
        for (String s: joe) {
            if (s.equals(ext)) {
                valido = true;
            }
        }
        return valido;
    }
    
    public static void main (String args[]) throws Exception {
        
        String requestMessageLine;
        String fileName;
        String lin;
        HashMap <String,String> h = new HashMap() ;    
        
        BufferedReader r = new BufferedReader(new FileReader("./src/servidor/server.cfg"));
        while ((lin=r.readLine()) != null) {
            String[] cachos = lin.split("=");
            h.put(cachos[0], cachos[1]);
        }
        
        String extension = h.get("extensions");
        int myPort = Integer.parseInt(h.get("porto"));
        ServerSocket listenSocket = new ServerSocket (myPort);

        while(true) {
            System.out.println ("Escoitando o porto " + myPort);
            Socket connectionSocket = listenSocket.accept();
            BufferedReader inFromClient = new BufferedReader (new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream (connectionSocket.getOutputStream());
            
            // tratamos a primeira liña da petición
            requestMessageLine = inFromClient.readLine();
            System.out.println (requestMessageLine);

            String[] cachos = requestMessageLine.split("\\s");

            if (cachos[0].equals("GET")) {
                fileName = h.get("ruta") + "\\" + cachos[1].substring(1);
                System.out.println(fileName);
//                if (fileName.startsWith("/") == true)
//                    fileName = fileName.substring(1);

                // ler o contido do ficheiro solicitado
                File file = new File(fileName);
                if (comprobaExtension(file, extension)) {
                    if (file.exists()) {

                        // converter o ficheiro nun array de bytes
                        int numOfBytes = (int) file.length();
                        FileInputStream inFile = new FileInputStream(fileName);
                        byte[] fileInBytes = new byte[numOfBytes];
                        inFile.read(fileInBytes);
                        // enviar a contestación
                        outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
                        /*
                         if (fileName.endsWith(".jpg"))
                         outToClient.writeBytes ("Content-Type: image/jpeg\r\n");
                         if (fileName.endsWith(".gif"))
                         outToClient.writeBytes ("Content-Type: image/gif\r\n");
                         */
                        //outToClient.writeBytes ("Content-Length: " + numOfBytes + "\r\n");
                        outToClient.writeBytes("\r\n");
                        outToClient.write(fileInBytes, 0, numOfBytes);

                    } else {
                        fileName = h.get("ruta").toString() + "\\" + h.get("erro404");
                        file = new File(fileName);
                        if (file.exists()) {
                            int numOfBytes = (int) file.length();
                            FileInputStream inFile = new FileInputStream(fileName);
                            byte[] fileInBytes = new byte[numOfBytes];
                            inFile.read(fileInBytes);
                            outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
                            outToClient.writeBytes("\r\n");
                            outToClient.write(fileInBytes, 0, numOfBytes);
                        }
                    }
                } else {
                    outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
                    outToClient.writeBytes("\r\n");
                    outToClient.writeBytes("<html><body>Extension non soportada</body></html>");
                }
                // ler, sen tratar, o resto de liñas da petición
                requestMessageLine = inFromClient.readLine();
                while (requestMessageLine.length() >= 5) {
                    System.out.println(requestMessageLine);
                    requestMessageLine = inFromClient.readLine();
                }
                System.out.println(requestMessageLine);

                connectionSocket.close();
            } else {
                System.out.println("Petición incorrecta");
            }
        }
    }
}

