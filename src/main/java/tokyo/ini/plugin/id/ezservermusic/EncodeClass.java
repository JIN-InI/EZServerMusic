package tokyo.ini.plugin.id.ezservermusic;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EncodeClass extends Thread{
    private final String name;
    private final Callback callback;
    public EncodeClass(String name,Callback callback){
        this.name = name;
        this.callback = callback;
    }

    interface Callback {
        void ENCfinished(Boolean result);
    }
    public void run(){
        String DeliverablePath = null;
        try {
            File source = new File(EZServerMusic.RawMusicPath+"\\"+name);
            File target = new File(EZServerMusic.OGGPath+"\\"+name.toLowerCase().replaceAll("\\..+",".ogg"));
            DeliverablePath = target.getAbsolutePath();
            //Audio Attributes
            AudioAttributes audio = new AudioAttributes();
            audio.setBitRate(128000);
            audio.setChannels(2);
            audio.setSamplingRate(44100);
            //Encoding attributes
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("ogg");
            attrs.setAudioAttributes(audio);
            //Encode
            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(source), target, attrs);
        } catch (Exception ex) {
            ex.printStackTrace();
            callback.ENCfinished(false);
        }
        if(EZServerMusic.addQueue){
            try {
                Files.copy(Path.of(DeliverablePath),
                        Path.of(EZServerMusic.QueuePath + "\\" + name.toLowerCase().replaceAll("\\..+",".ogg")));
            } catch (IOException e) {
                e.printStackTrace();
                callback.ENCfinished(false);
            }
        }
        callback.ENCfinished(true);
    }
}
