/*
 * Copyright (c) 2014 Robert Hewitt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Hewitt - initial API and implementation and/or initial documentation
 *    Zachary Michaels - update and maintain API
 */
package com.longlinkislong.gloop;

import java.util.Objects;

/**
 * Object that represents a plane. Defined from a position vector and a normal
 * vector.
 *
 * @author Runouw
 * @since 14.07.08
 * @version J1.8
 */
public class GLPlane {

    private final GLVec3F normal = new StaticVec3F(Vectors.DEFAULT_FACTORY);
    private final GLVec3F point = new StaticVec3F(Vectors.DEFAULT_FACTORY);

    /*
     * Cached value (for plane equation: ax + by + cz + d = 0)
     * d = -dot(normal, point);
     */
    private float d;

    /**
     * Constructs a new GLPlane. Default value is a plane located at origin with
     * normal [0, 1, 0]
     *
     * @since 14.07.08
     */
    public GLPlane() {
        this.normal.set(0f, 1f, 0f);
    }

    /**
     * Constructs a GLPlane from three points
     *
     * @param a First point
     * @param b Second point
     * @param c Third point
     * @throws NullPointerException if a is null, b is null, c is null.
     * @since 14.07.08
     */
    public GLPlane(final GLVec a, final GLVec b, final GLVec c) throws NullPointerException {
        if (a == null || b == null || c == null) {
            throw new NullPointerException();
        }
        this.setFromPoints(a, b, c);
    }

    /**
     * Constructs a GLPlane from a normal vector and a position vector
     *
     * @param normal Normal vector
     * @param point Position vector
     * @throws NullPointerException if normal is null or if point is null.
     * @since 14.07.08
     */
    public GLPlane(final GLVec normal, final GLVec point) throws NullPointerException {
        if (normal == null || point == null) {
            throw new NullPointerException();
        }
        this.setNormalAndPoint(normal, point);
    }

    /**
     * Copy constructor for a GLPlane. This will create a new plane with the
     * same data.
     *
     * @param plane Plane to copy data from.
     * @throws NullPointerException if plane is null.
     * @since 14.07.08
     */
    public GLPlane(final GLPlane plane) throws NullPointerException {
        if (plane == null) {
            throw new NullPointerException();
        }
        this.normal.set(plane.normal);
        this.point.set(plane.point);
        this.d = plane.d;
    }

    /**
     * Calculates a plane from 3 points
     *
     * @param a First point
     * @param b Second point
     * @param c third point
     * @throws NullPointerException if a is null, b is null, or c is null.
     * @since 14.07.08
     */
    public final void setFromPoints(final GLVec a, final GLVec b, final GLVec c) throws NullPointerException {
        if (a == null || b == null || c == null) {
            throw new NullPointerException();
        }
        GLVec ab = a.minus(b);
        GLVec cb = c.minus(b);
        this.normal.set(ab.cross(cb).normalize());
        point.set(b);

        d = -normal.dot(point);
    }

    /**
     * Sets a plane by defining the normal vector and point vector.
     *
     * @param normal Direction that is orthogonal to the plane (up).
     * @param point Position of the plane
     * @throws NullPointerException if normal is null or point is null.
     * @since 14.07.08
     */
    public final void setNormalAndPoint(final GLVec normal, final GLVec point) throws NullPointerException {
        if (normal == null || point == null) {
            throw new NullPointerException();
        }
        this.normal.set(normal);
        this.point.set(point);

        d = -this.normal.dot(this.point);
    }

    /**
     * Sets a plane from coefficients
     *
     * @param a First coefficient
     * @param b Second coefficient
     * @param c Third coefficient
     * @param d Fourth coefficient
     * @throws ArithmeticException if a is NaN, b is NaN, c is NaN, or d is NaN.
     * @since 14.07.08
     */
    public final void setFromCoefficients(final float a, final float b, final float c, final float d) throws ArithmeticException {
        if (Float.isNaN(a) || Float.isNaN(b) || Float.isNaN(c) || Float.isNaN(d)) {
            throw new ArithmeticException();
        }
        normal.set(a, b, c);
        float l = (float) normal.length();
        normal.set(normal.normalize());

        this.d = d / l;
    }

    /**
     * Calculates the distance a point is from the plane
     *
     * @param p Point
     * @return Distance
     * @throws NullPointerException if p is null.
     * @since 14.07.08
     */
    public final float distance(final GLVec p) throws NullPointerException {
        Objects.requireNonNull(p);                
        
        return d + p.asGLVecF().dot(normal);
    }
    
    @Override
    public boolean equals(final Object other) {
        if(this == other) {
            return true;
        } else if(other instanceof GLPlane) {
            final GLPlane oPlane = (GLPlane) other;
            
            return GLTools.compare(oPlane.d, this.d, GLTools.MEDIUMP) 
                    && this.normal.equals(oPlane.normal) 
                    && this.point.equals(oPlane.point);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.normal);
        hash = 67 * hash + Objects.hashCode(this.point);
        hash = 67 * hash + Float.floatToIntBits(this.d);
        return hash;
    }
}
