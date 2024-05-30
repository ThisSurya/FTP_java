package surya.project.ftpservice;
import java.io.*;
import java.util.Stack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import surya.project.GlobalAuth.Global;
import surya.project.components.DirInfo;


public class AuthService {
    private static FTPClient ftpclient;
    private ObservableList<DirInfo> childpathlocal;
    private ObservableList<DirInfo> childpathftp;
    private String workdirftp;
    private String workdirlocal;
    private Stack<String> hispathlocal;
    private Stack<String> hispathftp;

    public AuthService(){
        ftpclient = new FTPClient();
        this.workdirftp = "/";
        this.workdirlocal = new File("").getAbsolutePath();
        hispathlocal = new Stack<String>();
        hispathftp = new Stack<String>();
        childpathlocal = FXCollections.observableArrayList();
        childpathftp = FXCollections.observableArrayList();
    }
    public void loginFtp(String hostname, int port, String username, String password) throws Exception {
        ftpclient.addProtocolCommandListener((ProtocolCommandListener) new PrintCommandListener(new PrintStream(System.out)));

        try{
            ftpclient.connect(hostname, port);
            int reply = ftpclient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)){
                ftpclient.disconnect();
                throw new Exception("FTP server refused connection");
            }
            boolean result = ftpclient.login(username, password);
            if(!result){
                ftpclient.disconnect();
                throw new Exception("FTP server refused to login");
            }
            ftpclient.changeWorkingDirectory("/");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public boolean isConnected(){
        return ftpclient.isConnected();
    }

    public FTPClient getClient(){
        return this.ftpclient;
    }

    public void logoutFTP(FTPClient ftpclient) throws IOException {
        ftpclient.logout();
    }

    public FTPFile[] getContent() throws IOException {
        FTPFile[] files = null;
        files = ftpclient.listFiles();
        return files;
    }

    public void uploadFile(ObservableList<DirInfo> localpaths) throws FileNotFoundException, IOException {
        for(DirInfo f: localpaths){
            String path = f.getPath();
            File file = new File(path);
            if(file.isDirectory()){
                hispathlocal.push(this.workdirftp);
                this.workdirftp = this.workdirftp + File.separator + file.getName();
                this.changeWorkDir(this.workdirftp);
                this.newDirectory(this.workdirftp);
                this.renameFolder(this.workdirftp + File.separator + "New Folder", file.getName());
                this.childpathlocal = FXCollections.observableArrayList();
                for(File childFile : file.listFiles()){
                    if(childFile.exists()){
                        this.childpathlocal.add(new DirInfo(childFile.getName(),
                                childFile.getAbsolutePath(), "Folder",
                                childFile.length(), childFile.lastModified()));
                    }else{
                        break;
                    }
                }
                uploadFile(childpathlocal);
            }else if(file.isFile()){
                this.changeWorkDir(this.workdirftp);
                try{
                    InputStream fileupload = new FileInputStream(file);
                    boolean result = ftpclient.storeFile(f.getName(), fileupload);
                    System.out.printf("[uploadFile] [%d] is success to upload file : %s -> %b \n",
                            System.currentTimeMillis(), f.getName(), result);
                }catch (Exception e){
                    System.out.println(e);
                }

            }
        }
        if(!hispathlocal.isEmpty()){
            this.changeWorkDir(hispathlocal.pop());
        }
    }

    public void deleteFile(ObservableList<DirInfo> listFiles) throws Exception {
//        boolean checkDirorNot = ftpclient.changeWorkingDirectory(remotepath);
        for(DirInfo file: listFiles){
            String remotepath = file.getPath() +"/"+ file.getName();
            FTPFile filesftp = ftpclient.mlistFile(remotepath);
            if(filesftp.isDirectory()){
                FTPFile[] files = ftpclient.listFiles(remotepath);
                if(files.length > 0){
                    System.out.println("Total didalam: "+files.length);
                    hispathftp.push(remotepath);
                    this.changeWorkDir(remotepath);
                    childpathftp = FXCollections.observableArrayList();
                    for (FTPFile subfile : ftpclient.listFiles(remotepath)) {
                        if(subfile.isValid()){
                            childpathftp.add(new DirInfo(subfile.getName(),
                                    Global.globalClient.getClient().printWorkingDirectory(),
                                    "File",
                                    subfile.getSize(),
                                    subfile.getTimestamp().getTimeInMillis()
                            ));
                        }else{
                            break;
                        }
                    }
                    deleteFile(childpathftp);
                }
                ftpclient.removeDirectory(remotepath);
            }else{
                ftpclient.deleteFile(remotepath);
            }
        }
        if(!hispathftp.isEmpty()){
            System.out.println("change workdir....");
            this.changeWorkDir(hispathftp.pop());
        }
    }

    public void downloadFile(ObservableList<DirInfo> listFiles) throws FileNotFoundException, IOException {
        for(DirInfo file: listFiles){
            String remotepath = file.getPath() +"/"+ file.getName();
            FTPFile f = ftpclient.mlistFile(remotepath);
            if(f.isDirectory()){
                hispathftp.push(remotepath);

                String path = new File("").getAbsolutePath() + "/downloads/" + remotepath;
                File localfile = new File(path);
                boolean dircheck = localfile.mkdirs();
                if(dircheck){
                    childpathftp = FXCollections.observableArrayList();
                    this.changeWorkDir(remotepath);
                    for(FTPFile subfile : ftpclient.listFiles(remotepath)){
                        if(subfile.isValid()){
                            childpathftp.add(new DirInfo(subfile.getName(),
                                    Global.globalClient.getClient().printWorkingDirectory(),
                                    "File",
                                    subfile.getSize(),
                                    subfile.getTimestamp().getTimeInMillis()
                            ));
                        }
                        else{
                            break;
                        }
                    }
                    downloadFile(childpathftp);
                }
            }else{
                this.changeWorkDir(remotepath);
                String dir = new  File("").getAbsolutePath() + "/downloads/" + remotepath;
                FileOutputStream out = new FileOutputStream(dir);

                boolean result = ftpclient.retrieveFile(remotepath, out);
                System.out.printf("[downloadFIle] [%d] is success to download file : %s -> %b \n", System.currentTimeMillis(), remotepath, result);
            }
        }
        if(!hispathftp.isEmpty()){
            this.changeWorkDir(hispathftp.pop());
        }
    }

    public boolean newDirectory(String remotepath) throws FileNotFoundException, IOException {
        boolean result = ftpclient.makeDirectory(remotepath);
        return result;
    }

    public boolean renameFolder(String path, String name) throws IOException {
        return ftpclient.rename(path, name);
    }

    public void changeWorkDir(String remotepath) throws FileNotFoundException, IOException {
        this.workdirftp = remotepath;
        ftpclient.changeWorkingDirectory(this.workdirftp);
    }
}
