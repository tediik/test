package com.jm.online_store.service.impl;

import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.exception.InvalidEmailException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.repository.RoleRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConfirmationTokenRepository confirmTokenRepository;
    private final MailSenderServiceImpl mailSenderService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    @Setter
    private PasswordEncoder passwordEncoder;

    @Value("${spring.server.url}")
    private String urlActivate;

    @Transactional
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean isExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    @Transactional
    public void addUser(@NotNull User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (ValidationUtils.isNotValidEmail(user.getEmail())) {
            throw new InvalidEmailException();
        }
        if (isExist(user.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.setRoles(persistRoles(user.getRoles()));
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(@NotNull User user) {
        User editUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        if (!editUser.getEmail().equals(user.getEmail())) {
            if (isExist(user.getEmail())) {
                throw new EmailAlreadyExistsException();
            } else if (ValidationUtils.isNotValidEmail(user.getEmail())) {
                throw new InvalidEmailException();
            }
            editUser.setEmail(user.getEmail());
        }
        editUser.setRoles(persistRoles(user.getRoles()));
        log.debug("editUser: {}", editUser);
        userRepository.save(editUser);
    }

    @Override
    @Transactional
    public void deleteByID(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public void regNewAccount(User userForm) {
        ConfirmationToken confirmationToken = new ConfirmationToken(userForm.getEmail(), userForm.getPassword());
        confirmTokenRepository.save(confirmationToken);

        String message = String.format(
                "Hello, %s! \n" +
                        "Welcome to online-store. Please, visit next link: " +
                        urlActivate + "/activate/%s",
                userForm.getEmail(),
                confirmationToken.getConfirmationToken()
        );

        mailSenderService.send(userForm.getEmail(), "Activation code", message);
    }

    @Override
    public boolean activateUser(String token, HttpServletRequest request) {

        ConfirmationToken confirmationToken = confirmTokenRepository.findByConfirmationToken(token);
        if (confirmationToken == null) {
            return false;
        }

        Set<Role> userSetRoles = Collections.singleton(roleRepository.findByName("ROLE_CUSTOMER").get());

        User user = new User();
        user.setEmail(confirmationToken.getUserEmail());
        user.setPassword(confirmationToken.getUserPassword());
        user.setRoles(userSetRoles);

        addUser(user);

        try {
            request.login(user.getEmail(), confirmationToken.getUserPassword());
        } catch (ServletException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Transactional
    public void updateUserImage(Long userId, MultipartFile file) throws IOException {
        //Save image to Uploads folder in users directory
        String uploadDirectory = System.getProperty("user.dir") + "/uploads";
        Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
        byte[] bytes = file.getBytes();
        Files.write(fileNameAndPath, bytes);
        //Set user's profile picture
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        User user = userRepository.findById(userId).get();
        user.setProfilePicture(filename);
        userRepository.save(user);
    }

    public void deleteUserImage(Long userId) {
        User user = userRepository.findById(userId).get();
        String filename = StringUtils.cleanPath("def.jpg");
        user.setProfilePicture(filename);
        userRepository.save(user);
    }

    private Set<Role> persistRoles(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .map(roleRepository::findByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
}
