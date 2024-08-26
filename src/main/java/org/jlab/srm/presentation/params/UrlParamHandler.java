package org.jlab.srm.presentation.params;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

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
