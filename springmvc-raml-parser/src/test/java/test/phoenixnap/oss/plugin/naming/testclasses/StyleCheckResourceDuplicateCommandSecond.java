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
public class StyleCheckResourceDuplicateCommandSecond {
	

		@RequestMapping(value = "/status", method = RequestMethod.GET, headers = "application/headertest")
		@ResponseBody
		public Map<Long, Boolean> isSomethingCoolStatus(@RequestParam Long colorId)
				throws Exception {

			return null;
		}


		@RequestMapping(value = "/{somethingCoolId}/status", method = RequestMethod.GET,  headers = "application/headertest")
		@ResponseBody
		public Boolean  isAParticularSomethingCoolStatus(@PathVariable Long somethingCoolId)
				throws Exception {

			return null;
		}
	
}
