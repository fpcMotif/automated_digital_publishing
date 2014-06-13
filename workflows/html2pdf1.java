/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of html2pdf1 workflow.
 *
 * html2pdf1 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2pdf1 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2pdf1 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/html2pdf1.java
 * @brief Workflow to automatically process a semantic HTML input file based on
 *     template1 of odt2html to a PDF.
 * @author Stephan Kreutzer
 * @since 2014-06-13
 */



import java.io.File;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
/*

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;


*/


public class html2pdf1
{
    public static void main(String args[])
    {
        System.out.print("html2pdf1 workflow  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/automated_digital_publishing/\n\n");
    
        String programPath = html2pdf1.class.getProtectionDomain().getCodeSource().getLocation().getFile();


        File inputHTMLFile = null;

        if (args.length > 0)
        {
            inputHTMLFile = new File(args[0]);
        
            if (inputHTMLFile.exists() != true)
            {
                System.out.print("html2pdf1 workflow: '" + inputHTMLFile.getAbsolutePath() + "' doesn't exist.\n");
                System.exit(-1);
            }

            if (inputHTMLFile.isFile() != true)
            {
                System.out.print("html2pdf1 workflow: '" + inputHTMLFile.getAbsolutePath() + "' isn't a file.\n");
                System.exit(-2);
            }

            if (inputHTMLFile.canRead() != true)
            {
                System.out.print("html2pdf1 workflow: '" + inputHTMLFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-3);
            }
        }
        else
        {
            try
            {
                BufferedWriter writer = new BufferedWriter(
                                        new OutputStreamWriter(
                                        new FileOutputStream(new File(programPath + "../gui/file_picker/file_picker1/config.xml")),
                                        "UTF8"));

                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write("<!-- This file was created by html2pdf1 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/automated_digital_publishing/). -->\n");
                writer.write("<file-picker1-config>\n");
                writer.write("  <extension extension=\"html\">HTML input file (.html)</extension>\n");
                writer.write("</file-picker1-config>\n");
                
                writer.close();
            }
            catch (FileNotFoundException ex)
            {
                ex.printStackTrace();
                System.exit(-4);
            }
            catch (UnsupportedEncodingException ex)
            {
                ex.printStackTrace();
                System.exit(-5);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-6);
            }


            ProcessBuilder builder = new ProcessBuilder("java", "file_picker1");
            builder.directory(new File(programPath + "../gui/file_picker/file_picker1"));

            try
            {
                Process process = builder.start();
                Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
                
                while (scanner.hasNext() == true)
                {
                    String line = scanner.next();
                    
                    System.out.println(line);
                    
                    if (line.contains("' selected.") == true)
                    {
                        StringTokenizer tokenizer = new StringTokenizer(line, "'");
                        
                        if (tokenizer.countTokens() >= 2)
                        {
                            tokenizer.nextToken();
                            inputHTMLFile = new File(tokenizer.nextToken());
                        }
                    }
                }
                
                scanner.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-7);
            }
        }

        if (inputHTMLFile == null)
        {
            System.out.println("html2pdf1 workflow: No input HTML file.");
            System.exit(-8);
        }


        File tempDirectory = new File(programPath + "temp");
        
        if (tempDirectory.exists() != true)
        {
            System.out.print("html2pdf1 workflow: Temp directory '" + tempDirectory.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-9);
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("html2pdf1 workflow: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-10);
            }
        }


        File outputDirectory = new File(tempDirectory + File.separator + "pdf");
        
        if (outputDirectory.exists() == true)
        {  
            if (html2pdf1.DeleteFileRecursively(outputDirectory) != 0)
            {
                System.out.println("html2pdf1 workflow: Can't clean '" + outputDirectory.getAbsolutePath() + "'.");
                System.exit(-13);
            }
        }
        
        if (outputDirectory.mkdirs() != true)
        {
            System.out.print("html2pdf1 workflow: Can't create output directory '" + outputDirectory.getAbsolutePath() + "'.\n");
            System.exit(-14);
        }


        ProcessBuilder builder = new ProcessBuilder("java", "xsltransformator1", inputHTMLFile.getAbsolutePath(), programPath + "../html2latex/html2latex1/layout/layout1.xsl", outputDirectory.getAbsolutePath() + File.separator + "output.tex");
        builder.directory(new File(programPath + "../xsltransformator/xsltransformator1"));

        try
        {
            Process process = builder.start();
            Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
            
            while (scanner.hasNext() == true)
            {
                System.out.println(scanner.next());
            }
            
            scanner.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-15);
        }
        
        
        for (int i = 0; i < 4; i++)
        {
            builder = new ProcessBuilder("pdflatex", "-halt-on-error", outputDirectory.getAbsolutePath() + File.separator + "output.tex");
            builder.directory(new File(outputDirectory.getAbsolutePath()));

            try
            {
                Process process = builder.start();
                Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
                
                while (scanner.hasNext() == true)
                {
                    System.out.println(scanner.next());
                }
                
                scanner.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-16);
            }
        }

        return;
    }

    public static int DeleteFileRecursively(File file)
    {
        if (file.isDirectory() == true)
        {
            for (File child : file.listFiles())
            {
                if (html2pdf1.DeleteFileRecursively(child) != 0)
                {
                    return -1;
                }
            }
        }
        
        if (file.delete() != true)
        {
            System.out.println("html2pdf1 workflow: Can't delete '" + file.getAbsolutePath() + "'.");
            return -1;
        }
    
        return 0;
    }
}
