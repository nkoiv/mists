/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gamestate;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.GameMode;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.controls.ContextAction;
import com.nkoiv.mists.game.dialogue.Dialogue;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.networking.LocationClient;
import com.nkoiv.mists.game.networking.LocationServer;
import com.nkoiv.mists.game.ui.ActionButton;
import com.nkoiv.mists.game.ui.AudioControls;
import com.nkoiv.mists.game.ui.AudioControls.MuteMusicButton;
import com.nkoiv.mists.game.ui.CharacterPanel;
import com.nkoiv.mists.game.ui.CombatPopup;
import com.nkoiv.mists.game.ui.Console;
import com.nkoiv.mists.game.ui.DialoguePanel;
import com.nkoiv.mists.game.ui.GoMainMenuButton;
import com.nkoiv.mists.game.ui.IconButton;
import com.nkoiv.mists.game.ui.InventoryPanel;
import com.nkoiv.mists.game.ui.LocationButtons;
import com.nkoiv.mists.game.ui.Overlay;
import com.nkoiv.mists.game.ui.QuestPanel;
import com.nkoiv.mists.game.ui.QuitButton;
import com.nkoiv.mists.game.ui.TextButton;
import com.nkoiv.mists.game.ui.TextPanel;
import com.nkoiv.mists.game.ui.TiledPanel;
import com.nkoiv.mists.game.ui.TiledWindow;
import com.nkoiv.mists.game.ui.UIComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Level;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * LocationState handles the core of the game: being in Locations.
 * As a GameState it takes in Input from the user via the Game -class,
 * and provides the Game the output of the Location (graphics, etc)
 * @author nikok
 */
public class LocationState implements GameState {
    
    private LocationServer server;
    private LocationClient client;

    private final Game game;
    //private UIComponent currentMenu;
    public boolean gameMenuOpen;
    public boolean paused;
    public double lastDragX;
    public double lastDragY;
    public boolean infoBoxOpen;
    //private final AudioControls audioControls = new AudioControls();
    //private final LocationButtons locationControls = new LocationButtons();
    private boolean inConsole;
    private final HashMap<String, UIComponent> uiComponents;
    private final TreeSet<UIComponent> drawOrder;
    private TextPanel infobox;
    private ContextAction contextAction;
    private InventoryPanel playerInventory;
    private DialoguePanel dialoguePanel;
    private QuestPanel questPanel;
    private CharacterPanel characterPanel;
    private CombatPopup sct;
    
    private boolean movingWithMouse = false;
    private double movingTowardsX;
    private double movingTowardsY;
    
    public LocationState (Game game) {
        Mists.logger.info("Generating locationstate for "+Mists.gameMode);
        this.game = game;
        uiComponents = new HashMap<>();
        this.drawOrder = new TreeSet<>();
        this.loadDefaultUI();
        if (Mists.gameMode == GameMode.SERVER) {
            setToServer();
        }
        if (Mists.gameMode == GameMode.CLIENT) {
            setToClient();
        }
        
    }
    
    private void setToClient() {
        try {    
                this.client = new LocationClient(game);
                //game.locControls.setLocationClient(client);
            } catch (Exception e) {
                Mists.logger.warning("Error starting client: "+e.getMessage());
                Mists.logger.warning("Changing to Singleplayer");
            }
    }
    
    private void setToServer() {
        try {    
                this.server = new LocationServer(game);
                //game.locControls.setLocationServer(server);
            } catch (Exception e) {
                Mists.logger.warning("Error starting server: "+e.getMessage());
                Mists.logger.warning("Changing to Singleplayer");
            }
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
    
    public void loadDefaultUI() {
        this.uiComponents.clear();
        this.drawOrder.clear();
        this.infobox = new TextPanel(this, "InfoBox", 250, 150, game.WIDTH-300, game.HEIGHT-500, Mists.graphLibrary.getImageSet("panelBeigeLight"));
        this.infobox.addCloseButton();
        this.playerInventory = new InventoryPanel(this, game.getPlayer().getInventory());
        this.playerInventory.setBgOpacity(0.8);
        this.playerInventory.setName("PlayerInventory");
        ContextAction ca = new ContextAction(game.getPlayer());
        if (Mists.gameMode == GameMode.CLIENT) {
            ca.setLocationClient(client);
        }
        this.contextAction = ca;
        this.inConsole = false;
        
        DialoguePanel dp = new DialoguePanel(this);
        this.dialoguePanel = dp;
        dp.setPosition(100, 100);
        this.questPanel = new QuestPanel(this);
        questPanel.setPosition(20, 20);
        this.characterPanel = new CharacterPanel(this, game.getPlayer());
        this.sct = new CombatPopup();
        
        this.addUIComponent(this.generateActionBar());
        this.addUIComponent(generateGeneralBar());
    }
    
    private TiledWindow generateGeneralBar(){
        int buttonCount = 5;
        TiledWindow generalBar = new TiledWindow(this, "GeneralButtons", 70, buttonCount*60, 0, 80);
        generalBar.setBgOpacity(0.3);
        IconButton menuButton = new LocationButtons.ToggleLocationMenuButton(game);
        generalBar.addSubComponent(menuButton);
        IconButton characterButton = new LocationButtons.ToggleCharacterPanelButton(game);
        generalBar.addSubComponent(characterButton);
        IconButton inventoryButton = new LocationButtons.ToggleInventoryButton(game, playerInventory);
        generalBar.addSubComponent(inventoryButton);
        IconButton questlogButton = new LocationButtons.ToggleQuestLogButton(game);
        generalBar.addSubComponent(questlogButton);
        MuteMusicButton muteMusicButton = new AudioControls.MuteMusicButton();
        generalBar.addSubComponent(muteMusicButton);        
        generalBar.setRenderZ(-11);
        return generalBar;
    }
    
    private TiledWindow generateActionBar() {
        TiledWindow actionBar = new TiledWindow(this, "Actionbar", game.WIDTH, 80, 0, (game.HEIGHT - 80));
        actionBar.setBgOpacity(0.3);
        TextButton attackButton = new ActionButton(game.getPlayer(), "Firebolt",  80, 60);
        TextButton pathsButton = new LocationButtons.DrawPathsButton("Paths Off", 80, 60, this.game);
        TextButton lightenButton = new LocationButtons.IncreaseLightlevelButton("Lighten", 80, 60, this.game);
        TextButton darkenButton = new LocationButtons.ReduceLightlevelButton("Darken", 80, 60, this.game);
        TextButton toggleScaleButton = new LocationButtons.ToggleScaleButton("Resize", 80, 60, this.game);
        
        actionBar.addSubComponent(attackButton);
        actionBar.addSubComponent(pathsButton);
        actionBar.addSubComponent(lightenButton);
        actionBar.addSubComponent(darkenButton);
        actionBar.addSubComponent(toggleScaleButton);
        actionBar.setRenderZ(-10);
        return actionBar;
    }
    
    public void addDamageFloat(int damage, MapObject target) {
        this.sct.addNumberPopup(target, damage);
    }
    
    public void addTextFloat(String text, MapObject target) {
        this.sct.addSCT(target, text, Color.CYAN);
    }
    
    public void openDialogue(Dialogue dialogue) {
        Mists.logger.info("Opening dialogue: "+dialogue.getCurrentCard().getText());
        this.dialoguePanel.setDialogue(dialogue);
        this.addUIComponent(this.dialoguePanel);
    }
    
    public void closeDialogue() {
        this.removeUIComponent(this.dialoguePanel);
        this.dialoguePanel.setDialogue(null);
    }
    
    public void toggleCharacterPanel() {
        Mists.logger.info("Toggling character panel");
        if (this.uiComponents.containsKey(this.characterPanel.getName())) {
            //Mists.logger.info("Quest panel was already open");
            this.closeCharacterPanel();
        } else {
            //Mists.logger.info("Quest panel was closed, opening...");
            this.openCharacterPanel();
        }
    }
    
    public void toggleQuestPanel() {
        Mists.logger.info("Toggling quest panel");
        if (this.uiComponents.containsKey(this.questPanel.getName())) {
            //Mists.logger.info("Quest panel was already open");
            this.closeQuestPanel();
        } else {
            //Mists.logger.info("Quest panel was closed, opening...");
            this.openQuestPanel();
        }
    }
    
    public void openCharacterPanel() {
        this.addUIComponent(this.characterPanel);
    }
    
    public void closeCharacterPanel() {
        this.removeUIComponent(this.characterPanel);
    }
    
    public void openQuestPanel() {
        Mists.logger.info("Opening quest panel");
        this.addUIComponent(this.questPanel);
    }
    
    public void closeQuestPanel() {
        Mists.logger.info("Closing quest panel");
        this.removeUIComponent(this.questPanel);
    }

    /**
     * The LocationState renderer does things in two layers: Game and UI.
     * Both of these layers are handled with via Canvases. First the Game is rendered on
     * the gameCanvas, then the UI is rendered on the uiCanvas on top of it
     * TODO: Consider separating gameplay into several layers (ground, structures, creatures, (structure)frill, overhead?)
     * @param gameCanvas Canvas to draw the actual gameplay on
     * @param uiCanvas Canvas to draw the UI on
     * @param shadowCanvas Canvas for shadows
     */
    @Override
    public void render(Canvas gameCanvas, Canvas uiCanvas, Canvas shadowCanvas) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        GraphicsContext sc = shadowCanvas.getGraphicsContext2D();
        double screenWidth = gameCanvas.getWidth();
        double screenHeight = gameCanvas.getHeight();
        
        if (this.game.toggleScale) {
            toggleScale(gameCanvas.getGraphicsContext2D());
            this.game.toggleScale = false;
        }
        
        //Render the current Location unless paused
        if (!this.paused) {
            gc.clearRect(0, 0, screenWidth, screenHeight);
            sc.clearRect(0, 0, screenWidth, screenHeight);
            if (game.getCurrentLocation() != null) {
                game.getCurrentLocation().render(gc, sc);
                //Render Location overlay
                Overlay.drawAllHPBars(gc, game.getCurrentLocation().getLastRenderedMobs());
            }
        }
        
        //Render the UI
        this.renderUI(uiCanvas);
        
    }
    
    private void renderUI(Canvas uiCanvas) {
        GraphicsContext uigc = uiCanvas.getGraphicsContext2D();
        double screenWidth = uiCanvas.getWidth();
        double screenHeight = uiCanvas.getHeight();
        //Render the UI
        uigc.clearRect(0, 0, screenWidth, screenHeight);
        if (this.game.getCurrentLocation() != null) {
            this.updateInfoBox();
            this.contextAction.update();
            if (this.contextAction.getCurrentTrigger() != null) {
                Overlay.drawHighlightRectangle(uigc, this.contextAction.getTriggerObjects());
                Overlay.drawTriggerCursor(uigc, this.contextAction.getCurrentTrigger());
            }
            this.sct.render(uigc);
        }
        
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
    
    private void updateInfoBox() {
        if (game.getCurrentLocation().getTargets().size() < 1) {
            if (this.uiComponents.containsValue(this.infobox)) this.removeUIComponent("InfoBox");
            return;
        }
        this.infobox.setText(Overlay.generateInfoBoxText(game.getCurrentLocation().getTargets().get(0)));
    }
    
    
    /**
     * Toggles open/close the gameMenu, displayed at the middle of the screen.
     * TODO: Consider making a separate class for this, in case other GameStates utilize the same
     */
    public void toggleGameMenu() {
        Mists.logger.info("Game menu toggled");
        if (!gameMenuOpen) {
            gameMenuOpen = true;
            TiledPanel gameMenu = new TiledPanel(this, "GameMenu", 220, 300, 100, 100,Mists.graphLibrary.getImageSet("panelBeige"));
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
        Console console = new Console(this);
        this.addUIComponent(console);
        this.inConsole = true;
    }
    
    public void closeConsole() {
        this.inConsole = false;
        this.removeUIComponent("Console");
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
        if (this.paused || game.getCurrentLocation() == null) return;
        if (movingWithMouse) {
            if (pressedButtons.contains(KeyCode.SHIFT) && !game.getPlayer().dashOnCooldown()) {
                game.locControls.playerDash(movingTowardsX, movingTowardsY);
            }
            else game.locControls.playerMove(movingTowardsX, movingTowardsY);
        }
        handleLocationKeyPress(pressedButtons, releasedButtons);
        switch (Mists.gameMode) {
            case SINGLEPLAYER: this.tickSinglePlayer(time); break;
            case SERVER: this.tickServer(time); break;
            case CLIENT: this.tickClient(time); break;
            default: Mists.logger.warning("Trying to access an unavailable gamemode in location state!"); break;
        }
        this.sct.tick(time);
    }
    
    private void tickSinglePlayer(double time) {
        if(!this.paused) {
            game.getCurrentLocation().update(time);
        } 
    }
    
    private void tickServer(double time) {
        if (this.server == null) setToServer();
        this.server.tick(time);
    }
    
    private void tickClient(double time) {
        if (this.client == null) setToClient();
        this.client.tick(time);
        
    }
    
    @Override
    public void handleMouseEvent(MouseEvent me) {
        if (game.getCurrentLocation() !=null ) {
            double xOffset = this.game.getCurrentLocation().getLastxOffset();
            double yOffset = this.game.getCurrentLocation().getLastyOffset();
            movingTowardsX = me.getSceneX()+xOffset;
            movingTowardsY = me.getSceneY()+yOffset;
        }
        //See if there's an UI component to click
        if (me.getEventType() == MouseEvent.MOUSE_CLICKED || me.getEventType() == MouseEvent.MOUSE_PRESSED || me.getEventType() == MouseEvent.MOUSE_RELEASED) this.handleClicks(me);
        if (me.getEventType() == MouseEvent.MOUSE_DRAGGED) this.handleMouseDrags(me);
        me.consume();
    }
    
    private void handleClicks(MouseEvent me) {
        this.lastDragX = 0; this.lastDragY = 0;
        if(!mouseClickOnUI(me)){
            //If not, give the click to the underlying gameLocation
            //Mists.logger.info("Click didnt land on an UI button");
            this.mouseClickOnLocation(me);
        }
    }
    
    private void handleMouseDrags(MouseEvent me) {
        if (lastDragX == 0 || lastDragY == 0) {
            lastDragX = me.getX();
            lastDragY = me.getY();
        }
        UIComponent uic = this.getUIComponentAtCoordinates(me.getX(), me.getY());
        if (uic != null) uic.handleMouseDrag(me, lastDragX, lastDragY);
        lastDragX = me.getX(); lastDragY = me.getY();
    }
    
    protected UIComponent getUIComponentAtCoordinates(double xCoor, double yCoor) {
        for (UIComponent uic : this.drawOrder.descendingSet()) {
            double uicHeight = uic.getHeight();
            double uicWidth = uic.getWidth();
            double uicX = uic.getXPosition();
            double uicY = uic.getYPosition();
            //Check if the click landed on the ui component
            if (xCoor >= uicX && xCoor <= (uicX + uicWidth) && yCoor >= uicY && yCoor <= uicY + uicHeight) {
                return uic;
            }
        }
        //Click landed on area without UI component
        return null;
    }
    
    /**
     * Check if there's any UI component at the mouse event location.
     * If so, trigger that UI components "onClick". 
     * @param me MouseEvent got from the game user (via Game)
     * @return True if UI component was clicked. False if there was no UI there
     */
    public boolean mouseClickOnUI(MouseEvent me) {
        UIComponent uic = this.getUIComponentAtCoordinates(me.getX(), me.getY());
        if (uic != null) {
            uic.handleMouseEvent(me);
            me.consume();
            return true;
        }
        return false;
        
    }
    
    private boolean mouseClickOnLocation(MouseEvent me) {
        if (game.getCurrentLocation() == null) return false;
        double clickX = me.getX();
        double clickY = me.getY();
        double xOffset = this.game.getCurrentLocation().getLastxOffset();
        double yOffset = this.game.getCurrentLocation().getLastyOffset();
        if (me.getButton() == MouseButton.PRIMARY && me.getEventType() == MouseEvent.MOUSE_PRESSED) movingWithMouse = true;
        if (me.getButton() == MouseButton.PRIMARY && me.getEventType() == MouseEvent.MOUSE_RELEASED) movingWithMouse = false;
        if (me.getButton() == MouseButton.SECONDARY  && me.getEventType() == MouseEvent.MOUSE_RELEASED) {
            //Secondary mouse button teleports player
            //Mists.logger.info("Clicked right mousebutton at "+clickX+","+clickY+" - moving player there");
            //game.locControls.teleportPlayer(clickX+xOffset, clickY+yOffset);
            return true;
        }
        if (me.getButton() == MouseButton.PRIMARY && me.getEventType() == MouseEvent.MOUSE_RELEASED) {
            //use default action towards mouseclick coordinates
            //game.locControls.playerAttackMove(clickX+xOffset, clickY+yOffset);
            //Select a target if possible
            if (toggleTarget(clickX, clickY)) return true;
        }
        //Click didn't do anything
        return false;
    }

    /**
     * Check the given mouse coordinates for a map object,
     * and make it the current target (updating infobox) if
     * there is one.
     * If the same mob already was targetted, release the targeting
     * @param clickX local xCoordinate of the mouseclick
     * @param clickY local yCoordinate of the mouseclick
     * @return true if a mob was clicked
     */
    private boolean toggleTarget(double clickX, double clickY) {
        MapObject targetMob = game.getCurrentLocation().getMobAtLocation(clickX+game.getCurrentLocation().getLastxOffset(), clickY+game.getCurrentLocation().getLastyOffset());
        if (targetMob!=null) { 
            if (game.getCurrentLocation().getTargets().contains(targetMob)) {
                //There already is the same mob targetted, so reselection should clear the targetting
                //... unless it was for contextAction
                if (!contextAction.setTriggerOnMobIfInRange(targetMob)) {
                    game.getCurrentLocation().clearTarget();
                    this.removeUIComponent("InfoBox");
                }
            }
            else {
                Mists.logger.log(Level.INFO, "Targetted {0}", targetMob.toString());
                game.getCurrentLocation().setTarget(targetMob);
                this.addUIComponent(this.infobox);
                //If the target has context-action vible triggers, it should be selected for context action
                contextAction.setTriggerOnMobIfInRange(targetMob);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Close the topmost open menu if menus/windows
     * are open. Return false if nothing was closed.
     * @return 
     */
    private boolean closeMenus() {
        //Close the frontmost menu/window
        for (UIComponent uic : this.drawOrder.descendingSet()) {
            if (uic.getRenderZ() < 0) break;
            else return this.removeUIComponent(uic);
        }
        //If no window was closed, deselect a target if a target is selected
        if (!game.getCurrentLocation().getTargets().isEmpty()) {
            game.getCurrentLocation().clearTarget();
            return true;
        }
        //If nothing was closed, return false
        else return false;
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
               this.closeConsole();
            } else {
                Console c = (Console)this.uiComponents.get("Console");
                c.input(pressedButtons, releasedButtons);
            }
            releasedButtons.clear();
            return;
        }
        
        if (releasedButtons.contains(KeyCode.DIGIT1)) {
            TiledWindow ab = (TiledWindow)this.uiComponents.get("Actionbar");
            if (ab.getSubComponents().get(0) instanceof ActionButton) {
                ((ActionButton)ab.getSubComponents().get(0)).buttonPress();
            } else if (ab.getSubComponents().get(0) instanceof TextButton) {
                ((TextButton)ab.getSubComponents().get(0)).buttonPress();
            }
        }
        if (releasedButtons.contains(KeyCode.DIGIT2)) {
            TiledWindow ab = (TiledWindow)this.uiComponents.get("Actionbar");
            if (ab.getSubComponents().get(1) instanceof ActionButton) {
                ((ActionButton)ab.getSubComponents().get(1)).buttonPress();
            } else if (ab.getSubComponents().get(1) instanceof TextButton) {
                ((TextButton)ab.getSubComponents().get(1)).buttonPress();
            }
        }
        if (releasedButtons.contains(KeyCode.DIGIT3)) {
            TiledWindow ab = (TiledWindow)this.uiComponents.get("Actionbar");
            if (ab.getSubComponents().get(2) instanceof ActionButton) {
                ((ActionButton)ab.getSubComponents().get(2)).buttonPress();
            } else if (ab.getSubComponents().get(2) instanceof TextButton) {
                ((TextButton)ab.getSubComponents().get(2)).buttonPress();
            }
        }
        if (releasedButtons.contains(KeyCode.DIGIT4)) {
            TiledWindow ab = (TiledWindow)this.uiComponents.get("Actionbar");
            if (ab.getSubComponents().get(3) instanceof ActionButton) {
                ((ActionButton)ab.getSubComponents().get(3)).buttonPress();
            } else if (ab.getSubComponents().get(3) instanceof TextButton) {
                ((TextButton)ab.getSubComponents().get(3)).buttonPress();
            }
        }
        if (releasedButtons.contains(KeyCode.DIGIT5)) {
            TiledWindow ab = (TiledWindow)this.uiComponents.get("Actionbar");
            if (ab.getSubComponents().get(4) instanceof ActionButton) {
                ((ActionButton)ab.getSubComponents().get(4)).buttonPress();
            } else if (ab.getSubComponents().get(4) instanceof TextButton) {
                ((TextButton)ab.getSubComponents().get(4)).buttonPress();
            }
        }
        
        if (releasedButtons.contains(KeyCode.L)) {
            Mists.logger.info("L pressed for QuestPanel");
            game.locControls.toggleQuestPanel();
        }
        
        if (releasedButtons.contains(KeyCode.Q)) {
            Mists.logger.info("Q pressed for next ContextAction");
            this.contextAction.nextAction();
        }
        
        if (releasedButtons.contains(KeyCode.E)) {
            Mists.logger.info("E pressed for context action");
            int triggerTarget = this.contextAction.getSelectedTriggerSourceID();
            game.locControls.useTrigger(triggerTarget, 0);
        }
        
        if (releasedButtons.contains(KeyCode.I)) {
            Mists.logger.info("I pressed for Inventory");
            game.locControls.toggleInventory(this.playerInventory);
        }
        
        if (releasedButtons.contains(KeyCode.ENTER)) {
            game.locControls.printClearanceMapIntoConsole();
            game.locControls.printCollisionMapIntoConsole();
            
        }
        
        if (releasedButtons.contains(KeyCode.ESCAPE)) {
            if (!this.closeMenus()) game.locControls.toggleLocationMenu();
            game.getCurrentLocation().clearTarget();
        }

        //Location controls
        if (!this.paused) {
        if (pressedButtons.contains(KeyCode.UP) || pressedButtons.contains(KeyCode.W)) {
            if (pressedButtons.contains(KeyCode.SHIFT) && !game.getPlayer().dashOnCooldown()) {
                game.locControls.playerDash(Direction.UP);            
            }
            else game.locControls.playerMove(Direction.UP);            
        }
        if (pressedButtons.contains(KeyCode.DOWN) || pressedButtons.contains(KeyCode.S)) {
            //Mists.logger.log(Level.INFO, "Moving {0} DOWN", currentLocation.getPlayer().getName());
            if (pressedButtons.contains(KeyCode.SHIFT) && !game.getPlayer().dashOnCooldown()) {
                game.locControls.playerDash(Direction.DOWN);            
            }
            else game.locControls.playerMove(Direction.DOWN);
        }
        if (pressedButtons.contains(KeyCode.LEFT) || pressedButtons.contains(KeyCode.A)) {
            //Mists.logger.log(Level.INFO, "Moving {0} LEFT", currentLocation.getPlayer().getName());
            if (pressedButtons.contains(KeyCode.SHIFT) && !game.getPlayer().dashOnCooldown()) {
                game.locControls.playerDash(Direction.LEFT);            
            }
            else game.locControls.playerMove(Direction.LEFT);
        }
        if (pressedButtons.contains(KeyCode.RIGHT) || pressedButtons.contains(KeyCode.D)) {
            //Mists.logger.log(Level.INFO, "Moving {0} RIGHT", currentLocation.getPlayer().getName());
            if (pressedButtons.contains(KeyCode.SHIFT) && !game.getPlayer().dashOnCooldown()) {
                game.locControls.playerDash(Direction.RIGHT);            
            }
            else game.locControls.playerMove(Direction.RIGHT);
        }
        if ((pressedButtons.contains(KeyCode.UP) || pressedButtons.contains(KeyCode.W))
            && (pressedButtons.contains(KeyCode.RIGHT) || pressedButtons.contains(KeyCode.D))) {
            if (pressedButtons.contains(KeyCode.SHIFT) && !game.getPlayer().dashOnCooldown()) {
                game.locControls.playerDash(Direction.UPRIGHT);            
            }
            else game.locControls.playerMove(Direction.UPRIGHT);            
        }
        if ((pressedButtons.contains(KeyCode.DOWN) || pressedButtons.contains(KeyCode.S))
            && (pressedButtons.contains(KeyCode.RIGHT) || pressedButtons.contains(KeyCode.D))) {
            //Mists.logger.log(Level.INFO, "Moving {0} DOWN", currentLocation.getPlayer().getName());
            if (pressedButtons.contains(KeyCode.SHIFT) && !game.getPlayer().dashOnCooldown()) {
                game.locControls.playerDash(Direction.DOWNRIGHT);            
            }
            else game.locControls.playerMove(Direction.DOWNRIGHT);
        }
        if ((pressedButtons.contains(KeyCode.LEFT) || pressedButtons.contains(KeyCode.A))
                && (pressedButtons.contains(KeyCode.UP) || pressedButtons.contains(KeyCode.W))) {
            //Mists.logger.log(Level.INFO, "Moving {0} LEFT", currentLocation.getPlayer().getName());
            if (pressedButtons.contains(KeyCode.SHIFT) && !game.getPlayer().dashOnCooldown()) {
                game.locControls.playerDash(Direction.UPLEFT);            
            }
            else game.locControls.playerMove(Direction.UPLEFT);
        }
        if ((pressedButtons.contains(KeyCode.LEFT) || pressedButtons.contains(KeyCode.A)) 
                && (pressedButtons.contains(KeyCode.DOWN) || pressedButtons.contains(KeyCode.S))) {
            //Mists.logger.log(Level.INFO, "Moving {0} RIGHT", currentLocation.getPlayer().getName());
            if (pressedButtons.contains(KeyCode.SHIFT) && !game.getPlayer().dashOnCooldown()) {
                game.locControls.playerDash(Direction.DOWNLEFT);            
            }
            else game.locControls.playerMove(Direction.DOWNLEFT);
        }
        
        
        //TODO: These should be directed to the UI-layer, which knows which abilities player has bound where
        if (pressedButtons.contains(KeyCode.SPACE)) {
            //Mists.logger.log(Level.INFO, "{0} TRIED USING ABILITY 0", currentLocation.getPlayer().getName());
            game.locControls.playerAttack();
        }
        
        }
        
    }
    
    public LocationClient getClient() {
        return this.client;
    }
    
    public LocationServer getServer() {
        return this.server;
    }
    
    @Override
    public Game getGame() {
        return this.game;
    }
    
    @Override
    public void exit() {
        Mists.soundManager.stopMusic();
        this.paused = true;
    }

    @Override
    public void enter() {
        if (game.getCurrentLocation().getEnvironment().getDefaultMusic() != null) {
            Mists.logger.log(Level.INFO, "Location entered, playing music: {0}", game.getCurrentLocation().getEnvironment().getDefaultMusic());
            Mists.soundManager.playMusic(game.getCurrentLocation().getEnvironment().getDefaultMusic());
        }
        this.loadDefaultUI();
        this.paused = false;
    }

    
    @Override
    public void addUIComponent(UIComponent uic) {
        if (this.uiComponents.containsValue(uic)) {
            this.uiComponents.remove(uic);
            this.drawOrder.remove(uic);
        }
        this.uiComponents.put(uic.getName(), uic);
        uic.setRenderZ(getNextFreeDrawZ(uic));
        this.drawOrder.add(uic);
        Mists.logger.info("Currently in the UIC-map: "+this.uiComponents.keySet());
        Mists.logger.info("Currently in the drawOrder: "+this.drawOrder.toString());
    }
    
    private int getNextFreeDrawZ(UIComponent uic) {
        int newRenderZ = uic.getRenderZ();
        while (drawOrder.contains(uic)) {
            newRenderZ++;
            uic.setRenderZ(newRenderZ);
        }
        return newRenderZ;
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
