/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.gameobject;

/**
 * Templatable MabObjects can be used as
 * templates to create more of the same kind
 * This is in some way the opposite of serializing,
 * as templates are meant to discard unnecessary
 * data and states
 * @author nikok
 */
public interface Templatable {
    
    Object createFromTemplate();
    
}
