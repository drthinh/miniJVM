/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mini.gui;

import static org.mini.gui.GToolkit.nvgRGBA;
import static org.mini.nanovg.Nanovg.*;
import static org.mini.nanovg.Nanovg.nvgFill;

/**
 *
 * @author gust
 */
public abstract class GStyle {

    abstract float[] getBackgroundColor();

    abstract float[] getPopBackgroundColor();

    abstract float getTextFontSize();

    abstract float[] getDisabledTextFontColor();

    abstract float getTitleFontSize();

    abstract float getIconFontSize();

    abstract float[] getTextFontColor();

    abstract float[] getTextShadowColor();

    abstract float[] getHintFontColor();

    abstract float[] getSelectedColor();

    abstract float[] getUnselectedColor();

    abstract float[] getEditBackground();

    abstract float[] getFrameBackground();

    abstract float[] getFrameTitleColor();

    abstract float getIconFontWidth();


    public void drawEditBoxBase(long vg, float x, float y, float w, float h) {
        byte[] bg;
        // Edit
        bg = nvgBoxGradient(vg, x, y, w, h, 3, 4, getEditBackground(), nvgRGBA(32, 32, 32, 192));
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x + 1, y + 1, w - 2, h - 2, 4 - 1);
        nvgFillPaint(vg, bg);
        nvgFill(vg);

        nvgBeginPath(vg);
        nvgRoundedRect(vg, x + 0.5f, y + 0.5f, w - 1, h - 1, 4 - 0.5f);
        nvgStrokeColor(vg, nvgRGBA(0, 0, 0, 16));
        nvgStroke(vg);
    }

    public void drawFieldBoxBase(long vg, float x, float y, float w, float h) {
        byte[] bg;
        float cornerRadius = h / 2 - 1;
        bg = nvgBoxGradient(vg, x, y + 1.5f, w, h, h / 2, 5, nvgRGBA(0, 0, 0, 16), nvgRGBA(0, 0, 0, 92));
        nvgBeginPath(vg);
        nvgRoundedRect(vg, x, y, w, h, cornerRadius);
        nvgFillPaint(vg, bg);
        nvgFill(vg);
    }

}
