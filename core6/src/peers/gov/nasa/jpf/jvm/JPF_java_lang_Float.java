//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
// 
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
// 
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.jvm;

/**
 * MJI NativePeer class for java.lang.Float library abstraction
 */
public class JPF_java_lang_Float {
  public static int floatToIntBits__F__I (MJIEnv env, int rcls, float v0) {
    return Float.floatToIntBits(v0);
  }

  public static int floatToRawIntBits__F__I (MJIEnv env, int rcls, float v0) {
    return Float.floatToRawIntBits(v0);
  }

  public static float intBitsToFloat__I__F (MJIEnv env, int rcls, int v0) {
    return Float.intBitsToFloat(v0);
  }
  
  // we need to intercept this because it compares double values, which might
  // cause an ArithmeticException to be raised if -check-fp-compare is set (default)
  // but -check-fp isn't, and Double.isInfinit is used to handle the cases
  // explicitly in the program (which is supposed to be the right way)
  public static boolean isInfinite__F__Z (MJIEnv env, int rcls, float v) {
    return Float.isInfinite(v);
  }
  
  // ditto (see isInfinite)
  public static boolean isNaN__F__Z (MJIEnv env, int rcls, float v) {
    return Float.isNaN(v);
  }
}
