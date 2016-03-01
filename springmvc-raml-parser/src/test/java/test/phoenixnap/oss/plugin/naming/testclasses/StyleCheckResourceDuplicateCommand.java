package test.phoenixnap.oss.plugin.naming.testclasses;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/somethingCool")
public class StyleCheckResourceDuplicateCommand {
	

		@RequestMapping(value = "/child", method = RequestMethod.GET, headers = "application/headertest")
		@ResponseBody
		public Map<Long, Boolean> isSomethingCoolChild(@RequestParam Long colorId, @RequestParam String childName)
				throws Exception {

			return null;
		}


		@RequestMapping(value = "/{somethingCoolId}/child", method = RequestMethod.GET,  headers = "application/headertest")
		@ResponseBody
		public Boolean isAParticularSomethingCoolChild(@PathVariable Long somethingCoolId, @RequestParam String childName)
				throws Exception {

			return null;
		}
	
}
