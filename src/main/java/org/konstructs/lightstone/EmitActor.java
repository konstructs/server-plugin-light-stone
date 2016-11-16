package org.konstructs.lightstone;

import akka.actor.ActorRef;
import akka.actor.Props;


import akka.actor.SystemGuardian;
import konstructs.plugin.KonstructsActor;
import konstructs.api.*;
import konstructs.api.messages.*;

public class EmitActor extends KonstructsActor {
    public static final EventTypeId RECEIVER_EVENT_ID =
        EventTypeId.fromString("org/konstructs/light-stone/receiver");
    public static final BlockClassId LIGHT_STONE_CLASS =
        BlockClassId.fromString("org/konstructs/class/light-stone/LightStone");
    private final Position position;
    private final Direction direction;
    private final boolean on;
    private final BlockFactory factory;
    private final int propagationDelay;

    public EmitActor(ActorRef universe, Position position, Direction direction, boolean on, BlockFactory factory,
                     int propagationDelay) {
        super(universe);
        this.position = position;
        this.direction = direction;
        this.on = on;
        this.factory = factory;
        this.propagationDelay = propagationDelay;
        Position excludingEmitter = position.add(direction.getVector());

        /* Query for all blocks in the direction emitted in */
        boxShapeQuery(new DirectionalLine(excludingEmitter,
                                          direction,
                                          LightStonePlugin.LIGHT_DISTANCE));
    }

    @Override
    public void onViewBlockResult(ViewBlockResult block) {
        /* Send a light event for the block found */
        LightEvent event = new LightEvent(
                block.getPosition(),
                direction.inverse(),
                on,
                block.getBlock());

        scheduleOnce(new SendEvent(RECEIVER_EVENT_ID, event), propagationDelay, getUniverse());

        /* Our work here is done, terminate actor. */
        context().stop(getSelf());
    }

    @Override
    public void onBoxShapeQueryResult(BoxShapeQueryResult result) {
        /* Find the first block in the line queried for that is not transparent */
        DirectionalLine line = (DirectionalLine)result.getBox();
        for(int i = 0; i < line.getLength(); i++) {
            BlockTypeId block = line.getLocal(i, result.getBlocks());
            BlockType type = factory.getBlockType(block);
            if(type.hasClass(LIGHT_STONE_CLASS)) {
                Position hit = line.getBox().arrayIndexAsPosition(line.arrayIndexLocal(i));
                /* Query to get the block that was hit */
                viewBlock(hit);
                return;
            }
        }
        /* Terminate actor if no non-transparent block was found */
        context().stop(getSelf());
    }

    public static Props
        props(ActorRef universe, On on, BlockFactory factory, int propagationDelay) {
        Class currentClass = new Object() { }.getClass().getEnclosingClass();
        return Props.create(currentClass, universe, on.getPosition(), on.getDirection(), true, factory,
                            propagationDelay);
    }

    public static Props
        props(ActorRef universe, Off off, BlockFactory factory, int propagationDelay) {
        Class currentClass = new Object() { }.getClass().getEnclosingClass();
        return Props.create(currentClass, universe, off.getPosition(), off.getDirection(), false, factory,
                            propagationDelay);
    }

}
