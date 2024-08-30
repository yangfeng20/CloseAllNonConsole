package com.maple.close.all.non.console;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.actions.CloseAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * 关闭所有非控制台操作
 *
 * @author yangfeng
 * @since 2024/08/30
 */
public class CloseAllNonConsoleAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        CommandProcessor commandProcessor = CommandProcessor.getInstance();
        commandProcessor.executeCommand(
                project, () -> {
                    final EditorWindow window = e.getData(EditorWindow.DATA_KEY);
                    if (window != null) {
                        final VirtualFile[] files = window.getFiles();
                        for (final VirtualFile file : files) {
                            if (!sqlConsole(file)) {
                                window.closeFile(file);
                            }
                        }
                        return;
                    }
                    FileEditorManagerEx fileEditorManager = FileEditorManagerEx.getInstanceEx(project);
                    VirtualFile selectedFile = fileEditorManager.getSelectedFiles()[0];
                    Collection<VirtualFile> openFiles = fileEditorManager.getSiblings(selectedFile);
                    for (final VirtualFile openFile : openFiles) {
                        fileEditorManager.closeFile(openFile);
                    }
                }, IdeBundle.message("command.close.all.editors"), null
        );
    }


    private boolean sqlConsole(VirtualFile virtualFile) {
        return virtualFile.getName().startsWith("console") && virtualFile.getFileType().toString().contains("SqlFileType");
    }
}
