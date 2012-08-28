package org.umbra;

import java.io.*;
import java.util.*;

/**
 * Umbra Eclipse Class
 * @author Jiri Peterka
 *
 */
public class Umbra
{
	
    private List<String> metaDirs;
    private File umbraprops;
	
	/**
	 * Simple matcher abstract class
	 * @author Jiri Peterka
	 *
	 */
    public abstract class Matcher
    {
        abstract boolean matches(String s);
    }

    /** 
     * Workspace Matcher
     * @author Jiri Peterka
     *
     */
    public class WorkspaceMatcher extends Matcher
    {
        boolean matches(String name)
        {
            return name.equalsIgnoreCase(".metadata");
        }
    }

    /**
     * Creates settings file if needed
     * @param metaDir
     * @param parser
     * @return
     * @throws IOException
     */
    public File createSettingFile(String metaDir, PatternParser parser)
        throws IOException
    {
        File settingDirFile = new File((new StringBuilder(String.valueOf(metaDir))).append(File.separator).append(parser.getDir()).toString());
        if(!settingDirFile.exists())
        {
            Logger.log((new StringBuilder("Dir created:\"")).append(settingDirFile.getAbsolutePath()).append("\"").toString());
            settingDirFile.mkdirs();
        }
        File settingFile = new File((new StringBuilder(String.valueOf(metaDir))).append(File.separator).append(parser.getDir()).append(File.separator).append(parser.getFile()).toString());
        if(!settingFile.exists())
        {
            settingFile.createNewFile();
            Logger.log((new StringBuilder("File created:\"")).append(settingDirFile.getAbsolutePath()).append("\"").toString());
        }
        return settingFile;
    }

    /**
     * Write configuration 
     * @param metadirs
     * @param umbraFile
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void writeConfiguration(List<String> metadirs, File umbraFile)
        throws FileNotFoundException, IOException
    {
        Properties p = new Properties();
        p.load(new FileInputStream(umbraFile));
        for(Enumeration<Object> keys = p.keys(); keys.hasMoreElements();)
        {
            String nextElement = (String)keys.nextElement();
            if(!nextElement.toString().startsWith("$"))
            {
                String value = p.getProperty(nextElement);
                if(value == null)
                {
                    Logger.log((new StringBuilder("Error: Null value property for key ")).append(nextElement).toString());
                    return;
                }
                String pattern = p.getProperty((new StringBuilder("$")).append(nextElement).toString());
                PatternParser parser = new PatternParser(pattern);
                Properties s;
                FileOutputStream out;
                for(Iterator<String> iterator = metadirs.iterator(); iterator.hasNext(); s.store(out, "Property updated by Umbra"))
                {
                    String metaDir = (String)iterator.next();
                    File settingFile = createSettingFile(metaDir, parser);
                    s = new Properties();
                    s.load(new FileInputStream(settingFile));
                    s.put(parser.getVariable(), value);
                    out = new FileOutputStream(settingFile);
                }

                Logger.log((new StringBuilder("Record written (")).append(metadirs.size()).append("x) ").append(parser.getDir()).append(File.separator).append(parser.getFile()).append(" ").append(parser.getVariable()).append("=").append(value).toString());
            }
        }

    }

    /**
     * Main class
     * @param args
     */
    public static void main(String args[])
    {
        Date d1 = new Date();
        Logger.log("Umbra Eclipse 0.1");
        Umbra u = new Umbra();
        u.init(args);
        Date d2 = new Date();
        Logger.log((new StringBuilder("Umbra done in ")).append(d2.getTime() - d1.getTime()).append("ms").toString());
    }

    /**
     * Main Umbra method
     * @param args
     */
    public void init(String args[])
    {
        if(args.length == 0)
        {
            Logger.log("Initial workspace path is needed");
            return;
        }
        File file = new File(args[0]);
        if(!file.exists())
        {
            Logger.log((new StringBuilder("File ")).append(file.getAbsolutePath()).append("not found").toString());
            return;
        }
        umbraprops = new File("umbra.props");
        if(!umbraprops.exists())
        {
            Logger.log("Cannot find umbra.ini");
            return;
        }
        Matcher wm = new WorkspaceMatcher();
        try
        {
            metaDirs = new ArrayList<String>();
            findMetaDirs(file.getAbsolutePath(), wm);
            writeConfiguration(metaDirs, umbraprops);
        }
        catch(Exception e)
        {
            Logger.log((new StringBuilder("Error: ")).append(e.getMessage()).toString());
            e.printStackTrace();
        }
    }

    /**
     * Finds workspace dirs
     * @param path
     * @param m
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void findMetaDirs(String path, Matcher m)
        throws FileNotFoundException, IOException
    {
        File root = new File(path);
        File list[] = root.listFiles();
        File afile[];
        int j = (afile = list).length;
        for(int i = 0; i < j; i++)
        {
            File f = afile[i];
            if(f.isDirectory())
            {
                if(m.matches(f.getName()))
                {
                    Logger.log((new StringBuilder("Workspace found: ")).append(f.getAbsolutePath()).toString());
                    metaDirs.add(f.getAbsolutePath());
                    return;
                }
                findMetaDirs(f.getAbsolutePath(), m);
            }
        }

    }

}