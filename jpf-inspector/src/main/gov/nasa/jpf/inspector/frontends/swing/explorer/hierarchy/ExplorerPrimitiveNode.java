//
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

package gov.nasa.jpf.inspector.frontends.swing.explorer.hierarchy;

import gov.nasa.jpf.inspector.frontends.swing.explorer.Attachment;
import gov.nasa.jpf.inspector.frontends.swing.explorer.ProgramStateTreeModel;

/**
 * Represents a primitive value in the visual Explorer.
 */
public class ExplorerPrimitiveNode extends ExplorerLeafNode {
  private Object value;

  public ExplorerPrimitiveNode(Object value, Attachment attachment, ProgramStateTreeModel model, ExplorerNode parent) {
    super(attachment, model, parent);
    this.value = value;
    this.attachment = attachment;
  }

  @Override
  public String toString() {
    return attachment.getName() + ": " + value.toString();
  }

  @Override
  public void updateFromJpf(ExplorerNode newVersion) {
     this.value = ((ExplorerPrimitiveNode)newVersion).value;
  }

  @Override
  public boolean isRecognizableAs(ExplorerNode oldNode) {
    return oldNode.attachment.equals(this.attachment) && oldNode instanceof ExplorerPrimitiveNode;
  }
}
