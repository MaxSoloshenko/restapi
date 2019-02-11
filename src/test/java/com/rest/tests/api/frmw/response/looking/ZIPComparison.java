package com.rest.tests.api.frmw.response.looking;

import com.rest.tests.api.frmw.testcase.Response;
import net.minidev.json.JSONObject;
import org.testng.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by msolosh on 3/28/2016.
 */
public class ZIPComparison implements IExpectationValidator {

    private String expected;
    private String detected;
    ClassLoader classLoader = getClass().getClassLoader();

    public ZIPComparison(JSONObject expect) {
        this.expected = (String)expect.get("expected");
        this.detected = (String)expect.get("detected");
     }

    @Override
    public HashMap<String, String> validation(Response response, String file) throws IOException {

        String base = classLoader.getResource("SourceFiles/").toString().substring(5);

        ZipFile det = new ZipFile(base + detected);
        ZipFile exp = new ZipFile(base + expected);
        
        Set set1 = new LinkedHashSet();
        for (Enumeration e = det.entries(); e.hasMoreElements();)
            set1.add(((ZipEntry) e.nextElement()).getName());

        Set set2 = new LinkedHashSet();
        for (Enumeration e = exp.entries(); e.hasMoreElements();)
            set2.add(((ZipEntry) e.nextElement()).getName());

        for (Iterator i = set1.iterator(); i.hasNext();) {
            String name = (String) i.next();
            Assert.assertTrue(set2.contains(name), "Archive does not contain file " + name);
                set2.remove(name);
                Assert.assertTrue(streamsEqual(det.getInputStream(det.getEntry(name)), exp.getInputStream(exp.getEntry(name))),
                        String.format("File %s is not equal %s", det.getEntry(name), exp.getEntry(name)));
        }
        return null;
    }

    private static boolean streamsEqual(InputStream stream1, InputStream stream2) throws IOException {
        byte[] buf1 = new byte[4096];
        byte[] buf2 = new byte[4096];
        boolean done1 = false;
        boolean done2 = false;

        try {
            while (!done1) {
                int off1 = 0;
                int off2 = 0;

                while (off1 < buf1.length) {
                    int count = stream1.read(buf1, off1, buf1.length - off1);
                    if (count < 0) {
                        done1 = true;
                        break;
                    }
                    off1 += count;
                }
                while (off2 < buf2.length) {
                    int count = stream2.read(buf2, off2, buf2.length - off2);
                    if (count < 0) {
                        done2 = true;
                        break;
                    }
                    off2 += count;
                }
                if (off1 != off2 || done1 != done2)
                    return false;
                for (int i = 0; i < off1; i++) {
                    if (buf1[i] != buf2[i])
                        return false;
                }
            }
            return true;
        } finally {
            stream1.close();
            stream2.close();
        }
    }
}
