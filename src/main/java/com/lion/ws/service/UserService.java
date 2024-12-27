package com.lion.ws.service;

import com.lion.ws.entity.User;
import com.lion.ws.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    public static final int CORRECT_LOGIN = 0;
    public static final int WRONG_PASSWORD = 1;
    public static final int USER_NOT_EXIST = 2;
    @Autowired private UserRepository userRepository;

    public User findByUid(String uid) {
        return userRepository.findById(uid).orElse(null);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void registerUser(User user) {
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(String uid) {
        userRepository.deleteById(uid);
    }

    public int login(String uid, String pwd) {
        User user = findByUid(uid);
        if (user == null)
            return USER_NOT_EXIST;
        if (BCrypt.checkpw(pwd, user.getPwd()))
            return CORRECT_LOGIN;
        return WRONG_PASSWORD;
    }
}
