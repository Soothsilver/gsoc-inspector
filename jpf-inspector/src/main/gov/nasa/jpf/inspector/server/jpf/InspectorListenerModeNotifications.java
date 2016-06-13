package gov.nasa.jpf.inspector.server.jpf;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.inspector.server.breakpoints.BreakpointHandler;
import gov.nasa.jpf.inspector.server.breakpoints.CommandsManager;
import gov.nasa.jpf.inspector.server.breakpoints.DefaultForwardTraceManager;
import gov.nasa.jpf.inspector.server.choicegenerators.ChoiceGeneratorNotifications;
import gov.nasa.jpf.inspector.server.expression.InspectorState.ListenerMethod;
import gov.nasa.jpf.inspector.server.expression.InspectorStateImpl;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.*;

/**
 * Standard listener used in JPF Inspector when all parts of the Inspector should be notified about incoming events.
 *
 * This is contrary to {@link InspectorListenerModeSilent} which is active during backtracking and ignores breakpoints.
 */
public class InspectorListenerModeNotifications extends ListenerAdapter {
  private static final boolean DEBUG = true;

  private final JPFInspector inspector;
  private final CommandsManager cmdMgr;
  private final BreakpointHandler bpMgr;
  private final ChoiceGeneratorNotifications cgNotify;
  private final DefaultForwardTraceManager dftMgr;

  private final InspectorStateImpl inspState = new InspectorStateImpl();

  /**
   * Whether continue or not after a property is violated
   */
  private final boolean searchMultipleError;

  public InspectorListenerModeNotifications (JPFInspector inspector, CommandsManager cmdMgr, BreakpointHandler bpMgr, ChoiceGeneratorNotifications cgNotify,
                                             DefaultForwardTraceManager dftMgr, boolean searchMultipleError) {
    this.inspector = inspector;
    this.cmdMgr = cmdMgr;
    this.bpMgr = bpMgr;
    this.cgNotify = cgNotify;
    this.dftMgr = dftMgr;
    this.searchMultipleError = searchMultipleError;

  }

  @Override
  public void stateAdvanced (Search search) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".stateAdvanced()");
    }
    inspState.stateChanged(search, ListenerMethod.LM_STATE_ADVANCED);
    bpMgr.forwardJPFStep(inspState);
    bpMgr.checkBreakpoints(inspState);
    dftMgr.forwardStep(search);
    cmdMgr.tryStop(inspState);
  }

  @Override
  public void stateProcessed (Search search) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".stateProcessed()");
    }
    inspState.stateChanged(search, ListenerMethod.LM_NOT_IN_LIST);
    cmdMgr.tryStop(inspState);
  }

  @Override
  public void stateBacktracked (Search search) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".stateBacktracked()");
    }
    inspState.stateChanged(search, ListenerMethod.LM_STATE_BACKTRACKED);
    bpMgr.backwardJPFStep(inspState);
    // dftMgr.extendTrace(search.getTransition());
    cmdMgr.tryStop(inspState);
  }

  @Override
  public void searchStarted (Search search) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".searchStarted()");
    }
    inspState.stateChanged(search, ListenerMethod.LM_NOT_IN_LIST);
    cmdMgr.tryStop(inspState);
  }

  @Override
  public void searchConstraintHit (Search search) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".searchConstraintHit()");
    }
    inspState.stateChanged(search, ListenerMethod.LM_SEARCH_CONSTRAINT_HIT);
    cmdMgr.tryStop(inspState);
  }

  @Override
  public void searchFinished (Search search) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".searchFinished()");
    }
  }

  @Override
  public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".executeInstruction(" + instructionToExecute + ")");
    }
    inspState.notifyListenerMethodCall(ListenerMethod.LM_EXECUTE_INSTRUCTION, vm);
    inspState.setCurrentInstructionInformation(currentThread, instructionToExecute);
    bpMgr.checkBreakpoints(inspState);
  }



  @Override
  public void instructionExecuted(VM vm, ThreadInfo currentThread, Instruction nextInstruction, Instruction executedInstruction) {

    if (DEBUG) {
      inspector.getDebugPrintStream().println(
          this.getClass().getSimpleName() + ".instructionExecuted(" + executedInstruction + ", loc=" + executedInstruction.getFileLocation()
              + ")");
    }
    inspState.instructionExecuted(currentThread.getId(), executedInstruction, vm);
    bpMgr.checkBreakpoints(inspState);
  }

  @Override
  public void classLoaded(VM vm, ClassInfo loadedClass) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".classLoaded()");
    }
  }

  @Override
  public void gcBegin (VM vm) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".gcBegin()");
    }
    inspState.notifyListenerMethodCall(ListenerMethod.LM_GC_BEGIN, vm);
    bpMgr.checkBreakpoints(inspState);
  }

  @Override
  public void gcEnd (VM vm) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".gcEnd()");
    }
    inspState.notifyListenerMethodCall(ListenerMethod.LM_GC_END, vm);
    bpMgr.checkBreakpoints(inspState);
  }

  @Override
  public void objectCreated(VM vm, ThreadInfo currentThread, ElementInfo newObject) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".objectCreated()");
    }
    inspState.notifyListenerElementInfoMethodCall(vm, ListenerMethod.LM_OBJECT_CREATED, newObject);
    bpMgr.checkBreakpoints(inspState);
  }

  @Override
  public void objectReleased(VM vm, ThreadInfo currentThread, ElementInfo releasedObject) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".objectReleased()");
    }
    inspState.notifyListenerElementInfoMethodCall(vm, ListenerMethod.LM_OBJECT_RELEASED, releasedObject);
    bpMgr.checkBreakpoints(inspState);
  }


  @Override
  public void exceptionThrown(VM vm, ThreadInfo currentThread, ElementInfo thrownException) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".exceptionThrown()");
    }
    inspState.notifyListenerElementInfoMethodCall(vm, ListenerMethod.LM_EXCEPTION_THROWN, thrownException);
    bpMgr.checkBreakpoints(inspState);
  }

  @Override
  public void choiceGeneratorSet(VM vm, ChoiceGenerator<?> newCG) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(
          this.getClass().getSimpleName() + ".choiceGeneratorSet() - cg=" + newCG + " processedChoices="
              + newCG.getProcessedNumberOfChoices());
    }
  }

  @Override
  public void choiceGeneratorAdvanced(VM vm, ChoiceGenerator<?> currentCG) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(
          this.getClass().getSimpleName() + ".choiceGeneratorAdvanced() - cg=" + currentCG + " processedChoices="
              + currentCG.getProcessedNumberOfChoices());
    }
    inspState.notifyListenerMethodCall(ListenerMethod.LM_CHOICE_GENERATOR_ADVANCED, vm);
    cgNotify.notifyChoiceGeneratorAdvance(currentCG, inspState);
    bpMgr.checkBreakpoints(inspState);

  }

  @Override
  public void choiceGeneratorRegistered(VM vm, ChoiceGenerator<?> nextCG, ThreadInfo currentThread, Instruction executedInstruction) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(
          this.getClass().getSimpleName() + ".choiceGeneratorRegistered() - cg=" + nextCG + " processedChoices="
              + nextCG.getProcessedNumberOfChoices());
    }
  }

  @Override
  public void choiceGeneratorProcessed(VM vm, ChoiceGenerator<?> processedCG) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(
          this.getClass().getSimpleName() + ".choiceGeneratorProcessed() - cg=" + processedCG + " processedChoices="
              + processedCG.getProcessedNumberOfChoices());
    }
  }

  @Override
  public void threadScheduled(VM vm, ThreadInfo scheduledThread) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".threadScheduled()");
    }
    inspState.notifyListenerMethodCall(ListenerMethod.LM_THREAD_SCHEDULED, vm);
    bpMgr.checkBreakpoints(inspState);
  }

  @Override
  public void propertyViolated (Search search) {
    if (DEBUG) {
      inspector.getDebugPrintStream().println(this.getClass().getSimpleName() + ".propertyViolated()");
    }
    inspState.stateChanged(search, ListenerMethod.LM_PROPERTY_VIOLATED);

    // Simulate behavior of the JPF with original settings
    if (!searchMultipleError) {
      inspector.getStopHolder().terminateAfterResume();
    }

    bpMgr.checkBreakpoints(inspState);
    cmdMgr.tryStop(inspState);

    super.propertyViolated(search);
  }

}
