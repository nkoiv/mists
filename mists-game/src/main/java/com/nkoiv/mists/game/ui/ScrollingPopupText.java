/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 *
 * @author nikok
 */
public class ScrollingPopupText {
    private String text;
    private double xCoor;
    private double yCoor;
    private Paint colour;
    private double xDirection;
    private double yDirection;
    private double lifetime;


    public ScrollingPopupText(String text, double xCoor, double yCoor) {
        this(text, xCoor, yCoor, 1500, Color.RED);
    }

    public ScrollingPopupText(String text, double xCoor, double yCoor, double lifetimeMS, Color color) {
        this.text = text;
        this.colour = color;
        this.xCoor = xCoor;
        this.yCoor = yCoor;
        this.lifetime = lifetimeMS;
    }

    public void setDirection(double xDirection, double yDirection) {
        this.xDirection = xDirection;
        this.yDirection = yDirection;
    }

    public void setColour(Paint color) {
        this.colour = color;
    }

    public void render(GraphicsContext gc) {
        //Mists.logger.info("Rendering SCT");
        if (lifetime < 1000) gc.setGlobalAlpha(lifetime / 1000);
        gc.setFont(Mists.fonts.get("romulus"));
        gc.setFill(colour);
        gc.fillText(text, xCoor, yCoor);
    }

    public double getLifetime() {
        return this.lifetime;
    }

    /**
     * SCT tick moves the text towards its float
     * direction as well as decays the object.
     * @param time Seconds that have elapsed since last tick
     */
    public void tick(double time) {
        double timeInMS = time * 1000;
        this.xCoor = this.xCoor +  (this.xDirection * time);
        this.yCoor = this.yCoor +  (this.yDirection * time);
        this.lifetime = this.lifetime - (timeInMS);
    }

}