package com.lion.ws.controller;

import com.lion.ws.entity.User;
import com.lion.ws.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired private UserService userService;

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        List<User> userList = userService.getUsers();
        session.setAttribute("menu", "user");
        model.addAttribute("userList", userList);
        return "user/list";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "user/register";
    }

    @PostMapping("/register")
    public String registerProc(String uid, String pwd, String pwd2, String uname, String email, String profileUrl) {
        if (userService.findByUid(uid) == null && pwd.equals(pwd2) && pwd.length() >= 4) {
            String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
            User user = User.builder()
                    .uid(uid).pwd(hashedPwd).uname(uname).email(email).profileUrl(profileUrl)
                    .regDate(LocalDate.now()).role("ROLE_USER").provider("local")
                    .build();

            userService.registerUser(user);
        }
        return "redirect:/user/list";
    }

    @GetMapping("/update/{uid}")
    public String updateForm(@PathVariable String uid, Model model) {
        User user = userService.findByUid(uid);
        model.addAttribute("user", user);
        return "user/update";
    }

    @PostMapping("/update")
    public String updateProc(String uid, String pwd, String pwd2, String uname, String email, String profileUrl, String role, String provider) {
        User user = userService.findByUid(uid);
        if (pwd.equals(pwd2) && pwd.length() >= 4) {
            String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
            user.setPwd(hashedPwd);
        }
        user.setUname(uname);
        user.setEmail(email);
        user.setProfileUrl(profileUrl);
        user.setRole(role);
        user.setProvider(provider);
        userService.updateUser(user);
        return "redirect:/user/list";
    }

    @GetMapping("/delete/{uid}")
    public String delete(@PathVariable String uid) {
        userService.deleteUser(uid);
        return "redirect:/user/list";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String loginProc(String uid, String pwd, HttpSession session, Model model) {
        String msg, url;
        int result = userService.login(uid, pwd);
        if (result == UserService.CORRECT_LOGIN) {
            User user = userService.findByUid(uid);
            session.setAttribute("sessUid", uid);
            session.setAttribute("sessUname", user.getUname());
            session.setMaxInactiveInterval(4 * 60 * 60);        // 세션 타임아웃 시간: 4시간
            msg = user.getUname() + "님 환영합니다.";
            url = "/chat/home";
        } else if (result == UserService.WRONG_PASSWORD) {
            msg = "패스워드가 틀렸습니다.";
            url = "/user/login";
        } else {
            msg = "입력한 아이디가 존재하지 않습니다.";
            url = "/user/register";
        }
        model.addAttribute("msg", msg);
        model.addAttribute("url", url);
        return "common/alertMsg";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }
}
