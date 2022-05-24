package tp2.impl.dropbox;

import java.util.HashMap;
import java.util.List;

public class GetMetadataReturn {

    private FolderEntry entrie;

    public GetMetadataReturn() {

    }

    public FolderEntry getEntrie() {
        return entrie;
    }

    public void setEntries(FolderEntry entrie) {
        this.entrie = entrie;
    }

    public static class FolderEntry extends HashMap<String, Object> {
        private static final long serialVersionUID = 1L;
        private static final String NAME = "name";

        public String toString() {
            return get(NAME).toString();
        }
    }


}
