package be.ucll.java.ent.file;

import be.ucll.java.ent.controller.MessageController;
import be.ucll.java.ent.domain.ChatMessageDTO;
import be.ucll.java.ent.soap.model.v1.ChatRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class SheduledTasks {

    private static final Logger msgLogger = LoggerFactory.getLogger("messagelogger");

    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @Value("${io.input}")
    private String inputDir;

    @Value("${io.processing}")
    private String processingDir;

    @Value("${io.processed}")
    private String processedDir;

    @Value("${io.error}")
    private String errorDir;

    @Scheduled(fixedRate = 5000)
    public void procesChatFiles() {
        try {
            checkDirs();

            File input = new File(inputDir);
            File processing = new File(processingDir);

            // Any file in input dir? Move to processing dir
            File[] files = input.listFiles();
            if (files != null) {
                for (File file : files) {
                    // check if it is truly a File and not a Directory
                    if (file.isFile() && file.canWrite()) {
                        file.renameTo(new File(processing.getAbsolutePath() + File.separator + file.getName()));
                    }
                }
            }

            files = processing.listFiles();
            if (files != null) {
                for (File file : files) {
                    // check if it is truly a File and not a Directory
                    if (file.isFile() && file.canWrite()) {
                        try {
                            JAXBContext contextObj= JAXBContext.newInstance(ChatRequest.class);
                            Unmarshaller unmarshaller= contextObj.createUnmarshaller();
                            ChatRequest cr = (ChatRequest) unmarshaller.unmarshal(file);

                            msgLogger.info("FILE | " + cr.getSender() + " | " + cr.getMessage());

                            file.renameTo(new File(new File(processedDir).getAbsolutePath() + File.separator + file.getName()));
                        } catch (Exception e) {
                            file.renameTo(new File(new File(errorDir).getAbsolutePath() + File.separator + file.getName()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkDirs() throws Exception {
        checkDir(new File(inputDir));
        checkDir(new File(processingDir));
        checkDir(new File(processedDir));
        checkDir(new File(errorDir));
    }

    private void checkDir(File dir) throws Exception {
        if (!dir.exists()) dir.mkdirs();
        if (!dir.canWrite()) throw new Exception("Unable to write in directory " + dir.getAbsolutePath());
    }
}
