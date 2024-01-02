package com.skoow.relics_vivid_light.common.utility;

import net.minecraft.world.phys.Vec3;

public class MathUtils {
    public Vec3 normalizeVector(Vec3 vec) {
        double X = vec.x;
        double Y = vec.y;
        double Z = vec.z;
        double distance = Math.sqrt(X*X+Y*Y+Z*Z);
        return new Vec3(X/distance,Y/distance,Z/distance);
    }
}
