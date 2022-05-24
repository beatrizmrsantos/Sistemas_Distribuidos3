package tp2.impl.servers.common;

import org.pac4j.scribe.builder.api.DropboxApi20;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;

import tp2.impl.dropbox.CreateFolderV2Args;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tp2.api.service.java.Files;
import tp2.api.service.java.Result;

import tp2.impl.dropbox.DeleteArg;
import tp2.impl.dropbox.DownloadArg;
import tp2.impl.dropbox.ListFolderArgs;
import tp2.impl.dropbox.ListFolderContinueArgs;
import tp2.impl.dropbox.ListFolderReturn;
import tp2.impl.dropbox.UploadFileArg;
import util.IO;

import static tp2.api.service.java.Result.ErrorCode.INTERNAL_ERROR;
import static tp2.api.service.java.Result.ErrorCode.NOT_FOUND;
import static tp2.api.service.java.Result.error;
import static tp2.api.service.java.Result.ok;

public class JavaFilesDropBox implements Files {

    static final String DELIMITER = "$$$";
    private static final String ROOT = "/tmpDropBox/";

    private static final String apiKey = "ki9t63870k4ifvy";
    private static final String apiSecret = "j3nbd4eccpdvlj8";
    private static final String accessTokenStr = "sl.BIF23P_LP63yXezwdh0QBux72PycJUG1WWYW0J2t2t0SSfmNykDIgSZsk88yLoiRiAOK2qo8rwwVznfc-8PlDE-VT20IApkFdJOvIxaeft9GMxxWu94HiSXKVjlWgE_ZpMk3x6w";

    private static final String CREATE_FOLDER_V2_URL = "https://api.dropboxapi.com/2/files/create_folder_v2";

    private static final String LIST_FOLDER_URL = "https://api.dropboxapi.com/2/files/list_folder";
    private static final String LIST_FOLDER_CONTINUE_URL = "https://api.dropboxapi.com/2/files/list_folder/continue";

    private static final String DOWNLOAD_URL = "https://content.dropboxapi.com/2/files/download";

    private static final String DELETE_V2_URL = "https://api.dropboxapi.com/2/files/delete_v2";

    private static final String UPLOAD_URL = "https://content.dropboxapi.com/2/files/upload";

    private static final int HTTP_SUCCESS = 200;
    private static final String CONTENT_TYPE_HDR = "Content-Type";
    private static final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

    private final Gson json;
    private final OAuth20Service service;
    private final OAuth2AccessToken accessToken;

    public JavaFilesDropBox() {
        json = new Gson();
        accessToken = new OAuth2AccessToken(accessTokenStr);
        service = new ServiceBuilder(apiKey).apiSecret(apiSecret).build(DropboxApi20.INSTANCE);

        try{
            createDirectory(ROOT);
        } catch (Exception e){
        }

    }


    @Override
    public Result<byte[]> getFile(String fileId, String token) {
        fileId = fileId.replace( DELIMITER, "/");

        try{
            byte[] data = download(ROOT + fileId );

            return ok( data);

        }catch (Exception e){

            return error( NOT_FOUND );
        }

    }

    @Override
    public Result<Void> deleteFile(String fileId, String token) {
        fileId = fileId.replace( DELIMITER, "/");
        boolean res = true;

        try{
            removeFile(ROOT + fileId );
        }catch (Exception e){
            res = false;
        }

        return res ? ok() : error( NOT_FOUND );
    }

    @Override
    public Result<Void> writeFile(String fileId, byte[] data, String token) {
        fileId = fileId.replace( DELIMITER, "/");
        File file = new File(ROOT + fileId);
        file.getParentFile().mkdirs();
        IO.write( file, data);


        try{
            write(ROOT + fileId );
        }catch (Exception e){
        }

        return ok();
    }

    @Override
    public Result<Void> deleteUserFiles(String userId, String token) {
        userId = userId.concat(ROOT + userId);

        try{
            removeFile(ROOT + userId );
        }catch (Exception e){
            return error(INTERNAL_ERROR);
        }

        return ok();
    }

    public static String fileId(String filename, String userId) {
        return userId + JavaFiles.DELIMITER + filename;
    }



    private void createDirectory(String fileId) throws Exception {

        String dirName = "";

        var createFolder = new OAuthRequest(Verb.POST, CREATE_FOLDER_V2_URL);
        createFolder.addHeader(CONTENT_TYPE_HDR, JSON_CONTENT_TYPE);

        createFolder.setPayload(json.toJson(new CreateFolderV2Args(dirName, false)));

        service.signRequest(accessToken, createFolder);

        Response r = service.execute(createFolder);
        if (r.getCode() != HTTP_SUCCESS)
            throw new RuntimeException();
    }

    public List<String> ListFiles(String directoryName) throws Exception {
        var directoryContents = new ArrayList<String>();

        var listDirectory = new OAuthRequest(Verb.POST, LIST_FOLDER_URL);
        listDirectory.addHeader(CONTENT_TYPE_HDR, JSON_CONTENT_TYPE);
        listDirectory.setPayload(json.toJson(new ListFolderArgs(directoryName)));

        service.signRequest(accessToken, listDirectory);

        Response r = service.execute(listDirectory);;
        if (r.getCode() != HTTP_SUCCESS)
            throw new RuntimeException(String.format("Failed to list directory: %s, Status: %d, \nReason: %s\n", directoryName, r.getCode(), r.getBody()));

        var reply = json.fromJson(r.getBody(), ListFolderReturn.class);
        reply.getEntries().forEach( e -> directoryContents.add( e.toString() ) );

        while( reply.has_more() ) {
            listDirectory = new OAuthRequest(Verb.POST, LIST_FOLDER_CONTINUE_URL);
            listDirectory.addHeader(CONTENT_TYPE_HDR, JSON_CONTENT_TYPE);

            // In this case the arguments is just an object containing the cursor that was
            // returned in the previous reply.
            listDirectory.setPayload(json.toJson(new ListFolderContinueArgs(reply.getCursor())));
            service.signRequest(accessToken, listDirectory);

            r = service.execute(listDirectory);

            if (r.getCode() != HTTP_SUCCESS)
                throw new RuntimeException(String.format("Failed to list directory: %s, Status: %d, \nReason: %s\n", directoryName, r.getCode(), r.getBody()));

            reply = json.fromJson(r.getBody(), ListFolderReturn.class);
            reply.getEntries().forEach( e -> directoryContents.add( e.toString() ) );
        }

        return directoryContents;
    }

    public byte[] download(String uri) throws Exception{

        var download = new OAuthRequest(Verb.POST, DOWNLOAD_URL);
        download.addHeader(CONTENT_TYPE_HDR, JSON_CONTENT_TYPE);
        download.setPayload(json.toJson(new DownloadArg(uri)));

        service.signRequest(accessToken, download);

        Response r = service.execute(download);;
        if (r.getCode() != HTTP_SUCCESS)
            throw new RuntimeException();

        //var reply = json.fromJson(r.getBody(), GetMetadataReturn.class);

        var in = r.getStream();

        byte[] data = new byte[in.available()];
        in.read(data);

        return data;

    }

    public void removeFile(String uri) throws Exception{

        var deleted = new OAuthRequest(Verb.POST, DELETE_V2_URL);
        deleted.addHeader(CONTENT_TYPE_HDR, JSON_CONTENT_TYPE);
        deleted.setPayload(json.toJson(new DeleteArg(uri)));

        service.signRequest(accessToken, deleted);

        Response r = service.execute(deleted);;
        if (r.getCode() != HTTP_SUCCESS)
            throw new RuntimeException();

    }

    public void write(String uri) throws Exception{

        var write = new OAuthRequest(Verb.POST, UPLOAD_URL);
        write.addHeader(CONTENT_TYPE_HDR, JSON_CONTENT_TYPE);
        write.setPayload(json.toJson(new UploadFileArg(uri)));

        service.signRequest(accessToken, write);

        Response r = service.execute(write);;
        if (r.getCode() != HTTP_SUCCESS)
            throw new RuntimeException();

    }
}
