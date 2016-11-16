package org.konstructs.lightstone;

import konstructs.api.Direction;
import konstructs.api.Position;

public class On {
    private final Position position;
    private final Direction direction;

    public On(Position position, Direction direction) {
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

        On on = (On) o;

        if (!position.equals(on.position)) return false;
        return direction == on.direction;

    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        result = 31 * result + direction.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "On(" +
                "position=" + position +
                ", direction=" + direction +
                ')';
    }
}
