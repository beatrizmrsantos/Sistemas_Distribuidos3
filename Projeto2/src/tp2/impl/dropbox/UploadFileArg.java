package tp2.impl.dropbox;

//import com.dropbox.core.v2.files.WriteMode;

//falta o argumento mode que e o um writeMode para ser overwrite
public record UploadFileArg(String path, boolean autorename, boolean mute, boolean strict_conflict) {
    public UploadFileArg( String path) {
        this( path, false, false, false);
    }
}
