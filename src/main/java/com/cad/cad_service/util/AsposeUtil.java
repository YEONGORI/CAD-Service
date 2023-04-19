package com.cad.cad_service.util;

import com.aspose.cad.Image;
import com.aspose.cad.fileformats.cad.CadImage;
import com.aspose.cad.fileformats.cad.cadconsts.CadEntityTypeName;
import com.aspose.cad.fileformats.cad.cadobjects.CadBaseEntity;
import com.aspose.cad.fileformats.cad.cadobjects.CadBlockEntity;
import com.aspose.cad.fileformats.cad.cadobjects.CadMText;
import com.aspose.cad.fileformats.cad.cadobjects.CadText;
import com.aspose.cad.imageoptions.CadRasterizationOptions;
import com.aspose.cad.imageoptions.JpegOptions;
import com.cad.cad_service.controller.CadController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class AsposeUtil {
    private final S3Util s3Util;
    private final Logger log = LoggerFactory.getLogger(CadController.class);
    private static final String cadDir = System.getProperty("user.home") + File.separator + "cad" + File.separator;

    public Map<String, String[]> getCadInfo(String project) {
        try {
            Stream
        } catch () {
        }
    }

//    public Map<String, String[]> getCadInfo(String project) {
//        try {
//            Map<String, String[]> cadInfo = new HashMap<>();
//            Files.walkFileTree(Paths.get(cadDir + project), new SimpleFileVisitor<>() {
//                @Override
//                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                    if (!Files.isDirectory(file) && file.getFileName().toString().contains(".dwg")) {
//                        String title = file.getFileName().toString();
//                        String path = file.toAbsolutePath().toString();
//                        String index = extractCadIndex(path);
//                        ByteArrayOutputStream stream = convertCadToJpeg(path);
//                        String s3Url = s3Util.uploadImg(title, stream);
//                        path = path.substring(path.indexOf(project) + project.length(), path.indexOf(title) -1);
//                        cadInfo.put(index, new String[] {path, title, s3Url});
//                    }
//                    return FileVisitResult.CONTINUE;
//                }
//            });
//            return cadInfo;
//        } catch (IOException e) {
//            log.error("getCadInfo IOException: ", e);
//            return null;
//        }
//    }

    private ByteArrayOutputStream convertCadToJpeg(String cad) {
            Image image = Image.load(cad);
            CadRasterizationOptions options = new CadRasterizationOptions();
            options.setPageHeight(200);
            options.setPageWidth(200);

            JpegOptions jpegOptions = new JpegOptions();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            jpegOptions.setVectorRasterizationOptions(options);

            image.save(stream, jpegOptions);
            return stream;
    }

    private String extractCadIndex(String cad) {
        try {
            Set<String> index = new HashSet<>();
            CadImage cadImage = (CadImage) CadImage.load(cad);
            for (CadBlockEntity blockEntity : cadImage.getBlockEntities().getValues()) {
                for (CadBaseEntity entity : blockEntity.getEntities()) {
                    if (entity.getTypeName() == CadEntityTypeName.TEXT) {
                        CadText cadText = (CadText) entity;
                        index.add(filterCadIndex(cadText.getDefaultValue()));
                    }
                    else if (entity.getTypeName() == CadEntityTypeName.MTEXT) {
                        CadMText cadMText = (CadMText) entity;
                        index.add(filterCadIndex(cadMText.getText()));
                    }
                }
            }
            return String.join(" | ", index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String filterCadIndex(String index) {
        String filtered = index.replaceAll(" ", "");
        int numCnt = (int) filtered.chars().filter(c -> c >= '0' && c <= '9').count();
        if (numCnt >= filtered.length() / 2)
            return "";
        return filtered;
    }
}
