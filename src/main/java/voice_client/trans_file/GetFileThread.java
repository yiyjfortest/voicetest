package voice_client.trans_file;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

public class GetFileThread extends Thread {
    FileOutputStream fos; 
    InputStream is; 
    DataInputStream dis; 
    String fileName; 
    Socket socket; 
    String ip; 
    int port; 
    private JTextArea displayArea;

    public GetFileThread(String ip, String port, String fileName, JTextArea displayArea) { 
        this.ip = ip; 
        this.port = new Integer(port); 
        this.fileName = fileName; 
        this.displayArea = displayArea;
    } 

    public void run() { 
        try { 
            final String houzhui = fileName.substring(fileName.lastIndexOf(".") + 1); 

            JFileChooser jfc = new JFileChooser(""); 
            jfc.setSelectedFile(new File(fileName + "")); 
            jfc.setFileFilter(new FileFilter() { 
                public boolean accept(File pathname) { 
                    if (pathname.getName().endsWith(houzhui)) 
                        return true; 
                    return false; 
                } 

                public String getDescription() { 
                    return houzhui; 
                } 
            }); 

            int result = jfc.showSaveDialog(null); 
            if (result == JFileChooser.APPROVE_OPTION) { 
                File f = jfc.getCurrentDirectory(); 
                String fileurl = f.getCanonicalPath(); 

                fos = new FileOutputStream(fileurl + File.separator + fileName); 
                socket = new Socket(ip, port); 
                is = socket.getInputStream(); 
                dis = new DataInputStream(is); 

                byte b[] = new byte[8192]; 
                int a = 0; 
                long l = 0L; 
                while ((a = dis.read(b)) != -1) { 
                    fos.write(b, 0, a); 
                    fos.flush(); 
                    l += a; 
                    System.out.println("已接收文件大小：" + l); 
                } 
                displayArea.append("文件" + fileName + "接收成功\n"); 
                System.out.println("文件接收成功"); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            try { 
                if (fos != null) { 
                    fos.close(); 
                } 
                if (dis != null) { 
                    dis.close(); 
                } 
                if (socket != null) { 
                    socket.close(); 
                } 
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
        } 
    } 
} 
