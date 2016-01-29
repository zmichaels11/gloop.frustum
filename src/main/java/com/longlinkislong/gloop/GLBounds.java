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

import static com.longlinkislong.gloop.Vectors.X;
import static com.longlinkislong.gloop.Vectors.Y;
import static com.longlinkislong.gloop.Vectors.Z;
import java.util.Objects;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * Collection of Boundary calculation functions for use with GLFrustum
 */
public interface GLBounds {    
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
     * @since 14.07.24
     */
    public Status inside(final GLFrustum frustum, final GLVec3 center, final double... radius);
           
        
    /**
     * Functional interface for calculating the bounds using an elliptical frustum.
     * The ellipse bounds requires a 2D radius.
     * @since 14.07.24
     */
    public static final GLBounds ELLIPSE = (
            final GLFrustum frustum, 
            final GLVec3 center, final double... radius) -> {
        
        Objects.requireNonNull(frustum);
        Objects.requireNonNull(center);
                
        if(!Double.isFinite(radius[X])) {
            LoggerFactory.getLogger(GLBounds.class).error(
                    MarkerFactory.getMarker("GLOOP"), 
                    "Received non finite value: {}!", 
                    radius[X]);
            throw new ArithmeticException("X-radius must be finite!");
        } else if(!Double.isFinite(radius[Y])) {
            LoggerFactory.getLogger(GLBounds.class).error(
                    MarkerFactory.getMarker("GLOOP"), 
                    "Received non finite value: {}!", 
                    radius[Y]);
            throw new ArithmeticException("Y-radius must be finite!");
        }

        Status result = Status.INSIDE;
        for(GLFrustum.Plane plane : GLFrustum.Plane.values()){            
            double distance;
            
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
    public static final GLBounds CIRCLE = (
            final GLFrustum frustum, 
            final GLVec3 center, 
            final double... radius) -> { 
        
        Objects.requireNonNull(frustum);
        Objects.requireNonNull(center);
                
        if(!Double.isFinite(radius[X])) {
            LoggerFactory.getLogger(GLBounds.class).error(
                    MarkerFactory.getMarker("GLOOP"), 
                    "Received non finite value: {}!", 
                    radius[X]);
            throw new ArithmeticException("Radius must be finite!");
        }       

        Status result = Status.INSIDE;
        for(GLFrustum.Plane plane : GLFrustum.Plane.values()){
            if(plane == GLFrustum.Plane.NEAR || plane == GLFrustum.Plane.FAR){
                continue;
            }
            
            final double distance = frustum.getDistanceFromPlane(plane, center);

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
    public static final GLBounds ELLIPSOID = (
            final GLFrustum frustum, 
            final GLVec3 center, 
            final double... radius) -> { 
        
        Objects.requireNonNull(frustum);
        Objects.requireNonNull(center);
        
        if(!Double.isFinite(radius[X])) {
            LoggerFactory.getLogger(GLBounds.class).error(
                    MarkerFactory.getMarker("GLOOP"), 
                    "Received non finite value: {}!", 
                    radius[X]);
            throw new ArithmeticException("X-radius must be finite!");
        } else if(!Double.isFinite(radius[Y])) {
            LoggerFactory.getLogger(GLBounds.class).error(
                    MarkerFactory.getMarker("GLOOP"), 
                    "Received non finite value: {}!", 
                    radius[Y]);
            throw new ArithmeticException("Y-radius must be finite!");
        } else if(!Double.isFinite(radius[Z])) {
            LoggerFactory.getLogger(GLBounds.class).error(
                    MarkerFactory.getMarker("GLOOP"), 
                    "Received non finite value: {}!", 
                    radius[Z]);
            throw new ArithmeticException("Z-radius must be finite!");
        }        

        Status result = Status.INSIDE;
        for(GLFrustum.Plane plane : GLFrustum.Plane.values()){
            final double distance = frustum.getDistanceFromPlane(plane, center);
            
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
    public static final GLBounds SPHERE = (
            final GLFrustum frustum, 
            final GLVec3 center, 
            final double... radius) -> { 
        
        Objects.requireNonNull(frustum);
        Objects.requireNonNull(center);
        
        if(!Double.isFinite(radius[X])) {            
            LoggerFactory.getLogger(GLBounds.class).error(
                    MarkerFactory.getMarker("GLOOP"), 
                    "Received non finite value: {}!", 
                    radius[X]);
            throw new ArithmeticException("Radius must be finite!");
        }

        Status result = Status.INSIDE;
        for(GLFrustum.Plane plane : GLFrustum.Plane.values()){
            final double distance = frustum.getDistanceFromPlane(plane, center);
            
            if(distance < -radius[X]){
                return Status.OUTSIDE;
            }else if(distance < radius[X]){
                result = Status.INTERSECT;
            }
        }
        
        return result;
     };    
}
