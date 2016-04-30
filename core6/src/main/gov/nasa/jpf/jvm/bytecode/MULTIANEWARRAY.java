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
package gov.nasa.jpf.jvm.bytecode;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.Heap;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.Types;


/**
 * Create new multidimensional array
 * ..., count1, [count2, ...] => ..., arrayref
 */
public class MULTIANEWARRAY extends Instruction {
  protected String type;
  
  protected int dimensions;
  protected int[] arrayLengths;

  public MULTIANEWARRAY (String typeName, int dimensions){
    this.type = Types.getClassNameFromTypeName(typeName);
    this.dimensions = dimensions;
  }

  public static int allocateArray (Heap heap, String type, int[] dim, ThreadInfo ti, int d) {
    int         l = dim[d];
    int         arrayRef = heap.newArray(type.substring(d + 1), l, ti);
    ElementInfo e = heap.get(arrayRef);

    if (dim.length > (d + 1)) {
      for (int i = 0; i < l; i++) {
        e.setReferenceElement(i, allocateArray(heap, type, dim, ti, d + 1));
      }
    }

    return arrayRef;
  }

  public Instruction execute (SystemState ss, KernelState ks, ThreadInfo ti) {
    arrayLengths = new int[dimensions];

    for (int i = dimensions - 1; i >= 0; i--) {
      arrayLengths[i] = ti.pop();
    }

    // there is no clinit for array classes, but we still have  to create a class object
    // since its a builtin class, we also don't have to bother with NoClassDefFoundErrors
    ClassInfo ci = ClassInfo.getResolvedClassInfo(type);
    if (!ci.isRegistered()) {
      ci.registerClass(ti);
      ci.setInitialized();
    }
    
    int arrayRef = allocateArray(ti.getHeap(), type, arrayLengths, ti, 0);

    // put the result (the array reference) on the stack
    ti.push(arrayRef, true);

    return getNext(ti);
  }

  public int getLength() {
    return 4; // opcode, index1, index2, dimensions
  }
  
  public int getByteCode () {
    return 0xC5;
  }
  
  public void accept(InstructionVisitor insVisitor) {
	  insVisitor.visit(this);
  }

  public String getType(){
    return type;
  }
  
  public int getDimensions() {
    return dimensions;
  }
  
  public int getArrayLength (int dimension){
    if (dimension < dimensions && arrayLengths != null){
      return arrayLengths[dimension];
    } else {
      return -1;
    }
  }
  
  @Override
  public void cleanupTransients(){
    arrayLengths= null;
  }
}
