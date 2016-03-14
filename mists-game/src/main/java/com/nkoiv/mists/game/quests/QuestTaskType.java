/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.quests;

/**
 * QuestTaskType is an ENUM for various different quest objectives.
 * Each individual trackable thing should be some form of QuestTaskType
 * @author nikok
 */
public enum QuestTaskType {
    TRIGGERVALUE,   //Integer value
    CREATUREKILL,   //Deaths of creatures (generally in Locations)
    ITEMHAVE,       //(Checked on gain/lose of an item)
    ITEMUSE;        //Checked on use (possibly comsumption) of an item
}
