/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.langserver.extensions.ballerina.document;

import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NonTerminalNode;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.tools.text.LinePosition;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextRange;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.util.Optional;

/**
 * This is the BallerinaSyntaxTreeByRange class for related utils in retrieving the ST for a given selection.
 */

public class BallerinaSyntaxTreeByRangeUtil {
    public static Node getNode(Range range, SyntaxTree syntaxTree) {
        TextDocument textDocument = syntaxTree.textDocument();
        Position rangeStart = range.getStart();
        Position rangeEnd = range.getEnd();
        int start = textDocument.textPositionFrom(LinePosition.from(rangeStart.getLine(), rangeStart.getCharacter()));
        int end = textDocument.textPositionFrom(LinePosition.from(rangeEnd.getLine(), rangeEnd.getCharacter()));
        return findNode(syntaxTree.rootNode(), TextRange.from(start, end - start));
    }

    private static Node findNode(ModulePartNode node, TextRange textRange) {
        TextRange textRangeWithMinutiae = node.textRangeWithMinutiae();
        if (textRangeWithMinutiae.startOffset() > textRange.startOffset() ||
                textRangeWithMinutiae.endOffset() < textRange.endOffset()) {
            throw new IllegalStateException("Invalid Text Range for: " + textRange.toString());
        }

        Node foundNode = null;
        Optional<Node> temp = Optional.of(node);
        while (temp.isPresent()) {
            foundNode = temp.get();

            if (temp.get() instanceof Token) {
                break;
            }
            temp = findChildNode((NonTerminalNode) temp.get(), textRange);
        }

        return foundNode;
    }

    private static Optional<Node> findChildNode(NonTerminalNode node, TextRange textRange) {
        int offset = node.textRangeWithMinutiae().startOffset();

        for (Node internalChildNode: node.children()) {
            if (internalChildNode == null) {
                continue;
            }

            int offsetWithMinutiae = offset + internalChildNode.textRangeWithMinutiae().length();
            if (textRange.startOffset() >= offset && textRange.endOffset() <= offsetWithMinutiae) {
                return Optional.ofNullable(internalChildNode);
            } else if (textRange.startOffset() <= offset && textRange.endOffset() >= offsetWithMinutiae) {
                return Optional.empty();
            }

            offset += internalChildNode.textRangeWithMinutiae().length();
        }

        return Optional.empty();
    }

}
