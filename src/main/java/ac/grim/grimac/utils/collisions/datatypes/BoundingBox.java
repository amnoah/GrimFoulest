package ac.grim.grimac.utils.collisions.datatypes;

import org.bukkit.util.Vector;

public class BoundingBox {

    public final float minX;
    public final float minY;
    public final float minZ;
    public final float maxX;
    public final float maxY;
    public final float maxZ;

    public BoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public BoundingBox(Vector min, Vector max) {
        minX = (float) Math.min(min.getX(), max.getX());
        minY = (float) Math.min(min.getY(), max.getY());
        minZ = (float) Math.min(min.getZ(), max.getZ());
        maxX = (float) Math.max(min.getX(), max.getX());
        maxY = (float) Math.max(min.getY(), max.getY());
        maxZ = (float) Math.max(min.getZ(), max.getZ());
    }

    public BoundingBox(BoundingBox one, BoundingBox two) {
        minX = Math.min(one.minX, two.minX);
        minY = Math.min(one.minY, two.minY);
        minZ = Math.min(one.minZ, two.minZ);
        maxX = Math.max(one.maxX, two.maxX);
        maxY = Math.max(one.maxY, two.maxY);
        maxZ = Math.max(one.maxZ, two.maxZ);
    }

    public BoundingBox add(float x, float y, float z) {
        float newMinX = minX + x;
        float newMaxX = maxX + x;
        float newMinY = minY + y;
        float newMaxY = maxY + y;
        float newMinZ = minZ + z;
        float newMaxZ = maxZ + z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox add(Vector vector) {
        float x = (float) vector.getX(), y = (float) vector.getY(), z = (float) vector.getZ();

        float newMinX = minX + x;
        float newMaxX = maxX + x;
        float newMinY = minY + y;
        float newMaxY = maxY + y;
        float newMinZ = minZ + z;
        float newMaxZ = maxZ + z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox grow(float x, float y, float z) {
        float newMinX = minX - x;
        float newMaxX = maxX + x;
        float newMinY = minY - y;
        float newMaxY = maxY + y;
        float newMinZ = minZ - z;
        float newMaxZ = maxZ + z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox shrink(float x, float y, float z) {
        float newMinX = minX + x;
        float newMaxX = maxX - x;
        float newMinY = minY + y;
        float newMaxY = maxY - y;
        float newMinZ = minZ + z;
        float newMaxZ = maxZ - z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox add(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new BoundingBox(this.minX + minX, this.minY + minY, this.minZ + minZ, this.maxX + maxX, this.maxY + maxY, this.maxZ + maxZ);
    }

    public BoundingBox subtract(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new BoundingBox(this.minX - minX, this.minY - minY, this.minZ - minZ, this.maxX - maxX, this.maxY - maxY, this.maxZ - maxZ);
    }

    public boolean intersectsWithBox(Vector vector) {
        return (vector.getX() > minX && vector.getX() < maxX) && ((vector.getY() > minY && vector.getY() < maxY) && (vector.getZ() > minZ && vector.getZ() < maxZ));
    }

    public Vector getMinimum() {
        return new Vector(minX, minY, minZ);
    }

    public Vector getMaximum() {
        return new Vector(maxX, maxY, maxZ);
    }

    public boolean collides(Vector vector) {
        return (vector.getX() >= minX && vector.getX() <= maxX) && ((vector.getY() >= minY && vector.getY() <= maxY) && (vector.getZ() >= minZ && vector.getZ() <= maxZ));
    }

    public boolean collidesHorizontally(Vector vector) {
        return (vector.getX() >= minX && vector.getX() <= maxX) && ((vector.getY() > minY && vector.getY() < maxY) && (vector.getZ() >= minZ && vector.getZ() <= maxZ));
    }

    public boolean collidesVertically(Vector vector) {
        return (vector.getX() > minX && vector.getX() < maxX) && ((vector.getY() >= minY && vector.getY() <= maxY) && (vector.getZ() > minZ && vector.getZ() < maxZ));
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and Z dimensions, calculate the offset between them
     * in the X dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateXOffset(BoundingBox other, double offsetX) {
        if (other.maxY > minY && other.minY < maxY && other.maxZ > minZ && other.minZ < maxZ) {
            if (offsetX > 0.0D && other.maxX <= minX) {
                double d1 = minX - other.maxX;

                if (d1 < offsetX) {
                    offsetX = d1;
                }
            } else if (offsetX < 0.0D && other.minX >= maxX) {
                double d0 = maxX - other.minX;

                if (d0 > offsetX) {
                    offsetX = d0;
                }
            }

        }
        return offsetX;
    }

    /**
     * if instance and the argument bounding boxes overlap in the X and Z dimensions, calculate the offset between them
     * in the Y dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateYOffset(BoundingBox other, double offsetY) {
        if (other.maxX > minX && other.minX < maxX && other.maxZ > minZ && other.minZ < maxZ) {
            if (offsetY > 0.0D && other.maxY <= minY) {
                double d1 = minY - other.maxY;

                if (d1 < offsetY) {
                    offsetY = d1;
                }
            } else if (offsetY < 0.0D && other.minY >= maxY) {
                double d0 = maxY - other.minY;

                if (d0 > offsetY) {
                    offsetY = d0;
                }
            }

        }
        return offsetY;
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and X dimensions, calculate the offset between them
     * in the Z dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateZOffset(BoundingBox other, double offsetZ) {
        if (other.maxX > minX && other.minX < maxX && other.maxY > minY && other.minY < maxY) {
            if (offsetZ > 0.0D && other.maxZ <= minZ) {
                double d1 = minZ - other.maxZ;

                if (d1 < offsetZ) {
                    offsetZ = d1;
                }
            } else if (offsetZ < 0.0D && other.minZ >= maxZ) {
                double d0 = maxZ - other.minZ;

                if (d0 > offsetZ) {
                    offsetZ = d0;
                }
            }

        }
        return offsetZ;
    }

    public BoundingBox addCoord(float x, float y, float z) {
        float d0 = minX;
        float d1 = minY;
        float d2 = minZ;
        float d3 = maxX;
        float d4 = maxY;
        float d5 = maxZ;

        if (x < 0.0D) {
            d0 += x;
        } else if (x > 0.0D) {
            d3 += x;
        }

        if (y < 0.0D) {
            d1 += y;
        } else if (y > 0.0D) {
            d4 += y;
        }

        if (z < 0.0D) {
            d2 += z;
        } else if (z > 0.0D) {
            d5 += z;
        }

        return new BoundingBox(d0, d1, d2, d3, d4, d5);
    }

    public SimpleCollisionBox toCollisionBox() {
        return new SimpleCollisionBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public String toString() {
        return "[" + minX + ", " + minY + ", " + minZ + ", " + maxX + ", " + maxY + ", " + maxZ + "]";
    }
}