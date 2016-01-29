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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Object that represents a plane. Defined from a position vector and a normal
 * vector.
 *
 * @author Runouw
 * @since 14.07.08
 * @version J1.8
 */
public class GLPlane {
    private static final Marker MARKER = MarkerFactory.getMarker("GLOOP");
    private static final Logger LOGGER = LoggerFactory.getLogger(GLPlane.class);
    
    private final GLVec3D normal = new StaticVec3D(Vectors.DEFAULT_FACTORY);
    private final GLVec3D point = new StaticVec3D(Vectors.DEFAULT_FACTORY);

    /*
     * Cached value (for plane equation: ax + by + cz + d = 0)
     * d = -dot(normal, point);
     */
    private double d;

    /**
     * Constructs a new GLPlane. Default value is a plane located at origin with
     * normal [0, 1, 0]
     *
     * @since 14.07.08
     */
    public GLPlane() {
        this.normal.set(0.0, 1.0, 0.0);
    }

    /**
     * Constructs a GLPlane from three points
     *
     * @param a First point
     * @param b Second point
     * @param c Third point
     * @since 14.07.08
     */
    public GLPlane(final GLVec3 a, final GLVec3 b, final GLVec3 c) {
        this.setFromPoints(a, b, c);
    }

    /**
     * Constructs a GLPlane from a normal vector and a position vector
     *
     * @param normal Normal vector
     * @param point Position vector
     * @since 14.07.08
     */
    public GLPlane(final GLVec3 normal, final GLVec3 point) {
        this.setNormalAndPoint(normal, point);
    }

    /**
     * Copy constructor for a GLPlane. This will create a new plane with the
     * same data.
     *
     * @param plane Plane to copy data from.
     * @since 14.07.08
     */
    public GLPlane(final GLPlane plane) {
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
     * @since 14.07.08
     */
    public final void setFromPoints(
            final GLVec3 a,
            final GLVec3 b,
            final GLVec3 c) {

        final GLVec3D _a = a.asGLVec3D();
        final GLVec3D _b = b.asGLVec3D();
        final GLVec3D _c = c.asGLVec3D();
        
        final GLVec3D _ab = _a.minus(_b);
        final GLVec3D _cb = _c.minus(_b);

        this.normal.set(_ab.cross(_cb).normalize());
        this.point.set(_b);
        this.d = -this.normal.dot(this.point);
    }

    /**
     * Sets a plane by defining the normal vector and point vector.
     *
     * @param normal Direction that is orthogonal to the plane (up).
     * @param point Position of the plane
     * @since 14.07.08
     */
    public final void setNormalAndPoint(
            final GLVec3 normal,
            final GLVec3 point) {

        this.normal.set(normal.asGLVec3D());
        this.point.set(point.asGLVec3D());
        this.d = -this.normal.dot(this.point);
    }

    /**
     * Sets a plane from coefficients
     *
     * @param a First coefficient
     * @param b Second coefficient
     * @param c Third coefficient
     * @param d Fourth coefficient
     * @since 14.07.08
     */
    public final void setFromCoefficients(
            final double a,
            final double b,
            final double c,
            final double d) {

        if (!Double.isFinite(a)) {
            LOGGER.debug(MARKER, "Received non finite value: {}!", a);
            throw new ArithmeticException("Coefficient [a] is not finite!");
        } else if (!Double.isFinite(b)) {
            LOGGER.debug(MARKER, "Received non finite value: {}!", b);
            throw new ArithmeticException("Coefficient [b] is not finite!");
        } else if (!Double.isFinite(c)) {
            LOGGER.debug(MARKER, "Received non finite value: {}!", c);
            throw new ArithmeticException("Coefficient [c] is not finite!");
        } else if (!Double.isFinite(d)) {
            LOGGER.debug(MARKER, "Received non finite value: {}!", d);
            throw new ArithmeticException("Coefficient [d] is not finite!");
        }
        
        this.normal.set(a, b, c);

        final double l = this.normal.length();

        this.normal.set(this.normal.normalize());
        this.d = d / l;
    }

    /**
     * Calculates the distance a point is from the plane
     *
     * @param p Point
     * @return Distance     
     * @since 14.07.08
     */
    public final double distance(final GLVec3 p) {        
        return this.d + p.asGLVec3D().dot(this.normal);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof GLPlane) {
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
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.normal);
        hash = 11 * hash + Objects.hashCode(this.point);
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.d) ^ (Double.doubleToLongBits(this.d) >>> 32));
        return hash;
    }
}
