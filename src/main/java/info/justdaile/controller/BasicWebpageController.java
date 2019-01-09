package info.justdaile.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping(method = RequestMethod.GET)
public class BasicWebpageController {

    @RequestMapping(path = "/")
    public ModelAndView indexPage(@CookieValue(name = "DSESSION", required = false) String idtoken, Model model){
        model.addAttribute("hasUser", false); // false until proven true

        if(idtoken != null){
            try {
                FirebaseToken token = FirebaseAuth.getInstance().verifySessionCookieAsync(idtoken).get();
                model.addAttribute("idtoken", idtoken);
                model.addAttribute("username", token.getName());
                model.addAttribute("hasUser", true);
            } catch (InterruptedException | ExecutionException e) {
                // TODO session is invalid we need to DSESSION fbsession cookie
                // e.printStackTrace();
                // should just take user to login page
            }
        }
        return new ModelAndView("index", "model", model);
    }

}
