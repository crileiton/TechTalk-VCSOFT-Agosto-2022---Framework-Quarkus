package org.quarkus.vcsoft.helpers.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Problem {

  private String type;
  private String title;
  private String detail;
  private String instance;

}

