/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.ui;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gamestate.GameState;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

/**
 *
 * @author nikok
 */
public class Console extends TextWindow{
    
    private String[] textlog;
    private String current;
    public Console(GameState parent) {
        super(parent, "Console", parent.getGame().WIDTH, 150, 0, 0);
        this.textlog = new String[4];
        this.current = "";
        super.bgColor = Color.TEAL;
        this.setText("> ");
        
    }
    
    public void input(ArrayList<KeyCode> pressedButtons, ArrayList<KeyCode> releasedButtons)  {
        for (KeyCode k : releasedButtons) {
            if (k.isLetterKey() || k.isDigitKey()) {
                if (pressedButtons.contains(KeyCode.SHIFT)) {
                    this.current = this.current+k.toString();
                } else {
                    char c = Character.toLowerCase(k.toString().charAt(0));
                    this.current = this.current+c;
                }
                
            }
            if (k == KeyCode.SPACE) {
                if (this.current.length()>0) this.current = this.current+" ";
            }
            if (k == KeyCode.BACK_SPACE  && current.length()>0) {
                this.current = this.current.substring(0, this.current.length()-1);
            }
            if (k == KeyCode.ENTER) {
                this.parseCommand();
            }
            
            if (k == KeyCode.UP) {
                previousRow();
            }
            
        }
        
        String text ="> "+current;
        if (textlog[0]!=null) text = text+"\n"+textlog[0];
        if (textlog[1]!=null) text = text+"\n"+textlog[1];
        if (textlog[2]!=null) text = text+"\n"+textlog[2];
        this.setText(text);
    }
    
    
    private void parseCommand() {
        System.out.println(current +" (console input)");
        String command;
        if (current.contains(" ")) {
            command = current.substring(0, current.indexOf(" "));
        } else {
            command = current;
        }
        String attributes = "";
        if (current.contains(" ")) attributes = attributes+current.substring(current.indexOf(" ")+1, current.length());
        switch (command) {
            case "toggleFlag": super.getGame().locControls.toggleFlag(attributes); break;
            case "printClearance": super.getGame().locControls.printClearanceMapIntoConsole(); break;
            case "printCollision": super.getGame().locControls.printCollisionMapIntoConsole(); break;
            case "quit": Platform.exit(); System.exit(0);
            default: break;
        }
        
        clearRow();
    }
    
    private void previousRow() {
        if (this.textlog[0] != null) this.current = this.textlog[0];
        if (this.textlog[1] != null) this.textlog[0] = this.textlog[1];
        if (this.textlog[2] != null) this.textlog[1] = this.textlog[2];
    }
    
    private void clearRow() {
        if(this.textlog[1] != null) this.textlog[2] = this.textlog[1];
        if(this.textlog[0] != null) this.textlog[1] = this.textlog[0];
        this.textlog[0] = current;
        current = "";
        String text ="> ";
        if (textlog[0]!=null) text = text+"\n"+textlog[0];
        if (textlog[1]!=null) text = text+"\n"+textlog[1];
        if (textlog[2]!=null) text = text+"\n"+textlog[2];
        Mists.logger.info(text);
        this.setText(text);
    }
    
}
