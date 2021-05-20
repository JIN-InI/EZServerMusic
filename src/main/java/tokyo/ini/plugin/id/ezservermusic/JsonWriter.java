package tokyo.ini.plugin.id.ezservermusic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JsonWriter {
    public static boolean writerMain(String packname){
        try {
            writer(new File(EZServerMusic.PackPath + "\\" +packname+ "\\assets\\ezsm\\sounds.json"),packname);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void writer(File file,String packname) throws IOException {
        com.google.gson.stream.JsonWriter writer = new com.google.gson.stream.JsonWriter(new FileWriter(file));
        try {
            writer.setIndent(" ");
            writer.beginObject();
            for (String name : GeneratePackClass.FileSearch(EZServerMusic.QueuePath)) {
                name = name.replace(".ogg", "");
                writer.name(packname+"."+name);
                writer.beginObject();
                writer.name("subtitle").value("Server's Music");
                writer.name("sounds");
                writer.beginArray();
                writer.beginObject();
                writer.name("name").value("ezsm:"+packname+"/" + name);
                writer.name("stream").value(true);
                writer.endObject();
                writer.endArray();
                writer.endObject();
            }
            writer.endObject();
            writer.close();
        } catch (Throwable throwable) {
            try {
                writer.close();
            } catch (Throwable th) {
                throwable.addSuppressed(th);
            }throw throwable;
        }
    }
}
