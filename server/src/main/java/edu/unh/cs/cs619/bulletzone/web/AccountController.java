package edu.unh.cs.cs619.bulletzone.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import edu.unh.cs.cs619.bulletzone.repository.DataRepository;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;

@RestController
@RequestMapping(value = "/games/account")
public class AccountController {
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final DataRepository data;
    private GameUser gu;

    @Autowired
    public AccountController(DataRepository repo) {
        this.data = repo;
    }

    /**
     * Handles a PUT request to register a new user account. This calls the validateUser function on
     * the DataRepository variable we have in this class called "data". The function is called
     * inside a BooleanWrapper ResponseEntity so that the creation of the new user will either be
     * shown as completed (true) or failed (false). Because this function is looking to register a
     * new user, we are passing a boolean of true as the create parameter of validateUser. 
     *
     * @param name The username
     * @param password The password
     * @return a response w/ success boolean
     */
    @RequestMapping(method = RequestMethod.PUT, value = "register/{name}/{password}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<BooleanWrapper> register(@PathVariable String name, @PathVariable String password)
    {
        // Log the request
        log.debug("Register '" + name + "' with password '" + password + "'");
        // Return the response (true if account created)
//        /*
        return new ResponseEntity<BooleanWrapper>(new BooleanWrapper(
//                TODO: something that invokes users.createUser(name, password) and does
//                      other setup in the DataRepository (actually calls data.validateUser(...))
                (data.validateUser(name, password, true) != null)
                ),
                HttpStatus.CREATED);
    }

    /**
     * Handles a PUT request to login a user. This calls the validateUser() function on the
     * DataRepository variable we have called "data" and is called inside of a LongWrapper
     * ResponseEntity so that the ResponseEntity will contain the (possible) userID of the user
     * logging in. It will hold null otherwise. Because we are logging in rather than registering a
     * new account, a boolean of false is passed into the validateUser parameter for create.
     *
     * @param name The username
     * @param password The password
     * @return a response w/ the user ID (or -1 if invalid)
     */
    @RequestMapping(method = RequestMethod.PUT, value = "login/{name}/{password}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<LongWrapper> login(@PathVariable String name, @PathVariable String password)
    {
        // Log the request
        log.debug("Login '" + name + "' with password '" + password + "'");
        // Return the response (return user ID if valid login)

        return new ResponseEntity<LongWrapper>(new LongWrapper(
//                TODO: something that invokes users.validateLogin(name, password) in
//                      the DataRepository (actually calls data.validateUser(...))
                data.validateUser(name, password, false).getId()
                ),
                HttpStatus.OK);
    }

}
