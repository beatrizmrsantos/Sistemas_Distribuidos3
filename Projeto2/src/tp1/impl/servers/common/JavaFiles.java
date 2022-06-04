package tp1.impl.servers.common;

import static tp1.api.service.java.Result.ErrorCode.*;
import static tp1.api.service.java.Result.error;
import static tp1.api.service.java.Result.ok;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import tp1.api.service.java.Files;
import tp1.api.service.java.Result;
import tp1.impl.kafka.KafkaSubscriber;
import tp1.impl.kafka.RecordProcessor;
import util.IO;
import util.Token;

public class JavaFiles implements Files {

    static final String DELIMITER = "$$$";
    private static final String ROOT = "/tmp/";

    private static final String FROM_BEGINNING = "earliest";
    static final String KAFKA_BROKERS = "kafka:9092";
    static final String TOPIC = "delete";


    public JavaFiles() {
        new File(ROOT).mkdirs();

        KafkaSubscriber subscriber = KafkaSubscriber.createSubscriber(KAFKA_BROKERS, List.of(TOPIC),
                FROM_BEGINNING);

        subscriber.start(false, new RecordProcessor() {

            @Override
            public void onReceive(ConsumerRecord<String, String> r) {
                String[] value = r.value().split("#");
                String id = value[0];
                String token = value[1];

                if (r.key().equalsIgnoreCase("deleteUserFiles")) {
                    deleteUserFiles(id, token);
                } else {
                    if (getFile(id, token).isOK()) {
                        deleteFile(id, token);
                    }
                }
            }
        });
    }

    @Override
    public Result<byte[]> getFile(String fileId, String token) {
        if(!Token.matches(fileId, System.currentTimeMillis(), token, "FILES_EXTRA_ARGS")){
            return error(BAD_REQUEST);
        }

        fileId = fileId.replace(DELIMITER, "/");
        byte[] data = IO.read(new File(ROOT + fileId));
        return data != null ? ok(data) : error(NOT_FOUND);
    }

    @Override
    public Result<Void> deleteFile(String fileId, String token) {
        if(!Token.matches(fileId, System.currentTimeMillis(), token, "FILES_EXTRA_ARGS")){
            return error(BAD_REQUEST);
        }

        fileId = fileId.replace(DELIMITER, "/");
        boolean res = IO.delete(new File(ROOT + fileId));
        return res ? ok() : error(NOT_FOUND);
    }

    @Override
    public Result<Void> writeFile(String fileId, byte[] data, String token) {
        if(!Token.matches(fileId, System.currentTimeMillis(), token, "FILES_EXTRA_ARGS")){
            return error(BAD_REQUEST);
        }

        fileId = fileId.replace(DELIMITER, "/");
        File file = new File(ROOT + fileId);
        file.getParentFile().mkdirs();
        IO.write(file, data);

        return ok();
    }

    @Override
    public Result<Void> deleteUserFiles(String userId, String token) {
        if(!Token.matches(userId, System.currentTimeMillis(), token, "FILES_EXTRA_ARGS")){
            return error(BAD_REQUEST);
        }

        File file = new File(ROOT + userId);
        try {
            java.nio.file.Files.walk(file.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
            return error(INTERNAL_ERROR);
        }
        return ok();
    }

    public static String fileId(String filename, String userId) {
        return userId + JavaFiles.DELIMITER + filename;
    }

}
