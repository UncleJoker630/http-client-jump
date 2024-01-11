package com.joker.httpclientjump.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.joker.httpclientjump.request.Request;
import com.joker.httpclientjump.util.JavaUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author OuBa
 * @date 2023-10-24
 */

public class NewRequestAction extends AnAction {


    private PsiFile httpFile;
    private final PsiMethod method;

    public NewRequestAction(PsiMethod method) {
        this.method = method;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = method.getProject();
        httpFile = findAssociatedHttpFileForController(method);
        if (httpFile == null) {
            //未匹配到文件，创建一个NavigationGutterIconBuilder跳转icon
            String s = Messages.showInputDialog(project, "Please enter the parent path of the http file", "Create Http File", Messages.getQuestionIcon());
            if (StringUtils.isBlank(s)) {
                return;
            }
            String name = Objects.requireNonNull(method.getContainingClass()).getName();
            createHttpFile(project, name, s);
        }
        Integer lineOffset = addHttpTestMethod(project);
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, httpFile.getVirtualFile(), lineOffset, 0);
        FileEditorManager.getInstance(project).openTextEditor(descriptor, true);

    }

    private void createHttpFile(Project project, String fileName, String filePath) {
        String basePath = project.getBasePath();
        VirtualFile root = LocalFileSystem.getInstance().findFileByPath(Objects.requireNonNull(basePath));
        // Check if the http directory already exists
        String[] split = filePath.split("/");
        httpFile = WriteCommandAction.runWriteCommandAction(project, (Computable<PsiFile>) () -> {
            try {
                VirtualFile parent = root;
                for (String s : split) {
                    VirtualFile child = Objects.requireNonNull(parent).findChild(s);
                    if (child == null) {
                        child = parent.createChildDirectory(null, s);
                    }
                    parent = child;
                }
                VirtualFile childData = Objects.requireNonNull(parent).createChildData(null, fileName + ".http");
                PsiManager psiManager = PsiManager.getInstance(project);
                httpFile = psiManager.findFile(childData);
                return httpFile;
            } catch (Exception e) {
            }
            return null;
        });
    }

    public Integer addHttpTestMethod(Project project) {

        Request request = JavaUtil.getRestPathAbsolute(method);
        String methodName = Objects.requireNonNull(request).getPathAbsolute().iterator().next();
        int offset = findTestMethodInHttpFile(methodName);
        if (offset != -1) {
            return offset;
        }
        String httpRequest = generateMethodLine(request);
        // Add the HTTP request to the .http file.
        return WriteCommandAction.runWriteCommandAction(project, (Computable<Integer>) () -> {
            Document document = FileDocumentManager.getInstance().getDocument(httpFile.getVirtualFile());
            if (document != null) {
                document.insertString(document.getTextLength(), httpRequest);
            }
            return Objects.requireNonNull(document).getLineCount();
        });
    }

    private String generateMethodLine(Request request) {
        return "### " + method.getName() + " test" + "\n" +
                request.getMethod() + " " + "http://localhost:8080/" +
                request.getPathAbsolute().iterator().next() + "\n";
    }

    private int findTestMethodInHttpFile(String content) {
        String fileContent = httpFile.getText();
        String[] lines = fileContent.split("\n");
        int shortestDistance = Integer.MAX_VALUE;
        int offset = -1;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(content) && (lines[i].length() - content.length()) < shortestDistance) {
                offset = i;
            }
        }
        return offset; // No match found
    }

    private PsiFile findAssociatedHttpFileForController(PsiMethod method) {
        PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            return null;
        }

        String controllerName = containingClass.getName();
        if (controllerName == null) {
            return null;
        }

        // Create a probable http filename based on the controller name
        String probableHttpFileName = controllerName + ".http";

        Project project = method.getProject();
        PsiFile[] foundFiles = FilenameIndex.getFilesByName(project, probableHttpFileName, GlobalSearchScope.projectScope(project));
        if (foundFiles.length > 0) {
            return foundFiles[0];  // Return the first matched file
        }
        return null;
    }
}
