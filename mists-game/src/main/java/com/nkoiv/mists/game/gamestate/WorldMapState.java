/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeSet;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Game;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.controls.WorldMapControls;
import com.nkoiv.mists.game.gameobject.MapObject;
import com.nkoiv.mists.game.ui.GoMainMenuButton;
import com.nkoiv.mists.game.ui.LocationButtons;
import com.nkoiv.mists.game.ui.PopUpMenu;
import com.nkoiv.mists.game.ui.QuitButton;
import com.nkoiv.mists.game.ui.TextButton;
import com.nkoiv.mists.game.ui.TiledPanel;
import com.nkoiv.mists.game.ui.UIComponent;
import com.nkoiv.mists.game.world.worldmap.LocationNode;
import com.nkoiv.mists.game.world.worldmap.MapNode;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
/**
 *
 * @author nikok
 */
public class WorldMapState implements GameState {
	public boolean gameMenuOpen;
    private HashMap<String, UIComponent> uiComponents;
    private final TreeSet<UIComponent> drawOrder;
    private final Game game;
    //private UIComponent currentMenu;
    //private boolean gameMenuOpen;
    private double lastDragX;
    private double lastDragY;
    private long lastClick; //To prevent doubleclicks - TODO: Is this really a good way to do this?
    private double doubleClickTressholdMS = 200;
    
    public WorldMapState(Game game) {
        Mists.logger.info("Generating worldmapstate for "+Mists.gameMode);
        this.game = game;
        uiComponents = new HashMap<>();
        this.drawOrder = new TreeSet<>();
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
        Mists.soundManager.playMusic("menu");
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

   @Override
    public void render(Canvas gameCanvas, Canvas uiCanvas, Canvas shadowCanvas) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        GraphicsContext sc = shadowCanvas.getGraphicsContext2D();
        double screenWidth = gameCanvas.getWidth();
        double screenHeight = gameCanvas.getHeight();
        sc.clearRect(0, 0, screenWidth, screenHeight);
        gc.clearRect(0, 0, screenWidth, screenHeight);
        if (game.getCurrentWorldMap() != null) {
            game.getCurrentWorldMap().render(gc);
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

        if (this.drawOrder != null) {
            for (UIComponent uic : this.drawOrder) {
                uic.render(uigc, 0, 0);
            }
        }
        
    }

    @Override
    public void tick(double time, ArrayList<KeyCode> pressedButtons, ArrayList<KeyCode> releasedButtons) {
        handleWorldMapKeyPress(pressedButtons, releasedButtons);
        game.getCurrentWorldMap().tick(time);
        //Mists.logger.info("PlayerNode is now "+game.getCurrentWorldMap().getPlayerNode().getName());
    }

    /**
     * Toggles open/close the gameMenu, displayed at the middle of the screen.
     * TODO: Consider making a separate class for this, in case other GameStates utilize the same
     */
    public void toggleGameMenu() {
        Mists.logger.info("Game menu toggled");
        if (!gameMenuOpen) {
            gameMenuOpen = true;
            TiledPanel gameMenu = new TiledPanel(this, "GameMenu", 220, 360, 100, 100,Mists.graphLibrary.getImageSet("panelBeige"));
            //TextButton resumeButton = new LocationButtons.ResumeButton("Resume", 200, 60, this.game);
            TextButton saveButton = new LocationButtons.SaveButton("Save player", 200, 60, game);
            TextButton loadButton = new LocationButtons.LoadButton("Load player", 200, 60, game);
            TextButton optionsButton = new TextButton("TODO: Options", 200, 60);
            GoMainMenuButton mainMenuButton = new GoMainMenuButton(this.game, 200, 60);
            QuitButton quitButton = new QuitButton("Quit game", 200, 60);
            //gameMenu.addSubComponent(resumeButton);
            gameMenu.addSubComponent(saveButton);
            gameMenu.addSubComponent(loadButton);
            gameMenu.addSubComponent(optionsButton);
            gameMenu.addSubComponent(mainMenuButton);
            gameMenu.addSubComponent(quitButton);
            gameMenu.setRenderZ(-1);
            this.addUIComponent(gameMenu);
            Mists.logger.info("GameMenu opened");
        } else {
            gameMenuOpen = false;
            this.removeUIComponent("GameMenu");
            Mists.logger.info("GameMenu closed");
        }
        
    }
    
    @Override
    public void handleMouseEvent(MouseEvent me) {
        //See if there's an UI component to click
        if (me.getEventType() == MouseEvent.MOUSE_CLICKED || me.getEventType() == MouseEvent.MOUSE_PRESSED || me.getEventType() == MouseEvent.MOUSE_RELEASED) this.handleClicks(me);
        if (me.getEventType() == MouseEvent.MOUSE_DRAGGED) this.handleMouseDrags(me);
        me.consume();
    }
    
    private void handleClicks(MouseEvent me) {
    	if (System.currentTimeMillis() - lastClick < doubleClickTressholdMS) return;
    	this.lastClick = System.currentTimeMillis();
        this.lastDragX = 0; this.lastDragY = 0;
        if(!mouseClickOnUI(me)){
            //If not, give the click to the underlying gameLocation
            //Mists.logger.info("Click didnt land on an UI button");
            this.mouseClickOnMap(me);
        }
    }
    
    /**
     * 
     * @param me MouseEvent that landed on the world map
     */
    private void mouseClickOnMap(MouseEvent me) {
        double clickX = me.getX() + game.getCurrentWorldMap().getLastOffsets()[0];
        double clickY = me.getY() + game.getCurrentWorldMap().getLastOffsets()[1];
        MapObject mob = game.getCurrentWorldMap().mobAtCoordinates(clickX, clickY);
        MapNode mn = game.getCurrentWorldMap().getNodeAtCoordinates(clickX, clickY);
        if (!(mn instanceof MapNode)) return;
        //Node was a valid neighbour, so continue
        if (mn.equals(game.getCurrentWorldMap().getPlayerNode())) {
        	//If the clicked node was current node, move to the Location the node links to
        	if (mn instanceof LocationNode) {
                game.moveToLocation(((LocationNode)mn).getLocationID(), mn);
                game.moveToState(Game.LOCATION);
            } 
        } else  if (game.getCurrentWorldMap().getPlayerNode().getNeighboursAsAList().contains(mn.getID())) {
            //If the clicked node neighbours the node we're currently at, move to it
        	WorldMapControls.moveToNode(game.getCurrentWorldMap(), mn);
        }
        me.consume();
        /*
        //Logging for testing
        if (mob != null) Mists.logger.info("Click landed on MOB "+mob.getName());
        if (mn != null) Mists.logger.info("Click landed on MapNode"+mn.getName());
        if (mn == null && mob == null) Mists.logger.info("Click at "+clickX+","+clickY +" didn't land on anything");
        */
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
    
    private void handleWorldMapKeyPress(ArrayList<KeyCode> pressedButtons, ArrayList<KeyCode> releasedButtons) {
        //TODO: External loadable configfile for keybindings
        //(probably via a class of its own)
        if (pressedButtons.isEmpty() && releasedButtons.isEmpty()) {
            return;
        }
        
        if (releasedButtons.contains(KeyCode.ENTER) || releasedButtons.contains(KeyCode.E)) {
            MapNode mn = game.getCurrentWorldMap().getPlayerNode();
            if (mn instanceof LocationNode) {
                game.moveToLocation(((LocationNode)mn).getLocationID(), mn);
                game.moveToState(Game.LOCATION);
            }    
        }
        
        //Movement controls
        if (releasedButtons.contains(KeyCode.UP) || releasedButtons.contains(KeyCode.W)) {
        	WorldMapControls.moveToDirecition(game.getCurrentWorldMap(), Direction.UP);
            
        }
        if (releasedButtons.contains(KeyCode.DOWN) || releasedButtons.contains(KeyCode.S)) {
            WorldMapControls.moveToDirecition(game.getCurrentWorldMap(), Direction.DOWN);
        }
        if (releasedButtons.contains(KeyCode.LEFT) || releasedButtons.contains(KeyCode.A)) {
        	WorldMapControls.moveToDirecition(game.getCurrentWorldMap(), Direction.LEFT);
        }
        if (releasedButtons.contains(KeyCode.RIGHT) || releasedButtons.contains(KeyCode.D)) {
        	WorldMapControls.moveToDirecition(game.getCurrentWorldMap(), Direction.RIGHT);
        }
        if ((releasedButtons.contains(KeyCode.UP) || releasedButtons.contains(KeyCode.W))
            && (releasedButtons.contains(KeyCode.RIGHT) || releasedButtons.contains(KeyCode.D))) {
        	WorldMapControls.moveToDirecition(game.getCurrentWorldMap(), Direction.UPRIGHT);
        }
        if ((releasedButtons.contains(KeyCode.DOWN) || releasedButtons.contains(KeyCode.S))
            && (releasedButtons.contains(KeyCode.RIGHT) || releasedButtons.contains(KeyCode.D))) {
        	WorldMapControls.moveToDirecition(game.getCurrentWorldMap(), Direction.DOWNRIGHT);
        }
        if ((releasedButtons.contains(KeyCode.LEFT) || releasedButtons.contains(KeyCode.A))
                && (releasedButtons.contains(KeyCode.UP) || releasedButtons.contains(KeyCode.W))) {
        	WorldMapControls.moveToDirecition(game.getCurrentWorldMap(), Direction.UPLEFT);
        }
        if ((releasedButtons.contains(KeyCode.LEFT) || releasedButtons.contains(KeyCode.A)) 
                && (releasedButtons.contains(KeyCode.DOWN) || releasedButtons.contains(KeyCode.S))) {
        	WorldMapControls.moveToDirecition(game.getCurrentWorldMap(), Direction.DOWNLEFT);
        }
        
        
        
        if (pressedButtons.contains(KeyCode.SPACE)) {
            
        }

    }
    
    @Override
    public void closePopUpWindows() {
        Stack<UIComponent> popups = new Stack<>();
        for (String k : this.uiComponents.keySet()) {
            if (this.uiComponents.get(k) instanceof PopUpMenu) popups.add(this.uiComponents.get(k));
        }
        while (!popups.isEmpty()) {
            this.removeUIComponent(popups.pop());
        }
    }

    @Override
    public void updateUI() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public int getStateID() {
    	return Game.WORLDMAP;
    }

}
