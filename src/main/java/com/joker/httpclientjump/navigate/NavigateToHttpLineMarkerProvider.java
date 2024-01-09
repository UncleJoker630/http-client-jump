package com.joker.httpclientjump.navigate;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.joker.httpclientjump.action.NewRequestAction;
import com.joker.httpclientjump.util.JavaUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;

/**
 * java类导航到http文件
 */
public class NavigateToHttpLineMarkerProvider extends RelatedItemLineMarkerProvider {

    final static Icon icon = IconLoader.getIcon("/icons/request.svg", NavigateToHttpLineMarkerProvider.class.getClassLoader());

    public NavigateToHttpLineMarkerProvider() {
        super();
    }


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (JavaUtil.isRestMethod(element)) {

            PsiElement nameIdentifier = ((PsiNameIdentifierOwner) element).getNameIdentifier();
            if (nameIdentifier == null) {
                return;
            }
            NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(icon)
                            .setTarget(null)
                            .setAlignment(GutterIconRenderer.Alignment.CENTER)
                            .setTooltipTitle("Navigation to Target in Http File");
            GutterIconNavigationHandler<PsiElement> handler = (e, elt) -> {
                NewRequestAction newRequestAction = new NewRequestAction((PsiMethod) element);
                AnActionEvent event = AnActionEvent.createFromInputEvent(e, ActionPlaces.MOUSE_SHORTCUT, new Presentation(), DataContext.EMPTY_CONTEXT);
                newRequestAction.actionPerformed(event);
            };
            result.add(builder.createLineMarkerInfo(nameIdentifier, handler));
        }
    }
}
