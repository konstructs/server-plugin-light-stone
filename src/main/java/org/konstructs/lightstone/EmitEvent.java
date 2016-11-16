package org.konstructs.lightstone;

import konstructs.api.Block;
import konstructs.api.Direction;
import konstructs.api.Position;

public class EmitEvent {
    private final Position position;
    private final Direction direction;
    private final Block block;

    public EmitEvent(Position position, Direction direction, Block block) {
        this.position = position;
        this.direction = direction;
        this.block = block;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmitEvent emitEvent = (EmitEvent) o;

        if (!position.equals(emitEvent.position)) return false;
        if (!direction.equals(emitEvent.direction)) return false;
        return block.equals(emitEvent.block);

    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        result = 31 * result + direction.hashCode();
        result = 31 * result + block.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "EmitEvent(" +
                "position=" + position +
                ", direction=" + direction +
                ", block=" + block +
                ')';
    }
}
