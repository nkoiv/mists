/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gamestate;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.controls.ContextAction;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.ui.ActionButton;
import com.nkoiv.mists.game.ui.AudioControls;
import com.nkoiv.mists.game.ui.AudioControls.MuteMusicButton;
import com.nkoiv.mists.game.ui.Console;
import com.nkoiv.mists.game.ui.GoMainMenuButton;
import com.nkoiv.mists.game.ui.InventoryPanel;
import com.nkoiv.mists.game.ui.LocationButtons;
import com.nkoiv.mists.game.ui.Overlay;
import com.nkoiv.mists.game.ui.QuitButton;
import com.nkoiv.mists.game.ui.TextButton;
import com.nkoiv.mists.game.ui.TextPanel;
import com.nkoiv.mists.game.ui.TiledPanel;
import com.nkoiv.mists.game.ui.UIComponent;
import com.nkoiv.mists.game.ui.TiledWindow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * LocationState handles the core of the game: being in Locations.
 * As a GameState it takes in Input from the user via the Game -class,
 * and provides the Game the output of the Location (graphics, etc)
 * @author nikok
 */
public class LocationState implements GameState {
    
    private final Game game;
    private UIComponent currentMenu;
    private boolean gameMenuOpen;
    public boolean paused;
    private final AudioControls audioControls = new AudioControls();
    private final LocationButtons locationControls = new LocationButtons();
    private boolean inConsole;
    private final HashMap<String, UIComponent> uiComponents;
    private final TreeSet<UIComponent> drawOrder;
    private TextPanel infobox;
    private ContextAction contextAction;
    private InventoryPanel playerInventory;
    
    public LocationState (Game game) {
        this.game = game;
        uiComponents = new HashMap<>();
        this.drawOrder = new TreeSet<>();
        this.loadDefaultUI();
    }
    
    @Override
    public void updateUI() {
        //Move the actionbar to where it should be
        Mists.logger.info("Updating UI. Game dimensions: "+game.WIDTH+"x"+game.HEIGHT);
        uiComponents.get("Actionbar").setPosition(0, (game.HEIGHT - 80));
        if(gameMenuOpen) uiComponents.get("GameMenu").setPosition((game.WIDTH/2 - 110), 150);
    }
    
    public void toggleScale(GraphicsContext gc) {
        if (Mists.graphicScale == 1) {
            gc.scale(2, 2);
            Mists.graphicScale = 2;
        } else {
            gc.scale(0.5, 0.5);
            Mists.graphicScale = 1;
        }
        
    }
    
    private void loadDefaultUI() {
        TiledWindow actionBar = new TiledWindow(this, "Actionbar", game.WIDTH, 80, 0, (game.HEIGHT - 80));
        TextButton attackButton = new ActionButton(game.getPlayer(), "Smash!",  80, 60);
        TextButton pathsButton = new LocationButtons.DrawPathsButton("Paths Off", 80, 60, this.game);
        TextButton lightenButton = new LocationButtons.IncreaseLightlevelButton("Lighten", 80, 60, this.game);
        TextButton darkenButton = new LocationButtons.ReduceLightlevelButton("Darken", 80, 60, this.game);
        TextButton toggleScaleButton = new LocationButtons.ToggleScaleButton("Resize", 80, 60, this.game);
        MuteMusicButton muteMusicButton;
        muteMusicButton = new AudioControls.MuteMusicButton();
        
        actionBar.addSubComponent(attackButton);
        actionBar.addSubComponent(pathsButton);
        actionBar.addSubComponent(lightenButton);
        actionBar.addSubComponent(darkenButton);
        actionBar.addSubComponent(muteMusicButton);
        actionBar.addSubComponent(toggleScaleButton);
        actionBar.setRenderZ(-10);
        this.addUIComponent(actionBar);
        
        this.infobox = new TextPanel(this, "InfoBox", 250, 100, game.WIDTH-300, game.HEIGHT-500, Mists.graphLibrary.getImageSet("panelBeigeLight"));
        this.playerInventory = new InventoryPanel(this, game.getPlayer().getInventory());
        this.playerInventory.setName("PlayerInventory");
        this.contextAction = new ContextAction(game.getPlayer());
    }

    /**
     * The LocationState renderer does things in two layers: Game and UI.
     * Both of these layers are handled with via Canvases. First the Game is rendered on
     * the gameCanvas, then the UI is rendered on the uiCanvas on top of it
     * TODO: Consider separating gameplay into several layers (ground, structures, creatures, (structure)frill, overhead?)
     * @param gameCanvas Canvas to draw the actual gameplay on
     * @param uiCanvas Canvas to draw the UI on
     */
    @Override
    public void render(Canvas gameCanvas, Canvas uiCanvas) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        double screenWidth = gameCanvas.getWidth();
        double screenHeight = gameCanvas.getHeight();
        
        if (this.game.toggleScale == true) {
            toggleScale(gameCanvas.getGraphicsContext2D());
            this.game.toggleScale = false;
        }
        
        //Render the current Location unless paused
        if (this.paused == false) {
            gc.clearRect(0, 0, screenWidth, screenHeight);
            if (game.getCurrentLocation() != null) {
                game.getCurrentLocation().render(gc);
            }
        }
        //Render Location overlay
        Overlay.drawAllHPBars(gc, game.getCurrentLocation().getLastRenderedMobs());
        //Render the UI
        this.renderUI(uiCanvas);
        
    }
    
    private void renderUI(Canvas uiCanvas) {
        GraphicsContext uigc = uiCanvas.getGraphicsContext2D();
        double screenWidth = uiCanvas.getWidth();
        double screenHeight = uiCanvas.getHeight();
        //Render the UI
        uigc.clearRect(0, 0, screenWidth, screenHeight);
        
        this.contextAction.update();
        if (this.contextAction.getCurrentTrigger() != null) {
            Overlay.drawHighlightRectangle(uigc, this.contextAction.getTriggerObjects());
            Overlay.drawHandCursor(uigc, this.contextAction.getCurrentTrigger().getTarget());
        }
        if (!game.getCurrentLocation().getTargets().isEmpty()) Overlay.drawInfoBox(uigc, infobox, game.getCurrentLocation().getTargets().get(0));
        if (gameMenuOpen){
            try {
                Image controls = new Image("/images/controls.png");
                uigc.drawImage(controls, screenWidth-controls.getWidth(), 50);
            } catch (Exception e){
                Mists.logger.info("controls.png not found");
            }
            
        }
        
        if (this.drawOrder != null) {
            for (UIComponent uic : this.drawOrder) {
                uic.render(uigc, 0, 0);
            }
        }
        
    }
    
    
    
    
    /**
     * Toggles open/close the gameMenu, displayed at the middle of the screen.
     * TODO: Consider making a separate class for this, in case other GameStates utilize the same
     */
    public void toggleGameMenu() {
        Mists.logger.info("Game menu toggled");
        if (!gameMenuOpen) {
            gameMenuOpen = true;
            TiledPanel gameMenu = new TiledPanel(this, "GameMenu", 220, 300, (game.WIDTH/2 - 110), 150,Mists.graphLibrary.getImageSet("panelBeige"));
            TextButton resumeButton = new LocationButtons.ResumeButton("Resume", 200, 60, this.game);
            TextButton optionsButton = new TextButton("Options", 200, 60);
            GoMainMenuButton mainMenuButton = new GoMainMenuButton(this.game, 200, 60);
            QuitButton quitButton = new QuitButton("Quit game", 200, 60);
            gameMenu.addSubComponent(resumeButton);
            gameMenu.addSubComponent(optionsButton);
            gameMenu.addSubComponent(mainMenuButton);
            gameMenu.addSubComponent(quitButton);
            gameMenu.setRenderZ(-1);
            this.addUIComponent(gameMenu);
            this.paused = true;
            Mists.logger.info("GameMenu opened");
        } else {
            gameMenuOpen = false;
            this.removeUIComponent("GameMenu");
            this.paused = false;
            Mists.logger.info("GameMenu closed");
        }
        
    }
    
    public void openConsole() {
        uiComponents.put("Console", new Console(this));
        this.inConsole = true;
    }

    /**
     * The Tick command parses user input and sends an update(time) command to the
     * location game is currently at. These are both done only if game is not inside a menu.
     * In other words, the Location is paused while in a menu (that goes in the menu-stack).
     * @param time Time since last update
     * @param pressedButtons List of buttons pressed down by the user
     * @param releasedButtons  List of buttons released by the user
     */
    @Override
    public void tick(double time, ArrayList<KeyCode> pressedButtons, ArrayList<KeyCode> releasedButtons) {
        
        handleLocationKeyPress(pressedButtons, releasedButtons);
        if(this.paused == false) {
            game.getCurrentLocation().update(time);
        } 
    }
    
    @Override
    public void handleMouseEvent(MouseEvent me) {
        //See if there's an UI component to click      
        if(!mouseClickOnUI(me)){
            //If not, give the click to the underlying gameLocation
            Mists.logger.info("Click didnt land on an UI button");
            this.mouseClickOnLocation(me);
        }
    }
    
    /**
     * Check if there's any UI component at the mouse event location.
     * If so, trigger that UI components "onClick". 
     * @param me MouseEvent got from the game user (via Game)
     * @return True if UI component was clicked. False if there was no UI there
     */
    public boolean mouseClickOnUI(MouseEvent me) {
        double clickX = me.getX();
        double clickY = me.getY();
        //Mists.logger.info("UIC keyset: "+this.uiComponents.keySet());
        //Mists.logger.info("Descending keyset: "+this.uiComponents.descendingKeySet());
        for (UIComponent uic : this.drawOrder.descendingSet()) {
            double uicHeight = uic.getHeight();
            double uicWidth = uic.getWidth();
            double uicX = uic.getXPosition();
            double uicY = uic.getYPosition();
            //Check if the click landed on the ui component
            if (clickX >= uicX && clickX <= (uicX + uicWidth)) {
                if (clickY >= uicY && clickY <= uicY + uicHeight) {
                    uic.handleMouseEvent(me);
                    return true;
                }
            }
            
        }
        //Click landed on area without UI component
        return false;
    }
    
    private boolean mouseClickOnLocation(MouseEvent me) {
        double clickX = me.getX();
        double clickY = me.getY();
        if (me.getButton() == MouseButton.SECONDARY  && me.getEventType() == MouseEvent.MOUSE_RELEASED) {
            Mists.logger.info("Clicked right mousebutton at "+clickX+","+clickY+" - moving player there");
            double xOffset = this.game.getCurrentLocation().getLastxOffset();
            double yOffset = this.game.getCurrentLocation().getLastyOffset();
            this.game.getCurrentLocation().getPlayer().setCenterPosition(clickX+xOffset, clickY+yOffset);
            return true;
        }
        if (me.getButton() == MouseButton.PRIMARY && me.getEventType() == MouseEvent.MOUSE_RELEASED) {
            MapObject targetMob = game.getCurrentLocation().getMobAtLocation(clickX+game.getCurrentLocation().getLastxOffset(), clickY+game.getCurrentLocation().getLastyOffset());
            if (targetMob!=null) { 
                if (game.getCurrentLocation().getTargets().contains(targetMob)) game.getCurrentLocation().clearTarget();
                else {
                    Mists.logger.log(Level.INFO, "Targetted {0}", targetMob.toString());
                    game.getCurrentLocation().setTarget(targetMob);
                    //game.currentLocation.setScreenFocus(targetMob);
                }
                return true;
            }
            
        }
        //Click didn't do anything
        return false;
    }

    /**
     * Close the topmost open menu if menus/windows
     * are open. Return false if nothing was closed.
     * @return 
     */
    private boolean closeMenus() {
        for (UIComponent uic : this.drawOrder.descendingSet()) {
            if (uic.getRenderZ() < 0) return false;
            else this.removeUIComponent(uic);
            return true;
        }
        return false;
    }
    
    /**
     * HandleLocationKeyPresses takes in the arraylists of keypresses and releases from the Game,
     * and does location-appropriate things with them.
     * TODO: Load these keybindings from an external file.
     * @param pressedButtons
     * @param releasedButtons 
     */
    
    private void handleLocationKeyPress(ArrayList<KeyCode> pressedButtons, ArrayList<KeyCode> releasedButtons) {
        //TODO: External loadable configfile for keybindings
        //(probably via a class of its own)     
        game.getPlayer().stopMovement();
        if (pressedButtons.isEmpty() && releasedButtons.isEmpty()) {
            return;
        }
        
        if (!inConsole && releasedButtons.contains(KeyCode.F1)) {
            this.openConsole();
            releasedButtons.clear();
            return;
        }
        
        if (inConsole) {
            if (releasedButtons.contains(KeyCode.F1)) {
               this.uiComponents.remove("Console");
               this.inConsole = false;
            } else {
                Console c = (Console)this.uiComponents.get("Console");
                c.input(pressedButtons, releasedButtons);
            }
            releasedButtons.clear();
            return;
        }
        
        if (releasedButtons.contains(KeyCode.E)) {
            Mists.logger.info("E pressed for context action");
            this.contextAction.useTrigger();
        }
        
        if (releasedButtons.contains(KeyCode.I)) {
            Mists.logger.info("I pressed for Inventory");
            game.locControls.toggleInventory(this.playerInventory);
        }
        
        if (releasedButtons.contains(KeyCode.R)) {
            Mists.logger.info("RE pressed for next context action");
            this.contextAction.nextAction();
        }
        
        if (releasedButtons.contains(KeyCode.ENTER)) {
            game.locControls.printClearanceMapIntoConsole();
            game.locControls.printCollisionMapIntoConsole();
            
        }
        
        if (releasedButtons.contains(KeyCode.SHIFT)) {
            game.locControls.toggleFlag("testFlag");
        
        }
        
        if (releasedButtons.contains(KeyCode.ESCAPE)) {
            if (!this.closeMenus()) game.locControls.toggleLocationMenu();
        }

        //Location controls
        if (this.paused == false) {
        if (pressedButtons.contains(KeyCode.UP) || pressedButtons.contains(KeyCode.W)) {
            game.locControls.playerMove(Direction.UP);            
        }
        if (pressedButtons.contains(KeyCode.DOWN) || pressedButtons.contains(KeyCode.S)) {
            //Mists.logger.log(Level.INFO, "Moving {0} DOWN", currentLocation.getPlayer().getName());
            game.locControls.playerMove(Direction.DOWN);
        }
        if (pressedButtons.contains(KeyCode.LEFT) || pressedButtons.contains(KeyCode.A)) {
            //Mists.logger.log(Level.INFO, "Moving {0} LEFT", currentLocation.getPlayer().getName());
            game.locControls.playerMove(Direction.LEFT);
        }
        if (pressedButtons.contains(KeyCode.RIGHT) || pressedButtons.contains(KeyCode.D)) {
            //Mists.logger.log(Level.INFO, "Moving {0} RIGHT", currentLocation.getPlayer().getName());
            game.locControls.playerMove(Direction.RIGHT);
        }
        if ((pressedButtons.contains(KeyCode.UP) || pressedButtons.contains(KeyCode.W))
            && (pressedButtons.contains(KeyCode.RIGHT) || pressedButtons.contains(KeyCode.D))) {
            game.locControls.playerMove(Direction.UPRIGHT);            
        }
        if ((pressedButtons.contains(KeyCode.DOWN) || pressedButtons.contains(KeyCode.S))
            && (pressedButtons.contains(KeyCode.RIGHT) || pressedButtons.contains(KeyCode.D))) {
            //Mists.logger.log(Level.INFO, "Moving {0} DOWN", currentLocation.getPlayer().getName());
            game.locControls.playerMove(Direction.DOWNRIGHT);
        }
        if ((pressedButtons.contains(KeyCode.LEFT) || pressedButtons.contains(KeyCode.A))
                && (pressedButtons.contains(KeyCode.UP) || pressedButtons.contains(KeyCode.W))) {
            //Mists.logger.log(Level.INFO, "Moving {0} LEFT", currentLocation.getPlayer().getName());
            game.locControls.playerMove(Direction.UPLEFT);
        }
        if ((pressedButtons.contains(KeyCode.LEFT) || pressedButtons.contains(KeyCode.A)) 
                && (pressedButtons.contains(KeyCode.DOWN) || pressedButtons.contains(KeyCode.S))) {
            //Mists.logger.log(Level.INFO, "Moving {0} RIGHT", currentLocation.getPlayer().getName());
            game.locControls.playerMove(Direction.DOWNLEFT);
        }
        
        
        //TODO: These should be directed to the UI-layer, which knows which abilities player has bound where
        if (pressedButtons.contains(KeyCode.SPACE)) {
            //Mists.logger.log(Level.INFO, "{0} TRIED USING ABILITY 0", currentLocation.getPlayer().getName());
            game.locControls.playerAttack();
        }
        
        }
        
        releasedButtons.clear(); //Button releases are handled only once
    }
    

    
    @Override
    public Game getGame() {
        return this.game;
    }
    
    @Override
    public void exit() {
        Mists.soundManager.stopMusic();
    }

    @Override
    public void enter() {
        Mists.soundManager.playMusic("dungeon");
    }

    
    @Override
    public void addUIComponent(UIComponent uic) {
        this.uiComponents.put(uic.getName(), uic);
        this.drawOrder.add(uic);
    }
    
    @Override
    public boolean removeUIComponent(String uicName) {
        if (this.uiComponents.keySet().contains(uicName)) {
            //Mists.logger.info("Removing UIC "+uicName);
            this.drawOrder.remove(this.uiComponents.get(uicName));
            this.uiComponents.remove(uicName);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean removeUIComponent(UIComponent uic) {
        if (this.uiComponents.containsValue(uic)) {
            this.drawOrder.remove(uic);
            this.uiComponents.remove(uic.getName());
            return true;
        }
        return false;
    }
    
    @Override
    public UIComponent getUIComponent(String uicName) {
        return this.uiComponents.get(uicName);
    }
    
    
    
}
