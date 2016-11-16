package org.konstructs.lightstone;

import konstructs.api.Direction;
import konstructs.api.Position;

public class Off {
    private final Position position;
    private final Direction direction;

    public Off(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Off off = (Off) o;

        if (!position.equals(off.position)) return false;
        return direction == off.direction;

    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        result = 31 * result + direction.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Off(" +
                "position=" + position +
                ", direction=" + direction +
                ')';
    }
}
