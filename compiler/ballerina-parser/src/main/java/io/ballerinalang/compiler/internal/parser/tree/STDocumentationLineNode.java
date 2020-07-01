/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package io.ballerinalang.compiler.internal.parser.tree;

import io.ballerinalang.compiler.syntax.tree.DocumentationLineNode;
import io.ballerinalang.compiler.syntax.tree.Node;
import io.ballerinalang.compiler.syntax.tree.NonTerminalNode;
import io.ballerinalang.compiler.syntax.tree.SyntaxKind;

import java.util.Collection;
import java.util.Collections;

/**
 * This is a generated internal syntax tree node.
 *
 * @since 2.0.0
 */
public class STDocumentationLineNode extends STDocumentationNode {
    public final STNode hashToken;
    public final STNode referenceOrDescription;

    STDocumentationLineNode(
            SyntaxKind kind,
            STNode hashToken,
            STNode referenceOrDescription) {
        this(
                kind,
                hashToken,
                referenceOrDescription,
                Collections.emptyList());
    }

    STDocumentationLineNode(
            SyntaxKind kind,
            STNode hashToken,
            STNode referenceOrDescription,
            Collection<STNodeDiagnostic> diagnostics) {
        super(kind, diagnostics);
        this.hashToken = hashToken;
        this.referenceOrDescription = referenceOrDescription;

        addChildren(
                hashToken,
                referenceOrDescription);
    }

    public STNode modifyWith(Collection<STNodeDiagnostic> diagnostics) {
        return new STDocumentationLineNode(
                this.kind,
                this.hashToken,
                this.referenceOrDescription,
                diagnostics);
    }

    public STDocumentationLineNode modify(
            SyntaxKind kind,
            STNode hashToken,
            STNode referenceOrDescription) {
        if (checkForReferenceEquality(
                hashToken,
                referenceOrDescription)) {
            return this;
        }

        return new STDocumentationLineNode(
                kind,
                hashToken,
                referenceOrDescription,
                diagnostics);
    }

    public Node createFacade(int position, NonTerminalNode parent) {
        return new DocumentationLineNode(this, position, parent);
    }

    @Override
    public void accept(STNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T apply(STNodeTransformer<T> transformer) {
        return transformer.transform(this);
    }
}
