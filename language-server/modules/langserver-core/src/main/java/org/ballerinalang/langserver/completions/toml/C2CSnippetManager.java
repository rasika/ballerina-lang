/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballerinalang.langserver.completions.toml;

import io.ballerina.toml.validator.schema.Schema;
import org.apache.commons.io.IOUtils;
import org.ballerinalang.langserver.toml.TomlSyntaxTreeUtil;
import org.eclipse.lsp4j.CompletionItem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Maintains all the snippets for c2c.
 *
 * @since 2.0.0
 */
public class C2CSnippetManager {

    private static volatile C2CSnippetManager snippetManager = null;
    private final Map<String, Map<String, CompletionItem>> completions;

    private C2CSnippetManager() {
        Schema c2cRootSchema = Schema.from(getValidationSchema());
        C2CSchemaVisitor visitor = new C2CSchemaVisitor();
        c2cRootSchema.accept(visitor);
        this.completions = removeRedundantCompletions(visitor.getAllCompletionSnippets());
    }

    public Map<String, Map<String, CompletionItem>> getCompletions() {
        return Collections.unmodifiableMap(completions);
    }

    public static synchronized C2CSnippetManager getInstance() {
        if (snippetManager == null) {
            snippetManager = new C2CSnippetManager();
        }
        return snippetManager;
    }

    private Map<String, Map<String, CompletionItem>> removeRedundantCompletions(Map<String, Map<String,
            CompletionItem>> completions) {
        Map<String, Map<String, CompletionItem>> optimizedCompletions = new HashMap<>();
        for (Map.Entry<String, Map<String, CompletionItem>> entry : completions.entrySet()) {
            String key = entry.getKey();
            Map<String, CompletionItem> value = entry.getValue();
            if (!isRedundantCompletion(value)) {
                optimizedCompletions.put(key, value);
            }
        }
        return optimizedCompletions;
    }

    private boolean isRedundantCompletion(Map<String, CompletionItem> value) {
        boolean isRedundant = true;
        for (Map.Entry<String, CompletionItem> childEntry : value.entrySet()) {
            CompletionItem completionItem = childEntry.getValue();
            if (!(completionItem.getDetail().equals(TomlSyntaxTreeUtil.TABLE) ||
                    completionItem.getDetail().equals(TomlSyntaxTreeUtil.TABLE_ARRAY))) {
                isRedundant = false;
            }
        }
        return isRedundant;
    }

    private String getValidationSchema() {
        try {
            return IOUtils.resourceToString("c2c-schema.json", StandardCharsets.UTF_8, getClass().getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException("Schema Not found");
        }
    }
}

