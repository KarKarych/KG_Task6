package ru.computerGraphics.update;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class UpdateLabelModule extends AbstractModule {
  @Override
  protected void configure() {
    bindInterceptor(Matchers.any(), Matchers.annotatedWith(UpdateLabel.class), new UpdateLabelInterceptor());
  }
}
