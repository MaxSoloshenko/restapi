package com.rest.tests.api.frmw.response.looking.html;

import com.rest.tests.api.frmw.response.looking.ILookingObject;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;


/**
 * Created by msolosh on 3/29/2016.
 */
public class LookingFactory {

    public static ILookingObject getLookingNode(Object document, String xpath) {

        Object[] nodes = null;
        try {
            TagNode node;
            HtmlCleaner cleaner = new HtmlCleaner();

            node = cleaner.clean(document.toString());
            nodes = node.evaluateXPath(xpath);

            if (nodes.length == 0) {
                return null;
            }
            else if (nodes.length > 1) {
                return new LookingForArray(nodes);
            }
            else if (((TagNode) nodes[0]).getText().toString() instanceof String) {
                return new LookingForString(((TagNode) nodes[0]).getText().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}