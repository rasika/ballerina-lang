/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.ballerinalang.plugins.idea;

import com.intellij.facet.ui.ValidationResult;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.DirectoryProjectGenerator;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class BallerinaProjectGenerator implements DirectoryProjectGenerator {

    @Nls
    @NotNull
    @Override
    public String getName() {
        return BallerinaConstants.BALLERINA;
    }

    @Nullable
    @Override
    public Icon getLogo() {
        return BallerinaIcons.ICON;
    }

    @Override
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @Nullable Object settings,
                                @NotNull Module module) {

    }

    @NotNull
    @Override
    public ValidationResult validate(@NotNull String baseDirPath) {
        return ValidationResult.OK;
    }
}
