//
// Copyright (C) 2011 United States Government as represented by the
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

package gov.nasa.jpf.inspector.server.programstate;

import gov.nasa.jpf.inspector.interfaces.JPFInspectorException;
import gov.nasa.jpf.inspector.interfaces.exceptions.JPFInspectorNotSuperClassException;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.ElementInfo;

/**
 * Holds operation needed for reading the value.
 * 
 * Note: Not direct part of the {@link StateValue} class,
 * because it is necessary to distinguish among writeable values,
 * (which represents {@link StateValue}) and the read only instances which holds value
 * {#heap[1]} entries ClassName #className[]
 * 
 * @author Alf
 * 
 */
public interface StateReadableValueInterface extends StateNodeInterface {

  /**
   * @return Get type information about represented value
   */
  public ClassInfo getClassInfo ();

  /**
   * @return Gets representation of the readable value.
   *         For primitive types gets wrapped version of the primitive type.
   *         For objects (references and arrays) gets ElementInfo
   */
  public Object getValue ();

  /**
   * 
   * @return Gets true if represented value is object or array
   */
  public boolean isReference ();

  /**
   * @return If isReference gets representation of referenced value. Gets null if not reference or null reference.
   */
  public ElementInfo getReferenceValue ();

  /**
   * Limits accessible fields only to from the given ClassInfo.
   * (useful for access to hidden/overriden fields)
   * 
   * @return Gets value which represents only a subset of fields.
   */
  public StateReadableValueInterface createSuper () throws JPFInspectorException;

  public StateReadableValueInterface createPredecessorClass (ClassInfo ci) throws JPFInspectorNotSuperClassException;

  /**
   * Gets value that represents this instance. It means returns same "Value", but with different stateExpression. If not an instance (static,primitive type,
   * array) then throws exception)
   * 
   * @return Gets value that represents this instance.
   * @throws JPFInspectorException If not an instance (static,primitive type, array)
   */
  public StateReadableValueInterface createThisValue () throws JPFInspectorException;

}
