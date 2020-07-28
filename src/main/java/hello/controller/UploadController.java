package hello.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Description
 * @Author sgl
 * @Date 2018-05-15 14:04
 */
@Controller
public class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    public static List<String> fileList = new ArrayList<>();
    static {
        final File file = new File("/data/file");
        if (file.exists()) {
            final File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                final String f = files[i].getName();
                fileList.add(f);
                LOGGER.info("file {} added", f);
            }
        } else {
            LOGGER.info("no file.");
        }
    }

    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "上传失败，请选择文件";
        }

        String fileName = file.getOriginalFilename();
        String filePath = "/data/file/";
        File dir = new File(filePath);
        if (!dir.exists()) {
            try {
                dir.createNewFile();
            } catch (IOException e) {
                LOGGER.error("error", e);
            }
        }
        File dest = new File(filePath + fileName);
        try {
            dest.createNewFile();
            file.getInputStream();
            OutputStream os = new FileOutputStream(dest);
            os.write(file.getBytes());
//            file.transferTo(dest);
            LOGGER.info("上传成功");
            fileList.add(fileName);
            return "success";
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
        }
        return "fail！";
    }

    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }
}