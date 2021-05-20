package tokyo.ini.plugin.id.ezservermusic;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.AudioFormat;
import com.github.kiulian.downloader.model.formats.Format;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class YoutubeDL extends Thread{

    private final String URL, Name;
    private final Callback callback;
    public YoutubeDL(String URL, String Name,Callback callback) {
        this.URL = URL;
        this.Name = Name;
        this.callback = callback;
    }

    interface Callback {
        void YTfinished(Boolean result);
    }

    public void run(){
        System.out.println("Start download from Youtube");
        System.out.println("URL: "+URL);
        System.out.println("Name: "+Name);
        final String VideoID = URL.replace("https://www.youtube.com/watch?v=","");
        YoutubeDownloader downloader = new YoutubeDownloader();
        YoutubeVideo video = null;
        try {
            video = downloader.getVideo(VideoID);
        } catch (YoutubeException e) {
            e.printStackTrace();
            callback.YTfinished(false);
        }
        List<AudioFormat> AudioFormats = video.audioFormats();

        Format format = AudioFormats.get(0);

        try {
            File YTVideo = video.download(format,new File(EZServerMusic.RawMusicPath));
            File RenameYTVideo = new File(EZServerMusic.RawMusicPath+"\\"+Name+".m4a");
            if(! YTVideo.renameTo(RenameYTVideo)) callback.YTfinished(false);
            System.out.println("Try to save as "+RenameYTVideo.getName());
        } catch (IOException | YoutubeException e) {
            e.printStackTrace();
            callback.YTfinished(false);
        }
        System.out.println("Download is success!");
        callback.YTfinished(true);
    }
}
