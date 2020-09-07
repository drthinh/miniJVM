/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mini.gui;

import org.mini.glfm.Glfm;
import org.mini.gui.event.GActionListener;
import org.mini.gui.event.GFocusChangeListener;
import org.mini.gui.event.GStateChangeListener;

import java.util.ArrayList;
import java.util.List;

import static org.mini.gui.GToolkit.nvgRGBA;
import static org.mini.nanovg.Gutil.toUtf8;
import static org.mini.nanovg.Nanovg.*;

/**
 * @author gust
 */
public class GList extends GContainer implements GFocusChangeListener {
    public static final int MODE_MULTI_SHOW = 1, MODE_SINGLE_SHOW = 2;
    public static final int MODE_MULTI_SELECT = 4, MODE_SINGLE_SELECT = 8;

    protected char preicon;
    protected byte[] preicon_arr = toUtf8("" + ICON_CHEVRON_RIGHT);
    protected List<Integer> selected = new ArrayList();
    protected boolean pulldown;
    //
    protected int showMode = MODE_SINGLE_SHOW;
    //
    protected int selectMode = MODE_SINGLE_SELECT;

    protected GScrollBar scrollBar;
    GListPopWindow popWin;
    protected float[] lineh = {0f};
    protected float list_image_size = 28;
    protected float list_item_heigh = 40;
    protected float list_rows_max = 7;
    protected float list_rows_min = 3;
    protected float pad = 5;
    protected int scrollbarWidth = 20;

    protected boolean showScrollbar = false;

    protected float left, top, width, height;
    //
    protected final List<Integer> outOffilterList = new ArrayList();//if a item in this list , it would show dark text color

    GStateChangeListener stateChangeListener;

    /**
     *
     */
    public GList() {
        this(0f, 0f, 1f, 1f);
    }

    public GList(int left, int top, int width, int height) {
        this((float) left, top, width, height);
    }

    public GList(float left, float top, float width, float height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;

        setLocation(left, top);
        setSize(width, height);

        //
        scrollBar = new GScrollBar(0, GScrollBar.VERTICAL, 0, 0, scrollbarWidth, 100);
        scrollBar.setActionListener(new ScrollBarActionListener());
        popWin = new GListPopWindow();
        popWin.addImpl(scrollBar);
        popWin.addImpl(popView);
        setFocusListener(this);
        setScrollBar(false);
        sizeAdjust();
        changeCurPanel();

        setShowMode(MODE_SINGLE_SHOW);

        setFontSize(GToolkit.getStyle().getTextFontSize());
        setColor(GToolkit.getStyle().getTextFontColor());
    }

    @Override
    public void setSize(float w, float h) {
        width = w;
        height = h;
        super.setSize(w, h);
        sizeAdjust();
    }

    @Override
    public void setInnerSize(float w, float h) {
        setSize(w, h);
    }

    private void switchAppearanceSize(float w, float h) {
        super.setSize(w, h);
        sizeAdjust();
    }

    public void setScrollBar(boolean show) {
        this.showScrollbar = show;
        if (show) {
            scrollbarWidth = 20;
        } else {
            scrollbarWidth = 0;
        }
    }

    public void setItemHeight(float h) {
        list_item_heigh = h;
        list_image_size = h - 12;
    }

    public List<GObject> getItemList() {
        return popView.getElementsImpl();
    }


    @Override
    public float getInnerX() {
        return getX();
    }

    @Override
    public float getInnerY() {
        return getY();
    }

    @Override
    public float getInnerW() {
        return getW();
    }

    @Override
    public float getInnerH() {
        return getH();
    }

    @Override
    public void setInnerLocation(float x, float y) {
        setLocation(x, y);
    }

    @Override
    public float[] getInnerBoundle() {
        return getBoundle();
    }

    public void setIcon(char icon) {
        preicon = icon;
    }

    public GListItem addItems(GImage img, String lab) {
        GListItem gli = new GListItem(img, lab);
        add(gli);
        return gli;
    }

    public void add(GObject gli) {
        if (gli != null && gli instanceof GListItem) {
            popView.addImpl(gli);
            ((GListItem) gli).list = this;
            sizeAdjust();
        }
    }

    public void add(int index, GObject gli) {
        if (gli != null && gli instanceof GListItem) {
            popView.addImpl(index, gli);
            ((GListItem) gli).list = this;
            sizeAdjust();
        }
    }

    public void remove(int index) {
        popView.removeImpl(index);
        sizeAdjust();
    }

    public void remove(GObject go) {
        if (!(go instanceof GListItem)) {
            throw new IllegalArgumentException("need GListItem");
        }
        popView.removeImpl(go);
        sizeAdjust();
    }

    public GListItem[] getItems() {
        GListItem[] items = new GListItem[popView.getElementSizeImpl()];
        int i = 0;
        for (GObject go : popView.elements) {
            items[i] = (GListItem) go;
            i++;
        }
        return items;
    }


    public List<GObject> getElements() {
        return popView.getElementsImpl();
    }

    public int getElementSize() {
        return popView.elements.size();
    }

    public boolean contains(GObject son) {
        return popView.containsImpl(son);
    }

    public void clear() {
        popView.clearImpl();
    }


    void sizeAdjust() {
        int itemcount = popView.elements.size();
        if (itemcount <= 0) {
            return;
        }

        if (showMode == MODE_MULTI_SHOW) {
            popWin.setLocation(0, 0);
            popWin.setSize(width, height);

        } else {
            float popH = itemcount * list_item_heigh;
            if (itemcount < list_rows_min) {
                popH = list_rows_min * list_item_heigh;
            }
            if (itemcount > list_rows_max) {
                popH = list_rows_max * list_item_heigh;
            }

            popWin.setLocation(0, 0);
            popWin.setSize(width, popH);

        }

        reAlignItems();

        normalPanel.setLocation(0, 0);
        normalPanel.setSize(width, height);

        scrollBar.setPos(popView.scrolly);
        flush();
    }

    public void reAlignItems() {
        int i = 0;
        for (GObject go : popView.getElementsImpl()) {
            go.setLocation(pad, i * list_item_heigh);
            go.setSize(popView.getW() - pad * 2, list_item_heigh);
            i++;
        }
        //selected.clear();
    }

    void changeCurPanel() {
        int itemcount = popView.elements.size();
        super.clearImpl();

        if (showMode == MODE_MULTI_SHOW) {
            setLocation(left, top);
            switchAppearanceSize(width, height);
            super.addImpl(popWin);
        } else if (pulldown && itemcount > 0) {
            float popH = itemcount * list_item_heigh;
            if (itemcount < list_rows_min) {
                popH = list_rows_min * list_item_heigh;
            }
            if (itemcount > list_rows_max) {
                popH = list_rows_max * list_item_heigh;
            }
            float popY = 0;
            if (popH > parent.getH()) {// small than frame height
                popY = parent.getY();
            } else if (top + popH < parent.getH()) {
                popY = top;
            } else {
                popY = parent.getH() - popH;
            }
            setLocation(left, popY);
            switchAppearanceSize(popWin.getW(), popWin.getH());
            super.addImpl(popWin);
        } else {
            setLocation(left, top);
            switchAppearanceSize(width, height);
            super.addImpl(normalPanel);
        }
    }

    public void setItems(GImage[] imgs, String[] labs) {
        if ((imgs == null && labs == null) || (imgs != null && labs != null && imgs.length != labs.length)) {
            throw new IllegalArgumentException("need images and labels count equals.");
        }
        int len = imgs == null ? labs.length : imgs.length;
        for (int i = 0; i < len; i++) {
            addItems(imgs == null ? null : imgs[i], labs == null ? null : labs[i]);
        }
        sizeAdjust();
    }

    public void setShowMode(int m) {
        this.showMode = m;

        if (showMode == MODE_MULTI_SHOW) {
            setBgColor(GToolkit.getStyle().getBackgroundColor());
        } else {
            setBgColor(GToolkit.getStyle().getPopBackgroundColor());
        }

        sizeAdjust();
        changeCurPanel();
    }

    public int getShowMode() {
        return this.showMode;
    }

    public void setSelectMode(int m) {
        selectMode = m;
    }

    public int getSelectMode() {
        return selectMode;
    }

    public int getSelectedIndex() {
        if (selected.size() > 0) {
            return selected.get(0);
        }
        return -1;
    }

    public void setSelectedIndex(int i) {
        selected.clear();
        if (i >= 0 && i < popView.getElementsImpl().size()) {
            selected.add(i);
        }
    }

    public int[] getSelectedIndices() {
        int size = selected.size();
        int[] r = new int[size];
        for (int i = 0; i < size; i++) {
            r[i] = selected.get(i);
        }
        return r;
    }

    public void setSelectedIndices(int[] s) {
        selected.clear();
        for (int i = 0; i < s.length; i++) {
            selected.add(s[i]);
        }
    }

    boolean isSelected(int index) {
        return selected.contains(index);
    }

    public int getItemIndex(GListItem item) {
        return popView.getElementsImpl().indexOf(item);
    }

    public void selectAll() {
        if (selectMode == MODE_MULTI_SELECT) {
            int size = popView.getElementSizeImpl();
            for (int i = 0; i < size; i++) {
                selected.add(i);
            }
        }
    }

    public void deSelectAll() {
        selected.clear();
    }

    void select(int index) {
        if (selectMode == MODE_SINGLE_SELECT) {
            selected.clear();
            selected.add(new Integer(index));
        } else if (selected.contains(index)) {
            selected.remove(new Integer(index));
        } else {
            selected.add(new Integer(index));
        }
    }

    void unSelect(int index) {
        selected.remove(new Integer(index));
    }

    public GListItem getItem(int index) {
        if (index < 0 || index >= popView.elements.size()) {
            return null;
        }
        return (GListItem) popView.elements.get(index);
    }

    public void addOutOfFilter(int index) {
        outOffilterList.add(index);
    }

    public void removeOutOfFilter(int index) {
        outOffilterList.remove(index);
    }

    public void clearOutOfFilter() {
        outOffilterList.clear();
    }

    public boolean isOutOfFilter(int index) {
        return outOffilterList.contains(index);
    }

    public void filterLabelWithKey(String key) {
        if (key == null) {
            return;
        }
        clearOutOfFilter();
        List<GObject> list = getItemList();
        for (GObject go : list) {
            GListItem gli = (GListItem) go;
            if (gli.getLabel() != null && gli.getLabel().toLowerCase().contains(key.toLowerCase())) {

            } else {
                addOutOfFilter(getItemIndex(gli));
                //System.out.println("except item:" + getItemIndex(gli));
            }
        }

    }

    @Override
    public void focusGot(GObject go) {
    }

    @Override
    public void focusLost(GObject go) {
        pulldown = false;
        changeCurPanel();
    }

    /**
     * @param vg
     * @return
     */
    @Override
    public boolean paint(long vg) {

        //int itemcount = popView.elements.size();
        nvgFontSize(vg, GToolkit.getStyle().getTextFontSize());
        nvgFontFace(vg, GToolkit.getFontWord());
        nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);

        nvgTextMetrics(vg, null, null, lineh);

        return super.paint(vg);
    }

    /**
     *
     */
    GPanel normalPanel = new GPanel() {

        @Override
        public void touchEvent(int touchid, int phase, int x, int y) {

            if (phase == Glfm.GLFMTouchPhaseEnded) {
                if (!pulldown) {
                    pulldown = true;
                    GList.this.changeCurPanel();
                }
            }
            super.touchEvent(touchid, phase, x, y);

        }

        @Override
        public void mouseButtonEvent(int button, boolean pressed, int x, int y) {
            if (pressed) {
                if (!pulldown) {
                    pulldown = true;
                    GList.this.changeCurPanel();
                }
            }
            super.mouseButtonEvent(button, pressed, x, y);
        }


        @Override
        public boolean paint(long vg) {
            drawNormal(vg, normalPanel.getX(), normalPanel.getY(), normalPanel.getW(), normalPanel.getH());
            return true;
        }

        void drawNormal(long vg, float x, float y, float w, float h) {
            byte[] bg;

            bg = nvgLinearGradient(vg, x, y, x, y + h, nvgRGBA(255, 255, 255, 16), nvgRGBA(0, 0, 0, 16));
            float cornerRadius = 4.0f;
            nvgBeginPath(vg);
            nvgRoundedRect(vg, x + 1, y + 1, w - 2, h - 2, cornerRadius - 1);
            nvgFillPaint(vg, bg);
            nvgFill(vg);

            nvgBeginPath(vg);
            nvgRoundedRect(vg, x + 0.5f, y + 0.5f, w - 1, h - 1, cornerRadius - 0.5f);
            nvgStrokeColor(vg, nvgRGBA(0, 0, 0, 48));
            nvgStroke(vg);

            float thumb = h - pad;
            if (popView.elements.size() > 0) {
                int selectIndex = getSelectedIndex();
                if (selectIndex >= 0) {
                    GListItem gli = (GListItem) getItem(selectIndex);
                    GToolkit.drawImage(vg, gli.img, x + pad, y + h * 0.5f - thumb / 2, thumb, thumb, false, 1.0f);
                    GToolkit.drawTextLine(vg, x + (gli.img == null ? 0 : thumb) + pad + pad, y + h / 2, w - (thumb + pad + pad), thumb, gli.getText(), GList.this.fontSize, GList.this.color, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
                }
            }
            nvgFontSize(vg, GToolkit.getStyle().getIconFontSize());
            nvgFontFace(vg, GToolkit.getFontIcon());
            nvgFillColor(vg, GToolkit.getStyle().getTextFontColor());
            nvgTextJni(vg, x + w - pad * 3, y + h * 0.5f, preicon_arr, 0, preicon_arr.length);
        }
    };

    /**
     *
     */
    protected GViewPort popView = new GViewPort() {

        @Override
        public boolean paint(long vg) {
            float x = getX();
            float y = getY();
            float w = getW();
            float h = getH();

            GToolkit.drawRect(vg, x, y, w, h, GList.this.getBgColor());

            super.paint(vg);

//            // Hide fades
//            byte[] fadePaint;
//            fadePaint = nvgLinearGradient(vg, x, y, x, y + 6, nvgRGBA(20, 20, 20, 192), nvgRGBA(30, 30, 30, 0));
//            nvgBeginPath(vg);
//            nvgRect(vg, x + 2, y, w - 4, 6);
//            nvgFillPaint(vg, fadePaint);
//            nvgFill(vg);
//
//            fadePaint = nvgLinearGradient(vg, x, y + h, x, y + h - 6, nvgRGBA(20, 20, 20, 192), nvgRGBA(30, 30, 30, 0));
//            nvgBeginPath(vg);
//            nvgRect(vg, x + 2, y + h - 6, w - 4, 6);
//            nvgFillPaint(vg, fadePaint);
//            nvgFill(vg);
            return true;
        }

    };


    class GListPopWindow extends GPanel {


        @Override
        public void setSize(float width, float height) {
            super.setSize(width, height);

            popView.setLocation(0, 0);
            popView.setSize(width - scrollbarWidth, height);

            scrollBar.setLocation(width - scrollbarWidth, 0);
            scrollBar.setSize(20, height);
        }

    }

    ;

    class ScrollBarActionListener implements GActionListener {

        @Override
        public void action(GObject gobj) {
            popView.setScrollY(((GScrollBar) gobj).getPos());
            sizeAdjust();
            flush();
        }

    }

    public GStateChangeListener getStateChangeListener() {
        return stateChangeListener;
    }

    public void setStateChangeListener(GStateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }


}
