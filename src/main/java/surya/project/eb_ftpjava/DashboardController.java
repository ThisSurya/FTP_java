package surya.project.eb_ftpjava;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTPFile;
import surya.project.GlobalAuth.Global;
import surya.project.components.DirInfo;
import surya.project.dirtype.LocalDir;
//import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Stack;

public class DashboardController {
    @FXML
    private TableView<DirInfo> TableLocalView;
    @FXML
    private TableView<DirInfo> TableRemoteView;
    @FXML
    private TableView<DirInfo> TableProgressView;
    @FXML
    private TableColumn<DirInfo, String> TableLocalFilename;
    @FXML
    private TableColumn<DirInfo, String> TableLocalType;
    @FXML
    private TableColumn<DirInfo, Integer> TableLocalSize;
    @FXML
    private TableColumn<DirInfo, String> TableRemoteFilename;
    @FXML
    private TableColumn<DirInfo, String> TableRemoteType;
    @FXML
    private TableColumn<DirInfo, Integer> TableRemoteSize;
    private TableColumn<DirInfo, String> TableProgressFilename;
    private TableColumn<DirInfo, String> TableProgressType;
    private TableColumn<DirInfo, Integer> TableProgressSize;
    private TableColumn<DirInfo, String> TableProgressStatus;
    @FXML
    private Button deleteButton;
    @FXML
    private Button createDirButton;
    @FXML
    private Button backLocalButton;
    @FXML
    private Button backRemoteButton;
    @FXML
    private Button downloadButton;
    @FXML
    private Button uploadButton;
    @FXML
    private Button renameButton;

    private HelloApplication app;

//
//    UTILITIES FOR REMOVING, BACK DIRECTORY ETC.
//
    private Stage stage;

    private String filepathFTP;
    private String workftpdir;
    private Stack<String> lastpathftp;

    private Stack<String> lastpath;
    private String worklocaldir;
    private ObservableList<DirInfo> selectedItemsLocal;
    private ObservableList<DirInfo> selectedItemsftp;
    private String hisPath;

    public void Initialize(HelloApplication app) throws Exception {
        this.app = app;

//
//        Initialization
//
        lastpath = new Stack<String>();
        lastpathftp = new Stack<String>();
        workftpdir = Global.globalClient.getClient().printWorkingDirectory();
        worklocaldir = new File("").getAbsolutePath();
//
//        LOAD THE COMPONENT
//
        TableLocalFilename = new TableColumn<DirInfo, String>("name");
        TableLocalType = new TableColumn<DirInfo, String>("type");
        TableLocalSize = new TableColumn<DirInfo, Integer>("size");
        TableLocalView.setEditable(true);
        TableLocalView.getColumns().addAll(TableLocalFilename, TableLocalType, TableLocalSize);

        TableRemoteFilename = new TableColumn<DirInfo, String>("name");
        TableRemoteType = new TableColumn<DirInfo, String>("type");
        TableRemoteSize = new TableColumn<DirInfo, Integer>("size");
        TableRemoteView.setEditable(true);
        TableRemoteView.getColumns().addAll(TableRemoteFilename, TableRemoteType, TableRemoteSize);

        TableLocalFilename.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableLocalType.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableLocalSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        TableRemoteFilename.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableRemoteType.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableRemoteSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        TableProgressFilename = new TableColumn<DirInfo, String>("name");
        TableProgressType = new TableColumn<DirInfo, String>("type");
        TableProgressSize = new TableColumn<DirInfo, Integer>("size");
        TableProgressView.setEditable(true);
        TableProgressView.getColumns().addAll(TableProgressFilename, TableProgressType, TableProgressSize);

        TableLocalView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableRemoteView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        deleteButton.setDisable(true);
        backLocalButton.setDisable(true);
        backRemoteButton.setDisable(true);
        downloadButton.setDisable(true);
        renameButton.setDisable(true);

//
//        FILL THE TABLE WITH CONTENT
//
        showTableLocalFile(worklocaldir);
        showTableRemoteFile(workftpdir);

//
//        EVENT IN THE APPLICATION
//

        TableLocalView.setOnMouseClicked((event) -> {clickOnLocalTable(event);});
        TableRemoteView.setOnMouseClicked((event) -> {clickOnRemoteTable(event);});
        deleteButton.setOnAction((event) -> {deleteOnRemote();});
        createDirButton.setOnAction((event) -> {newDirectory();});
        backLocalButton.setOnAction((event) -> {backDirectoryLocal();});
        backRemoteButton.setOnAction((event) -> {backDirectoryFTP();});
        downloadButton.setOnAction((event) -> {downloadMultiFile();});
        uploadButton.setOnAction((event) -> {uploadFile();});
        renameButton.setOnAction((event) -> {
            try {
                renameForm();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void showTableLocalFile(String path) throws Exception {
//        Check lastpath have minimal a history path
        this.worklocaldir = path;
        if(lastpath.empty()){
            backLocalButton.setDisable(true);
        }else{
            backLocalButton.setDisable(false);
        }

        File[] file = new LocalDir().getContent(path);
        TableLocalView.getItems().clear();
        if(file.length == 0 ){
            throw new Exception("Kesalahan saat membaca direktori tersebut");
        }

        for(File f : file){
            if(f.isDirectory()){
                DirInfo dir = new DirInfo(f.getName(), f.getAbsolutePath(), "Folder", f.length(), f.lastModified());
                TableLocalView.getItems().add(dir);
            } else if (f.isFile()) {
                DirInfo fil = new DirInfo(f.getName(), f.getAbsolutePath(), "File", file.length, f.lastModified());
                TableLocalView.getItems().add(fil);
            }else{
                continue;
            }
        }
    }

    public void showTableRemoteFile(String path) throws Exception {
//        Check if lastPath have minimal a history path
        this.workftpdir = path;
        try{
            Global.globalClient.changeWorkDir(workftpdir);
            if(lastpathftp.empty()){
                backRemoteButton.setDisable(true);
            }else{
                backRemoteButton.setDisable(false);
            }

            FTPFile[] files = Global.globalClient.getContent();
            TableRemoteView.getItems().clear();
            for(FTPFile f : files){
                if(f.isFile()){
                    DirInfo fil = new DirInfo(f.getName(),
                            Global.globalClient.getClient().printWorkingDirectory(),
                            "File",
                            f.getSize(),
                            f.getTimestamp().getTimeInMillis()
                    );
                    TableRemoteView.getItems().add(fil);
                } else if (f.isDirectory()) {
                    DirInfo fil = new DirInfo(f.getName(),
                            Global.globalClient.getClient().printWorkingDirectory(),
                            "Folder",
                            f.getSize(),
                            f.getTimestamp().getTimeInMillis()
                    );
                    TableRemoteView.getItems().add(fil);
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }

    }

    private void clickOnLocalTable(MouseEvent event){
        DirInfo checkdir = TableLocalView.getSelectionModel().getSelectedItem();
        selectedItemsLocal = TableLocalView.getSelectionModel().getSelectedItems();

        if(selectedItemsLocal.isEmpty() || checkdir.getName().isEmpty()){
            uploadButton.setDisable(true);
        }else {
            uploadButton.setDisable(false);
        }

        if(!checkdir.getPath().isEmpty()){
            this.filepathFTP = checkdir.getPath();
        }
//        if double click
        if(event.getClickCount() == 2){
            try{
                if(new File(checkdir.getPath()).isDirectory()){
                    lastpath.push(worklocaldir);
                    showTableLocalFile(checkdir.getPath());
                }
                else if(new File(checkdir.getPath()).isFile()){
//                    Global.globalClient.uploadFile(selectedItems);
//                    this.uploadFile(checkdir.getPath(), checkdir.getName());
                    this.uploadFile();
                    showTableRemoteFile(this.workftpdir);
                }
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }

    private void clickOnRemoteTable(MouseEvent event){
        DirInfo checkdir = TableRemoteView.getSelectionModel().getSelectedItem();
        selectedItemsftp = TableRemoteView.getSelectionModel().getSelectedItems();

        if(!checkdir.getPath().isEmpty() || !selectedItemsftp.isEmpty()){
            this.filepathFTP = checkdir.getPath() +"/"+ checkdir.getName();
            deleteButton.setDisable(false);
            downloadButton.setDisable(false);
            renameButton.setDisable(false);
        }else {
            downloadButton.setDisable(true);
            deleteButton.setDisable(true);
            renameButton.setDisable(true);
            System.out.println("Pliss if u want to delete a file select random file first!!");
        }

        if (event.getClickCount() == 2){
            try{
                boolean isDir = Global.globalClient.getClient().changeWorkingDirectory(this.filepathFTP);
                if(isDir){
                    lastpathftp.push(this.workftpdir);
//                    Global.globalClient.changeWorkDir(this.filepathFTP);
                    showTableRemoteFile(this.filepathFTP);
                }else {
                    this.downloadSingleFile();
                }
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }


    private void deleteOnRemote(){
        try{
            Global.globalClient.deleteFile(this.selectedItemsftp);
            deleteButton.setDisable(true);
            showTableRemoteFile(this.workftpdir);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void backDirectoryLocal(){
        try{
            String hisPath = lastpath.pop();
            showTableLocalFile(hisPath);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void backDirectoryFTP(){
        try{
            this.hisPath = lastpathftp.pop();
            System.out.println(hisPath);
            showTableRemoteFile(hisPath);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void newDirectory(){
        try{
            String pathDir = Global.globalClient.getClient().printWorkingDirectory() + "/New Folder";
            Global.globalClient.newDirectory(pathDir);
            showTableRemoteFile(workftpdir);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void downloadMultiFile(){
        try{
            Global.globalClient.downloadDir(this.selectedItemsftp);
            showTableRemoteFile(this.workftpdir);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void downloadSingleFile(){
        try{
            Global.globalClient.downloadFile(this.filepathFTP);
            showTableRemoteFile(this.workftpdir);
        }catch(Exception e){
            System.out.println(e);
        }
    }

//    private void uploadFile(String filepath, String name){
//        try{
//            Global.globalClient.uploadFile(filepath, name);
//        }catch(Exception e){
//            System.out.println(e);
//        }
//    }
    private void renameForm() throws Exception {
        try{
            app.showRename(this.filepathFTP);
        }catch(Exception e){
            System.out.println(e);
        }finally{
            showTableRemoteFile(this.hisPath);
        }

    }

    private void uploadFile(){
        try{
            if(!this.selectedItemsLocal.isEmpty()){
                Global.globalClient.uploadFile(this.selectedItemsLocal);
                showTableRemoteFile(this.workftpdir);
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
