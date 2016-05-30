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

package gov.nasa.jpf.inspector.exceptions;

import gov.nasa.jpf.inspector.server.programstate.StateValue;
import gov.nasa.jpf.vm.ClassInfo;

/**
 * Note: This is "internal" error and should not be thrown. (If thrown there is error in the implementation)
 * 
 * @author Alf
 * 
 */
public class JPFInspectorNotSuperClassException extends JPFInspectorException {

  private static final long serialVersionUID = 7785538860963123962L;

  public JPFInspectorNotSuperClassException (ClassInfo ciSuper, ClassInfo ciBase) {
    super("The class \"" + StateValue.getSimpleName(ciSuper) + "\" is not a superclass of the \"" + StateValue.getSimpleName(ciBase) + "\" class.");
  }

}