package test.phoenixnap.oss.plugin.naming.testclasses;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

public class BugController {
	
	/**
     * Making request to resend stuff to user some other javadoc stuff that was here but removed
     *
     * @param id
     *            the user something id
     * @throws Exception exception
     */
    @RequestMapping(value = "forgotStuff/{somethingID}/resendStuff", method = POST)
    public void forgotStuffResendStuff(@PathVariable String tokenID)
            throws IllegalStateException {
    	//donothing
    }


}
