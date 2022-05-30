package tp1.impl.dropbox;

public class UploadFileArg {

    final String path;
    final String mode;
    final boolean autorename;
    final boolean mute;

    public UploadFileArg(String path) {
        this.path = path;
        this.mode = "overwrite";
        this.autorename = false;
        this.mute = false;
    }
}
