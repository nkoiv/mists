/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.ui;

import java.util.ArrayList;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gamestate.GameState;

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
                    char c;
                    if (k.isDigitKey()) c = Character.toLowerCase(k.toString().charAt(5));
                    else c = Character.toLowerCase(k.toString().charAt(0));
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
            case "toggleflag": super.getGame().locControls.toggleFlag(attributes); break;
            case "drawcollisions": break;
            case "whois": super.getGame().locControls.printMobInfo(attributes); break;
            case "clearancemap": super.getGame().locControls.printClearanceMapIntoConsole(); break;
            case "collisionmap": super.getGame().locControls.printCollisionMapIntoConsole(); break;
            case "addcreature" : super.getGame().locControls.addCreature(attributes); break;
            case "createloc":super.getGame().locControls.createLoc(attributes); break;
            case "togglescale":super.getGame().toggleScale =true; break;
            case "giveitem": super.getGame().locControls.giveItem(attributes); break;
            case "printlightmap": super.getGame().locControls.printLightMapIntoConsole(); break;
            case "help": printHelp(); break;
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
