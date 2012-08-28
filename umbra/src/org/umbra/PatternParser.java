package org.umbra;

/**
 * Configuration pattern parser
 * @author Jiri Peterka
 *
 */
public class PatternParser
{

    PatternParser(String mappingDef)
    {
        String split[] = mappingDef.split("\\|");
        dir = split[0];
        file = split[1];
        var = split[2];
        type = split[3];
    }

    public String getDir()
    {
        return dir;
    }

    public String getFile()
    {
        return file;
    }

    public String getVariable()
    {
        return var;
    }

    public String getType()
    {
        return type;
    }

    private String file;
    private String var;
    private String type;
    private String dir;
}