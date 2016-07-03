//
// Copyright (C) 2010-2011 Pavel Jančík
// Copyright (C) 2016 Petr Hudeček
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package gov.nasa.jpf.inspector.server.expression.expressions;

import gov.nasa.jpf.inspector.interfaces.InstructionPosition;
import gov.nasa.jpf.inspector.server.expression.ExpressionBooleanLeaf;
import gov.nasa.jpf.inspector.server.expression.InspectorState;
import gov.nasa.jpf.inspector.server.expression.InspectorState.ListenerMethod;
import gov.nasa.jpf.inspector.server.jpf.JPFInspector;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.jvm.bytecode.JSR;
import gov.nasa.jpf.jvm.bytecode.JSR_W;
import gov.nasa.jpf.jvm.bytecode.RET;
import gov.nasa.jpf.vm.bytecode.ReturnInstruction;

/**
 * Represent expression (Breakpoint) that stops on given position in the program.
 */

// TODO the NEW instruction can stand in place of the call instruction (calls the constructor!!)
// problem is connected with the DirectCalls (cinits) and reexecution of the same instruction !!
public class ExpressionBreakpointPosition extends ExpressionBooleanLeaf {
  private static final boolean DEBUG = false;
  @SuppressWarnings("FieldCanBeLocal") // IDEA bug
  private final JPFInspector inspector;

  private final InstructionPosition instPos;

  public ExpressionBreakpointPosition (JPFInspector inspector, InstructionPosition pos) {
    assert pos != null;
    assert inspector != null;
    this.inspector = inspector;

    if (DEBUG) {
      inspector.getDebugPrintStream().println(
          ExpressionBreakpointPosition.class.getSimpleName() + "." + ExpressionBreakpointPosition.class.getSimpleName() + "(pos=" + pos + ")");
    }

    this.instPos = pos;
  }

  @Override
  public boolean evaluateExpression (InspectorState state) {
    assert state != null;
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".evaluateExpression(...)");
    }
    if (state.getListenerMethod() != ListenerMethod.LM_EXECUTE_INSTRUCTION) {
      return false;
    }

    VM vm = state.getVM();
    assert vm != null;

    int thisThread = vm.getCurrentThread().getId();

    /*
    // TODO this is NOT recommended by the Javadoc or jpf-core. What to do about it?
    final Path path = vm.getPath();
    */
    final Instruction thisInstruction = vm.getInstruction();


    // This represents whether we are the same file and line, but we still need to ensure that we are the FIRST
    // instruction on this line. However, a line may contain multiple functions or classes even!, and may contain
    // even multiple lines! (because file can be given as a wildcard).
    final boolean lastInstrHitPos = instPos.hitPosition(thisInstruction);
    if (lastInstrHitPos == false) {
      return false;
    }

    if (DEBUG) {
      inspector.getDebugPrintStream().println("\tlasInstr=" + instructionPosition(thisInstruction) + "\n\tlastInstrHitPos=" + lastInstrHitPos);
    }

    Instruction prevInstr = state.getLastExecutedInstruction(thisThread);
    //Instruction prevInstr = getInstructionForThread(vm.getSystemState().getTrail(), path, thisThread, 1);
    if (DEBUG) {
      inspector.getDebugPrintStream().println("\tprevInstr=" + instructionPosition(prevInstr));
    }


    prevInstr = getPrevInstructionInSameMethod(prevInstr, thisInstruction);
    final boolean prevInstrHitPos = instPos.hitPosition(prevInstr);
    if (DEBUG) {
      inspector.getDebugPrintStream().println("\tprevInstr=" + instructionPosition(prevInstr) + "\n\tprevInstrHitPos=" + prevInstrHitPos);
    }
    return !prevInstrHitPos;
    // This ensures that we hit only if we are on the source line but the previous instruction
    // is NOT on the source line.
  }

  @Override
  public String getDetails (InspectorState state) {
    if (state != null && evaluateExpression(state)) {
      return "SuT will now execute \"" + state.getVM().getInstruction().toString() + "\" at position " + instPos.toString() + ".";
    }
    return "";
  }

  /**
   * @param prevInstr
   *        Instruction executed (by the same thread) immediately before the last instruction.
   * @param lastInstr
   *        Last execute instruction
   * @return prevInstruction to executed in the same method as the lastInstr.
   * 
   *         Note: Method requires that the lastInstruction is executed directly after the prevInstr (in one thread)
   */
  private static Instruction getPrevInstructionInSameMethod(Instruction prevInstr, Instruction lastInstr) {
    // Handle special cases Calls and JSR
    if (prevInstr instanceof ReturnInstruction) {
      // We should ignore instructions in called method, we focus on previous instruction in current method
      prevInstr = lastInstr.getPrev();
      // TODO ... problem with direct calls !!!
      // assert prevInstr instanceof InvokeInstruction;
    }

    if (prevInstr instanceof RET) {
      // We should ignore instructions in called subrutine, we focus on previous instruction in current method
      prevInstr = lastInstr.getPrev();
      assert (prevInstr instanceof JSR) || (prevInstr instanceof JSR_W);
    }
    return prevInstr;
  }

  public static String instructionPosition (Instruction inst) {
    if (inst == null) {
      return "(null)";
    }
    return inst.getMethodInfo().getSourceFileName() + ":" + inst.getLineNumber() + "(method=" + inst.getMethodInfo().getName() + ") - " + inst.toString();
  }

  @Override
  public String getNormalizedExpression () {
    String string = "position=" +
            instPos.getFileName() +
            ':' +
            instPos.getLineNumber();
    return string;
  }

}
