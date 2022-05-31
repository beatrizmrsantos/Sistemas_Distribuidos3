package tp1.impl.dropbox;

public record UploadFileArg (String path, String mode, boolean autorename, boolean mute, boolean strict_conflict) {

    public UploadFileArg(String path) { this(path, "overwrite", false, false, false);   }
}
