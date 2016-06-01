package gov.nasa.jpf.inspector.server.pathanalysis;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.Step;
import gov.nasa.jpf.jvm.bytecode.ATHROW;
import gov.nasa.jpf.jvm.bytecode.JVMInstruction;
import gov.nasa.jpf.jvm.bytecode.JVMInstructionVisitorAdapter;

public class CheckThrowInstruction extends JVMInstructionVisitorAdapter {
  private boolean isThrowInstr = false;

  public boolean isThrowStep(Step step) {
    assert(step != null);
    return isThrowInstruction(step.getInstruction());
  }

  public boolean isThrowInstruction(Instruction inst) {
    assert(inst != null);
    isThrowInstr = false;
    assert (inst instanceof JVMInstruction);
    ((JVMInstruction)inst).accept(this);
    return isThrowInstr;
  }

  @Override
  public void visit(ATHROW ins) {
    isThrowInstr = true;
  }
}
