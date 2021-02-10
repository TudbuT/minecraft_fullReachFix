package tudbut.tools;

import de.tudbut.io.StreamReader;
import tudbut.global.DebugStateManager;
import tudbut.obj.Atomic;
import tudbut.obj.Partial;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Tools2 {
    static Robot defaultRobot;
    
    static {
        try {
            defaultRobot = new Robot();
        } catch (AWTException e) {
            System.err.println("[TuddyLIB] Couldn't initialize AWT Tools");
        }
    }
    
    public synchronized static BufferedImage screenshot() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return defaultRobot.createScreenCapture(new Rectangle(screenSize));
    }
    
    public synchronized static void addFilesToZIP(File zipFile, Partial.Listener<File> done, File... files) throws IOException {
        byte[] buf = new byte[StreamReader.BUFFER_SIZE];
        
        ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(new StreamReader(new FileInputStream(zipFile)).readAllAsBytes()));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
        
        
        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            try {
                String name = entry.getName();
                boolean notInFiles = true;
                for (File f : files) {
                    if (f.getName().equals(name)) {
                        notInFiles = false;
                        break;
                    }
                }
                if (notInFiles) {
                    out.putNextEntry(new ZipEntry(name));
                    int len;
                    while ((len = zin.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
                entry = zin.getNextEntry();
            } catch (EOFException ignore) {
                entry = null;
            }
        }
        zin.close();
        for (File file : files) {
            InputStream in = new FileInputStream(file);
            out.putNextEntry(new ZipEntry(file.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in.close();
            done.onComplete(file);
        }
        
        out.close();
    }
    public synchronized static void addFilesToZIP(File zipFile, File... files) throws IOException {
        addFilesToZIP(zipFile, file -> {}, files);
    }
    
    public static void deleteDir(File dir) {
        try {
            File[] files = dir.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else
                    file.delete();
            }
            dir.delete();
        } catch (Exception ignore) {
        }
    }
    
    public static void copyDir(File dir, File dest) {
    
        Path sourcePath = dir.toPath();
        Path targetPath = dest.toPath();
        try(Stream<Path> filePaths = Files.walk(sourcePath)) {
            filePaths.forEach(filePath -> {
                try {
                    if (Files.isRegularFile(filePath)) {
                        Path newFile = targetPath.resolve(sourcePath.relativize(filePath));
                        Files.copy(filePath, newFile);
                    }else{
                        Path newDir = targetPath.resolve(sourcePath.relativize(filePath));
                        Files.createDirectory(newDir);
                    }
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }
    }
    
    public static double round(double d, int i) {
        if(i < 0)
            throw new IllegalArgumentException();
    
        return Math.round(d * Math.pow(10, i)) / Math.pow(10, i);
    }
}
