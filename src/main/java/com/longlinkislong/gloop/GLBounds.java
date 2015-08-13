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

/**
 * Collection of Boundary calculation functions for use with GLFrustum
 */
public interface GLBounds {
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
    /**
     * Boundary statuses compared against the formula.
     */
    public static enum Status{
        /** Point is considered inside the frustum */
        INSIDE,
        /** Point is outside of the frustum */
        OUTSIDE,
        /** Point intersects with the frustum */
        INTERSECT
    }
        
    /**
     * Function that calculates the status of the point in relation to the frustum
     * @param frustum Frustum object
     * @param center Point's center
     * @param radius Radius of point
     * @return Status
     * @throws NullPointerException if frustum or center is null
     * @throws IndexOutOfBoundsException if radius does not have enough elements.
     * @throws ArithmeticException if radius is NaN     
     * @since 14.07.24
     */
    public Status inside(final GLFrustum frustum, final GLVec center, final float... radius) throws NullPointerException, IndexOutOfBoundsException, ArithmeticException;
           
        
    /**
     * Functional interface for calculating the bounds using an elliptical frustum.
     * The ellipse bounds requires a 2D radius.
     * @since 14.07.24
     */
    public static final GLBounds ELLIPSE = (final GLFrustum frustum, final GLVec center, final float... radius) -> { if(frustum == null || center == null){
            throw new NullPointerException();
        } else if(radius.length < 2) {
            throw new IndexOutOfBoundsException();
        } else if(Float.isNaN(radius[X]) || Float.isNaN(radius[Y])){
            throw new ArithmeticException();
        }

        Status result = Status.INSIDE;
        for(GLFrustum.Plane plane : GLFrustum.Plane.values()){            
            float distance;
            switch(plane){
                case NEAR:
                case FAR:
                    continue;
                case TOP:
                case BOTTOM:
                    distance = frustum.getDistanceFromPlane(plane, center);
                    
                    if (distance < -radius[Y]){
                        return Status.OUTSIDE;
                    } else if(distance < radius[Y]){
                        result = Status.INTERSECT;
                    }
                    break;
                case LEFT:
                case RIGHT:
                    distance = frustum.getDistanceFromPlane(plane, center);
                    
                    if (distance < -radius[X]){
                        return Status.OUTSIDE;
                    } else if(distance < radius[X]){
                        result = Status.INTERSECT;
                    }
                    break;
            }            
        }
        return result;
     };

    /**
     * Functional interface for calculating the bounds using a circular frustum.
     * The circular bounds requires a 1D radius.
     * @since 14.07.24
     */
    public static final GLBounds CIRCLE = (final GLFrustum frustum, final GLVec center, final float... radius) -> { if(frustum == null || center == null){
            throw new NullPointerException();
        } else if(radius.length == 0){
            throw new IndexOutOfBoundsException();
        } else if(Float.isNaN(radius[X])){
            throw new ArithmeticException();
        }

        Status result = Status.INSIDE;
        for(GLFrustum.Plane plane : GLFrustum.Plane.values()){
            if(plane == GLFrustum.Plane.NEAR || plane == GLFrustum.Plane.FAR){
                continue;
            }
            final float distance = frustum.getDistanceFromPlane(plane, center);

            if(distance < -radius[X]){
                return Status.OUTSIDE;
            }else if(distance < radius[X]){
                return Status.INTERSECT;
            }
        }

        return result;
     };

    /**
     * Functional interface for calculating the bounds using an ellipsoid function
     * The ellipsoid bounds requires a 3D radius.
     * @since 14.07.25
     */
    public static final GLBounds ELLIPSOID = (final GLFrustum frustum, final GLVec center, final float... radius) -> { if(frustum == null || center == null){
            throw new NullPointerException();
        } else if(radius.length < 3){
            throw new IndexOutOfBoundsException();
        } else if(Float.isNaN(radius[X]) || Float.isNaN(radius[Y]) || Float.isNaN(radius[Z])){
            throw new ArithmeticException();
        }

        Status result = Status.INSIDE;
        for(GLFrustum.Plane plane : GLFrustum.Plane.values()){
            final float distance = frustum.getDistanceFromPlane(plane, center);
            
            switch(plane){
                case LEFT:
                case RIGHT:
                    if(distance < -radius[X]){
                        return Status.OUTSIDE;
                    } else if(distance < radius[X]){
                        result = Status.INTERSECT;
                    }
                    break;
                case TOP:
                case BOTTOM:
                    if(distance < -radius[Y]){
                        return Status.OUTSIDE;
                    } else if(distance < radius[Y]){
                        result = Status.INTERSECT;
                    }
                    break;
                case NEAR:
                case FAR:
                    if(distance < -radius[Z]){
                        return Status.OUTSIDE;
                    } else if(distance < radius[Z]){
                        result = Status.INTERSECT;
                    }
                    break;
            }
        }
        return result;
     };

    /**
     * Functional interface for calculating the bounds using a spherical frustum.
     * The sphere bounds requires a 1D radius.
     * @since 14.07.24
     */
    public static final GLBounds SPHERE = (final GLFrustum frustum, final GLVec center, final float... radius) -> { if(frustum == null || center == null){
            throw new NullPointerException();
        } else if(radius.length == 0){
            throw new IndexOutOfBoundsException();        
        } else if(Float.isNaN(radius[X])){
            throw new ArithmeticException();
        }

        Status result = Status.INSIDE;
        for(GLFrustum.Plane plane : GLFrustum.Plane.values()){
            final float distance = frustum.getDistanceFromPlane(plane, center);
            
            if(distance < -radius[X]){
                return Status.OUTSIDE;
            }else if(distance < radius[X]){
                result = Status.INTERSECT;
            }
        }
        return result;
     };    
}
