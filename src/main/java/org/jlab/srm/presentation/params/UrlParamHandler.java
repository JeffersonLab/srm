package org.jlab.srm.presentation.params;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface UrlParamHandler<E> {
  E convert();

  void validate(E params);

  void store(E params);

  E defaults();

  E materialize();

  boolean qualified();

  String message(E params);

  void redirect(HttpServletResponse response, E params) throws IOException;
}
