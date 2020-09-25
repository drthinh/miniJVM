/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mini.gui;

import org.mini.gui.event.*;
import org.mini.nanovg.Gutil;
import org.mini.nanovg.Nanovg;

import java.util.Timer;
import java.util.TimerTask;

import static org.mini.gl.GL.*;
import static org.mini.gui.GToolkit.nvgRGBA;
import static org.mini.nanovg.Nanovg.*;

/**
 * @author gust
 */
public class GForm extends GPanel {

    final static Timer timer = new Timer(true);//用于更新画面，UI系统采取按需刷新的原则

    static GCmdHandler cmdHandler = new GCmdHandler();
    private boolean inited = false;

    protected String title;
    protected long display; //glfw display
    protected long vg; //nk contex
    protected GCallBack callback;
    protected float pxRatio;
    //
    //

    protected GPhotoPickedListener pickListener;
    protected GKeyboardShowListener keyshowListener;
    protected GAppActiveListener activeListener;
    protected GNotifyListener notifyListener;
    protected GSizeChangeListener sizeChangeListener;

    public GForm() {
        this.title = title;
        callback = GCallBack.getInstance();

        display = callback.getDisplay();
        vg = callback.getNvContext();
        if (vg == 0) {
            System.out.println("callback.getNvContext() is null.");
        }

        int winWidth, winHeight;
        winWidth = callback.getDeviceWidth();
        winHeight = callback.getDeviceHeight();

        pxRatio = callback.getDeviceRatio();


        //System.out.println("fbWidth=" + fbWidth + "  ,fbHeight=" + fbHeight);
        flush();
        setLocation(0, 0);
        setSize(winWidth, winHeight);
    }


    public GCallBack getCallBack() {
        return this.callback;
    }

    public long getNvContext() {
        return callback.getNvContext();
    }

    public long getWinContext() {
        return display;
    }

    public int getDeviceWidth() {
        return (int) callback.getDeviceWidth();
    }

    public int getDeviceHeight() {
        return (int) callback.getDeviceHeight();
    }

    public void setTitle(String title) {
        callback.setDisplayTitle(title);
    }

    public boolean isInited() {
        return inited;
    }

    @Override
    public void init() {

        inited = true;
    }

    public void display(long vg) {

        try {

            // Update and render
            int fbWidth, fbHeight;
            fbWidth = callback.getFrameBufferWidth();
            fbHeight = callback.getFrameBufferHeight();
            glViewport(0, 0, fbWidth, fbHeight);
            float[] bgc = GToolkit.getStyle().getBackgroundColor();
            glClearColor(bgc[0], bgc[1], bgc[2], bgc[3]);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            int winWidth, winHeight;
            winWidth = callback.getDeviceWidth();
            winHeight = callback.getDeviceHeight();

            nvgBeginFrame(vg, winWidth, winHeight, pxRatio);
            //drawDebugInfo(vg);
            Nanovg.nvgReset(vg);
            Nanovg.nvgResetScissor(vg);
            Nanovg.nvgScissor(vg, 0, 0, winWidth, winHeight);
            paint(vg);
            cmdHandler.update(this);
            nvgEndFrame(vg);

            cmdHandler.process(this);
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void drawDebugInfo(long vg) {
        float font_size = 15;
        nvgFontSize(vg, font_size);
        nvgFontFace(vg, GToolkit.getFontWord());
        nvgTextAlign(vg, Nanovg.NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);

        float dx = 2, dy = 40;
        byte[] b;
        nvgFillColor(vg, nvgRGBA(255, 255, 255, 255));

        dy += font_size;
        b = Gutil.toUtf8("form:" + getX() + "," + getY() + "," + getW() + "," + getH() + "  " + getInnerX() + "," + getInnerY() + "," + getInnerW() + "," + getInnerH());

        Nanovg.nvgTextJni(vg, dx, dy, b, 0, b.length);
        dy += font_size;
        if (focus != null) {
            b = Gutil.toUtf8("focus:" + focus.getX() + "," + focus.getY() + "," + focus.getW() + "," + focus.getH() + "  " + ((focus instanceof GContainer) ? ((GContainer) focus).getInnerX() + "," + ((GContainer) focus).getInnerY() + "," + ((GContainer) focus).getInnerW() + "," + ((GContainer) focus).getInnerH() : ""));
            Nanovg.nvgTextJni(vg, dx, dy, b, 0, b.length);
        }
    }

    TimerTask tt_OnTouch = new TimerTask() {
        public void run() {
            flush();
        }
    };

    void tt_setupOnTouch() {
        timer.schedule(tt_OnTouch, 0L);//, (long) (1000 / fpsExpect));
    }


    public void onPhotoPicked(int uid, String url, byte[] data) {
        if (pickListener != null) {
            pickListener.onPicked(uid, url, data);
        }
    }

    /**
     * @return the pickListener
     */
    public GPhotoPickedListener getPickListener() {
        return pickListener;
    }

    /**
     * @param pickListener the pickListener to set
     */
    public void setPickListener(GPhotoPickedListener pickListener) {
        this.pickListener = pickListener;
    }

    public void KeyboardPopEvent(boolean visible, float x, float y, float w, float h) {
        if (keyshowListener != null) {
            keyshowListener.keyboardShow(visible, x, y, w, h);
        }
    }

    public GSizeChangeListener getSizeChangeListener() {
        return sizeChangeListener;
    }

    public void setSizeChangeListener(GSizeChangeListener sizeChangeListener) {
        this.sizeChangeListener = sizeChangeListener;
    }


    /**
     * @return the keyshowListener
     */
    public GKeyboardShowListener getKeyshowListener() {
        return keyshowListener;
    }

    /**
     * @param keyshowListener the keyshowListener to set
     */
    public void setKeyshowListener(GKeyboardShowListener keyshowListener) {
        this.keyshowListener = keyshowListener;
    }

    public static void deleteImage(int texture) {
        cmdHandler.addCmd(GCmd.GCMD_DESTORY_TEXTURE, texture);
    }

    public void onAppFocus(boolean focus) {
        //System.out.println("app focus:" + focus);
        if (activeListener != null) {
            activeListener.onAppActive(focus);
        }
        flush();
    }

    public void onNotify(String key, String val) {
        if (notifyListener != null) {
            notifyListener.onNotify(key, val);
        }
    }

    public void onSizeChange(int width, int height) {
        if (sizeChangeListener != null) {
            sizeChangeListener.onSizeChange(width, height);
        }
    }

    /**
     * @return the activeListener
     */
    public GAppActiveListener getActiveListener() {
        return activeListener;
    }

    /**
     * @param activeListener the activeListener to set
     */
    public void setActiveListener(GAppActiveListener activeListener) {
        this.activeListener = activeListener;
    }

    /**
     * @return the notifyListener
     */
    public GNotifyListener getNotifyListener() {
        return notifyListener;
    }

    /**
     * @param notifyListener the notifyListener to set
     */
    public void setNotifyListener(GNotifyListener notifyListener) {
        this.notifyListener = notifyListener;
    }

    /**
     * @param s
     */
    public static void addMessage(String s) {
        cmdHandler.addCmd(GCmd.GCMD_SHOW_MESSAGE, s);
    }

    public static void clearMessage() {
        cmdHandler.addCmd(GCmd.GCMD_CLEAR_MESSAGE);
    }

    public static void showKeyboard() {
        cmdHandler.addCmd(GCmd.GCMD_SHOW_KEYBOARD);
    }

    public static void hideKeyboard() {
        cmdHandler.addCmd(GCmd.GCMD_HIDE_KEYBOARD);
    }

    public static void addCmd(GCmd cmd) {
        cmdHandler.addCmd(cmd);
    }

    public float getRatio() {
        return pxRatio;
    }
}
