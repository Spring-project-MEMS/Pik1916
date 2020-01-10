package course.spring.webmvc.web;
import course.spring.webmvc.domain.UsersService;
import course.spring.webmvc.exception.InvalidEntityException;
import course.spring.webmvc.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@Slf4j
public class UserController {
    private static final String UPLOADS_DIR = "tmp";

    @Autowired
    private UsersService usersService;

    @GetMapping

    public String getUsers(Model model) {
        model.addAttribute("path", "users");
        model.addAttribute("users", usersService.findAllUsers());
        log.debug("GET Users: " + usersService.findAllUsers());
        return "users";
    }

    @PostMapping("/registration")
    public String addUser(@Valid @ModelAttribute ("user") User user,
                          BindingResult errors,
//                             @RequestParam(name = "cancel", required = false) String cancelBtn,
                          @RequestParam("file") MultipartFile file,
                          Model model) {
//        if(cancelBtn != null) return "redirect:/posts";
        if (errors.hasErrors()) {
            log.info(errors.getAllErrors().toString());
            List<String> errorMessages = errors.getAllErrors().stream().map(err -> {
                ConstraintViolation cv = err.unwrap(ConstraintViolation.class);
                return String.format("Error in '%s' - invalid value: '%s'", cv.getPropertyPath(), cv.getInvalidValue());
            }).collect(Collectors.toList());

            model.addAttribute("myErrors", errorMessages);
            model.addAttribute("fileError", null);
            return "registration";
        } else {
            log.info("POST User: " + user);
            if (!file.isEmpty() && file.getOriginalFilename().length() > 0) {
                if (Pattern.matches("\\w+\\.(jpg|png)", file.getOriginalFilename())) {
                    handleMultipartFile(file);
                    user.setPictureUrl(file.getOriginalFilename());
                } else {
                    model.addAttribute("myErrors", null);
                    model.addAttribute("fileError", "Submit picture [.jpg, .png]");
                    return "registration";
                }
            }
            if (user.getId() == null) {
                log.info("ADD New User: " + user);
                user.setRole("ROLE_BLOGGER");
                usersService.addUser(user);
            } else {
                log.info("UPDATE user: " + user);
                usersService.updateUser(user);
            }
            return "redirect:/users";
        }
    }

    @GetMapping("/registration")
    public String getRegistration(@ModelAttribute ("user") User user, ModelMap model,
                              @RequestParam(value="mode", required=false) String mode,
                              @RequestParam(value="userId", required=false) String userId){
        String title = "Register";
        if("edit".equals(mode)) {
            user = usersService.findUserByIs(userId);
            model.addAttribute("user", user);
            title = "Edit User Info";
        }

        model.addAttribute("path", "registration");
        model.addAttribute("title", title);
        return "registration";
    }


    @PostMapping(params = "edit")
    public String editUser(@RequestParam("edit") String editId, Model model){
        log.info("Editing user: " + editId);
        return "redirect:/users/registration?mode=edit&userId=" + editId;
    }

    @PostMapping(params = "delete")
    public String deleteUser(@RequestParam("delete") String deleteId, Model model){
        log.info("Deleting user: " + deleteId);
        usersService.removeUser(deleteId);
        return "redirect:/users";
    }

    @PostMapping(params = "active")
    public String activateUser(@RequestParam("active") String activeId, Model model){
        log.info("Activating user: " + activeId);
        usersService.activateUser(activeId);
        return "redirect:/users";
    }

    @PostMapping(params = "inactive")
    public String deactivateUser(@RequestParam("inactive") String inactiveId, Model model){
        log.info("Deactivating user: " + inactiveId);
        usersService.deactivateUser(inactiveId);
        return "redirect:/users";
    }

    private void handleMultipartFile(MultipartFile file) {
        String name = file.getOriginalFilename();
        long size = file.getSize();
        log.info("File: " + name + ", Size: " + size);
        try {
            File currentDir = new File(UPLOADS_DIR);
            if(!currentDir.exists()) {
                currentDir.mkdirs();
            }
            String path = currentDir.getAbsolutePath() + "/" + file.getOriginalFilename();
            path = new File(path).getAbsolutePath();
            log.info(path);
            File f = new File(path);
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(f));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
