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
 * Camera manipulation class for easy of use 3D camera control.
 *
 * @author Robert Hewitt
 * @since 15.11.05
 * @version J1.8
 */
public final class GLCamera {
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    
    private final GLVec3F pos;
    private final double[] r = {0d, 0d, 0d};
    private final GLPlane[] planes;

    private GLMat4F translate = null, rotx = null, roty = null, rotz = null;
    private GLMat4F rotXYZ = null, rotYZ = null;
    private GLMat4F view = null;
    protected boolean changed = true;

    /**
     * Constructs a new Camera object located at point (0,0,0) without any
     * rotation
     *
     * @since 14.07.08
     */
    public GLCamera() {
        this.pos = new StaticVec3F(Vectors.DEFAULT_FACTORY);
        this.planes = new GLPlane[6];
        for (int i = 0; i < 6; i++) {
            this.planes[i] = new GLPlane();
        }
    }

    /**
     * Constructs a new Camera object located at the specified position without
     * any rotation.
     *
     * @param pos Position of the camera
     * @since 14.07.25
     * @throws NullPointerException if pos is null.
     */
    public GLCamera(final GLVec pos) throws NullPointerException {
        if (pos == null) {
            throw new NullPointerException();
        }
        this.pos = new StaticVec3F(Vectors.DEFAULT_FACTORY, pos);
        this.planes = new GLPlane[6];
        for (int i = 0; i < 6; i++) {
            this.planes[i] = new GLPlane();
        }
    }

    /**
     * Constructs a new Camera object located at the specified position looking
     * at a specified point.
     *
     * @param pos Position of the camera
     * @param center Point the camera is looking at
     * @since 14.07.08
     * @throws NullPointerException if pos or center is null.
     */
    public GLCamera(final GLVec pos, final GLVec center) throws NullPointerException {
        if (pos == null || center == null) {
            throw new NullPointerException();
        }

        this.pos = new StaticVec3F(Vectors.DEFAULT_FACTORY, pos);
        this.lookAt(center);
        this.planes = new GLPlane[6];
        for (int i = 0; i < 6; i++) {
            this.planes[i] = new GLPlane();
        }
    }

    public GLCamera(final GLCamera otherCamera) throws NullPointerException {
        if (otherCamera == null) {
            throw new NullPointerException();
        }
        this.pos = new StaticVec3F(Vectors.DEFAULT_FACTORY, otherCamera.pos);
        this.setRX(otherCamera.r[X]);
        this.setRY(otherCamera.r[Y]);
        this.setRZ(otherCamera.r[Z]);
        this.planes = new GLPlane[6];
        for (int i = 0; i < 6; i++) {
            this.planes[i] = new GLPlane();
        }
    }

    /**
     * Rotates the camera to look at the specified location
     *
     * @param center Location for the camera to look at.
     * @throws NullPointerException if center is null.
     * @since 14.07.08
     */
    public final void lookAt(final GLVec center) throws NullPointerException {
        Objects.requireNonNull(center);

        final GLVec3F d = this.pos.minus(center);

        this.r[X] = -Math.atan2(d.x(), d.z());
        this.r[Y] = Math.atan2(d.y(), Math.sqrt(d.x() * d.x() + d.z() * d.z()));

        this.rotx = null;
        this.roty = null;
    }

    /**
     * Obtains the view matrix associated with the camera. This includes both
     * translation and rotation.
     *
     * @return Camera's view matrix
     * @since 14.07.25
     */
    private final GLMat4F calculateViewMatrix() {
        if (this.translate == null) {
            final GLVec3F n = this.pos.negative();
            this.translate = GLMat4F.translation(n.x(), n.y(), n.z()).asStaticMat();
            this.view = null;
        }
        if (this.rotx == null) {
            this.rotx = GLMat4F.rotateY((float) this.r[X]).asStaticMat();
            this.rotXYZ = null;
        }
        if (this.roty == null) {
            this.roty = GLMat4F.rotateX((float) this.r[Y]).asStaticMat();
            this.rotYZ = null;
        }
        if (this.rotz == null) {
            this.rotz = GLMat4F.rotateZ((float) this.r[Z]).asStaticMat();
            this.rotYZ = null;
        }
        if (this.rotYZ == null) {
            this.rotYZ = this.roty.multiply(this.rotz).asStaticMat();
            this.rotXYZ = null;
        }
        if (this.rotXYZ == null) {
            this.rotXYZ = this.rotx.multiply(this.rotYZ).asStaticMat();
            this.view = null;
        }
        if (this.view == null) {
            this.changed = true;
            this.view = this.translate.multiply(this.rotXYZ).asStaticMat();
        }

        return this.view;
    }

    /**
     * Moves the camera forward (relative to camera) a specified distance
     *
     * @param distance Distance to move the camera forwards
     * @throws ArithmeticException if distance is not finite.
     * @since 14.07.08
     */
    public final void stepForwards(final float distance) throws ArithmeticException {
        if (!Float.isFinite(distance)) {
            throw new ArithmeticException();
        }
        GLVec3F rVec = GLVec3F.create();

        rVec.set(
                (float) (Math.sin(this.r[X]) * Math.cos(r[Y])),
                -(float) (Math.sin(this.r[Y])),
                -(float) (Math.cos(this.r[X]) * Math.cos(this.r[Y])));

        rVec = rVec.scale(distance);

        this.pos.set(this.pos.plus(rVec));

        this.translate = null;
        this.changed = true;
    }

    /**
     * Moves the camera upward (relative to camera) a specified distance
     *
     * @param distance Distance to move camera upwards
     * @throws ArithmeticException if distance is not finite.
     * @since 14.07.08
     */
    public final void stepUpwards(final float distance) throws ArithmeticException {
        if (!Float.isFinite(distance)) {
            throw new ArithmeticException();
        }
        GLVec3F temp = GLVec3F.create(0f, 1f, 0f);

        temp = temp.scale(distance);
        this.pos.set(this.pos.plus(temp));
        this.translate = null;
        this.changed = true;
    }

    /**
     * Moves the camera sideways (relative to camera) a specified distance
     *
     * @param distance Distance to move camera sideways
     * @throws ArithmeticException if distance is not finite.
     * @since 14.07.08
     */
    public final void stepSideways(final float distance) throws ArithmeticException {
        if (!Float.isFinite(distance)) {
            throw new ArithmeticException();
        }
        GLVec3F temp = GLVec3F.create(
                (float) (Math.sin(r[X] + Math.PI / 2.0)),
                0f,
                -(float) (Math.cos(r[X] + Math.PI / 2.0)));
        temp = temp.scale(distance);
        this.pos.set(this.pos.plus(temp));
        this.translate = null;
        this.changed = true;
    }

    /**
     * Gets the position of the camera
     *
     * @return Position of the camera as a GLVec
     * @since 14.07.08
     */
    public final GLVec3F getPosition() {
        return GLVec3F.create().set(this.pos);
    }

    /**
     * Increases the rotation of the camera along the X-axis
     *
     * @param v Any value type to rotate the camera.
     * @throws ArithmeticException if v is not finite
     * @since 14.07.08
     */
    public final void incRX(final double v) throws ArithmeticException {
        if (!Double.isFinite(v)) {
            throw new ArithmeticException();
        }
        this.r[X] += v;
        this.rotx = null;
        this.changed = true;
    }

    /**
     * Increases the rotation of the camera along the Y-axis
     *
     * @param v Any value type to rotate the camera
     * @throws ArithmeticException if v is not finite.
     * @since 14.07.08
     */
    public final void incRY(final double v) throws ArithmeticException {
        if (!Double.isFinite(v)) {
            throw new ArithmeticException();
        }
        this.r[Y] += v;
        this.roty = null;
        this.changed = true;
    }

    /**
     * Increases the rotation of the camera along the Z-axis
     *
     * @param v Any value type to rotate the camera
     * @throws ArithmeticException if v is not finite
     * @since 14.07.08
     */
    public final void incRZ(final double v) throws ArithmeticException {
        if (!Double.isFinite(v)) {
            throw new ArithmeticException();
        }
        this.r[Z] += v;
        this.rotz = null;
        this.changed = true;
    }

    /**
     * Sets the rotation of the camera along the X-axis
     *
     * @param v X-rotation value
     * @throws ArithmeticException if v is not finite.
     * @since 14.07.08
     */
    public final void setRX(final double v) throws ArithmeticException {
        if (!Double.isFinite(v)) {
            throw new ArithmeticException();
        }
        this.r[X] = v;
        this.rotx = null;
        this.changed = true;
    }

    /**
     * Sets the rotation of the camera along the Y-axis
     *
     * @param v Y-rotation value
     * @throws ArithmeticException if v is not finite.
     * @since 14.07.08
     */
    public final void setRY(final double v) throws ArithmeticException {
        if (!Double.isFinite(v)) {
            throw new ArithmeticException();
        }
        this.r[Y] = v;
        this.roty = null;
        this.changed = true;
    }

    /**
     * Sets the rotation of the camera along the Z-axis
     *
     * @param v Z-rotation value
     * @throws ArithmeticException if v is not finite.
     * @since 14.07.08
     */
    public final void setRZ(final double v) throws ArithmeticException {
        if (!Double.isFinite(v)) {
            throw new ArithmeticException();
        }
        this.r[Z] = v;
        this.rotz = null;
        this.changed = true;
    }

    /**
     * Sets the position of the camera
     *
     * @param pos Position value
     * @throws NullPointerException if pos is null.
     * @since 14.07.08
     */
    public final void setPosition(final GLVec pos) throws NullPointerException {
        if (pos == null) {
            throw new NullPointerException();
        }
        this.pos.set(pos);
        this.translate = null;
        this.changed = true;
    }

    /**
     * Sets the position of the camera
     *
     * @param v Position value as floats
     * @since 14.07.08
     */
    @SafeVarargs
    public final void setPosition(final float... v) {
        this.pos.set(v, 0, v.length);
        this.translate = null;
    }

    /**
     * Sets the position of the camera to a position vector defined as an array
     *
     * @param array Array containing a position vector
     * @param offset Offset to start reading from the array
     * @param size Number of elements to read from the array.
     * @throws NullPointerException if array is null.
     * @throws IndexOutOfBoundsException if offset is negative, size is
     * negative, or size is greater than array.length - offset.
     * @since 14.07.08
     */
    public final void setPosition(final float[] array, final int offset, final int size) throws NullPointerException, IndexOutOfBoundsException {
        if (array == null) {
            throw new NullPointerException();
        } else if (offset < 0 || size < 0 || size > array.length - offset) {
            throw new IndexOutOfBoundsException();
        }

        this.pos.set(array, offset, size);
        this.translate = null;
        this.changed = true;
    }

    /**
     * Gets the rotation of the camera along the X-axis
     *
     * @return X-rotation
     * @since 14.07.08
     */
    public final double getRX() {
        return this.r[X];
    }

    /**
     * Gets the rotation of the camera along the Y-axis
     *
     * @return Y-rotation
     * @since 14.07.08
     */
    public final double getRY() {
        return this.r[Y];
    }

    /**
     * Gets the rotation of the camera along the Z-axis
     *
     * @return Z-rotation
     * @since 14.07.08
     */
    public final double getRZ() {
        return this.r[Z];
    }

    private final GLMat4F cachedViewProjMat = new StaticMat4F(Matrices.DEFAULT_FACTORY);

    /**
     * Retrieves the concatenation of the view matrix with the supplied
     * projection matrix. The frustum will also be updated. This function
     * references a cached view-projection matrix which may be edited outside of
     * this class. Any changes to the matrix will result in recalculation.
     *
     * @return View matrix
     * @throws NullPointerException if pMat is null.
     * @since 14.07.25
     */
    public GLMat4F getViewMatrix() {
        if (this.changed) {
            final GLMat4F result = this.calculateViewMatrix();
            
            this.cachedViewProjMat.set(result);
            this.changed = false;
        }

        return cachedViewProjMat.copyTo(Matrices.DEFAULT_FACTORY);
    }
    
    @Override
    public boolean equals(Object other) {
        if(this == other) {
            return true;
        } else if(other instanceof GLCamera) {
            final GLCamera oCam = (GLCamera) other;
            
            return oCam.getViewMatrix().equals(this.getViewMatrix());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(getViewMatrix());
        return hash;
    }
    
}
