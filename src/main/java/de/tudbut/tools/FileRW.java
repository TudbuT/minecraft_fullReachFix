package de.tudbut.tools;

import de.tudbut.io.StreamReader;
import de.tudbut.type.StringArray;

import java.io.*;

public class FileRW {

    private final StringArray lines;
    protected File file;

    public FileRW(String path) throws IOException {

        this.file = new File(path);

        if (!this.file.exists()) {
            this.file.createNewFile();
            new BufferedWriter(new FileWriter(this.file)).write("\n");
        }
        this.lines = new StringArray();
        rereadFile();
    }

    public StringArray getContent() {
        return this.lines;
    }

    public void setContent(String content) throws IOException {
        this.lines.clear();
        this.lines.set(content.split("\n"));
        FileOutputStream fileWriter = new FileOutputStream(this.file);
        PrintWriter writer = new PrintWriter(fileWriter);
        writer.write(this.lines.join("\n"));
        writer.close();
    }

    public void setContent(StringArray content) throws IOException {
        setContent(content.join("\n"));
    }

    public void rereadFile() throws IOException {
        StreamReader reader = new StreamReader(new FileInputStream(file));
        char[] chars = reader.readAllAsChars();
        String[] s = new String(chars).split("\n");
        lines.clear();
        for (String value : s) {
            lines.add(value);
        }
    }
} 
