package org.konstructs.lightstone;

import akka.actor.ActorRef;
import akka.actor.Props;

import konstructs.plugin.KonstructsActor;
import konstructs.plugin.PluginConstructor;
import konstructs.plugin.Config;

import konstructs.api.*;
import konstructs.api.messages.*;

public class LightStonePlugin extends KonstructsActor {
    public static final int LIGHT_DISTANCE = 32;

    private BlockFactory factory = null;
    private int propagationDelay;

    public LightStonePlugin(String name, ActorRef universe, int propagationDelay) {
        super(universe);
        getUniverse().tell(GetBlockFactory.MESSAGE, getSelf());
    }

    @Override
    public void onReceive(Object message) {
        /* Handle initialization of block factory before all other messages */
        if(factory == null) {
            if(message instanceof BlockFactory) {
                factory = (BlockFactory)message;
                unstashAll();
            } else {
                stash();
            }
            return;
        }

        if(message instanceof On) {
            getContext().actorOf(EmitActor.props(getUniverse(), (On) message, factory, propagationDelay));
        } else if(message instanceof Off) {
            getContext().actorOf(EmitActor.props(getUniverse(), (Off) message, factory, propagationDelay));
        } else {
            super.onReceive(message); // Handle konstructs messages
        }
    }

    @PluginConstructor
    public static Props props(String pluginName, ActorRef universe,
                              @Config(key = "propagation-delay") int propagationDelay) {
        Class currentClass = new Object() { }.getClass().getEnclosingClass();
        return Props.create(currentClass, pluginName, universe, propagationDelay);
    }
}
