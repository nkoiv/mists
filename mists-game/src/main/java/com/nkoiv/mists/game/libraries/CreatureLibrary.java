/*
 * This software (code) is free to use as it is, as long as it's not used for commercial purposes
 * and as long as you credit the author accordingly. For commercial purposes please contact the author.
 * The software is provided "as is" with absolutely no warranty of any kind.
 * Using this software is entirely up to you, and the author is in no way responsible for anything you do with it.
 * (c) nkoiv / Niko Koivumäki
 */
package com.nkoiv.mists.game.libraries;

import java.util.List;
import java.util.Map;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.AI.MonsterAI;
import com.nkoiv.mists.game.actions.MeleeAttack;
import com.nkoiv.mists.game.actions.ProjectileWeaponAttack;
import com.nkoiv.mists.game.gameobject.Creature;
import com.nkoiv.mists.game.gameobject.MapObject;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * CreatureLibrary extends the generic MapObject library (MobLibrary)
 * by providing services related directly to Creatures in particular.
 * This includes but isn't limited to YAML-parsing and object verification.
 * @author daedra
 */
public class CreatureLibrary<E extends Creature> extends MobLibrary<Creature> {
    
	public CreatureLibrary() {
		super();
	}

    public static Creature generateCreatureFromYAML(Map creatureData) {
        Creature creep;
        String mobname = (String)creatureData.get("name");
        String spriteType = (String)creatureData.get("spriteType");
        if (("static").equals(spriteType)) {
            Image monsterImage = new Image((String)creatureData.get("image"));
            creep = new Creature(mobname, monsterImage);
        } else {
            ImageView monsterSprites = new ImageView((String)creatureData.get("spritesheet"));
            Mists.logger.info("Spritesheet loaded: "+monsterSprites.getImage().getWidth()+"x"+monsterSprites.getImage().getHeight());
            List<String> p = (List<String>)creatureData.get("spritesheetParameters");
            Mists.logger.info("Parameters loaded: "+p.toString());
            creep = new Creature(mobname, monsterSprites,
                    Integer.parseInt(p.get(0)),
                    Integer.parseInt(p.get(1)),
                    Integer.parseInt(p.get(2)),
                    Integer.parseInt(p.get(3)),
                    Integer.parseInt(p.get(4)),
                    Integer.parseInt(p.get(5)),
                    Integer.parseInt(p.get(6)));
        }
        Mists.logger.info("Creature base generated, adding attributes and flags");
        
        addAttributesFromYAML(creatureData, creep);
        addFlagsFromYAML(creatureData, creep);
        
        if (creatureData.containsKey("aiType")) {
            String aiType = (String)creatureData.get("aiType");
            switch (aiType){
                case "monster": creep.setAI(new MonsterAI(creep));
                    break;
                default: break;
            }
        }
        
        //TODO: Add actions to YAML
        creep.addAction(new MeleeAttack());
        if ("Rabbit".equals(creep.getName())) {
            creep.addAction(new ProjectileWeaponAttack());
        }
        return creep;
    }
    
}
