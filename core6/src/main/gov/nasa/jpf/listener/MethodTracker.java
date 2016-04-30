//
// Copyright (C) 2007 United States Government as represented by the
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
package gov.nasa.jpf.listener;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.INVOKESPECIAL;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.VirtualInvocation;
import gov.nasa.jpf.search.Search;

import java.io.PrintWriter;

/**
 * simple tool to log method invocations
 *
 * at this point, it doesn't do fancy things yet, but gives a more high
 * level idea of what got executed by JPF than the ExecTracker
 */
public class MethodTracker extends ListenerAdapter {

  static final String INDENT = "  ";

  MethodInfo lastMi;
  PrintWriter out;

  public MethodTracker (Config conf, JPF jpf) {
    out = new PrintWriter(System.out, true);
  }

  void logMethodCall(ThreadInfo ti, MethodInfo mi, int stackDepth) {
    out.print(ti.getId());
    out.print(":");

    for (int i=0; i<stackDepth%80; i++) {
      out.print(INDENT);
    }

    if (mi.isMJI()) {
      out.print("native ");
    }

    out.print(mi.getFullName());

    if (ti.isFirstStepInsn()) {
      out.print("...");
    }

    out.println();
  }

  public void executeInstruction (JVM vm) {
    Instruction insn = vm.getLastInstruction();
    MethodInfo mi = insn.getMethodInfo();
    ThreadInfo ti = vm.getLastThreadInfo();

    if (mi != lastMi) {
      logMethodCall(ti, mi, ti.getStackDepth());
      lastMi = mi;

    } else if (insn instanceof InvokeInstruction) {
      MethodInfo callee;

      // that's the only little gist of it - if this is a VirtualInvocation,
      // we have to dig the callee out by ourselves (it's not known
      // before execution)

      if (insn instanceof VirtualInvocation) {
        VirtualInvocation callInsn = (VirtualInvocation)insn;
        int objref = callInsn.getCalleeThis(ti);
        callee = callInsn.getInvokedMethod(ti, objref);

      } else if (insn instanceof INVOKESPECIAL) {
        INVOKESPECIAL callInsn = (INVOKESPECIAL)insn;
        callee = callInsn.getInvokedMethod(ti);

      } else {
        InvokeInstruction callInsn = (InvokeInstruction)insn;
        callee = callInsn.getInvokedMethod(ti);
      }

      if (callee != null) {
        if (callee.isMJI()) {
          logMethodCall(ti, callee, ti.getStackDepth()+1);
        }
      } else {
        out.println("ERROR: unknown callee of: " + insn);
      }
    }
  }

  /*
   * those are not really required, but mark the transition boundaries
   */

  public void stateRestored(Search search) {
    int id = search.getStateId();
    out.println("----------------------------------- [" +
                       search.getDepth() + "] restored: " + id);
  }

  //--- the ones we are interested in
  public void searchStarted(Search search) {
    out.println("----------------------------------- search started");
  }

  public void stateAdvanced(Search search) {
    int id = search.getStateId();

    out.print("----------------------------------- [" +
                     search.getDepth() + "] forward: " + id);
    if (search.isNewState()) {
      out.print(" new");
    } else {
      out.print(" visited");
    }

    if (search.isEndState()) {
      out.print(" end");
    }

    out.println();

    lastMi = null;
  }

  public void stateBacktracked(Search search) {
    int id = search.getStateId();

    lastMi = null;

    out.println("----------------------------------- [" +
                       search.getDepth() + "] backtrack: " + id);
  }

  public void searchFinished(Search search) {
    out.println("----------------------------------- search finished");
  }

}
