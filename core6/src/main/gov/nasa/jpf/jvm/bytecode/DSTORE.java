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

import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;

/**
 * Store double into local variable
 * ..., value => ...
 */
public class DSTORE extends LocalVariableInstruction implements StoreInstruction  {

  public DSTORE(int localVarIndex) {
    super(localVarIndex);
  }

  public Instruction execute (SystemState ss, KernelState ks, ThreadInfo th) {
    //th.setLongLocalVariable(index, th.longPop());
    th.storeLongOperand(index);

    return getNext(th);
  }

  public int getLength() {
    return 2; // opcode, index
  }
  
  public int getByteCode () {
    switch (index) {
      case 0: return 0x47;
      case 1: return 0x48;
      case 2: return 0x49;
      case 3: return 0x4a;
    }

    return 0x39; // ?? wide
  }
  
  public String getBaseMnemonic() {
    return "dstore";
  }
  
  public void accept(InstructionVisitor insVisitor) {
	  insVisitor.visit(this);
  }
}
