package com.finops.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtil {


    public static void moveFilesToFileSystem(Map<String, byte[]> map, String path, String module, String uniqueId) {
        for (Map.Entry<String, byte[]> entry : map.entrySet()) {
            OutputStream os = null;
            try {
                String directory = path + module + "\\" + uniqueId.trim();
                Files.createDirectories(Paths.get(directory));
                if(entry.getValue() != null) {
                    File file = new File(directory + "\\" + entry.getKey());
                    os = new FileOutputStream(file);
                    os.write(entry.getValue());
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if(os != null)
                        os.close();
                } catch (IOException ex) {
                    Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static Map<String, byte[]> getKycAttachmentsDetailFromStream(MultipartFile... files) throws IOException {

        Map<String, byte[]> attachmentMap = new LinkedHashMap<>();
        int i = 1;
        for (MultipartFile file : files) {
            if (file != null && !StringUtils.hasText(file.getOriginalFilename())) {
                attachmentMap.put("EMPTY"+i, null);
                i++;
                continue;
            }
            String a = file.getOriginalFilename();
            InputStream is = file.getInputStream();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            byte[] bytes = buffer.toByteArray();

            //byte[] bytes = new byte[is.available()];
            if (StringUtils.hasText(a)) {
                attachmentMap.put(a, bytes);
            }
            i++;
        }
        return attachmentMap;
    }

}
