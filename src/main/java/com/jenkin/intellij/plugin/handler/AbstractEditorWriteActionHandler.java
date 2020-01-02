package com.jenkin.intellij.plugin.handler;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * for showing dialogs before the action
 */
public abstract class AbstractEditorWriteActionHandler<T> extends EditorActionHandler {

  private final Class actionClass;

  public AbstractEditorWriteActionHandler(Class actionClass) {
    super(false);
    this.actionClass = actionClass;
  }

  @Override
  protected final void doExecute(final Editor editor, @Nullable final Caret caret, final DataContext dataContext) {

    final Pair<Boolean, T> additionalParameter = beforeWriteAction(editor, dataContext);
    if (!additionalParameter.first) {
      return;
    }

    final Runnable runnable = () -> executeWriteAction(editor, caret, dataContext, additionalParameter.second);
    new com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler(false) {
      @Override
      public void executeWriteAction(Editor editor1, @Nullable Caret caret1, DataContext dataContext1) {
        runnable.run();
      }
    }.doExecute(editor, caret, dataContext);
  }


  protected abstract void executeWriteAction(Editor editor, @Nullable Caret caret, DataContext dataContext, @Nullable T additionalParameter);

  @NotNull
  protected Pair<Boolean, T> beforeWriteAction(Editor editor, DataContext dataContext) {
    return continueExecution();
  }

  protected final Pair<Boolean, T> stopExecution() {
    return new Pair<>(false, null);
  }

  protected final Pair<Boolean, T> continueExecution(T additionalParameter) {
    return new Pair<>(true, additionalParameter);
  }

  protected final Pair<Boolean, T> continueExecution() {
    return new Pair<>(true, null);
  }

}
