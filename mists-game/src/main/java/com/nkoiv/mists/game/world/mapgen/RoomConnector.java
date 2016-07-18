/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.world.mapgen;


/**
 * RoomConnector links different rooms together.
 * Having the connectors as separate entities helps
 * create puzzles that block access to separate parts
 * of a dungeon.
 * A connector is always either horizontal or vertical 
 * @author nkoiv
 *
 */
public class RoomConnector {
	private int xPosition;
	private int yPosition;
	private boolean horizontal; //if false, the connector is vertical
	private int neighbours[]; //0:up, 1:right, 2:down, 3:left
	
	public RoomConnector(int xPos, int yPos, boolean horizontal, int[]neighbours) {
		this.xPosition = xPos;
		this.yPosition = yPos;
		this.horizontal = horizontal;
		this.neighbours = neighbours;
	}
	
	
	public boolean isHorizontal() {
		return this.horizontal;
	}
	
	
	
}
