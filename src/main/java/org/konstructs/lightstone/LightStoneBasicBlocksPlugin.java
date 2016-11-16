package org.konstructs.lightstone;

import akka.actor.ActorRef;
import akka.actor.Props;

import com.typesafe.config.Config;

import konstructs.plugin.KonstructsActor;
import konstructs.plugin.PluginConstructor;

import konstructs.api.*;
import konstructs.api.messages.*;

import java.util.Map;

public class LightStoneBasicBlocksPlugin extends KonstructsActor {

    public static final BlockTypeId EMITTER = BlockTypeId.fromString("org/konstructs/light-stone/emitter");
    public static final BlockTypeId EMITTER_OFF = BlockTypeId.fromString("org/konstructs/light-stone/emitter-off");
    public static final Direction EMITTER_DIRECTION = Direction.FORWARD;
    public static final BlockTypeId MIRROR = BlockTypeId.fromString("org/konstructs/light-stone/mirror");
    public static final BlockFilter MIRROR_FILTER = BlockFilterFactory.withBlockTypeId(MIRROR);
    public static final BlockTypeId MIRROR_PASS = BlockTypeId.fromString("org/konstructs/light-stone/mirror-pass");
    public static final BlockFilter MIRROR_PASS_FILTER = BlockFilterFactory.withBlockTypeId(MIRROR_PASS);
    public static final BlockTypeId MIRROR_OFF = BlockTypeId.fromString("org/konstructs/light-stone/mirror-off");
    public static final BlockFilter MIRROR_OFF_FILTER = BlockFilterFactory.withBlockTypeId(MIRROR_OFF);
    public static final BlockTypeId MIRROR_PASS_OFF = BlockTypeId.fromString("org/konstructs/light-stone/mirror-pass-off");
    public static final BlockFilter MIRROR_PASS_OFF_FILTER = BlockFilterFactory.withBlockTypeId(MIRROR_PASS_OFF);
    public static final Direction MIRROR_DIRECTION = Direction.RIGHT;
    public static final Direction MIRROR_PASS_DIRECTION = Direction.BACKWARD;
    public static final Direction MIRROR_RECEIVER_DIRECTION = Direction.FORWARD;
    public static final Direction MIRROR_CONTROL_DIRECTION = Direction.LEFT;

    public static final BlockTypeId DETECTOR_OFF = BlockTypeId.fromString("org/konstructs/light-stone/detector-off");
    public static final BlockTypeId DETECTOR_ON = BlockTypeId.fromString("org/konstructs/light-stone/detector-on");
    public static final BlockFilter DETECTOR_OFF_FILTER = BlockFilterFactory.withBlockTypeId(DETECTOR_OFF);
    public static final BlockFilter DETECTOR_ON_FILTER = BlockFilterFactory.withBlockTypeId(DETECTOR_ON);
    public static final Direction DETECTOR_DIRECTION = Direction.FORWARD;

    public static final BlockTypeId LAMP_OFF = BlockTypeId.fromString("org/konstructs/light-stone/lamp-off");
    public static final BlockTypeId LAMP_ON = BlockTypeId.fromString("org/konstructs/light-stone/lamp-on");
    public static final BlockFilter LAMP_OFF_FILTER = BlockFilterFactory.withBlockTypeId(LAMP_OFF);
    public static final BlockFilter LAMP_ON_FILTER = BlockFilterFactory.withBlockTypeId(LAMP_ON);

    public static final BlockTypeId SPLITTER_OFF = BlockTypeId.fromString("org/konstructs/light-stone/splitter-off");
    public static final BlockTypeId SPLITTER_ON = BlockTypeId.fromString("org/konstructs/light-stone/splitter-on");
    public static final BlockFilter SPLITTER_OFF_FILTER = BlockFilterFactory.withBlockTypeId(SPLITTER_OFF);
    public static final BlockFilter SPLITTER_ON_FILTER = BlockFilterFactory.withBlockTypeId(SPLITTER_ON);
    public static final Direction SPLITTER_RECEIVER_DIRECTION = Direction.FORWARD;
    public static final Direction SPLITTER_EMIT1_DIRECTION = Direction.BACKWARD;
    public static final Direction SPLITTER_EMIT2_DIRECTION = Direction.RIGHT;

    private final ActorRef lightStonePlugin;


    public LightStoneBasicBlocksPlugin(String name, ActorRef universe, ActorRef lightStonePlugin) {
        super(universe);
        this.lightStonePlugin = lightStonePlugin;
    }

    @Override
    public void onBlockUpdateEvent(BlockUpdateEvent update) {
        for(Map.Entry<Position, BlockUpdate> p: update.getUpdatedBlocks().entrySet()) {
            /* Handle turning off if block is removed */
            Block before = p.getValue().getBefore();
            BlockTypeId beforeType = before.getType();
            Orientation beforeOrientation = before.getOrientation();
            if(beforeType.equals(EMITTER)) {
                Direction direction = beforeOrientation.translateFacePointingIn(EMITTER_DIRECTION);
                lightStonePlugin.tell(new Off(p.getKey(), direction), getSelf());
            } else if(beforeType.equals(MIRROR)) {
                Direction direction = beforeOrientation.translateFacePointingIn(MIRROR_DIRECTION);
                lightStonePlugin.tell(new Off(p.getKey(), direction), getSelf());
            } else if(beforeType.equals(MIRROR_PASS)) {
                Direction direction = beforeOrientation.translateFacePointingIn(MIRROR_PASS_DIRECTION);
                lightStonePlugin.tell(new Off(p.getKey(), direction), getSelf());
            } else if(beforeType.equals(SPLITTER_ON)) {
                Direction dir1 = beforeOrientation.translateFacePointingIn(SPLITTER_EMIT1_DIRECTION);
                Direction dir2 = beforeOrientation.translateFacePointingIn(SPLITTER_EMIT2_DIRECTION);
                lightStonePlugin.tell(new Off(p.getKey(), dir1), getSelf());
                lightStonePlugin.tell(new Off(p.getKey(), dir2), getSelf());
            } else {
                /* Not one of our blocks */
            }

            /* Handle turning on if block is added */
            Block after = p.getValue().getAfter();
            BlockTypeId afterType = after.getType();
            Orientation afterOrientation = after.getOrientation();
            if(afterType.equals(EMITTER)) {
                Direction direction = afterOrientation.translateFacePointingIn(EMITTER_DIRECTION);
                lightStonePlugin.tell(new On(p.getKey(), direction), getSelf());
            } else if(afterType.equals(MIRROR)) {
                Direction direction = afterOrientation.translateFacePointingIn(MIRROR_DIRECTION);
                lightStonePlugin.tell(new On(p.getKey(), direction), getSelf());
            } else if(afterType.equals(MIRROR_PASS)) {
                Direction direction = afterOrientation.translateFacePointingIn(MIRROR_PASS_DIRECTION);
                lightStonePlugin.tell(new On(p.getKey(), direction), getSelf());
            } else if(afterType.equals(SPLITTER_ON)) {
                Direction dir1 = beforeOrientation.translateFacePointingIn(SPLITTER_EMIT1_DIRECTION);
                Direction dir2 = beforeOrientation.translateFacePointingIn(SPLITTER_EMIT2_DIRECTION);
                lightStonePlugin.tell(new On(p.getKey(), dir1), getSelf());
                lightStonePlugin.tell(new On(p.getKey(), dir2), getSelf());
            } else {
                /* Not one of our blocks */
            }
        }
    }


    @Override
    public void onReceive(Object message) {
        /* Handle light events */
        if(message instanceof LightEvent) {
            LightEvent event = (LightEvent)message;
            Block block = event.getBlock();

            /* Get the face of the rotated block was pointed at */
            Direction face =
                block.getOrientation().translateFacePointedAt(event.getDirection());

            if(event.isOn()) { // Handle events where the light is on
                if(block.isType(DETECTOR_OFF) && face.equals(DETECTOR_DIRECTION)) {
                    replaceBlock(DETECTOR_OFF_FILTER, event.getPosition(),
                                 block.withType(DETECTOR_ON));
                } if(block.isType(SPLITTER_OFF) && face.equals(SPLITTER_RECEIVER_DIRECTION)) {
                    replaceBlock(SPLITTER_OFF_FILTER, event.getPosition(),
                                 block.withType(SPLITTER_ON));
                } else if(block.isType(LAMP_OFF)) {
                    replaceBlock(LAMP_OFF_FILTER, event.getPosition(),
                                 block.withType(LAMP_ON));
                }


                if(face.equals(MIRROR_RECEIVER_DIRECTION)) {
                    /* If the mirror is off, since receiver was illuminated turn it on */
                    if(block.isType(MIRROR_OFF)) {
                        replaceBlock(MIRROR_OFF_FILTER, event.getPosition(),
                                     block.withType(MIRROR));
                    } else if(block.isType(MIRROR_PASS_OFF)) {
                        replaceBlock(MIRROR_PASS_OFF_FILTER, event.getPosition(),
                                     block.withType(MIRROR_PASS));

                    }
                } else if(face.equals(MIRROR_CONTROL_DIRECTION)) {
                    /* If the mirror is not in pass through mode (reflect),
                     * since the controller was illuminated, turn it into
                     * pass.
                     */
                    if(block.isType(MIRROR_OFF)) {
                        replaceBlock(MIRROR_OFF_FILTER, event.getPosition(),
                                     block.withType(MIRROR_PASS_OFF));
                    } else if(block.isType(MIRROR)) {
                        replaceBlock(MIRROR_FILTER, event.getPosition(),
                                     block.withType(MIRROR_PASS));
                    }
                }
            } else { // Handle events where the light is off
                if(block.isType(DETECTOR_ON) && face.equals(DETECTOR_DIRECTION)) {
                    replaceBlock(DETECTOR_ON_FILTER, event.getPosition(),
                                 block.withType(DETECTOR_OFF));
                } else if(block.isType(SPLITTER_ON) && face.equals(SPLITTER_RECEIVER_DIRECTION)) {
                    replaceBlock(SPLITTER_ON_FILTER, event.getPosition(),
                                 block.withType(SPLITTER_OFF));
                } else if(block.isType(LAMP_ON)) {
                    replaceBlock(LAMP_ON_FILTER, event.getPosition(),
                                 block.withType(LAMP_OFF));
                }
                if(face.equals(MIRROR_RECEIVER_DIRECTION)) {
                    /* If the mirror is on, since receiver is not illuminated
                     * turn it off
                     */
                    if(block.isType(MIRROR)) {
                        replaceBlock(MIRROR_FILTER, event.getPosition(),
                                     block.withType(MIRROR_OFF));
                    } else if(block.isType(MIRROR_PASS)) {
                        replaceBlock(MIRROR_PASS_FILTER, event.getPosition(),
                                     block.withType(MIRROR_PASS_OFF));
                    }
                } else if(face.equals(MIRROR_CONTROL_DIRECTION)) {
                    /* If the mirror is in pass through mode,
                     * since the controller was not illuminated, turn it into
                     * reflect mode.
                     */
                    if(block.isType(MIRROR_PASS_OFF)) {
                        replaceBlock(MIRROR_PASS_OFF_FILTER, event.getPosition(),
                                     block.withType(MIRROR_OFF));
                    } else if(block.isType(MIRROR_PASS)) {
                        replaceBlock(MIRROR_PASS_FILTER, event.getPosition(),
                                     block.withType(MIRROR));
                    }
                }
            }
        } else if(message instanceof InteractTertiaryFilter) {
            /* Handle tertiary clicking on emitters to turn them
             * on and off.
             */
            InteractTertiaryFilter filter = (InteractTertiaryFilter)message;
            InteractTertiary interaction = filter.getMessage();
            Block block = interaction.getBlockAtPosition();
            if(interaction.isWorldPhase() && block != null) {
                if(block.getType().equals(EMITTER)) {
                    // If EMITTER is on, this turns it off
                    filter.skipWith(getSelf(), interaction.withBlockAtPosition(block.withType(EMITTER_OFF)));
                } else if(block.getType().equals(EMITTER_OFF)) {
                    // If EMITTER is off, this turns it on
                    filter.skipWith(getSelf(), interaction.withBlockAtPosition(block.withType(EMITTER)));
                } else {
                    /* Not a light stone block*/
                    filter.next(getSelf());
                }
            } else {
                filter.next(getSelf());
            }
        } else {
            super.onReceive(message); // Handle konstructs messages
        }
    }

    @PluginConstructor
    public static Props
        props(
              String pluginName,
              ActorRef universe,
              @konstructs.plugin.Config(key = "light-stone-plugin") ActorRef lightStonePlugin
              ) {
        Class currentClass = new Object() { }.getClass().getEnclosingClass();
        return Props.create(currentClass, pluginName, universe, lightStonePlugin);
    }

}
