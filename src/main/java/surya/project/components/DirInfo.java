package surya.project.components;

import javafx.scene.image.ImageView;

import java.text.SimpleDateFormat;

public class DirInfo {
    private ImageView icon;
    private String name;
    private String path;
    private String type;
    private long size;
    private long lastMod;

    public DirInfo(String name, String path, String type, long size, long lastMod) {
//        this.icon = icon;
        this.name = name;
        this.path = path;
        this.type = type;
        this.size = size;
        this.lastMod = lastMod;
    }

    public ImageView getIcon() {
        return icon;
    }

    public String getName(){
        return name;
    }

    public String getPath(){
        return path;
    }

    public String getType(){
        return type;
    }

    public long getSize(){
        return size;
    }

    public long getLastMod(){
        return lastMod;
    }

    public String convertSizetoString(long size){
        if(size > 1449616){
            return size / (1024*1024) + "Mb";
        } else if (size > 1024) {
            return size / (1024) + "Kb";
        }else {
            return size + "B";
        }
    }

    public String lastModtoString(long lastMod){
        return new SimpleDateFormat("yyyy/MM/dd HH::mm:ss").format(lastMod);
    }

    @Override
    public String toString() {
        return name;
    }
}
