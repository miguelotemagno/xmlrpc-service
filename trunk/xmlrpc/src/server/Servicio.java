/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/**
 *
 * @author miguel
 */
public class Servicio
{
    private Hashtable settings = null;
    private String fileConf = null;

    Servicio(String file)
    {
        fileConf = new String(file);
        settings = new Hashtable();
        initConfigXML();
    }

    public String ping(String arg)
    {
        //System.out.println("pong");
        return Base64.encodeBytes((new String("pong")).getBytes());
    }

    public String refresh(String arg)
    {
        settings = new Hashtable();
        initConfigXML();
        return listarMetodos("");
    }

    public String execute(String arg)
    {
        String value = new String();
        StringBuffer buffer = new StringBuffer();
        String[] lst = arg.split(" ");
        String fecha = getDateTime("yyyy-MM-dd HH:mm:ss");
        String file  = "/export/home/mvargas/conexiones/logs/"+getDateTime("yyyy-MM-dd")+".log";

        try
        {
            String command = (String) settings.get(lst[0]);
            
            for(Integer i=lst.length-1; i>=0; i--)
                command = command.replace("\"$"+i+"\"", "\""+lst[i]+"\"");

            command = command.replaceAll("\"[$][0-9]+\"", "");

            String log = fecha+"\tlocalhost\tXML-RPC\t\t"+command;
            System.out.println(log);
            //Runtime.getRuntime().exec(log);

            Process p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            do
            {
                value = br.readLine();
                buffer.append(value+"\n");
            }
            while(br.ready());
            
            value = buffer.toString();

            value = Base64.encodeBytes(value.getBytes());
        } catch (Exception ex) {
            Logger.getLogger(Servicio.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return value;
    }

    public String listarMetodos(String arg)
    {
        StringBuffer buffer = new StringBuffer();
        Enumeration set = settings.keys();

        for(int i=0; set.hasMoreElements(); i++)
        {
            String var = set.nextElement().toString();
            String val = (String) settings.get(var);
            buffer.append(var+" = "+val+"\n");
        }

        String list[] = buffer.toString().split("\n");
        Arrays.sort(list);

        return Base64.encodeBytes(Arrays.toString(list).toString().replaceAll(",", "\n").getBytes());
//        return Base64.encodeBytes(buffer.toString().getBytes());
    }
    
    private void initConfigXML()
    {
        try
        {
            File xmlFile               = new File(this.fileConf);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db         = dbf.newDocumentBuilder();
            Document doc               = db.parse(xmlFile);

            doc.getDocumentElement().normalize();
            System.out.println("#### CREANDO LA TABLA DE PROCEDIMIENTOS ####");
            System.out.println("Root element " + doc.getDocumentElement().getNodeName());

            NodeList padre = doc.getChildNodes();

            for(int p=0; p<padre.getLength(); p++)
            {
                NodeList hijos = padre.item(p).getChildNodes();
                for(int i=0; padre.item(p).hasChildNodes() && i<hijos.getLength( ); i++)
                {                    
                    NodeList nietos = hijos.item(i).getChildNodes( );
                    for(int j=0; hijos.item(i).hasChildNodes() && j<nietos.getLength(); j++)
                    {
                        Node hijo  = (Node)hijos.item(i);
                        Node nieto = (Node)nietos.item(j);
                        String var = new String(hijo.getNodeName()+"."+nieto.getNodeName());
                        String val = nieto.getTextContent().trim();

                        //CREANDO LA TABLA DE PROCEDIMIENTOS
                        if(val.length() > 1)
                        {
                            this.settings.put(var, val);
                            System.out.println(var+"="+val);
                        }
                    }
                }
            }

            System.out.println("#### TABLA DE PROCEDIMIENTOS CREADA ####");
        } catch (SAXException ex) {
            Logger.getLogger(Servicio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Servicio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Servicio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    private void initConfigINI()
    {
        File file = new File(this.fileConf);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        try
        {
            fis = new FileInputStream(file);

            // Here BufferedInputStream is added for fast reading.
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            System.out.println("#### CREANDO LA TABLA DE PROCEDIMIENTOS ####");
            // dis.available() returns 0 if the file does not have more lines.
            while (dis.available() != 0)
            {
                String line  = dis.readLine();
                String[] lst = line.split("=");

                if(line.length() > 0 && lst.length == 2)
                {
                    String var   = lst[0];
                    String val   = lst[1];

                    //CREANDO LA TABLA DE PROCEDIMIENTOS
                    if(val != "")
                    {
                        this.settings.put(var, val);
                        System.out.println(var+"="+val);
                    }
                }
            }
            System.out.println("#### TABLA DE PROCEDIMIENTOS CREADA ####");

            // dispose all the resources after using them.
            fis.close();
            bis.close();
            dis.close();

        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }

    private String getDateTime(String format)
    {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }
}
