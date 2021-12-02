
	@Controller
    @RequestMapping("/")
    public class RedirectController {
        
        @GetMapping("/redirectWithRedirectPrefix")
        public ModelAndView redirectWithUsingRedirectPrefix(ModelMap model) {
            model.addAttribute("attribute", "redirectWithRedirectPrefix");
            return new ModelAndView("redirect:/images/page", model);
        }
    }