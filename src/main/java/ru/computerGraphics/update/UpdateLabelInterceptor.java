package ru.computerGraphics.update;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.computerGraphics.screen.CurvesPanel;

public class UpdateLabelInterceptor implements MethodInterceptor {

  @Override
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    Object object = methodInvocation.proceed();

    ((CurvesPanel) methodInvocation.getThis()).updateLabelText();

    return object;
  }
}
