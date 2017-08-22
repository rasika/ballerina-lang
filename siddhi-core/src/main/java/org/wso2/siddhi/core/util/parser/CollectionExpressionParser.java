/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.siddhi.core.util.parser;

import org.wso2.siddhi.core.config.SiddhiAppContext;
import org.wso2.siddhi.core.event.stream.MetaStreamEvent;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.executor.ConstantExpressionExecutor;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.VariableExpressionExecutor;
import org.wso2.siddhi.core.table.Table;
import org.wso2.siddhi.core.table.holder.IndexedEventHolder;
import org.wso2.siddhi.core.util.SiddhiConstants;
import org.wso2.siddhi.core.util.collection.executor.AndMultiPrimaryKeyCollectionExecutor;
import org.wso2.siddhi.core.util.collection.executor.AnyAndCollectionExecutor;
import org.wso2.siddhi.core.util.collection.executor.CollectionExecutor;
import org.wso2.siddhi.core.util.collection.executor.CompareCollectionExecutor;
import org.wso2.siddhi.core.util.collection.executor.CompareExhaustiveAndCollectionExecutor;
import org.wso2.siddhi.core.util.collection.executor.ExhaustiveCollectionExecutor;
import org.wso2.siddhi.core.util.collection.executor.NonAndCollectionExecutor;
import org.wso2.siddhi.core.util.collection.executor.NonCollectionExecutor;
import org.wso2.siddhi.core.util.collection.executor.NotCollectionExecutor;
import org.wso2.siddhi.core.util.collection.executor.OrCollectionExecutor;
import org.wso2.siddhi.core.util.collection.expression.AndCollectionExpression;
import org.wso2.siddhi.core.util.collection.expression.AndMutiPrimaryKeyCollectionExpression;
import org.wso2.siddhi.core.util.collection.expression.AttributeCollectionExpression;
import org.wso2.siddhi.core.util.collection.expression.BasicCollectionExpression;
import org.wso2.siddhi.core.util.collection.expression.CollectionExpression;
import org.wso2.siddhi.core.util.collection.expression.CompareCollectionExpression;
import org.wso2.siddhi.core.util.collection.expression.NotCollectionExpression;
import org.wso2.siddhi.core.util.collection.expression.NullCollectionExpression;
import org.wso2.siddhi.core.util.collection.expression.OrCollectionExpression;
import org.wso2.siddhi.core.util.collection.operator.MatchingMetaInfoHolder;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.expression.AttributeFunction;
import org.wso2.siddhi.query.api.expression.Expression;
import org.wso2.siddhi.query.api.expression.Variable;
import org.wso2.siddhi.query.api.expression.condition.And;
import org.wso2.siddhi.query.api.expression.condition.Compare;
import org.wso2.siddhi.query.api.expression.condition.In;
import org.wso2.siddhi.query.api.expression.condition.IsNull;
import org.wso2.siddhi.query.api.expression.condition.Not;
import org.wso2.siddhi.query.api.expression.condition.Or;
import org.wso2.siddhi.query.api.expression.constant.Constant;
import org.wso2.siddhi.query.api.expression.math.Add;
import org.wso2.siddhi.query.api.expression.math.Divide;
import org.wso2.siddhi.query.api.expression.math.Mod;
import org.wso2.siddhi.query.api.expression.math.Multiply;
import org.wso2.siddhi.query.api.expression.math.Subtract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.wso2.siddhi.core.util.collection.expression.CollectionExpression.CollectionScope.EXHAUSTIVE;
import static org.wso2.siddhi.core.util.collection.expression.CollectionExpression.CollectionScope.INDEXED_ATTRIBUTE;
import static org.wso2.siddhi.core.util.collection.expression.CollectionExpression.CollectionScope.INDEXED_RESULT_SET;
import static org.wso2.siddhi.core.util.collection.expression.CollectionExpression.CollectionScope.MULTI_PRIMARY_KEY_ATTRIBUTE;
import static org.wso2.siddhi.core.util.collection.expression.CollectionExpression.CollectionScope.NON;
import static org.wso2.siddhi.core.util.collection.expression.CollectionExpression.CollectionScope.OPTIMISED_INDEXED_RESULT_SET;
import static org.wso2.siddhi.core.util.collection.expression.CollectionExpression.CollectionScope.OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET;

/**
 * Class to parse Expressions and create Expression executors.
 */
public class CollectionExpressionParser {

    /**
     * Parse the given expression and create the appropriate Executor by recursively traversing the expression
     *
     * @param expression             Expression to be parsed
     * @param matchingMetaInfoHolder matchingMetaInfoHolder
     * @param indexedEventHolder     indexed event holder
     * @return ExpressionExecutor
     */
    public static CollectionExpression parseCollectionExpression(Expression expression, MatchingMetaInfoHolder
            matchingMetaInfoHolder, IndexedEventHolder indexedEventHolder) {
        if (expression instanceof And) {

            CollectionExpression leftCollectionExpression = parseCollectionExpression(((And) expression)
                    .getLeftExpression(), matchingMetaInfoHolder, indexedEventHolder);
            CollectionExpression rightCollectionExpression = parseCollectionExpression(((And) expression)
                    .getRightExpression(), matchingMetaInfoHolder, indexedEventHolder);

            if (leftCollectionExpression.getCollectionScope() == NON &&
                    rightCollectionExpression.getCollectionScope() == NON) {
                return new BasicCollectionExpression(expression, NON);
            } else if (leftCollectionExpression.getCollectionScope() == EXHAUSTIVE &&
                    rightCollectionExpression.getCollectionScope() == EXHAUSTIVE) {
                return new BasicCollectionExpression(expression, EXHAUSTIVE);
            } else if ((leftCollectionExpression.getCollectionScope() == MULTI_PRIMARY_KEY_ATTRIBUTE
                    || leftCollectionExpression.getCollectionScope() == OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET)
                    && (rightCollectionExpression.getCollectionScope() == MULTI_PRIMARY_KEY_ATTRIBUTE
                    || rightCollectionExpression.getCollectionScope() == OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET)) {
                Set<String> primaryKeys = new HashSet<>();
                primaryKeys.addAll(leftCollectionExpression.getMultiPrimaryKeys());
                primaryKeys.addAll(rightCollectionExpression.getMultiPrimaryKeys());
                if (primaryKeys.size() == indexedEventHolder.getPrimaryKeyReferenceHolders().length) {
                    return new AndMutiPrimaryKeyCollectionExpression(expression,
                            OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET,
                            leftCollectionExpression, rightCollectionExpression);
                } else {
                    return new AndCollectionExpression(expression,
                            OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET,
                            leftCollectionExpression, rightCollectionExpression);
                }
            } else {
                return new AndCollectionExpression(expression, OPTIMISED_INDEXED_RESULT_SET,
                        leftCollectionExpression, rightCollectionExpression);
            }
        } else if (expression instanceof Or) {
            CollectionExpression leftCollectionExpression = parseCollectionExpression(((Or) expression)
                    .getLeftExpression(), matchingMetaInfoHolder, indexedEventHolder);
            CollectionExpression rightCollectionExpression = parseCollectionExpression(((Or) expression)
                    .getRightExpression(), matchingMetaInfoHolder, indexedEventHolder);

            if (leftCollectionExpression.getCollectionScope() == NON &&
                    rightCollectionExpression.getCollectionScope() == NON) {
                return new BasicCollectionExpression(expression, NON);
            } else if (leftCollectionExpression.getCollectionScope() == EXHAUSTIVE
                    || leftCollectionExpression.getCollectionScope() == MULTI_PRIMARY_KEY_ATTRIBUTE
                    || leftCollectionExpression.getCollectionScope() == OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET
                    || rightCollectionExpression.getCollectionScope() == EXHAUSTIVE
                    || rightCollectionExpression.getCollectionScope() == MULTI_PRIMARY_KEY_ATTRIBUTE
                    || rightCollectionExpression.getCollectionScope() == OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET) {
                return new BasicCollectionExpression(expression, EXHAUSTIVE);
            } else {
                return new OrCollectionExpression(expression,
                        OPTIMISED_INDEXED_RESULT_SET,
                        leftCollectionExpression, rightCollectionExpression);
            }
        } else if (expression instanceof Not) {
            CollectionExpression notCollectionExpression = parseCollectionExpression(((Not) expression).getExpression
                    (), matchingMetaInfoHolder, indexedEventHolder);

            if (notCollectionExpression.getCollectionScope() == EXHAUSTIVE ||
                    notCollectionExpression.getCollectionScope() == MULTI_PRIMARY_KEY_ATTRIBUTE ||
                    notCollectionExpression.getCollectionScope() == OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET) {
                return new BasicCollectionExpression(expression, EXHAUSTIVE);
            } else if (notCollectionExpression.getCollectionScope() == NON) {
                return new BasicCollectionExpression(expression, NON);
            } else if (notCollectionExpression.getCollectionScope() == INDEXED_ATTRIBUTE) {
                return new NotCollectionExpression(expression, INDEXED_RESULT_SET, notCollectionExpression);
            } else {
                return new NotCollectionExpression(expression, OPTIMISED_INDEXED_RESULT_SET, notCollectionExpression);
            }
        } else if (expression instanceof Compare) {
//            if (((Compare) expression).getOperator() == Compare.Operator.EQUAL) {

            CollectionExpression leftCollectionExpression = parseCollectionExpression(((Compare) expression)
                    .getLeftExpression(), matchingMetaInfoHolder, indexedEventHolder);
            CollectionExpression rightCollectionExpression = parseCollectionExpression(((Compare) expression)
                    .getRightExpression(), matchingMetaInfoHolder, indexedEventHolder);

            if (leftCollectionExpression.getCollectionScope() == NON &&
                    rightCollectionExpression.getCollectionScope() == NON) {
                //comparing two stream attributes with O(1) time complexity
                return new BasicCollectionExpression(expression, NON);
            } else if ((leftCollectionExpression.getCollectionScope() == INDEXED_ATTRIBUTE ||
                    leftCollectionExpression.getCollectionScope() == MULTI_PRIMARY_KEY_ATTRIBUTE) &&
                    rightCollectionExpression.getCollectionScope() == NON) {
                if (indexedEventHolder.isAttributeIndexed(
                        ((AttributeCollectionExpression) leftCollectionExpression).getAttribute())) {
                    //comparing indexed table attribute with stream attributes
                    return new CompareCollectionExpression((Compare) expression, INDEXED_RESULT_SET,
                            leftCollectionExpression, ((Compare) expression).getOperator(), rightCollectionExpression);
                } else if (indexedEventHolder.isMultiPrimaryKey(
                        ((AttributeCollectionExpression) leftCollectionExpression).getAttribute())) {
                    //comparing indexed table attribute with stream attributes
                    return new CompareCollectionExpression((Compare) expression, OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET,
                            leftCollectionExpression, ((Compare) expression).getOperator(), rightCollectionExpression);
                } else {
                    //comparing non indexed table attribute with stream attributes
                    return new BasicCollectionExpression(expression, EXHAUSTIVE);
                }
            } else if (leftCollectionExpression.getCollectionScope() == NON
                    && (rightCollectionExpression.getCollectionScope() == INDEXED_ATTRIBUTE ||
                    rightCollectionExpression.getCollectionScope() == MULTI_PRIMARY_KEY_ATTRIBUTE)) {
                Compare.Operator operator = ((Compare) expression).getOperator();
                //moving let to right
                switch (operator) {
                    case LESS_THAN:
                        operator = Compare.Operator.GREATER_THAN;
                        break;
                    case GREATER_THAN:
                        operator = Compare.Operator.LESS_THAN;
                        break;
                    case LESS_THAN_EQUAL:
                        operator = Compare.Operator.GREATER_THAN_EQUAL;
                        break;
                    case GREATER_THAN_EQUAL:
                        operator = Compare.Operator.LESS_THAN_EQUAL;
                        break;
                    case EQUAL:
                        break;
                    case NOT_EQUAL:
                        break;
                }
                if (indexedEventHolder.isAttributeIndexed(
                        ((AttributeCollectionExpression) rightCollectionExpression).getAttribute())) {
                    //comparing indexed table attribute with stream attributes
                    return new CompareCollectionExpression((Compare) expression,
                            INDEXED_RESULT_SET, rightCollectionExpression,
                            operator, leftCollectionExpression);
                } else if (indexedEventHolder.isMultiPrimaryKey(
                        ((AttributeCollectionExpression) rightCollectionExpression).getAttribute())) {
                    //comparing indexed table attribute with stream attributes
                    return new CompareCollectionExpression((Compare) expression,
                            OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET, rightCollectionExpression,
                            operator, leftCollectionExpression);
                } else {
                    //comparing non indexed table attribute with stream attributes
                    return new BasicCollectionExpression(expression, EXHAUSTIVE);
                }
            } else {
                //comparing non indexed table with stream attributes or another table attribute
                return new BasicCollectionExpression(expression, EXHAUSTIVE);
            }
        } else if (expression instanceof Constant) {
            return new BasicCollectionExpression(expression, NON);
        } else if (expression instanceof Variable) {
            if (isCollectionVariable(matchingMetaInfoHolder, (Variable) expression)) {
                if (indexedEventHolder.isAttributeIndexed(((Variable) expression).getAttributeName())) {
                    return new AttributeCollectionExpression(expression, ((Variable) expression).getAttributeName(),
                            INDEXED_ATTRIBUTE);
                } else if (indexedEventHolder.isMultiPrimaryKey(((Variable) expression).getAttributeName())) {
                    return new AttributeCollectionExpression(expression, ((Variable) expression).getAttributeName(),
                            MULTI_PRIMARY_KEY_ATTRIBUTE);
                } else {
                    return new BasicCollectionExpression(expression, EXHAUSTIVE);
                }
            } else {
                return new BasicCollectionExpression(expression, NON);
            }
        } else if (expression instanceof Multiply) {
            CollectionExpression left = parseCollectionExpression(((Multiply) expression).getLeftValue(),
                    matchingMetaInfoHolder, indexedEventHolder);
            CollectionExpression right = parseCollectionExpression(((Multiply) expression).getRightValue(),
                    matchingMetaInfoHolder, indexedEventHolder);
            if (left.getCollectionScope() == NON && right.getCollectionScope() == NON) {
                return new BasicCollectionExpression(expression, NON);
            } else {
                return new BasicCollectionExpression(expression, EXHAUSTIVE);
            }
        } else if (expression instanceof Add) {
            CollectionExpression left = parseCollectionExpression(((Add) expression).getLeftValue(),
                    matchingMetaInfoHolder, indexedEventHolder);
            CollectionExpression right = parseCollectionExpression(((Add) expression).getRightValue(),
                    matchingMetaInfoHolder, indexedEventHolder);
            if (left.getCollectionScope() == NON && right.getCollectionScope() == NON) {
                return new BasicCollectionExpression(expression, NON);
            } else {
                return new BasicCollectionExpression(expression, EXHAUSTIVE);
            }
        } else if (expression instanceof Subtract) {
            CollectionExpression left = parseCollectionExpression(((Subtract) expression).getLeftValue(),
                    matchingMetaInfoHolder, indexedEventHolder);
            CollectionExpression right = parseCollectionExpression(((Subtract) expression).getRightValue(),
                    matchingMetaInfoHolder, indexedEventHolder);
            if (left.getCollectionScope() == NON && right.getCollectionScope() == NON) {
                return new BasicCollectionExpression(expression, NON);
            } else {
                return new BasicCollectionExpression(expression, EXHAUSTIVE);
            }
        } else if (expression instanceof Mod) {
            CollectionExpression left = parseCollectionExpression(((Mod) expression).getLeftValue(),
                    matchingMetaInfoHolder, indexedEventHolder);
            CollectionExpression right = parseCollectionExpression(((Mod) expression).getRightValue(),
                    matchingMetaInfoHolder, indexedEventHolder);
            if (left.getCollectionScope() == NON && right.getCollectionScope() == NON) {
                return new BasicCollectionExpression(expression, NON);
            } else {
                return new BasicCollectionExpression(expression, EXHAUSTIVE);
            }
        } else if (expression instanceof Divide) {
            CollectionExpression left = parseCollectionExpression(((Divide) expression).getLeftValue(),
                    matchingMetaInfoHolder, indexedEventHolder);
            CollectionExpression right = parseCollectionExpression(((Divide) expression).getRightValue(),
                    matchingMetaInfoHolder, indexedEventHolder);
            if (left.getCollectionScope() == NON && right.getCollectionScope() == NON) {
                return new BasicCollectionExpression(expression, NON);
            } else {
                return new BasicCollectionExpression(expression, EXHAUSTIVE);
            }
        } else if (expression instanceof AttributeFunction) {
            Expression[] innerExpressions = ((AttributeFunction) expression).getParameters();
            for (Expression aExpression : innerExpressions) {
                CollectionExpression aCollectionExpression = parseCollectionExpression(aExpression,
                        matchingMetaInfoHolder, indexedEventHolder);
                if (aCollectionExpression.getCollectionScope() != NON) {
                    return new BasicCollectionExpression(expression, EXHAUSTIVE);
                }
            }
            return new BasicCollectionExpression(expression, NON);
        } else if (expression instanceof In) {
            CollectionExpression inCollectionExpression = parseCollectionExpression(((In) expression).getExpression()
                    , matchingMetaInfoHolder, indexedEventHolder);
            if (inCollectionExpression.getCollectionScope() != NON) {
                return new BasicCollectionExpression(expression, EXHAUSTIVE);
            }

            return new BasicCollectionExpression(expression, NON);
        } else if (expression instanceof IsNull) {

            CollectionExpression nullCollectionExpression = parseCollectionExpression(((IsNull) expression)
                            .getExpression(),
                    matchingMetaInfoHolder, indexedEventHolder);

            if (nullCollectionExpression.getCollectionScope() == NON) {
                return new BasicCollectionExpression(expression, NON);
            } else if (nullCollectionExpression.getCollectionScope() == INDEXED_ATTRIBUTE) {
                return new NullCollectionExpression(expression, INDEXED_RESULT_SET,
                        ((AttributeCollectionExpression) nullCollectionExpression).getAttribute());
            } else {
                return new BasicCollectionExpression(expression, EXHAUSTIVE);
            }
        }
        throw new UnsupportedOperationException(expression.toString() + " not supported!");
    }


    private static boolean isCollectionVariable(MatchingMetaInfoHolder matchingMetaInfoHolder, Variable variable) {
        if (variable.getStreamId() != null) {
            MetaStreamEvent collectionStreamEvent = matchingMetaInfoHolder.getMetaStateEvent().getMetaStreamEvent
                    (matchingMetaInfoHolder.getStoreEventIndex());
            if (collectionStreamEvent != null) {
                if ((collectionStreamEvent.getInputReferenceId() != null && variable.getStreamId().equals
                        (collectionStreamEvent.getInputReferenceId())) ||
                        (collectionStreamEvent.getLastInputDefinition().getId().equals(variable.getStreamId()))) {
//                    if (Arrays.asList(collectionStreamEvent.getLastInputDefinition().getAttributeNameArray())
// .contains(indexAttribute)) {
                    return true;
//                    }
                }
            }
        }
        return false;
    }

    public static CollectionExecutor buildCollectionExecutor(CollectionExpression collectionExpression,
                                                             MatchingMetaInfoHolder matchingMetaInfoHolder,
                                                             List<VariableExpressionExecutor>
                                                                     variableExpressionExecutors,
                                                             Map<String, Table> tableMap,
                                                             SiddhiAppContext siddhiAppContext,
                                                             boolean isFirst, String queryName) {
        if (collectionExpression instanceof AttributeCollectionExpression) {
            ExpressionExecutor expressionExecutor = null;
            if (isFirst) {
                expressionExecutor = ExpressionParser.parseExpression(collectionExpression.getExpression(),
                        matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder.getCurrentState(),
                        tableMap, variableExpressionExecutors, siddhiAppContext, false, 0, queryName);
            }
            return new CompareCollectionExecutor(expressionExecutor, matchingMetaInfoHolder.getStoreEventIndex(), (
                    (AttributeCollectionExpression) collectionExpression).getAttribute(), Compare.Operator.EQUAL, new
                    ConstantExpressionExecutor(true, Attribute.Type.BOOL));
        } else if (collectionExpression instanceof CompareCollectionExpression) {
            ExpressionExecutor valueExpressionExecutor = ExpressionParser.parseExpression(((CompareCollectionExpression) collectionExpression).getValueCollectionExpression().getExpression(),
                    matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder.getCurrentState(), tableMap, variableExpressionExecutors, siddhiAppContext, false, 0, queryName);
            AttributeCollectionExpression attributeCollectionExpression = ((AttributeCollectionExpression) ((CompareCollectionExpression) collectionExpression).getAttributeCollectionExpression());
            ExpressionExecutor expressionExecutor = null;
            if (isFirst) {
                expressionExecutor = ExpressionParser.parseExpression(collectionExpression.getExpression(),
                        matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder.getCurrentState(),
                        tableMap, variableExpressionExecutors, siddhiAppContext, false, 0, queryName);
            }
            return new CompareCollectionExecutor(expressionExecutor, matchingMetaInfoHolder.getStoreEventIndex(),
                    attributeCollectionExpression.getAttribute(), ((CompareCollectionExpression)
                    collectionExpression).getOperator(), valueExpressionExecutor);
        } else if (collectionExpression instanceof NullCollectionExpression) {
            ExpressionExecutor expressionExecutor = null;
            if (isFirst) {
                expressionExecutor = ExpressionParser.parseExpression(collectionExpression.getExpression(),
                        matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder.getCurrentState(),
                        tableMap, variableExpressionExecutors, siddhiAppContext, false, 0, queryName);
            }
            return new CompareCollectionExecutor(expressionExecutor, matchingMetaInfoHolder.getStoreEventIndex(), (
                    (NullCollectionExpression) collectionExpression).getAttribute(),
                    Compare.Operator.EQUAL, new ConstantExpressionExecutor(null, Attribute.Type.OBJECT));
        } else if (collectionExpression instanceof AndMutiPrimaryKeyCollectionExpression) {

            Map<String, ExpressionExecutor> multiPrimaryKeyExpressionExecutors =
                    buildMultiPrimaryKeyExpressionExecutors(collectionExpression,
                            matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                            siddhiAppContext, isFirst, queryName);
            List<Attribute> attributes = matchingMetaInfoHolder.getStoreDefinition().getAttributeList();
            StringBuilder compositePrimaryKey = new StringBuilder();
            List<ExpressionExecutor> sortedExecutors = new ArrayList<ExpressionExecutor>();
            for (Attribute attribute : attributes) {
                ExpressionExecutor expressionExecutor = multiPrimaryKeyExpressionExecutors.get(attribute.getName());
                if (expressionExecutor != null) {
                    sortedExecutors.add(expressionExecutor);
                    compositePrimaryKey.append(attribute.getName()).append(SiddhiConstants.KEY_DELIMITER);
                }

            }
            return new AndMultiPrimaryKeyCollectionExecutor(compositePrimaryKey.toString(), sortedExecutors);

//
        } else if (collectionExpression instanceof AndCollectionExpression) {

            CollectionExpression leftCollectionExpression = ((AndCollectionExpression) collectionExpression)
                    .getLeftCollectionExpression();
            CollectionExpression rightCollectionExpression = ((AndCollectionExpression) collectionExpression)
                    .getRightCollectionExpression();

            ExpressionExecutor expressionExecutor = null;
            CollectionExecutor aCollectionExecutor = null;
            ExhaustiveCollectionExecutor exhaustiveCollectionExecutor = null;
            CollectionExecutor leftCollectionExecutor;
            CollectionExecutor rightCollectionExecutor;
            switch (leftCollectionExpression.getCollectionScope()) {
                case NON:
                    switch (rightCollectionExpression.getCollectionScope()) {

                        case NON:
                            expressionExecutor = ExpressionParser.parseExpression(collectionExpression.getExpression(),
                                    matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder
                                            .getCurrentState(), tableMap, variableExpressionExecutors,
                                    siddhiAppContext, false, 0, queryName);
                            return new NonCollectionExecutor(expressionExecutor);
                        case INDEXED_ATTRIBUTE:
                        case INDEXED_RESULT_SET:
                        case OPTIMISED_INDEXED_RESULT_SET:
                        case MULTI_PRIMARY_KEY_ATTRIBUTE:
                        case OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET:
                        case EXHAUSTIVE:
                            expressionExecutor = ExpressionParser.parseExpression(leftCollectionExpression
                                            .getExpression(),
                                    matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder
                                            .getCurrentState(), tableMap, variableExpressionExecutors,
                                    siddhiAppContext, false, 0, queryName);
                            aCollectionExecutor = buildCollectionExecutor(rightCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, isFirst, queryName);
                            return new NonAndCollectionExecutor(expressionExecutor, aCollectionExecutor,
                                    rightCollectionExpression.getCollectionScope());
                    }
                    break;
                case INDEXED_ATTRIBUTE:
                    switch (rightCollectionExpression.getCollectionScope()) {

                        case NON:
                            expressionExecutor = ExpressionParser.parseExpression(rightCollectionExpression
                                            .getExpression(),
                                    matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder
                                            .getCurrentState(), tableMap, variableExpressionExecutors,
                                    siddhiAppContext, false, 0, queryName);
                            aCollectionExecutor = buildCollectionExecutor(leftCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, isFirst, queryName);
                            return new NonAndCollectionExecutor(expressionExecutor, aCollectionExecutor,
                                    rightCollectionExpression.getCollectionScope());
                        case INDEXED_ATTRIBUTE:
                        case INDEXED_RESULT_SET:
                        case OPTIMISED_INDEXED_RESULT_SET:
                            exhaustiveCollectionExecutor = new ExhaustiveCollectionExecutor(ExpressionParser
                                    .parseExpression(collectionExpression.getExpression(),
                                            matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder
                                                    .getCurrentState(), tableMap, variableExpressionExecutors,
                                            siddhiAppContext, false, 0, queryName), matchingMetaInfoHolder
                                    .getStoreEventIndex());
                            leftCollectionExecutor = buildCollectionExecutor(leftCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, false, queryName);
                            rightCollectionExecutor = buildCollectionExecutor(rightCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, false, queryName);
                            return new AnyAndCollectionExecutor(leftCollectionExecutor, rightCollectionExecutor,
                                    exhaustiveCollectionExecutor);
                        case MULTI_PRIMARY_KEY_ATTRIBUTE:
                        case OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET:
                        case EXHAUSTIVE:
                            leftCollectionExecutor = buildCollectionExecutor(leftCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, isFirst, queryName);
                            if (isFirst || leftCollectionExecutor.getDefaultCost() == CollectionExecutor.Cost
                                    .SINGLE_RETURN_INDEX_MATCHING) {
                                exhaustiveCollectionExecutor = new ExhaustiveCollectionExecutor(ExpressionParser
                                        .parseExpression(collectionExpression.getExpression(),
                                                matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder
                                                        .getCurrentState(), tableMap,
                                                variableExpressionExecutors, siddhiAppContext, false, 0,
                                                queryName), matchingMetaInfoHolder.getStoreEventIndex());
                            }
                            return new CompareExhaustiveAndCollectionExecutor(leftCollectionExecutor,
                                    exhaustiveCollectionExecutor);
                    }
                    break;
                case INDEXED_RESULT_SET:
                case OPTIMISED_INDEXED_RESULT_SET:
                    switch (rightCollectionExpression.getCollectionScope()) {

                        case NON:
                            expressionExecutor = ExpressionParser.parseExpression(rightCollectionExpression
                                            .getExpression(),
                                    matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder
                                            .getCurrentState(), tableMap, variableExpressionExecutors,
                                    siddhiAppContext, false, 0, queryName);
                            aCollectionExecutor = buildCollectionExecutor(leftCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, isFirst, queryName);
                            return new NonAndCollectionExecutor(expressionExecutor, aCollectionExecutor,
                                    rightCollectionExpression.getCollectionScope());

                        case INDEXED_ATTRIBUTE:
                            exhaustiveCollectionExecutor = new ExhaustiveCollectionExecutor(ExpressionParser
                                    .parseExpression(collectionExpression.getExpression(),
                                            matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder
                                                    .getCurrentState(), tableMap, variableExpressionExecutors,
                                            siddhiAppContext, false, 0, queryName), matchingMetaInfoHolder
                                    .getStoreEventIndex());
                            leftCollectionExecutor = buildCollectionExecutor(leftCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, false, queryName);
                            rightCollectionExecutor = buildCollectionExecutor(rightCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, false, queryName);
                            return new AnyAndCollectionExecutor(rightCollectionExecutor, leftCollectionExecutor,
                                    exhaustiveCollectionExecutor);
                        case INDEXED_RESULT_SET:
                        case OPTIMISED_INDEXED_RESULT_SET:
                            exhaustiveCollectionExecutor = new ExhaustiveCollectionExecutor(ExpressionParser
                                    .parseExpression(collectionExpression.getExpression(),
                                            matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder
                                                    .getCurrentState(), tableMap, variableExpressionExecutors,
                                            siddhiAppContext, false, 0, queryName), matchingMetaInfoHolder
                                    .getStoreEventIndex());
                            leftCollectionExecutor = buildCollectionExecutor(leftCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, false, queryName);
                            rightCollectionExecutor = buildCollectionExecutor(rightCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, false, queryName);
                            return new AnyAndCollectionExecutor(leftCollectionExecutor, rightCollectionExecutor,
                                    exhaustiveCollectionExecutor);
                        case MULTI_PRIMARY_KEY_ATTRIBUTE:
                        case OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET:
                        case EXHAUSTIVE:
                            leftCollectionExecutor = buildCollectionExecutor(leftCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, isFirst, queryName);
                            if (isFirst || leftCollectionExecutor.getDefaultCost() == CollectionExecutor.Cost
                                    .SINGLE_RETURN_INDEX_MATCHING) {
                                exhaustiveCollectionExecutor = new ExhaustiveCollectionExecutor(ExpressionParser
                                        .parseExpression(collectionExpression.getExpression(),
                                                matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder
                                                        .getCurrentState(), tableMap,
                                                variableExpressionExecutors, siddhiAppContext, false, 0,
                                                queryName), matchingMetaInfoHolder.getStoreEventIndex());
                            }
                            return new CompareExhaustiveAndCollectionExecutor(leftCollectionExecutor,
                                    exhaustiveCollectionExecutor);
                    }
                    break;
                case MULTI_PRIMARY_KEY_ATTRIBUTE:
                case OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET:
                case EXHAUSTIVE:
                    switch (rightCollectionExpression.getCollectionScope()) {

                        case NON:
                            expressionExecutor = ExpressionParser.parseExpression(rightCollectionExpression
                                            .getExpression(),
                                    matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder
                                            .getCurrentState(), tableMap, variableExpressionExecutors,
                                    siddhiAppContext, false, 0, queryName);
                            aCollectionExecutor = buildCollectionExecutor(leftCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, isFirst, queryName);
                            return new NonAndCollectionExecutor(expressionExecutor, aCollectionExecutor,
                                    rightCollectionExpression.getCollectionScope());

                        case INDEXED_ATTRIBUTE:
                        case INDEXED_RESULT_SET:
                        case OPTIMISED_INDEXED_RESULT_SET:
                            rightCollectionExecutor = buildCollectionExecutor(rightCollectionExpression,
                                    matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                                    siddhiAppContext, isFirst, queryName);
                            if (isFirst || rightCollectionExecutor.getDefaultCost() == CollectionExecutor.Cost
                                    .SINGLE_RETURN_INDEX_MATCHING) {
                                exhaustiveCollectionExecutor = new ExhaustiveCollectionExecutor(ExpressionParser
                                        .parseExpression(collectionExpression.getExpression(),
                                                matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder
                                                        .getCurrentState(), tableMap,
                                                variableExpressionExecutors, siddhiAppContext, false, 0,
                                                queryName), matchingMetaInfoHolder.getStoreEventIndex());
                            }
                            return new CompareExhaustiveAndCollectionExecutor(rightCollectionExecutor,
                                    exhaustiveCollectionExecutor);
                        case MULTI_PRIMARY_KEY_ATTRIBUTE:
                        case OPTIMISED_MULTI_PRIMARY_KEY_RESULT_SET:
                        case EXHAUSTIVE:
                            if (isFirst) {
                                expressionExecutor = ExpressionParser.parseExpression(collectionExpression
                                                .getExpression(),
                                        matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder
                                                .getCurrentState(),
                                        tableMap, variableExpressionExecutors, siddhiAppContext, false, 0,
                                        queryName);
                            }
                            return new ExhaustiveCollectionExecutor(expressionExecutor, matchingMetaInfoHolder
                                    .getStoreEventIndex());
                    }
                    break;
            }
        } else if (collectionExpression instanceof OrCollectionExpression) {
            CollectionExpression leftCollectionExpression = ((OrCollectionExpression) collectionExpression)
                    .getLeftCollectionExpression();
            CollectionExpression rightCollectionExpression = ((OrCollectionExpression) collectionExpression)
                    .getRightCollectionExpression();

            ExpressionExecutor expressionExecutor = null;
            CollectionExecutor aCollectionExecutor = null;
            CollectionExecutor leftCollectionExecutor;
            CollectionExecutor rightCollectionExecutor;
            if (leftCollectionExpression.getCollectionScope() == NON &&
                    rightCollectionExpression.getCollectionScope() == NON) {
                expressionExecutor = ExpressionParser.parseExpression(collectionExpression.getExpression(),
                        matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder.getCurrentState(),
                        tableMap, variableExpressionExecutors, siddhiAppContext, false, 0, queryName);
                return new NonCollectionExecutor(expressionExecutor);
            } else if (leftCollectionExpression.getCollectionScope() == EXHAUSTIVE ||
                    rightCollectionExpression.getCollectionScope() == EXHAUSTIVE) {
                if (isFirst) {
                    expressionExecutor = ExpressionParser.parseExpression(collectionExpression.getExpression(),
                            matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder.getCurrentState(),
                            tableMap, variableExpressionExecutors, siddhiAppContext, false, 0, queryName);
                }
                return new ExhaustiveCollectionExecutor(expressionExecutor,
                        matchingMetaInfoHolder.getStoreEventIndex());
            } else {
                if (isFirst) {
                    aCollectionExecutor = new ExhaustiveCollectionExecutor(ExpressionParser.parseExpression(
                            collectionExpression.getExpression(),
                            matchingMetaInfoHolder.getMetaStateEvent(),
                            matchingMetaInfoHolder.getCurrentState(),
                            tableMap, variableExpressionExecutors, siddhiAppContext, false,
                            0, queryName), matchingMetaInfoHolder.getStoreEventIndex());
                }
                leftCollectionExecutor = buildCollectionExecutor(leftCollectionExpression, matchingMetaInfoHolder,
                        variableExpressionExecutors, tableMap, siddhiAppContext, isFirst, queryName);
                rightCollectionExecutor = buildCollectionExecutor(rightCollectionExpression, matchingMetaInfoHolder,
                        variableExpressionExecutors, tableMap, siddhiAppContext, isFirst, queryName);
                return new OrCollectionExecutor(leftCollectionExecutor, rightCollectionExecutor, aCollectionExecutor);
            }
        } else if (collectionExpression instanceof NotCollectionExpression) {
            ExpressionExecutor expressionExecutor = null;
            switch (collectionExpression.getCollectionScope()) {

                case NON:
                    expressionExecutor = ExpressionParser.parseExpression(collectionExpression.getExpression(),
                            matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder.getCurrentState(),
                            tableMap, variableExpressionExecutors, siddhiAppContext, false, 0, queryName);
                    return new NonCollectionExecutor(expressionExecutor);
                case INDEXED_ATTRIBUTE:
                case INDEXED_RESULT_SET:
                case OPTIMISED_INDEXED_RESULT_SET:
                    ExhaustiveCollectionExecutor exhaustiveCollectionExecutor = null;
                    if (isFirst) {
                        exhaustiveCollectionExecutor = new ExhaustiveCollectionExecutor(
                                ExpressionParser.parseExpression(collectionExpression.getExpression(),
                                        matchingMetaInfoHolder.getMetaStateEvent(),
                                        matchingMetaInfoHolder.getCurrentState(), tableMap, variableExpressionExecutors,
                                        siddhiAppContext, false, 0, queryName),
                                matchingMetaInfoHolder.getStoreEventIndex());
                    }
                    CollectionExecutor notCollectionExecutor = buildCollectionExecutor(((NotCollectionExpression)
                                    collectionExpression).getCollectionExpression(), matchingMetaInfoHolder,
                            variableExpressionExecutors, tableMap, siddhiAppContext, isFirst, queryName);
                    return new NotCollectionExecutor(notCollectionExecutor, exhaustiveCollectionExecutor);

                case EXHAUSTIVE:
                    if (isFirst) {
                        expressionExecutor = ExpressionParser.parseExpression(collectionExpression.getExpression(),
                                matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder.getCurrentState(),
                                tableMap, variableExpressionExecutors, siddhiAppContext, false, 0, queryName);
                    }
                    return new ExhaustiveCollectionExecutor(expressionExecutor, matchingMetaInfoHolder
                            .getStoreEventIndex());
            }
        } else { // Basic
            ExpressionExecutor expressionExecutor = null;

            if (collectionExpression.getCollectionScope() == NON) {
                expressionExecutor = ExpressionParser.parseExpression(collectionExpression.getExpression(),
                        matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder.getCurrentState(),
                        tableMap, variableExpressionExecutors, siddhiAppContext, false, 0, queryName);
                return new NonCollectionExecutor(expressionExecutor);
            } else {// EXHAUSTIVE
                if (isFirst) {
                    expressionExecutor = ExpressionParser.parseExpression(collectionExpression.getExpression(),
                            matchingMetaInfoHolder.getMetaStateEvent(), matchingMetaInfoHolder.getCurrentState(),
                            tableMap, variableExpressionExecutors, siddhiAppContext, false, 0, queryName);
                }
                return new ExhaustiveCollectionExecutor(expressionExecutor,
                        matchingMetaInfoHolder.getStoreEventIndex());
            }
        }
        throw new UnsupportedOperationException(collectionExpression.getClass().getName() + " not supported!");
    }

    private static Map<String, ExpressionExecutor> buildMultiPrimaryKeyExpressionExecutors(
            CollectionExpression collectionExpression, MatchingMetaInfoHolder matchingMetaInfoHolder,
            List<VariableExpressionExecutor> variableExpressionExecutors, Map<String, Table> tableMap,
            SiddhiAppContext siddhiAppContext, boolean isFirst, String queryName) {

        if (collectionExpression instanceof AndMutiPrimaryKeyCollectionExpression) {
            CollectionExpression leftCollectionExpression = ((AndMutiPrimaryKeyCollectionExpression)
                    collectionExpression).getLeftCollectionExpression();
            CollectionExpression rightCollectionExpression = ((AndMutiPrimaryKeyCollectionExpression)
                    collectionExpression).getRightCollectionExpression();
            Map<String, ExpressionExecutor> expressionExecutors = buildMultiPrimaryKeyExpressionExecutors(
                    leftCollectionExpression, matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                    siddhiAppContext, false, queryName);
            expressionExecutors.putAll(buildMultiPrimaryKeyExpressionExecutors(
                    rightCollectionExpression, matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                    siddhiAppContext, false, queryName));
            return expressionExecutors;
        } else if (collectionExpression instanceof AndCollectionExpression) {
            CollectionExpression leftCollectionExpression = ((AndCollectionExpression)
                    collectionExpression).getLeftCollectionExpression();
            CollectionExpression rightCollectionExpression = ((AndCollectionExpression)
                    collectionExpression).getLeftCollectionExpression();
            Map<String, ExpressionExecutor> expressionExecutors = buildMultiPrimaryKeyExpressionExecutors(
                    leftCollectionExpression, matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                    siddhiAppContext, false, queryName);
            expressionExecutors.putAll(buildMultiPrimaryKeyExpressionExecutors(
                    rightCollectionExpression, matchingMetaInfoHolder, variableExpressionExecutors, tableMap,
                    siddhiAppContext, false, queryName));
            return expressionExecutors;
        } else if (collectionExpression instanceof CompareCollectionExpression) {

            if (((CompareCollectionExpression) collectionExpression).getOperator() == Compare.Operator.EQUAL) {
                CollectionExpression attributeCollectionExpression =
                        ((CompareCollectionExpression) collectionExpression).getAttributeCollectionExpression();
                if (attributeCollectionExpression instanceof AttributeCollectionExpression) {
                    String attribue = ((AttributeCollectionExpression) attributeCollectionExpression).getAttribute();
                    CollectionExpression valueCollectionExpression =
                            ((CompareCollectionExpression) collectionExpression).getValueCollectionExpression();
                    ExpressionExecutor valueExpressionExecutor = ExpressionParser.parseExpression(
                            valueCollectionExpression.getExpression(), matchingMetaInfoHolder.getMetaStateEvent(),
                            matchingMetaInfoHolder.getCurrentState(), tableMap, variableExpressionExecutors,
                            siddhiAppContext, false, 0, queryName);
                    Map<String, ExpressionExecutor> expressionExecutors = new HashMap<String, ExpressionExecutor>();
                    expressionExecutors.put(attribue, valueExpressionExecutor);
                    return expressionExecutors;
                } else {
                    throw new SiddhiAppCreationException("Only attribute EQUAL " +
                            "comparision supported for multiple primary key optimization, " +
                            "but found  '" + attributeCollectionExpression.getClass() + "'");
                }
            } else {
                throw new SiddhiAppCreationException("Only '" + Compare.Operator.EQUAL + "' supported for multiple " +
                        "primary key for multiple primary key optimization, but found '" +
                        ((CompareCollectionExpression) collectionExpression).getOperator() + "'");
            }
        } else {//Attribute Collection
            throw new SiddhiAppCreationException("Only 'AND' and '" + Compare.Operator.EQUAL + "' operators are " +
                    "supported for multiple primary key optimization, but found '" +
                    ((CompareCollectionExpression) collectionExpression).getOperator() + "'");
        }

    }

}
