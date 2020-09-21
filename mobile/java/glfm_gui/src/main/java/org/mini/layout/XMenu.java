package org.mini.layout;

import org.mini.gui.GImage;
import org.mini.gui.GMenu;
import org.mini.gui.GMenuItem;
import org.mini.gui.GObject;
import org.mini.gui.event.GActionListener;
import org.mini.layout.xmlpull.KXmlParser;
import org.mini.layout.xmlpull.XmlPullParser;

import java.util.Vector;

public class XMenu extends XObject implements GActionListener {
    static public final String XML_NAME = "menu";

    static class MenuItem {
        static public final String XML_NAME = "mi";
        String name;
        String attachment;
        String text;
        String pic;
    }

    protected Vector items = new Vector();
    protected boolean contextMenu = false;
    protected boolean fixed = true;

    protected GMenu menu;

    public XMenu() {
        super(null);
    }

    public XMenu(XContainer xc) {
        super(xc);
    }

    @Override
    protected String getXmlTag() {
        return XML_NAME;
    }

    protected void parseMoreAttribute(String attName, String attValue) {
        super.parseMoreAttribute(attName, attValue);
        if (attName.equals("contextmenu")) {
            contextMenu = "0".equals(attValue) ? false : true;
        } else if (attName.equals("fixed")) {
            fixed = "0".equals(attValue) ? false : true;
        }
    }


    /**
     * 解析
     *
     * @param parser KXmlParser
     * @throws Exception
     */
    public void parse(KXmlParser parser) throws Exception {
        super.parse(parser);
        int depth = parser.getDepth();

        //得到域
        do {
            parser.next();
            String tagName = parser.getName();
            if (parser.getEventType() == XmlPullParser.START_TAG) {

                if (tagName.equals(MenuItem.XML_NAME)) {
                    MenuItem item = new MenuItem();

                    item.name = parser.getAttributeValue(null, "name");
                    item.pic = parser.getAttributeValue(null, "pic");
                    item.attachment = parser.getAttributeValue(null, "attachment");
                    String tmp = parser.nextText();
                    item.text = tmp.length() == 0 ? null : tmp;
                    items.add(item);
                }
                toEndTag(parser, MenuItem.XML_NAME);
                parser.require(XmlPullParser.END_TAG, null, tagName);
            }
        }
        while (!(parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals(XML_NAME) && depth == parser.getDepth()));

    }

    protected void preAlignVertical() {
        super.preAlignVertical();
        int parentTrialViewH = parent.getTrialViewH();
        if (y == XDef.NODEF) {
            if (raw_yPercent != XDef.NODEF && parentTrialViewH != XDef.NODEF) {
                y = raw_yPercent * parentTrialViewH / 100;
            } else {
                y = XDef.DEFAULT_COMPONENT_HEIGHT;
            }
        }
    }

    protected void preAlignHorizontal() {
        super.preAlignHorizontal();
        int parentTrialViewW = parent.getTrialViewW();
        if (x == XDef.NODEF) {
            if (raw_xPercent == XDef.NODEF) {
                x = parentTrialViewW;
            } else {
                x = raw_xPercent * parentTrialViewW / 100;
            }
        }
    }

    protected int getDefaultWidth(int parentViewW) {
        return parentViewW;
    }

    protected int getDefaultHeight(int parentViewH) {
        return XDef.DEFAULT_COMPONENT_HEIGHT;
    }

    public GObject getGui() {
        return menu;
    }

    protected void createGui() {
        if (menu == null) {
            menu = new GMenu(x, y, width, height);
            initGui();
            for (int i = 0; i < items.size(); i++) {
                MenuItem item = (MenuItem) items.elementAt(i);
                GImage img = null;
                if (item.pic != null) {
                    img = GImage.createImageFromJar(item.pic);
                }
                GMenuItem gli = menu.addItem(item.text, img);
                gli.setActionListener(this);
                gli.setName(item.name);
                gli.setAttachment(item.attachment);
                gli.setEnable(enable);
            }
            menu.setContextMenu(contextMenu);
            menu.setFixed(fixed);
        } else {
            menu.setLocation(x, y);
            menu.setSize(width, height);
        }
    }

    @Override
    public void action(GObject gobj) {
        getRoot().getEventHandler().action(gobj, null);
    }

}
