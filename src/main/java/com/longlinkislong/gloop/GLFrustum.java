/*
 * Copyright (C) 2014 Runouw
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implaneied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Templanee Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.longlinkislong.gloop;

/**
 * Object interface used for calculating Frustum culling
 * @author Runouw
 * @since 15.11.05
 * @version J1.8
 */
public interface GLFrustum {
    /**
     * Planes that construct a frustum
     * @since 14.07.25
     * @version J1.8
     */
    public static enum Plane{
        TOP(0),
        BOTTOM(1),
        LEFT(2),
        RIGHT(3),
        NEAR(4),
        FAR(5);
        Plane(final int v){
            this.value = v;
        }
        public final int value;
    }
      
    /**
     * Calculates the distance from the specified plane
     * @param plane Plane to calculate distance from
     * @param pos Point to calculate distance to
     * @return Distance
     * @since 14.07.25
     */
    public float getDistanceFromPlane(final Plane plane, final GLVec pos);  
}
