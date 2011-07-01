/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;
import helma.xmlrpc.XmlRpc;
import helma.xmlrpc.XmlRpcClient;
import helma.xmlrpc.XmlRpcException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public class Client 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        if (args.length < 3)
        {
            System.out.println(
            "Usage: java -jar xmlrpcclient.jar [ip:port] [method] [argument]");
            System.exit(-1);
        }
        try
        {
        // Use the Apache Xerces SAX Driver
            XmlRpc.setDriver("org.apache.xerces.parsers.SAXParser");
            // Specify the server
            XmlRpcClient client =
            new XmlRpcClient("http://"+args[0]+"/");
            // Create request
            Vector params = new Vector( );
            params.addElement(args[2]);
            // Make a request and print the result
            String result = (String)client.execute(args[1], params);

            byte base[] = Base64.decode(result.getBytes());
            result = new String(base);

            //System.out.println("$VAR1='" + result + "';");
            System.out.println(result);

            // Make a request and print the result
        } catch (ClassNotFoundException e) {
            System.out.println("Could not locate SAX Driver");
        } catch (MalformedURLException e) {
            System.out.println(
            "Incorrect URL for XML-RPC server format: " +
            e.getMessage( ));
        } catch (XmlRpcException e) {
            System.out.println("XML-RPC Exception: " + e.getMessage( ));
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage( ));
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

}
