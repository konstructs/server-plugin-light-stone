package org.konstructs.lightstone;

import konstructs.api.Block;
import konstructs.api.Direction;
import konstructs.api.Position;

public class LightEvent {
    private final Position position;
    private final Direction direction;
    private final boolean on;
    private final Block block;

    public LightEvent(Position position, Direction direction, boolean on, Block block) {
        this.position = position;
        this.direction = direction;
        this.on = on;
        this.block = block;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isOn() {
        return on;
    }


    public Block getBlock() {
        return block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LightEvent that = (LightEvent) o;

        if (on != that.on) return false;
        if (!position.equals(that.position)) return false;
        if (!direction.equals(that.direction)) return false;
        return block.equals(that.block);

    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        result = 31 * result + direction.hashCode();
        result = 31 * result + (on ? 1 : 0);
        result = 31 * result + block.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LightEvent(" +
                "position=" + position +
                ", direction=" + direction +
                ", on=" + on +
                ", block=" + block +
                ')';
    }
}
