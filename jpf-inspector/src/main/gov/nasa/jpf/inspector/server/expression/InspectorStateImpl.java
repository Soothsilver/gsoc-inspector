//
// Copyright (C) 2010 United States Government as represented by the
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

package gov.nasa.jpf.inspector.server.expression;

import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.VM;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a program state, i.e. a point in the program timeline.
 */
public class InspectorStateImpl implements InspectorState {

  /**
   * For each thread, this stores the instruction that was last executed. The instructions threads are currently
   * pointing to but have not yet executed are not in this.
   *
   * The key is thread id.
   */
  private final Map<Integer, Instruction> lastExecutedInstructions = new HashMap<>();

  private VM vm;
  /**
   * Search class used for the last executed step.
   */
  private Search search;

  /**
   * The instruction about to be executed.
   */
  private Instruction currentInstruction = null;
  /**
   * The thread that will execute the next instruction to be executed.
   */
  private int currentThread = 0;

  private InspectorState.ListenerMethod listenerMethod = ListenerMethod.LM_NOT_IN_LIST;

  @Override
  public VM getJVM () {
    return vm;
  }

  @Override
  public Map<Integer, Instruction> getPreviousSteps () {
    return lastExecutedInstructions;
  }

  @Override
  public Search getSearch () {
    return search;
  }

  @Override
  public ListenerMethod getListenerMethod () {
    return listenerMethod;
  }

  // Has to be called after each executed instruction
  public void instructionExecuted (VM newJVM) {
    this.vm = newJVM;

    lastExecutedInstructions.put(currentThread, currentInstruction);
    currentInstruction = newJVM.getInstruction();
    currentThread = newJVM.getCurrentThread().getId();
    this.listenerMethod = ListenerMethod.LM_INSTRUCTION_EXECUTED;

  }

  public void stateChanged (Search search, ListenerMethod listenerMethod) {
    this.vm = search.getVM();
    this.search = search;
    this.listenerMethod = listenerMethod;
  }

  public void notifyListenerMethodCall (ListenerMethod listenerMethod, VM jvm) {
    this.listenerMethod = listenerMethod;
    this.vm = jvm;
  }

}