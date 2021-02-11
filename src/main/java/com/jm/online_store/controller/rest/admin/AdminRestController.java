package com.jm.online_store.controller.rest.admin;

import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.User;
<<<<<<< HEAD
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.UserDto;
=======
import com.jm.online_store.service.interf.CommonSettingsService;
>>>>>>> dev
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin rest controller
 */
@Slf4j
@AllArgsConstructor
@RestController
@Api(value = "Rest controller for actions from admins page")
@RequestMapping(value = "/api/admin")
public class AdminRestController {

    private final UserService userService;

    private final FavouritesGroupService favouritesGroupService;

    private final CommonSettingsService commonSettingsService;

    /**
     * Rest mapping to  receive authenticated user. from admin page
     * @return ResponseEntity<ResponseDto<UserDto>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/authUser")
    @ApiOperation(value = "receive authenticated user. from admin page", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<UserDto>> showAuthUserInfo() {
        User authUser = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(new ResponseDto<>(true, UserDto.fromUser(authUser)));
    }

    /**
     * Rest mapping to receive all users from db. from admin page
     * @return ResponseEntity<ResponseDto<List<UserDto>>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/allUsers")
    @ApiOperation(value = "return list of users", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No users in db"),
            @ApiResponse(code = 200, message = "")
    })
    public ResponseEntity<ResponseDto<List<UserDto>>> getAllUsersList() {
        List<UserDto> allUsersDto = new ArrayList<>();
        for (User user: userService.findAll()){
            allUsersDto.add(UserDto.fromUser(user));
        }
        if (allUsersDto.size() == 0) {
            log.debug("There are no users in db");
            return new ResponseEntity<>(new ResponseDto<>(false, "No users in db"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ResponseDto<>(true, allUsersDto), HttpStatus.OK);
    }

    /**
     * rest mapping to receive user by id from db. from admin page
     * @param id - user id (Long)
     * @return ResponseEntity<ResponseDto<UserDto>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/users/{id}")
    @ApiOperation(value = "receive user by id from db. from admin page", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User with id \"id\" not found"),
            @ApiResponse(code = 200, message = "")
    })
    public ResponseEntity<ResponseDto<UserDto>> getUserInfo(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            log.debug("User with id: {} not found", id);
            return new ResponseEntity<>(new ResponseDto<>(false, "User with id " + id + " not found"), HttpStatus.NOT_FOUND);
        }
        User user = userService.findById(id).get();
        log.debug("User with id: {} found, email is: {}", id, user.getEmail());
        return new ResponseEntity<>(new ResponseDto<>(true, UserDto.fromUser(user)), HttpStatus.OK);
    }

    /**
     * Rest mapping to delete user from db by his id from admin page
     * @param id - id of User to delete {@link Long}
     * @return ResponseEntity<ResponseDto<UserDto>>(userToDelete, HttpStatus) {@link ResponseEntity}
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "delete user from db by his id from admin page", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User with id \"id\"  not found"),
            @ApiResponse(code = 200, message = ""),
    })
    public ResponseEntity<ResponseDto<UserDto>> deleteUser(@PathVariable Long id) {
        User userToDelete = userService.findUserById(id);
        try {
            userService.deleteByID(id);
        } catch (IllegalArgumentException | EmptyResultDataAccessException e) {
            log.debug("There is no user with id: {}", id);
            return new ResponseEntity<>(new ResponseDto<>(false, "User with id" + id + "not found"), HttpStatus.NOT_FOUND);
        }
        log.debug("User with id: {}, was deleted successfully", id);
        return new ResponseEntity<>(new ResponseDto<>(true, UserDto.fromUser(userToDelete)), HttpStatus.OK);
    }

    /**
     * rest mapping to modify user from admin page
     * @param user {@link User}
     * @return new ResponseEntity<ResponseDto>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @PutMapping
    @ApiOperation(value = "modify user from admin page", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 400, message = "Not valid email error / Empty roles error / Duplicated email error"),
            @ApiResponse(code = 200, message = "")
    })
    public ResponseEntity<ResponseDto<UserDto>> editUser(@RequestBody User user) {
        if (userService.findById(user.getId()).isEmpty()) {
            log.debug("There are no user with id: {}", user.getId());
            return new ResponseEntity<>(new ResponseDto<>(false, "User not found"), HttpStatus.NOT_FOUND);
        }
        if (ValidationUtils.isNotValidEmail(user.getEmail())) {
            log.debug("Wrong email! Не правильно введен email");
            return new ResponseEntity<>(new ResponseDto<>(false, "Not valid email error"), HttpStatus.BAD_REQUEST);
        }
        if (user.getRoles().size() == 0) {
            log.debug("Roles not selected");
            return new ResponseEntity<>(new ResponseDto<>(false, "Empty roles error"), HttpStatus.BAD_REQUEST);
        }
        if (!userService.findById(user.getId()).get().getEmail().equals(user.getEmail())
                && userService.isExist(user.getEmail())) {
            log.debug("User with same email already exists");
            return new ResponseEntity<>(new ResponseDto<>(false, "Duplicated email error"), HttpStatus.BAD_REQUEST);
        }
        userService.updateUserFromAdminPage(user);
        log.debug("Changes to user with id: {} was successfully added", user.getId());
        return new ResponseEntity<>(new ResponseDto<>(true, UserDto.fromUser(user)), HttpStatus.OK);
    }

    /**
     * Rest mapping to add new user from admin page
     * @param newUser {@link User}
     * @return new ResponseEntity<ResponseDto>(UserDto user, HttpStatus) {@link ResponseEntity}
     */
    @PostMapping
    @ApiOperation(value = "add new user from admin page", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "User with same email already exists"),
            @ApiResponse(code = 400, message = "Not valid Email / Empty password or roles error"),
    })
    public ResponseEntity<ResponseDto<UserDto>> addNewUser(@RequestBody User newUser) {
        if (ValidationUtils.isNotValidEmail(newUser.getEmail())) {
            log.debug("Wrong email! Не правильно введен email");
            return new ResponseEntity<>(new ResponseDto<>(false, "Not valid Email"), HttpStatus.BAD_REQUEST);
        }
        if (userService.isExist(newUser.getEmail())) {
            log.debug("User with same email already exists");
            return new ResponseEntity<>(new ResponseDto<>(false, "User with same email already exists"), HttpStatus.CONFLICT);
        }
        if (newUser.getPassword().equals("")) {
            log.debug("Password empty");
            return new ResponseEntity<>(new ResponseDto<>(false, "Empty password error"), HttpStatus.BAD_REQUEST);
        }
        if (newUser.getRoles().size() == 0) {
            log.debug("Roles not selected");
            return new ResponseEntity<>(new ResponseDto<>(false, "Empty roles error"), HttpStatus.BAD_REQUEST);
        }
        userService.addNewUserFromAdmin(newUser);
        User customer = userService.findByEmail(newUser.getEmail()).get();
        FavouritesGroup favouritesGroup = new FavouritesGroup();
        favouritesGroup.setName("Все товары");
        favouritesGroup.setUser(customer);
        favouritesGroupService.save(favouritesGroup);
        userService.updateUser(customer);
        return new ResponseEntity<>(new ResponseDto<>(true, UserDto.fromUser(newUser)), HttpStatus.OK);
    }

    /**
     * Rest mapping to filter list on users by choosen role
     * @param role - choosen role
     * @return ResponseEntity<ResponseDto<List<UserDto>>>(ResponseDto, HttpStatus) filtered user's list
     */
    @PutMapping(value = "/{role}")
    @ApiOperation(value = "filter list on users by chosen role", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "There are no users with chosen role in db"),
            @ApiResponse(code = 200, message = "")
    })
    public ResponseEntity<ResponseDto<List<UserDto>>> filterByRoles(@PathVariable String role) {
        List<UserDto> allUsersWithRoleDto = new ArrayList<>();
        for (User user: userService.findByRole(role)){
            allUsersWithRoleDto.add(UserDto.fromUser(user));
        }
        if (allUsersWithRoleDto.size() == 0) {
            log.debug("There are no users with chosen role in db");
            return new ResponseEntity<>(new ResponseDto<>(false, "No users with chosen role in db"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ResponseDto<>(true, allUsersWithRoleDto), HttpStatus.OK);
    }

    /**
     * Метод для изменения наименования магазина
     * @param commonSettings настройки, содержащие название магазина
     * @return ResponseEntity
     */
    @ApiOperation(value = "edit store name", authorizations = { @Authorization(value="jwtToken") })
    @PutMapping(value = "/editStoreName")
    public ResponseEntity<Integer> editStoreName(CommonSettings commonSettings){
        commonSettingsService.updateTextValue(commonSettings);
        return ResponseEntity.ok().build();
    }
}
