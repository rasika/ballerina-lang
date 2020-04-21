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
package io.ballerinalang.compiler.syntax.tree;

import io.ballerinalang.compiler.internal.parser.tree.STNode;

/**
 * This is a generated syntax tree node.
 *
 * @since 1.3.0
 */
public class RecordTypeDescriptorNode extends NonTerminalNode {

    public RecordTypeDescriptorNode(STNode internalNode, int position, NonTerminalNode parent) {
        super(internalNode, position, parent);
    }

    public Token objectKeyword() {
        return childInBucket(0);
    }

    public Token bodyStartDelimiter() {
        return childInBucket(1);
    }

    public NodeList<Node> fields() {
        return new NodeList<>(childInBucket(2));
    }

    public Token bodyEndDelimiter() {
        return childInBucket(3);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T apply(NodeTransformer<T> visitor) {
        return visitor.transform(this);
    }

    @Override
    protected String[] childNames() {
        return new String[]{
                "objectKeyword",
                "bodyStartDelimiter",
                "fields",
                "bodyEndDelimiter"};
    }

    public RecordTypeDescriptorNode modify(
            Token objectKeyword,
            Token bodyStartDelimiter,
            NodeList<Node> fields,
            Token bodyEndDelimiter) {
        if (checkForReferenceEquality(
                objectKeyword,
                bodyStartDelimiter,
                fields.underlyingListNode(),
                bodyEndDelimiter)) {
            return this;
        }

        return NodeFactory.createRecordTypeDescriptorNode(
                objectKeyword,
                bodyStartDelimiter,
                fields,
                bodyEndDelimiter);
    }
}