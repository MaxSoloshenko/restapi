package com.rest.tests.api.frwm.settings;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by msolosh on 3/2/2016.
 */
public class Filewalker {

    ClassLoader classLoader = getClass().getClassLoader();
    ArrayList<String> list = new ArrayList<String>();

    public ArrayList<String> walk( String dir ) throws UnsupportedEncodingException {


//        boolean test = true;
            if (dir == null) {
//                test = true;
                dir = URLDecoder.decode(classLoader.getResource("TestSuite").getPath(), "utf-8");
            }
            else if (dir.startsWith("TestSuite"))
            {
                try {

                    dir = URLDecoder.decode(classLoader.getResource(dir).getPath(), "utf-8");
                } catch (Exception e) {
                    Tools.printFixLineString("SetUp actions are missed: " + dir, "█");
                    return null;
                }
            }
            else if ((dir.endsWith("_SetUp")) || (dir.endsWith("_TearDown")))
            {
//                test = false;
                return null;
            }


        File root = new File( dir );
        File[] list = root.listFiles();

        if (list == null) return null;

        Collections.sort(Arrays.asList(list), Tools.FILE_ALPHABETICAL_ORDER);
        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( f.getAbsolutePath() );
            }
            else if (f.getAbsoluteFile().toString().endsWith(".json"))
            {
                    this.list.add(f.getAbsoluteFile().toString());
            }
            else {

            }
        }

        return this.list;
    }

    public ArrayList<String> walk() throws UnsupportedEncodingException {
        return walk(null);
    }
}
