package test.phoenixnap.oss.plugin.naming.testclasses;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Emmanuel TOURDOT
 * @since 0.2.1
 *
 */
@RestController
@RequestMapping("/base")
public class NoValueController {

  @RequestMapping(method = { RequestMethod.GET })
  public String simpleMethodAllHttpMethods() {
    return null;
  }

}
