import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


import java.io.*;

public class JavaImageIOTest
{
    public static boolean setCurrentDirectory(String directory_name)
    {
        boolean result = false;  // Boolean indicating whether directory was set
        File    directory;       // Desired current working directory

        directory = new File(directory_name).getAbsoluteFile();
        if (directory.exists() || directory.mkdirs())
        {
            result = (System.setProperty("user.dir", directory.getAbsolutePath()) != null);
        }

        return result;
    }

    public static PrintWriter openOutputFile(String file_name)
    {
        PrintWriter output = null;  // File to open for writing

        try
        {
            output = new PrintWriter(new File(file_name).getAbsoluteFile());
        }
        catch (Exception exception) {}

        return output;
    }




  public JavaImageIOTest()
  {
    try
    {
      // the line that reads the image file
      BufferedImage image = ImageIO.read(new File("/Users/al/some-picture.jpg"));

      // work with the image here ...
    }
    catch (IOException e)
    {
      // log the exception
      // re-throw if desired
    }
  }

    public static void main(String[] args) throws Exception
    {
        FileUtils.openOutputFile("DefaultDirectoryFile.txt");
        FileUtils.setCurrentDirectory("NewCurrentDirectory");
        FileUtils.openOutputFile("CurrentDirectoryFile.txt");

    	new ImageIOTest();
    }




}
