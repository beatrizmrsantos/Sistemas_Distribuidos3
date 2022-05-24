package tp2.impl.dropbox;


public record GetMetadataArg( String path, boolean include_media_info, boolean include_deleted, boolean include_has_explicit_shared_members) {
    public GetMetadataArg( String path) {
        this( path, false, false, false);
    }
}