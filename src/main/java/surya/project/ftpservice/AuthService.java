package surya.project.ftpservice;
import java.io.*;
import java.util.Stack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import surya.project.components.DirInfo;


public class AuthService {
    private static FTPClient ftpclient;
    private ObservableList<DirInfo> childpath;
    private String workdir;
    private Stack<String> hispath;

    public AuthService(){
        ftpclient = new FTPClient();
        this.workdir = "/";
        hispath = new Stack<String>();
        childpath = FXCollections.observableArrayList();
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
                hispath.push(this.workdir);
                this.workdir = this.workdir + File.separator + file.getName();
                this.changeWorkDir(this.workdir);
                this.newDirectory(this.workdir);
                this.renameFolder(this.workdir + File.separator + "New Folder", file.getName());
                this.childpath = FXCollections.observableArrayList();
                for(File childFile : file.listFiles()){
                    if(childFile.exists()){
                        this.childpath.add(new DirInfo(childFile.getName(),
                                childFile.getAbsolutePath(), "Folder", childFile.length(), childFile.lastModified()));
                    }else{

                        break;
                    }
                }
                uploadFile(childpath);
            }else if(file.isFile()){
                this.changeWorkDir(this.workdir);
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
        if(!hispath.isEmpty()){
            this.changeWorkDir(hispath.pop());
        }
    }

    public void uploadSingleFile(String localPath, String remotePath) throws  Exception{
        File file = new File(localPath);
        InputStream fileupload = new FileInputStream(file);

        try {
            ftpclient.storeFile(remotePath, fileupload);
        }catch(Exception e){
            System.out.println(e);
        }finally{
            fileupload.close();
        }
    }

    public void deleteFile(ObservableList<DirInfo> listFiles) throws Exception {
//        boolean checkDirorNot = ftpclient.changeWorkingDirectory(remotepath);
        for(DirInfo file: listFiles){
            String remotepath = file.getPath() +"/"+ file.getName();
            ftpclient.deleteFile(remotepath);
            ftpclient.removeDirectory(remotepath);
        }
    }

    public void downloadFile(ObservableList<DirInfo> listFiles) throws FileNotFoundException, IOException {
        for(DirInfo file: listFiles){
            String remotepath = file.getPath() +"/"+ file.getName();

            FTPFile f = ftpclient.mlistFile(remotepath);


            String dir = new  File("").getAbsolutePath() + "/downloads/" + f.getName();
            FileOutputStream out = new FileOutputStream(dir);

            boolean result = ftpclient.retrieveFile(remotepath, out);
            System.out.printf("[downloadFIle] [%d] is success to download file : %s -> %b \n", System.currentTimeMillis(), remotepath, result);
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
        this.workdir = remotepath;
        ftpclient.changeWorkingDirectory(this.workdir);
    }
}
