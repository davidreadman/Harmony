


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WriteLog
{
    public String filePath;
    //CSVWriter writer;
    FileWriter outputfile;
    File file;
    List<String> linesWritten = new ArrayList<>();

    //constructor
    WriteLog(String directoryName)
    {
        //set up a logging file with a time basis
        //get local time
        LocalDateTime date = LocalDateTime.now();
        //format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmm");
        String text = date.format(formatter);
        LocalDateTime parsedDate = LocalDateTime.parse(text, formatter);
        //remove annoying extra stuff that localtimedate sticks in
        filePath = parsedDate.toString().replace("-", "")
                .replace("T", "").replace(":", "");
        filePath = "log" + filePath +".csv";
        filePath = String.format("%s/%s",directoryName, filePath);
        //next line will create writing file
        file = new File(filePath);


        try

        {
            // create FileWriter object with file as parameter
            outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            // writer = new CSVWriter(outputfile);

        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void writeStringToFile(String outputString)
    {
        try
        {
            outputfile.write(outputString);
            linesWritten.add(outputString);
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    void closeAndFlush()
    {
        try
        {
            outputfile.flush();
            outputfile.close();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    void Flush()
    {
        try
        {
            outputfile.flush();

        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    String getTimeStamp()
    {       //get local time

        LocalDateTime date = LocalDateTime.now();
        //format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmSS");
        String text = date.format(formatter);
        //LocalDateTime parsedDate = LocalDateTime.parse(text, formatter);

        //remove annoying extra stuff that localtimedate sticks in
        return text.replace("-", "")
                .replace("T", "").replace(":", "");
    }

}
