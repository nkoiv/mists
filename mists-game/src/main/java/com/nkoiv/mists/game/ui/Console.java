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
 * Console is command prompt mainly for debugging the game
 * TODO: Consider making commands objects, and inserting 
 * them in a command array or list.
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
    
    /**
     * Input takes in raw button presses from keyevents by keycode,
     * and stores them in the "current" string. This string is then
     * handed over to "parseCommmand()" on Enter.
     * @param pressedButtons Buttons the user has pressed in the console
     * @param releasedButtons Buttons the user has released in the console
     */

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
    
    /**
     * ParseCommand uses simple Switch structure to pick out
     * valid commands from what's been typed in the console.
     * Anything not dictated here wont do a thing
     * 
     */
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
            case "quit": Platform.exit(); System.exit(0); break;
            case "toggleFlag": super.getGame().locControls.toggleFlag(attributes); break;
            case "drawCollisions": ; break;
            case "clearanceMap": super.getGame().locControls.printClearanceMapIntoConsole(); break;
            case "collisionMap": super.getGame().locControls.printCollisionMapIntoConsole(); break;
            case "addCreature" : super.getGame().locControls.addCreature(attributes); break;
            case "createLoc":super.getGame().locControls.createLoc(attributes); break;
            case "toggleScale":super.getGame().toggleScale =true;
            case "help": printHelp(); ;break;
            default: break;
        }
        
        clearRow();
    }
    
    private void printHelp() {
        //??
    }
    
    /**
     * Takes previously typed command at puts it on the commandline
     * (used when user presses UP on keyboard)
     */
    private void previousRow() {
        if (this.textlog[0] != null) this.current = this.textlog[0];
        if (this.textlog[1] != null) this.textlog[0] = this.textlog[1];
        if (this.textlog[2] != null) this.textlog[1] = this.textlog[2];
    }
    
    /**
     * Clears console input (when command is parsed)
     * and saves the line to textlog
     */
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
