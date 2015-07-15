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
 * A simple implementation of GLFrustum that can turn a GLMat into culling
 * planes.
 * @author Robert
 */
public class GLPlanes implements GLFrustum{
    private static final int E11 = 0;
    private static final int E12 = 1;
    private static final int E13 = 2;
    private static final int E14 = 3;
    private static final int E21 = 4;
    private static final int E22 = 5;
    private static final int E23 = 6;
    private static final int E24 = 7;
    private static final int E31 = 8;
    private static final int E32 = 9;
    private static final int E33 = 10;
    private static final int E34 = 11;
    private static final int E41 = 12;
    private static final int E42 = 13;
    private static final int E43 = 14;
    private static final int E44 = 15;
    
    private final GLPlane[] planes;
    
    private final GLMat4F proj = StaticMat4F.create();

    public GLPlanes() {
        this.planes = new GLPlane[6];
        for (int i = 0; i < 6; i++) {
            this.planes[i] = new GLPlane();
        }
    }
    public GLPlanes(final GLMat frustum) {
        this.planes = new GLPlane[6];
        for (int i = 0; i < 6; i++) {
            this.planes[i] = new GLPlane();
        }
        
        setPlanes(frustum);
    }
    
    private void setPlanes(final GLMat frustum) {
        this.proj.set(frustum);
        
        final GLMat4F in0 = frustum.asGLMatF().asGLMat4F();
        final GLMat4F temp = in0.transpose(); // TODO: use the un-transposed data instead.

        //<editor-fold defaultstate="collapsed" desc="temp">
        final float[] data = temp.data();
        final int offset = temp.offset();

        final int e41 = E41 + offset;
        final int e42 = E42 + offset;
        final int e43 = E43 + offset;
        final int e44 = E44 + offset;
        final int e31 = E31 + offset;
        final int e32 = E32 + offset;
        final int e33 = E33 + offset;
        final int e34 = E34 + offset;
        final int e21 = E21 + offset;
        final int e22 = E22 + offset;
        final int e23 = E23 + offset;
        final int e24 = E24 + offset;
        final int e11 = E11 + offset;
        final int e12 = E12 + offset;
        final int e13 = E13 + offset;
        final int e14 = E14 + offset;
        //</editor-fold>

        planes[GLFrustum.Plane.NEAR.value].setFromCoefficients(
                data[e41] + data[e31],
                data[e42] + data[e32],
                data[e43] + data[e33],
                data[e44] + data[e34]);
        planes[GLFrustum.Plane.FAR.value].setFromCoefficients(
                data[e41] - data[e31],
                data[e42] - data[e32],
                data[e43] - data[e33],
                data[e44] - data[e34]);
        planes[GLFrustum.Plane.BOTTOM.value].setFromCoefficients(
                data[e41] + data[e21],
                data[e42] + data[e22],
                data[e43] + data[e23],
                data[e44] + data[e24]);
        planes[GLFrustum.Plane.TOP.value].setFromCoefficients(
                data[e41] - data[e21],
                data[e42] - data[e22],
                data[e43] - data[e23],
                data[e44] - data[e24]);
        planes[GLFrustum.Plane.LEFT.value].setFromCoefficients(
                data[e41] + data[e11],
                data[e42] + data[e12],
                data[e43] + data[e13],
                data[e44] + data[e14]);
        planes[GLFrustum.Plane.RIGHT.value].setFromCoefficients(
                data[e41] - data[e11],
                data[e42] - data[e12],
                data[e43] - data[e13],
                data[e44] - data[e14]);
    }

    @Override
    public float getDistanceFromPlane(Plane plane, GLVec pos) {
        return this.planes[plane.value].distance(pos);
    }

    @Override
    public GLMat getViewProjectionMatrix(GLMat pMat) {
        return proj.copyTo(Matrices.DEFAULT_FACTORY);
    }
}
