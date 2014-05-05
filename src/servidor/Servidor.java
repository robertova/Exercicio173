
package servidor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author ROBERTOVA
 */

public class Servidor {    
    
    public static void main(String args[]) throws Exception {

        ServerSocket socket = new ServerSocket(6786);

        while (true) {

            Socket conexion = socket.accept();
            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            String comando = entrada.readLine();
            System.out.println(comando);
            //cachos almacena o ficheiro
            String[] cachos = comando.split("\\s");
            if (cachos[0].equals("GET")) {
                String fileName = cachos[1];
                if (fileName.startsWith("/")) {
                    fileName = fileName.substring(1);
                }
                File file = new File(fileName);
                if (file.exists()) {
                    int numOfBytes = (int) file.length();
                    FileInputStream inFile = new FileInputStream(fileName);
                    byte[] fileInBytes = new byte[numOfBytes];
                    inFile.read(fileInBytes);
                    salida.writeBytes("HTTP/1.0 200 Document Follows\r\n");
                    salida.writeBytes("Content-Length: " + numOfBytes + "\r\n");
                    salida.writeBytes("\r\n");
                    salida.write(fileInBytes, 0, numOfBytes);
                } else {
                    salida.writeBytes("HTTP/1.0 404 NOT_FOUND\r\n");
                    salida.writeBytes("\r\n");
                    salida.writeBytes("<html><body>Non se autopou o arquivo</body></html>");
                }
            }
            // lee as seguintes ordes sen tratalas          
            while (comando.length() > 5) {
            comando = entrada.readLine();
            System.out.println(comando);
            }
            conexion.close();
        }
    }
}
